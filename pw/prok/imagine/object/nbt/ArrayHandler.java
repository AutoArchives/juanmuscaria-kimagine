package pw.prok.imagine.object.nbt;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import pw.prok.imagine.object.ImagineObject;
import pw.prok.imagine.util.Array;
import pw.prok.imagine.writer.WritableBuf;

@RegisterHandler(from = Object[].class, to = NBTTagByteArray.class)
public class ArrayHandler<T> implements INBTHandler<T[], NBTTagByteArray> {
    @Override
    public T[] read(NBTTagByteArray nbt) {
        WritableBuf buf = new WritableBuf(nbt.func_150292_c());
        final int length = buf.readInt();
        Class<T> componentType = buf.readClass();
        T[] array = Array.newArray(componentType, length);
        for (int i = 0; i < length; i++) {
            NBTBase elem = buf.readNBT();
            //array[i] = buf.readW
        }
        return null;
    }

    @Override
    public NBTTagByteArray write(T[] value) {
        WritableBuf buf = new WritableBuf(Unpooled.buffer());
        buf.writeInt(value.length);
        buf.writeClass(value.getClass().getComponentType());
        for (T t : value) {
            NBTBase nbt = ImagineObject.nbt(t);
            if (nbt == null) {
                throw new RuntimeException("Null items not allowed");
            }
            buf.writeNBT(nbt);
        }
        return new NBTTagByteArray(buf.array());
    }
}
