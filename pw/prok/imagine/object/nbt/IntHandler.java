package pw.prok.imagine.object.nbt;

import net.minecraft.nbt.NBTTagInt;

@RegisterHandler(from = int.class, to = NBTTagInt.class)
public class IntHandler implements INBTHandler<Integer, NBTTagInt> {
    @Override
    public Integer read(NBTTagInt nbt) {
        return nbt.func_150287_d();
    }

    @Override
    public NBTTagInt write(Integer value) {
        return new NBTTagInt(value);
    }
}
