package pw.prok.imagine.object.nbt;

import net.minecraft.nbt.NBTBase;

public interface INBTHandler<T, B extends NBTBase> {
    T read(B nbt);

    B write(T value);
}
