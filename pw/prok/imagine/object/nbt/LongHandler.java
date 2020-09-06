package pw.prok.imagine.object.nbt;

import net.minecraft.nbt.NBTTagLong;

@RegisterHandler(from = long.class, to = NBTTagLong.class)
public class LongHandler implements INBTHandler<Long, NBTTagLong> {
    @Override
    public Long read(NBTTagLong nbt) {
        return nbt.func_150291_c();
    }

    @Override
    public NBTTagLong write(Long value) {
        return new NBTTagLong(value);
    }
}
