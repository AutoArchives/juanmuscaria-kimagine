package pw.prok.imagine.network;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class ImaginePacketRegistry {
    private TIntObjectMap<Class<? extends ImaginePacket>> mClassMap = new TIntObjectHashMap<Class<? extends ImaginePacket>>();

    public boolean missing(Class<? extends ImaginePacket> clazz) {
        return !mClassMap.containsKey(id(clazz));
    }

    public int id(Class<? extends ImaginePacket> clazz) {
        return clazz.getName().hashCode();
    }

    public void register(int id, Class<? extends ImaginePacket> clazz) {
        mClassMap.put(id, clazz);
    }

    public Class<? extends ImaginePacket> get(int id) {
        return mClassMap.get(id);
    }
}
