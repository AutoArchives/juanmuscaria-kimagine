package pw.prok.imagine.asm;

import pw.prok.imagine.api.Pair;
import pw.prok.imagine.api.Triple;

public enum Mapping {
    OBF {
        @Override
        public String clazz(String name) {
            for (Pair<String, String> mapping : ImagineRemapper.clazz()) {
                if (name.equals(mapping.first()) || name.equals(mapping.second()))
                    return mapping.first();
            }
            return name;
        }

        @Override
        public FieldDesc field(String owner, String name) {
            String obfOwner = OBF.clazz(owner);
            String srgOwner = SRG.clazz(owner);
            for (Triple<FieldDesc, FieldDesc, FieldDesc> mapping : ImagineRemapper.fields()) {
                if (mapping.first().equals(obfOwner, name)
                        || mapping.second().equals(srgOwner, name)
                        || mapping.third().equals(srgOwner, name)) {
                    return mapping.first();
                }
            }
            return new FieldDesc(obfOwner, name);
        }

        @Override
        public MethodDesc method(String owner, String name, String desc) {
            String obfOwner = OBF.clazz(owner);
            String obfDesc = ImagineRemapper.descObf(desc);
            String srgOwner = SRG.clazz(owner);
            String srgDesc = ImagineRemapper.descSrg(desc);
            for (Triple<MethodDesc, MethodDesc, MethodDesc> mapping : ImagineRemapper.methods()) {
                if (mapping.first().equals(obfOwner, name, obfDesc)
                        || mapping.second().equals(srgOwner, name, srgDesc)
                        || mapping.third().equals(srgOwner, name, srgDesc))
                    return mapping.first();
            }
            return new MethodDesc(obfOwner, name, obfDesc);
        }
    }, SRG {
        @Override
        public String clazz(String name) {
            for (Pair<String, String> mapping : ImagineRemapper.clazz()) {
                if (name.equals(mapping.first()) || name.equals(mapping.second()))
                    return mapping.second();
            }
            return name;
        }

        @Override
        public FieldDesc field(String owner, String name) {
            String obfOwner = OBF.clazz(owner);
            String srgOwner = SRG.clazz(owner);
            for (Triple<FieldDesc, FieldDesc, FieldDesc> mapping : ImagineRemapper.fields()) {
                if (mapping.first().equals(obfOwner, name)
                        || mapping.second().equals(srgOwner, name)
                        || mapping.third().equals(srgOwner, name)) {
                    return mapping.second();
                }
            }
            return new FieldDesc(srgOwner, name);
        }

        @Override
        public MethodDesc method(String owner, String name, String desc) {
            String obfOwner = OBF.clazz(owner);
            String obfDesc = ImagineRemapper.descObf(desc);
            String srgOwner = SRG.clazz(owner);
            String srgDesc = ImagineRemapper.descSrg(desc);
            for (Triple<MethodDesc, MethodDesc, MethodDesc> mapping : ImagineRemapper.methods()) {
                if (mapping.first().equals(obfOwner, name, obfDesc)
                        || mapping.second().equals(srgOwner, name, srgDesc)
                        || mapping.third().equals(srgOwner, name, srgDesc))
                    return mapping.second();
            }
            return new MethodDesc(srgOwner, name, srgDesc);
        }
    }, DEV {
        @Override
        public String clazz(String name) {
            return SRG.clazz(name);
        }

        @Override
        public FieldDesc field(String owner, String name) {
            String obfOwner = OBF.clazz(owner);
            String srgOwner = SRG.clazz(owner);
            for (Triple<FieldDesc, FieldDesc, FieldDesc> mapping : ImagineRemapper.fields()) {
                if (mapping.first().equals(obfOwner, name)
                        || mapping.second().equals(srgOwner, name)
                        || mapping.third().equals(srgOwner, name)) {
                    return mapping.third();
                }
            }
            return new FieldDesc(srgOwner, name);
        }

        @Override
        public MethodDesc method(String owner, String name, String desc) {
            String obfOwner = OBF.clazz(owner);
            String obfDesc = ImagineRemapper.descObf(desc);
            String srgOwner = SRG.clazz(owner);
            String srgDesc = ImagineRemapper.descSrg(desc);
            for (Triple<MethodDesc, MethodDesc, MethodDesc> mapping : ImagineRemapper.methods()) {
                if (mapping.first().equals(obfOwner, name, obfDesc)
                        || mapping.second().equals(srgOwner, name, srgDesc)
                        || mapping.third().equals(srgOwner, name, srgDesc))
                    return mapping.third();
            }
            return new MethodDesc(srgOwner, name, srgDesc);
        }
    };

    public abstract String clazz(String name);

    public abstract FieldDesc field(String owner, String name);

    public abstract MethodDesc method(String owner, String name, String desc);
}
