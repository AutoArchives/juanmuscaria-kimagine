package pw.prok.imagine.object.nbt;

import net.minecraft.nbt.NBTTagByte;

@RegisterHandler(from = boolean.class, to = NBTTagByte.class)
public class BooleanHandler implements INBTHandler<Boolean, NBTTagByte> {
    private static final byte TRUE = 1;
    private static final byte FALSE = 0;

    @Override
    public Boolean read(NBTTagByte nbt) {
        return nbt.func_150290_f() != 0;
    }

    @Override
    public NBTTagByte write(Boolean value) {
        return new NBTTagByte(value ? TRUE : FALSE);
    }
}
