package pw.prok.imagine.asm;

import com.google.common.collect.Lists;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import pw.prok.imagine.util.Array;

import java.util.LinkedList;
import java.util.List;

public class ImagineDesc {
    private SubDesc mReturnType;
    private List<SubDesc> mParameterTypes;

    public ImagineDesc(SubDesc returnType, SubDesc... parameterTypes) {
        assert returnType != null;
        assert parameterTypes != null;
        mReturnType = returnType;
        mParameterTypes = Lists.newArrayList(parameterTypes);
    }

    public SubDesc returnType() {
        return mReturnType;
    }

    public ImagineDesc returnType(SubDesc returnType) {
        mReturnType = returnType;
        return this;
    }

    public List<SubDesc> parameters() {
        return mParameterTypes;
    }

    public SubDesc parameter(int i) {
        return mParameterTypes.get(i);
    }

    public ImagineDesc insert(SubDesc sub, int pos) {
        mParameterTypes.add(pos, sub);
        return this;
    }

    public ImagineDesc insert(Type type, int level, int pos) {
        return insert(SubDesc.create(type, level), pos);
    }

    public ImagineDesc first(SubDesc sub) {
        return insert(sub, 0);
    }

    public ImagineDesc first(Type type, int level) {
        return first(SubDesc.create(type, level));
    }

    public ImagineDesc last(SubDesc sub) {
        mParameterTypes.add(sub);
        return this;
    }

    public ImagineDesc last(Type type, int level) {
        return last(SubDesc.create(type, level));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for (SubDesc sub : mParameterTypes) {
            builder.append(sub.toString());
        }
        builder.append(')');
        builder.append(mReturnType.toString());
        return builder.toString();
    }

    public int returnOpcode() {
        return mReturnType.opcodeReturn();
    }

    public static class SubDesc {
        public final Type type;
        public final int level;

        public static SubDesc create(Type type, int level) {
            assert level >= 0;
            assert type != null;
            return new SubDesc(type, level);
        }

        private SubDesc(Type type, int level) {
            this.type = type;
            this.level = level;
        }

        public int opcodeLoad() {
            if (level >= 1) return Opcodes.ALOAD;
            return type.getOpcode(Opcodes.ILOAD);
        }

        public int opcodeReturn() {
            if (level >= 1) return Opcodes.ARETURN;
            return type.getOpcode(Opcodes.IRETURN);
        }

        @Override
        public String toString() {
            String desc = type.getDescriptor();
            if (level == 0) return desc;
            StringBuilder b = new StringBuilder(desc.length() + level);
            for (int i = 0; i < level; i++) b.append('[');
            b.append(desc);
            return b.toString();
        }
    }

    public static ImagineDesc parse(String desc) {
        boolean returnTypeFlag = false;
        SubDesc returnType = null;
        List<SubDesc> parameterTypes = new LinkedList<>();
        int level = 0;
        String name;
        Type type;
        for (int i = 0; i < desc.length(); i++) {
            char c = desc.charAt(i);
            if (c == '(') continue;
            if (c == ')') {
                returnTypeFlag = true;
                continue;
            }
            if (c == '[') {
                level++;
                continue;
            } else if (c == 'L') {
                int end = desc.indexOf(';', i + 1);
                name = desc.substring(i, end + 1);
                i = end;
            } else {
                name = new String(new char[]{c});
            }
            type = Type.getType(name);
            if (returnTypeFlag) {
                returnType = new SubDesc(type, level);
            } else {
                parameterTypes.add(new SubDesc(type, level));
            }
            level = 0;
        }
        return new ImagineDesc(returnType, Array.asArray(parameterTypes, SubDesc.class));
    }
}
