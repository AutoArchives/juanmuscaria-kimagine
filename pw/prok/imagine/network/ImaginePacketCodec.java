package pw.prok.imagine.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.AttributeKey;
import pw.prok.imagine.writer.WritableBuf;

import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class ImaginePacketCodec extends MessageToMessageCodec<FMLProxyPacket, ImaginePacket> {
    public static final AttributeKey<ThreadLocal<WeakReference<FMLProxyPacket>>> PACKET_TRACKER = new AttributeKey<ThreadLocal<WeakReference<FMLProxyPacket>>>("imagine:inboundpacket");
    public static final AttributeKey<ImaginePacketRegistry> PACKET_REGISTRY = new AttributeKey<ImaginePacketRegistry>("imagine:packet_registry");
    private static final Charset UTF_8 = Charset.forName("utf-8");
    private final ImagineNetwork mNetwork;

    public ImaginePacketCodec(ImagineNetwork network) {
        mNetwork = network;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ImaginePacket packet, List<Object> out) throws Exception {
        WritableBuf buffer = new WritableBuf(Unpooled.buffer());

        final ImaginePacketRegistry packetRegistry = ctx.attr(PACKET_REGISTRY).get();
        final Class<? extends ImaginePacket> packetClass = packet.getClass();
        final int packetClassId = packetRegistry.id(packetClass);

        if (packetRegistry.missing(packetClass)) {
            packetRegistry.register(packetClassId, packetClass);
            buffer.writeBoolean(true);
            buffer.writeInt(packetClassId);
            final byte[] classNameBytes = packetClass.getName().getBytes(UTF_8);
            final int classNameLength = classNameBytes.length;
            buffer.writeInt(classNameLength);
            buffer.writeBytes(classNameBytes, 0, classNameLength);
        } else {
            buffer.writeBoolean(false);
            buffer.writeInt(packetClassId);
        }
        packet.writePacket(ctx, buffer);

        FMLProxyPacket proxy = new FMLProxyPacket(buffer, ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
        WeakReference<FMLProxyPacket> ref = ctx.attr(PACKET_TRACKER).get().get();
        FMLProxyPacket old = ref == null ? null : ref.get();
        if (old != null) {
            proxy.setDispatcher(old.getDispatcher());
        }
        out.add(proxy);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {
        ctx.attr(PACKET_TRACKER).get().set(new WeakReference<>(msg));

        final ImaginePacketRegistry packetRegistry = ctx.attr(PACKET_REGISTRY).get();

        ByteBuf payload = msg.payload();
        final boolean newPacketClass = payload.readBoolean();
        final Class<? extends ImaginePacket> packetClass;
        final int packetClassId = payload.readInt();
        if (newPacketClass) {
            final int classNameLength = payload.readInt();
            if (classNameLength <= 0) {
                new IllegalArgumentException("Too short class name: " + classNameLength).printStackTrace();
                ctx.close();
            }
            if (classNameLength > 1024) {
                new IllegalArgumentException("Too long class name: " + classNameLength).printStackTrace();
                ctx.close();
            }
            final byte[] classNameBytes = new byte[classNameLength];
            payload.readBytes(classNameBytes, 0, classNameLength);
            final String className = new String(classNameBytes, 0, classNameLength, UTF_8);
            packetClass = (Class<? extends ImaginePacket>) Class.forName(className);
            if (!ImaginePacket.class.isAssignableFrom(packetClass)) {
                new IllegalArgumentException("Provided class isn't imagine packet: " + packetClass).printStackTrace();
                ctx.close();
            }
            packetRegistry.register(packetClassId, packetClass);
        } else {
            packetClass = packetRegistry.get(packetClassId);
        }
        if (packetClass == null) {
            new NullPointerException("Undefined message in channel " + msg.channel()).printStackTrace();
            ctx.close();
        }
        ImaginePacket packet = ImagineNetwork.obtainPacket(packetClass);
        packet.readPacket(ctx, new WritableBuf(payload.slice()));
        out.add(packet);
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        ctx.attr(PACKET_TRACKER).set(new ThreadLocal<WeakReference<FMLProxyPacket>>());
        ctx.attr(PACKET_REGISTRY).set(new ImaginePacketRegistry());
    }
}
