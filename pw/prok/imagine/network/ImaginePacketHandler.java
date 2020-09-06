package pw.prok.imagine.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.INetHandler;
import pw.prok.imagine.ImagineModContainer;

@ChannelHandler.Sharable
public class ImaginePacketHandler extends SimpleChannelInboundHandler<ImaginePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ImaginePacket packet) throws Exception {
        INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
        packet.process(ctx, ImagineModContainer.proxy().obtainPlayer(netHandler));
        ImagineNetwork.releasePacket(packet);
    }
}
