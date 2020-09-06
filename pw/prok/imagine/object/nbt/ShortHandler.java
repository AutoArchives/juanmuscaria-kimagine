package pw.prok.imagine.object.nbt;

import net.minecraft.nbt.NBTTagShort;

@RegisterHandler(from = short.class, to = NBTTagShort.class)
public class ShortHandler implements INBTHandler<Short, NBTTagShort> {
    @Override
    public Short read(NBTTagShort nbt) {
        return nbt.func_150289_e();
    }

    @Override
    public NBTTagShort write(Short value) {
        return new NBTTagShort(value);
    }
}
