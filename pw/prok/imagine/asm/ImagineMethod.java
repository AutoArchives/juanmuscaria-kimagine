package pw.prok.imagine.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import pw.prok.imagine.collections.CuttableList;
import pw.prok.imagine.collections.LazyIterable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static org.objectweb.asm.Opcodes.*;

public class ImagineMethod extends ImagineAccess<ImagineMethod> {
    private final ImagineASM mAsm;
    private final MethodNode mMethod;

    ImagineMethod(ImagineASM asm, MethodNode method) {
        mAsm = asm;
        mMethod = method;
    }

    public ImagineMethod exception(String... exceptions) {
        if (exceptions == null) return this;
        for (String exception : exceptions)
            mMethod.exceptions.add(mAsm.mapClazz(exception));
        return this;
    }

    public ImagineMethod constMethod(int value) {
        InsnList instructions = mMethod.instructions;
        instructions.clear();
        pushInt(value, instructions);
        instructions.add(new InsnNode(IRETURN));
        return this;
    }

    private static void pushInt(int value, InsnList instructions) {
        switch (value) {
            case -1:
                instructions.add(new InsnNode(ICONST_M1));
                break;
            case 0:
                instructions.add(new InsnNode(ICONST_0));
                break;
            case 1:
                instructions.add(new InsnNode(ICONST_1));
                break;
            case 2:
                instructions.add(new InsnNode(ICONST_2));
                break;
            case 3:
                instructions.add(new InsnNode(ICONST_3));
                break;
            case 4:
                instructions.add(new InsnNode(ICONST_4));
                break;
            case 5:
                instructions.add(new InsnNode(ICONST_5));
                break;
            default:
                if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE)
                    instructions.add(new IntInsnNode(BIPUSH, value));
                else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE)
                    instructions.add(new IntInsnNode(SIPUSH, value));
                else instructions.add(new LdcInsnNode(value));
        }
    }

    public ImagineMethod push(int value) {
        pushInt(value, mMethod.instructions);
        return this;
    }

    public ImagineMethod constMethod(boolean value) {
        return constMethod(value ? 1 : 0);
    }

    public ImagineMethod constMethod(double value) {
        InsnList instructions = mMethod.instructions;
        instructions.clear();
        pushDouble(value, instructions);
        instructions.add(new InsnNode(DRETURN));
        return this;
    }

    private static void pushDouble(double value, InsnList instructions) {
        if (value == 0d) instructions.add(new InsnNode(DCONST_0));
        else if (value == 1d) instructions.add(new InsnNode(DCONST_1));
        else instructions.add(new LdcInsnNode(value));
    }

    public ImagineMethod push(double value) {
        pushDouble(value, mMethod.instructions);
        return this;
    }

    public ImagineMethod constMethod(float value) {
        InsnList instructions = mMethod.instructions;
        instructions.clear();
        pushFloat(value, instructions);
        instructions.add(new InsnNode(FRETURN));
        return this;
    }

    public ImagineMethod push(float value) {
        pushFloat(value, mMethod.instructions);
        return this;
    }

    private static void pushFloat(float value, InsnList instructions) {
        if (value == 0f) instructions.add(new InsnNode(FCONST_0));
        else if (value == 1f) instructions.add(new InsnNode(FCONST_1));
        else if (value == 2f) instructions.add(new InsnNode(FCONST_2));
        else instructions.add(new LdcInsnNode(value));
    }

    public static InsnList methodCall(ImagineMethod img, String owner, String methodName, boolean staticForward, boolean needReturn) {
        if (!staticForward || owner == null) {
            owner = img.mAsm.getActualName();
        }
        owner = ImagineASM.toDesc(owner);
        ImagineDesc desc = ImagineDesc.parse(img.mMethod.desc);
        if (!needReturn) {
            desc.returnType(ImagineDesc.SubDesc.create(Type.VOID_TYPE, 0));
        }
        InsnList instructions = new InsnList();
        if (!img.isStatic()) {
            staticForward = true;
            instructions.add(new IntInsnNode(ALOAD, 0));
        }
        int i = img.isStatic() ? 0 : 1;
        for (ImagineDesc.SubDesc sub : desc.parameters()) {
            instructions.add(new IntInsnNode(sub.opcodeLoad(), i++));
        }
        if (staticForward && !img.isStatic()) {
            desc.first(Type.getType("L" + ImagineASM.toDesc(img.mAsm.getActualName()) + ";"), 0);
        }
        MethodDesc method = img.mAsm.mapMethod(owner, methodName, desc.toString());
        instructions.add(new MethodInsnNode(staticForward ? INVOKESTATIC : INVOKEINTERFACE, method.first(), method.second(), method.third(), false));
        if (needReturn) instructions.add(new InsnNode(desc.returnOpcode()));
        return instructions;
    }

    private static void forward(ImagineMethod img, String owner, String methodName, boolean staticForward) {
        InsnList list = img.mMethod.instructions;
        list.clear();
        list.add(methodCall(img, owner, methodName, staticForward, true));
    }

    public ImagineMethod forward(String owner, String methodName) {
        forward(this, owner, methodName, owner != null);
        return this;
    }

    public ImagineMethod forward(String methodName) {
        return forward(null, methodName);
    }

    public ImagineMethod callFirst(String owner, String methodName) {
        InsnList instructions = mMethod.instructions;
        instructions.insertBefore(instructions.getFirst(), methodCall(this, owner, methodName, owner != null, false));
        return this;
    }

    public ImagineMethod callFirst(String methodName) {
        return callFirst(null, methodName);
    }

    public ImagineMethod callLast(String owner, String methodName) {
        InsnList instructions = mMethod.instructions;
        InsnList methodCall = methodCall(this, owner, methodName, owner != null, false);
        ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            switch (node.getOpcode()) {
                case RETURN:
                case ARETURN:
                case DRETURN:
                case FRETURN:
                case IRETURN:
                case LRETURN:
                    instructions.insertBefore(node, methodCall);
            }
        }
        return this;
    }

    public ImagineMethod callLast(String methodName) {
        return callLast(null, methodName);
    }

    public InsnList instructions() {
        return mMethod.instructions;
    }

    public String getName() {
        return mAsm.getActualName();
    }

    public Iterable<ImagineMethodPosition> find(final InsnList patternRaw) {
        final List<AbstractInsnNode> pattern = ImagineASM.asList(patternRaw);
        return new LazyIterable<ImagineMethodPosition>(new LazyIterable.LazyAction<ImagineMethodPosition>() {
            private int mPosition = 0;
            private CuttableList<AbstractInsnNode> mInstructions = new CuttableList<AbstractInsnNode>(ImagineASM.asList(mMethod.instructions));

            @Override
            public ImagineMethodPosition acquire() {
                int index = Collections.indexOfSubList(mInstructions, pattern);
                if (index >= 0) {
                    mPosition += index;
                    for (int i = 0; i < index; i++) {

                    }
                }
                Iterator<AbstractInsnNode> patternIterator = pattern.iterator();
                /*while(mInstructions.hasNext()) {
                    AbstractInsnNode node = mInstructions.next();
                    FieldInsnNode
                }*/
                return null;
            }
        });
    }

    public ImagineMethod find(InsnList pattern, Action<ImagineMethodPosition> action) {
        for (ImagineMethodPosition position : find(pattern)) {
            action.action(position);
        }
        return this;
    }

    public boolean isStatic() {
        return (mMethod.access & Opcodes.ACC_STATIC) != 0;
    }

    @Override
    public ImagineMethod addAccess(int modifiers) {
        mMethod.access |= modifiers;
        return this;
    }

    @Override
    public ImagineMethod limitAccess(int modifiers) {
        mMethod.access &= modifiers;
        return this;
    }
}
