package pw.prok.imagine.object.nbt;

import net.minecraft.nbt.NBTTagDouble;

@RegisterHandler(from = double.class, to = NBTTagDouble.class)
public class DoubleHandler implements INBTHandler<Double, NBTTagDouble> {
    @Override
    public Double read(NBTTagDouble nbt) {
        return nbt.func_150286_g();
    }

    @Override
    public NBTTagDouble write(Double value) {
        return new NBTTagDouble(value);
    }
}
