package pw.prok.imagine.object.nbt;

import net.minecraft.nbt.NBTTagIntArray;

@RegisterHandler(from = int.class, to = NBTTagIntArray.class)
public class IntArrayHandler implements INBTHandler<int[], NBTTagIntArray> {
    @Override
    public int[] read(NBTTagIntArray nbt) {
        return nbt.func_150302_c();
    }

    @Override
    public NBTTagIntArray write(int[] value) {
        return new NBTTagIntArray(value);
    }
}
