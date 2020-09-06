package pw.prok.imagine.network;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.util.AttributeKey;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pw.prok.imagine.pool.Pool;
import pw.prok.imagine.pool.Pools;

import java.util.HashMap;
import java.util.Map;

public class ImagineNetwork {
    private static final Map<Class<?>, Pool<? extends ImaginePacket>> sPacketPool = new HashMap<>();

    public static <T extends ImaginePacket> T obtainPacket(Class<T> packetClass) {
        Pool<T> pool = (Pool<T>) sPacketPool.get(packetClass);
        if (pool == null) {
            synchronized (sPacketPool) {
                pool = (Pool<T>) sPacketPool.get(packetClass);
                if (pool == null) {
                    pool = Pools.create(packetClass);
                    sPacketPool.put(packetClass, (Pool<ImaginePacket>) pool);
                }
            }
        }
        return pool.obtain();
    }

    public static <T extends ImaginePacket> void releasePacket(T packet) {
        Pool<T> pool = (Pool<T>) sPacketPool.get((Class<T>) packet.getClass());
        pool.release(packet);
    }

    public static final AttributeKey<ImagineNetwork> PACKET_NETWORK = new AttributeKey<ImagineNetwork>("imagine:network");

    private final FMLEmbeddedChannel mServerChannel;
    private final FMLEmbeddedChannel mClientChannel;

    public ImagineNetwork(String channelName) {
        Map<Side, FMLEmbeddedChannel> channels = NetworkRegistry.INSTANCE.newChannel(channelName,
                new ImaginePacketCodec(this), new ImaginePacketHandler());
        mServerChannel = channels.get(Side.SERVER);
        mClientChannel = channels.get(Side.CLIENT);
    }

    public void sendAllAround(ImaginePacket packet, TileEntity tileEntity, double range) {
        sendAllAround(packet, new NetworkRegistry.TargetPoint(tileEntity.getWorldObj().provider.dimensionId, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, range));
    }

    public void sendAllAround(ImaginePacket packet, World world, double x, double y, double z, double range) {
        sendAllAround(packet, new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, range));
    }

    public void sendAllAround(ImaginePacket packet, NetworkRegistry.TargetPoint point) {
        mServerChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        mServerChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        mServerChannel.writeOutbound(packet);
    }

    public void sendToPlayer(ImaginePacket packet, EntityPlayer player) {
        mServerChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        mServerChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        mServerChannel.writeOutbound(packet);
    }

    public void sendToWorld(ImaginePacket packet, World world) {
        mServerChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        mServerChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(world.provider.dimensionId);
        mServerChannel.writeOutbound(packet);
    }

    public void sendToAll(ImaginePacket packet) {
        mServerChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        mServerChannel.writeOutbound(packet);
    }

    public void send(ImaginePacket packet) {
        mClientChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        mClientChannel.writeOutbound(packet);
    }
}
