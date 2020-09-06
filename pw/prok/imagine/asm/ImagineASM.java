package pw.prok.imagine.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import pw.prok.imagine.ImagineLoadingPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM5;

public class ImagineASM {
    private static final Filter CONSTRUCTOR_FILTER = new Filter() {
        @Override
        public boolean matching(ImagineMethod method) {
            return "<init>".equals(method.getName());
        }
    };
    protected byte[] mClass;
    protected boolean mDev;
    protected ClassNode mClassNode;
    protected String mName;
    protected String mDevName;

    public ImagineASM() {
        mDev = ImagineLoadingPlugin.DEV;
    }

    public static ImagineASM create() {
        return new ImagineASM();
    }

    public ImagineASM loadClass(byte[] bytes) {
        return loadClass(null, null, bytes);
    }

    public ImagineASM loadClass(String name, String devName, byte[] bytes) {
        mClass = bytes;
        mClassNode = null;
        mName = name;
        mDevName = devName;

        if (mName == null) {
            reset();
            mName = toDesc(mClassNode.name);
            mClassNode = null;
        }
        if (mDevName == null) {
            mDevName = toDesc(ImagineRemapper.clazzDev(mName));
        }
        return this;
    }

    public String getName() {
        return mName;
    }

    public String getDevName() {
        return mDevName;
    }

    public boolean is(String name) {
        return ImagineASM.toDesc(mDevName).equals(ImagineASM.toDesc(ImagineRemapper.clazzDev(name)));
    }

    public boolean isDev() {
        return mDev;
    }

    protected void readClass() {
        if (mClassNode != null) return;
        reset();
    }

    public ImagineASM reset() {
        if (mClass == null) {
            mClassNode = null;
            return this;
        }
        mClassNode = new ClassNode(ASM5);
        ClassReader reader = new ClassReader(mClass);
        reader.accept(mClassNode, ClassReader.EXPAND_FRAMES);
        return this;
    }

    public ImagineASM clear() {
        mClass = null;
        mClassNode = null;
        mName = null;
        mDevName = null;
        return null;
    }

    public byte[] build() {
        if (mClassNode == null) return mClass;
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        mClassNode.accept(writer);
        return writer.toByteArray();
    }

    public ImagineASM renameClass(String name) {
        readClass();
        mClassNode.name = ImagineASM.toDesc(name);
        return this;
    }

    public ImagineASM renameClass(String obfName, String devName) {
        return renameClass(mDev ? devName : obfName);
    }

    public ImagineASM renameMappedClass(String name) {
        return renameClass(mapClazz(name));
    }

    public ImagineMethod method(String name, String desc) {
        return method(name, desc, true);
    }

    public ImagineMethod method(String name, String desc, boolean map) {
        readClass();
        MethodDesc m = mapMethod(toDesc(mName), name, desc, map);
        for (MethodNode method : mClassNode.methods) {
            if (m.second().equals(method.name) && m.third().equals(method.desc)) {
                return new ImagineMethod(this, method);
            }
        }
        name = m.second();
        desc = m.third();
        throw new RuntimeException(new NoSuchMethodException(name + desc));
    }

    public ImagineMethod addMethod(int flags, String name, String desc) {
        readClass();
        MethodNode node = new MethodNode(ASM5, flags, name, desc, null, null);
        mClassNode.methods.add(node);
        return new ImagineMethod(this, node);
    }

    public static String toDesc(String name) {
        return name == null ? null : name.replace('.', '/');
    }

    public static String toName(String desc) {
        return desc == null ? null : desc.replace('/', '.');
    }

    public String mapClazz(String name) {
        return ImagineRemapper.clazz(toDesc(name), mapping());
    }

    public Mapping mapping() {
        return mDev ? Mapping.DEV : Mapping.OBF;
    }

    public String mapDesc(String desc) {
        return mapDesc(desc, true);
    }

    public String mapDesc(String desc, boolean map) {
        if (!map) return desc;
        return ImagineRemapper.desc(desc, mapping());
    }

    public ImagineField field(String name) {
        return field(name, null, true);
    }

    public ImagineField field(String name, String desc) {
        return field(name, desc, true);
    }

    public ImagineField field(String name, String desc, boolean map) {
        readClass();
        FieldDesc m = mapField(toDesc(mName), name, map);
        desc = desc != null ? mapDesc(desc, map) : null;
        for (FieldNode field : mClassNode.fields) {
            if (m.second().equals(field.name) && (desc == null || desc.equals(field.desc))) {
                return new ImagineField(this, field);
            }
        }
        name = m.first();
        desc = m.second();
        throw new RuntimeException(new NoSuchFieldException(desc == null ? name : (desc + name)));
    }

    public ImagineField addField(String name, String desc, Object value) {
        readClass();
        FieldNode node = new FieldNode(ASM5, 0, name, desc, null, value);
        mClassNode.fields.add(node);
        return new ImagineField(this, node);
    }

    public FieldDesc mapField(String owner, String name) {
        return mapField(owner, name, true);
    }

    public FieldDesc mapField(String owner, String name, boolean map) {
        if (!map) return new FieldDesc(owner, name);
        return ImagineRemapper.field(owner, name, mapping());
    }

    public MethodDesc mapMethod(String owner, String method, String desc) {
        return mapMethod(owner, method, desc, true);
    }

    public MethodDesc mapMethod(String owner, String method, String desc, boolean map) {
        if (!map) return new MethodDesc(owner, method, desc);
        return ImagineRemapper.method(owner, method, desc, mapping());
    }

    public String getActualName() {
        if (mClassNode != null) {
            return ImagineASM.toName(mClassNode.name);
        }
        return mName;
    }

    public ImagineMethod constructor(String desc) {
        return method("<init>", desc);
    }

    public Iterable<ImagineMethod> constructors() {
        return filter(CONSTRUCTOR_FILTER);
    }

    public ImagineASM constructors(Action<ImagineMethod> action) {
        return action(constructors(), action);
    }

    public Iterable<ImagineMethod> filter(Filter filter) {
        return new MethodFilter(this, filter);
    }

    public ImagineASM action(Iterable<ImagineMethod> methods, Action<ImagineMethod> action) {
        for (ImagineMethod method : methods) {
            action.action(method);
        }
        return this;
    }

    public ImagineASM action(Filter filter, Action<ImagineMethod> action) {
        return action(filter(filter), action);
    }

    public static List<AbstractInsnNode> asList(InsnList instructions) {
        List<AbstractInsnNode> list = new ArrayList<AbstractInsnNode>(instructions.size());
        Iterator<AbstractInsnNode> iterator = instructions.iterator();
        while (iterator.hasNext()) list.add(iterator.next());
        return list;
    }

    public ClassNode getClassNode() {
        readClass();
        return mClassNode;
    }
}
