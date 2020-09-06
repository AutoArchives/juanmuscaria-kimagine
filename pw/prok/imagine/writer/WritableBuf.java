package pw.prok.imagine.writer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.network.PacketBuffer;
import pw.prok.imagine.inject.Creator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.*;

public class WritableBuf extends PacketBuffer {
    private static final Charset UTF_8 = Charset.forName("utf-8");
    public static final int FLAG_NULL = 1 << 1;
    public static final int FLAG_LENGTH_SUPPLIED = 1 << 2;
    public static final int FLAG_GENERIC_KEY_SUPPLIED = 1 << 3;
    public static final int FLAG_GENERIC_VALUE_SUPPLIED = 1 << 4;
    private DataOutputStream mDataOutputStream;
    private DataInputStream mDataInputStream;

    public static boolean flag(byte flags, int flag) {
        return (flags & flag) == flag;
    }

    public WritableBuf(byte[] bytes) {
        this(Unpooled.wrappedBuffer(bytes));
    }

    public WritableBuf(ByteBuf buf) {
        super(buf);
    }

    public DataInputStream getDataInputStream() {
        if (mDataInputStream == null) {
            mDataInputStream = new DataInputStream(new ByteBufInputStream(this));
        }
        return mDataInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        if (mDataOutputStream == null) {
            mDataOutputStream = new DataOutputStream(new ByteBufOutputStream(this));
        }
        return mDataOutputStream;
    }


