package pw.prok.imagine.asm;

import org.objectweb.asm.tree.FieldNode;

public class ImagineField extends ImagineAccess<ImagineField> {
    private final ImagineASM mAsm;
    private final FieldNode mField;

    ImagineField(ImagineASM asm, FieldNode field) {
        mAsm = asm;
        mField = field;
    }

    @Override
    public ImagineField addAccess(int modifiers) {
        mField.access |= modifiers;
        return this;
    }

    @Override
    public ImagineField limitAccess(int modifiers) {
        mField.access &= modifiers;
        return this;
    }
}
