package pw.prok.imagine.object.nbt;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NBTTagByteArray;
import pw.prok.imagine.writer.WritableBuf;

import java.util.BitSet;

@RegisterHandler(from = boolean[].class, to = NBTTagByteArray.class)
public class BooleanArrayHandler implements INBTHandler<boolean[], NBTTagByteArray> {
    private static final boolean[] EMPTY = new boolean[0];

    private static byte[] booleanToBytes(boolean[] value) {
        final int length = value.length;
        BitSet set = new BitSet();
        for (int i = 0; i < length; i++)
            set.set(i, value[i]);
        return set.toByteArray();
    }

    private static boolean[] bytesToBoolean(byte[] value, int length) {
        boolean[] array = new boolean[length];
        BitSet set = BitSet.valueOf(value);
        for (int i = 0; i < length; i++)
            array[i] = set.get(i);
        return array;
    }

    @Override
    public boolean[] read(NBTTagByteArray nbt) {
        final byte[] bytes = nbt.func_150292_c();
        final WritableBuf buf = new WritableBuf(bytes);
        final int length = buf.readInt();
        if (length == 0) return EMPTY;
        final int bytesLength = buf.readInt();
        final byte[] z = new byte[bytesLength];
        buf.readBytes(z, 0, bytesLength);
        return bytesToBoolean(z, length);
    }

    @Override
    public NBTTagByteArray write(boolean[] value) {
        final int length = value.length;
        final byte[] bytes = booleanToBytes(value);
        final int byteLength = Integer.SIZE / Byte.SIZE + bytes.length;
        WritableBuf buf = new WritableBuf(Unpooled.buffer(byteLength, byteLength));
        buf.writeInt(length);
        if (length != 0) {
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        }
        return new NBTTagByteArray(buf.array());
    }
}