    public static <T> Class<T> parseClass(String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T extends IWritable<T>> Class<T> readWritableClass(String className) {
        Class<?> clazz = parseClass(className);
        if (clazz == null || !IWritable.class.isAssignableFrom(clazz)) {
            new IllegalArgumentException("Class isn't implement IWritable").printStackTrace();
            return null;
        }
        return (Class<T>) clazz;
    }

    public <T> Class<T> readClass() {
        return parseClass(readString());
    }

    public WritableBuf writeClass(Class<?> clazz) {
        return writeString(clazz.getName());
    }

    public WritableBuf writeString(String s) {
        final byte[] bytes = s.getBytes(UTF_8);
        final int length = bytes.length;
        writeInt(length);
        writeBytes(bytes, 0, length);
        return this;
    }

    public String readString() {
        final int length = readInt();
        final byte[] bytes = new byte[length];
        readBytes(bytes, 0, length);
        return new String(bytes, UTF_8);
    }

    public <T extends IWritable<T>> WritableBuf writeWritable(T object) {
        writeObjectInfo(object);
        object.write(this);
        return this;
    }

    public WritableBuf writeObjectInfo(Object object) {
        if (object == null) {
            writeByte(FLAG_NULL);
            return this;
        }
        writeByte(0);
        writeString(object.getClass().getName());
        return this;
    }

    public <T extends IWritable<T>> T readWritable() {
        final byte flags = readByte();
        if (flag(flags, FLAG_NULL)) return null;
        Class<T> objectClass = readClass();
        return Creator.creator(objectClass).arg(this).build();
    }

    public <T extends IWritable<T>> WritableBuf writeArray(T[] objects) {
        if (objects == null) {
            writeByte(FLAG_NULL);
            return this;
        }
        writeByte(FLAG_LENGTH_SUPPLIED | FLAG_GENERIC_VALUE_SUPPLIED);
        writeInt(objects.length);
        writeClass(objects.getClass().getComponentType());
        for (T object : objects) writeWritable(object);
        return this;
    }

    public <T extends IWritable<T>> T[] readArray() {
        final byte flags = readByte();
        if (flag(flags, FLAG_NULL)) return null;
        if (!flag(flags, FLAG_LENGTH_SUPPLIED | FLAG_GENERIC_VALUE_SUPPLIED)) {
            throw new RuntimeException("Array require to known generic type and length");
        }
        final int length = readInt();
        T[] array = (T[]) Array.newInstance(readClass(), length);
        for (int i = 0; i < length; i++) array[i] = this.<T>readWritable();
        return array;
    }

    public <T extends IWritable<T>> WritableBuf writeCollection(Collection<T> collection) {
        return writeCollection(collection, null);
    }

    public <T extends IWritable<T>> WritableBuf writeCollection(Collection<T> collection, Class<T> objectType) {
        if (collection == null) {
            writeByte(FLAG_NULL);
            return this;
        }
        final boolean hasGenType = objectType != null;
        writeByte(FLAG_LENGTH_SUPPLIED | (hasGenType ? FLAG_GENERIC_VALUE_SUPPLIED : 0));
        writeInt(collection.size());
        if (hasGenType) {
            writeClass(objectType);
        }
        for (T object : collection) writeWritable(object);
        return this;
    }

    public <T extends IWritable<T>> List<T> readCollection() {
        final byte flags = readByte();
        if (flag(flags, FLAG_NULL)) {
            return null;
        }
        final int length = flag(flags, FLAG_LENGTH_SUPPLIED) ? readInt() : -1;
        final String genType = flag(flags, FLAG_GENERIC_VALUE_SUPPLIED) ? readString() : null;
        List<T> collection = length > 0 ? new ArrayList<T>(length) : new LinkedList<T>();
        for (int i = 0; i < length; i++) collection.add(this.<T>readWritable());
        return collection;
    }

    public <T extends IWritable<T>, C extends Collection<T>> C readCollection(C collection) {
        final byte flags = readByte();
        if (flag(flags, FLAG_NULL)) {
            return null;
        }
        final int length = flag(flags, FLAG_LENGTH_SUPPLIED) ? readInt() : -1;
        final String genType = flag(flags, FLAG_GENERIC_VALUE_SUPPLIED) ? readString() : null;
        for (int i = 0; i < length; i++) collection.add(this.<T>readWritable());
        return collection;
    }

    public <K extends IWritable<K>, V extends IWritable<V>> WritableBuf writeMap(Map<K, V> map) {
        if (map == null) {
            writeByte(FLAG_NULL);
            return this;
        }
        writeByte(FLAG_LENGTH_SUPPLIED);
        writeInt(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            writeWritable(entry.getKey());
            writeWritable(entry.getValue());
        }
        return this;
    }

    public <K extends IWritable<K>, V extends IWritable<V>> HashMap<K, V> readMap() {
        final byte flags = readByte();
        if (flag(flags, FLAG_NULL)) return null;
        if (!flag(flags, FLAG_LENGTH_SUPPLIED)) throw new RuntimeException("Map require to specify size");
        final int length = readInt();
        HashMap<K, V> map = new HashMap<K, V>(length);
        for (int i = 0; i < length; i++) map.put(this.<K>readWritable(), this.<V>readWritable());
        return map;
    }

    public <K extends IWritable<K>, V extends IWritable<V>, M extends Map<K, V>> M readMap(M map) {
        final byte flags = readByte();
        if (flag(flags, FLAG_NULL)) return null;
        if (!flag(flags, FLAG_LENGTH_SUPPLIED)) throw new RuntimeException("Map require to specify size");
        final int length = readInt();
        for (int i = 0; i < length; i++) map.put(this.<K>readWritable(), this.<V>readWritable());
        return map;
    }

    public <E extends Enum<E>> WritableBuf writeEnum(E enumObject) {
        if (enumObject == null) {
            writeByte(FLAG_NULL);
            return this;
        }
        writeByte(0);
        writeString(enumObject.getDeclaringClass().getName());
        writeInt(enumObject.ordinal());
        return this;
    }

    public <E extends Enum<E>> E readEnum() {
        final byte flags = readByte();
        if (flag(flags, FLAG_NULL)) return null;
        Class<E> enumClass = readClass();
        if (enumClass == null) {
            throw new IllegalArgumentException("Enum class not found");
        }
        E[] values = enumClass.getEnumConstants();
        int index = readInt();
        if (index >= 0 && index < values.length) {
            return values[index];
        }
        throw new IndexOutOfBoundsException("Illegal index: " + index + ". Size: " + values.length);
    }

    public <K extends Enum<K>, V extends IWritable<V>> WritableBuf writeEnumMap(Map<K, V> map) {
        if (map == null) {
            writeByte(FLAG_NULL);
            return this;
        }
        writeByte(FLAG_LENGTH_SUPPLIED);
        writeInt(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            writeEnum(entry.getKey());
            writeWritable(entry.getValue());
        }
        return this;
    }

    public <K extends Enum<K>, V extends IWritable<V>> EnumMap<K, V> readEnumMap(Class<K> keyClass) {
        EnumMap<K, V> map = new EnumMap<K, V>(keyClass);
        readEnumMap(map);
        return map;
    }

    public <K extends Enum<K>, V extends IWritable<V>, M extends Map<K, V>> M readEnumMap(M map) {
        final byte flags = readByte();
        if (flag(flags, FLAG_NULL)) return null;
        if (!flag(flags, FLAG_LENGTH_SUPPLIED)) throw new RuntimeException("Map require to specify size");
        final int length = readInt();
        for (int i = 0; i < length; i++) map.put(this.<K>readEnum(), this.<V>readWritable());
        return map;
    }

    public void writeNBT(NBTBase nbt) {
        if (nbt == null) {
            writeByte(-1);
            return;
        }
        writeInt(nbt.getId());
        try {
            nbt.write(getDataOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends NBTBase> T readNBT() {
        byte type = readByte();
        if (type == -1) return null;
        T nbt = (T) NBTBase.func_150284_a(type);
        try {
            nbt.func_152446_a(getDataInputStream(), 0, NBTSizeTracker.field_152451_a);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return nbt;
    }
}
