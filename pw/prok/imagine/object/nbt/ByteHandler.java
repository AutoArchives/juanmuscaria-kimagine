package pw.prok.imagine.object.nbt;

import net.minecraft.nbt.NBTTagByte;

@RegisterHandler(from = byte.class, to = NBTTagByte.class)
public class ByteHandler implements INBTHandler<Byte, NBTTagByte> {
    @Override
    public Byte read(NBTTagByte nbt) {
        return nbt.func_150290_f();
    }

    @Override
    public NBTTagByte write(Byte value) {
        return new NBTTagByte(value);
    }
}
