package pw.prok.imagine.asm;

import LZMA.LzmaInputStream;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import pw.prok.imagine.api.Pair;
import pw.prok.imagine.api.Triple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ImagineRemapper {
    public static Triple<String, String, String> createMethodDesc(String s) {
        return Triple.create(cutMethodClass(s), cutMethod(s), cutMethodDesc(s));
    }

    public enum MappingType {
        Srg, Method, Field
    }

    protected static final BiMap<String, String> sRawClass = HashBiMap.create();
    protected static final BiMap<String, String> sRawField = HashBiMap.create();
    protected static final BiMap<String, String> sRawMethod = HashBiMap.create();

    protected static final Map<String, String> sSrgMethods = new HashMap<>();
    protected static final Map<String, String> sSrgFields = new HashMap<>();

    private static Set<Pair<String, String>> sClasses;
    private static Set<Triple<MethodDesc, MethodDesc, MethodDesc>> sMethods;
    private static Set<Triple<FieldDesc, FieldDesc, FieldDesc>> sFields;
    private static boolean sMapsDirty = true;

    public static String descDev(String desc) {
        return desc(desc, Mapping.DEV);
    }

    public static String descObf(String desc) {
        return desc(desc, Mapping.OBF);
    }

    public static String descSrg(String desc) {
        return desc(desc, Mapping.SRG);
    }

    public static String desc(String desc, Mapping mapping) {
        StringBuilder newDesc = new StringBuilder();
        for (int i = 0; i < desc.length(); i++) {
            char c = desc.charAt(i);
            if (c == 'L') {
                int end = desc.indexOf(';', i + 1);
                newDesc.append('L');
                newDesc.append(mapping.clazz(desc.substring(i + 1, end)));
                newDesc.append(';');
                i = end;
            } else {
                newDesc.append(c);
            }
        }
        return newDesc.toString();
    }

    public static String clazzDev(String name) {
        return clazz(name, Mapping.DEV);
    }

    public static String clazzObf(String name) {
        return clazz(name, Mapping.OBF);
    }

    public static String clazzSrg(String name) {
        return clazz(name, Mapping.SRG);
    }

    public static String clazz(String name, Mapping mapping) {
        return mapping.clazz(name);
    }

    private static String methodSrgToDev(String srg) {
        String dev = sSrgMethods.get(srg);
        return dev != null ? dev : srg;
    }

    public static MethodDesc methodDev(String owner, String name, String desc) {
        return method(owner, name, desc, Mapping.DEV);
    }

    public static MethodDesc methodObf(String owner, String name, String desc) {
        return method(owner, name, desc, Mapping.OBF);
    }

    public static MethodDesc methodSrg(String owner, String name, String desc) {
        return method(owner, name, desc, Mapping.SRG);
    }

    public static MethodDesc method(String owner, String name, String desc, Mapping mapping) {
        return mapping.method(owner, name, desc);
    }

    public static String cutMethod(String fullDesc) {
        int c = fullDesc.indexOf('(');
        if (c > 0) {
            int n = fullDesc.lastIndexOf('/', c);
            return fullDesc.substring(n >= 0 ? n + 1 : 0, c);
        }
        return fullDesc;
    }

    public static String cutMethodClass(String fullDesc) {
        int c = fullDesc.indexOf('(');
        if (c > 0) {
            int n = fullDesc.lastIndexOf('/', c);
            if (n > 0) return fullDesc.substring(0, n);
        }
        return fullDesc;
    }

    public static String cutDesc(String fullDesc) {
        int c = fullDesc.indexOf('(');
        if (c > 0) return fullDesc.substring(c);
        return fullDesc;
    }

    public static String cutMethodDesc(String fullDesc) {
        int c = fullDesc.indexOf('(');
        if (c > 0) {
            return fullDesc.substring(c);
        }
        return fullDesc;
    }

    public static String cutField(String fullDesc) {
        int c = fullDesc.lastIndexOf('/');
        if (c > 0) return fullDesc.substring(c + 1);
        return fullDesc;
    }

    public static String cutFieldClass(String fullDesc) {
        int c = fullDesc.lastIndexOf('/');
        if (c > 0) return fullDesc.substring(0, c);
        return fullDesc;
    }

    private static String fieldSrgToDev(String srg) {
        String dev = sSrgFields.get(srg);
        return dev != null ? dev : srg;
    }

    public static FieldDesc fieldDev(String owner, String name) {
        return field(owner, name, Mapping.DEV);
    }

    public static FieldDesc fieldObf(String owner, String name) {
        return field(owner, name, Mapping.OBF);
    }

    public static FieldDesc fieldSrg(String owner, String name) {
        return field(owner, name, Mapping.SRG);
    }

    public static FieldDesc field(String owner, String name, Mapping mapping) {
        return mapping.field(owner, name);
    }


    public static void mergeDeobfuscationData(InputStream is, MappingType type) throws Exception {
        Map<String, String> srgData = null;
        switch (type) {
            case Method:
                srgData = sSrgMethods;
                markDirty();
                break;
            case Field:
                srgData = sSrgFields;
                markDirty();
                break;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        if (type == MappingType.Srg) {
            mergeSrgData(reader);
            return;
        }
        String line;
        boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if (firstLine) {
                firstLine = false;
                continue;
            }
            int c1 = line.indexOf(',');
            if (c1 < 0) continue;
            int c2 = line.indexOf(',', c1 + 1);
            if (c2 < 0) continue;
            String srgName = line.substring(0, c1);
            String devName = line.substring(c1 + 1, c2);
            srgData.put(srgName, devName);
        }
        reader.close();
        is.close();
    }

    private static void markDirty() {
        sMapsDirty = true;
    }

    private static void mergeSrgData(BufferedReader reader) throws Exception {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            switch (parts[0]) {
                case "CL:":
                    sRawClass.put(parts[1], parts[2]);
                    break;
                case "FD:":
                    sRawField.put(parts[1], parts[2]);
                    break;
                case "MD:":
                    sRawMethod.put(parts[1] + parts[2], parts[3] + parts[4]);
                    break;
            }
        }
    }

    public static void setupDeobfuscationData(String srg, String methodData, String fieldData) {
        try {
            mergeDeobfuscationData(new LzmaInputStream(ImagineRemapper.class.getResourceAsStream(srg)), MappingType.Srg);
            mergeDeobfuscationData(new LzmaInputStream(ImagineRemapper.class.getResourceAsStream(methodData)), MappingType.Method);
            mergeDeobfuscationData(new LzmaInputStream(ImagineRemapper.class.getResourceAsStream(fieldData)), MappingType.Field);
        } catch (Exception e) {
            throw new IllegalStateException("Couldn't load deobfuscation data", e);
        }
    }

    private static void ensureMappings() {
        if (!sMapsDirty) return;

        Set<Pair<String, String>> classes = new HashSet<>();
        for (Map.Entry<String, String> clazz : sRawClass.entrySet()) {
            String obf = clazz.getKey();
            String srg = clazz.getValue();
            classes.add(new Pair<>(obf, srg));
        }
        sClasses = ImmutableSet.copyOf(classes);

        Set<Triple<MethodDesc, MethodDesc, MethodDesc>> methods = new HashSet<>();
        for (Map.Entry<String, String> method : sRawMethod.entrySet()) {
            MethodDesc obf = new MethodDesc(method.getKey());
            MethodDesc srg = new MethodDesc(method.getValue());
            MethodDesc dev = new MethodDesc(srg.first(), methodSrgToDev(srg.second()), srg.third());
            methods.add(new Triple<>(obf, srg, dev));
        }
        sMethods = ImmutableSet.copyOf(methods);

        Set<Triple<FieldDesc, FieldDesc, FieldDesc>> fields = new HashSet<>();
        for (Map.Entry<String, String> field : sRawField.entrySet()) {
            FieldDesc obf = new FieldDesc(field.getKey());
            FieldDesc srg = new FieldDesc(field.getValue());
            FieldDesc dev = new FieldDesc(srg.first(), fieldSrgToDev(srg.second()));
            fields.add(new Triple<>(obf, srg, dev));
        }
        sFields = ImmutableSet.copyOf(fields);

        sMapsDirty = false;
    }

    public static Set<Pair<String, String>> clazz() {
        ensureMappings();
        return sClasses;
    }

    public static Set<Triple<MethodDesc, MethodDesc, MethodDesc>> methods() {
        ensureMappings();
        return sMethods;
    }

    public static Set<Triple<FieldDesc, FieldDesc, FieldDesc>> fields() {
        ensureMappings();
        return sFields;
    }
}
