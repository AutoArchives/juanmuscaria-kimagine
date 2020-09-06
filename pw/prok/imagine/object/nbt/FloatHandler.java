package pw.prok.imagine.object.nbt;

import net.minecraft.nbt.NBTTagFloat;

@RegisterHandler(from = float.class, to = NBTTagFloat.class)
public class FloatHandler implements INBTHandler<Float, NBTTagFloat> {
    @Override
    public Float read(NBTTagFloat nbt) {
        return nbt.func_150288_h();
    }

    @Override
    public NBTTagFloat write(Float value) {
        return new NBTTagFloat(value);
    }
}
