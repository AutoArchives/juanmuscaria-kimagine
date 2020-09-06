package pw.prok.imagine.asm;

import static org.objectweb.asm.Opcodes.*;

public abstract class ImagineAccess<T extends ImagineAccess<T>> {
    public abstract T addAccess(int modifiers);

    public abstract T limitAccess(int modifiers);

    public T removeAccess(int modifiers) {
        return limitAccess(~modifiers);
    }

    public T setAccess(int modifiers, boolean state) {
        return state ? addAccess(modifiers) : removeAccess(modifiers);
    }

    public T removeVisibilityAccess() {
        return removeAccess(ACC_PRIVATE | ACC_PUBLIC | ACC_PROTECTED);
    }

    public T makePublic() {
        return removeVisibilityAccess().addAccess(ACC_PUBLIC);
    }

    public T makeProtected() {
        return removeVisibilityAccess().addAccess(ACC_PROTECTED);
    }

    public T makePrivate() {
        return removeVisibilityAccess().addAccess(ACC_PRIVATE);
    }

    public T setNative(boolean s) {
        return setAccess(ACC_NATIVE, s);
    }

    public T removeNative() {
        return removeAccess(ACC_NATIVE);
    }

    public T addNative() {
        return addAccess(ACC_NATIVE);
    }

}
