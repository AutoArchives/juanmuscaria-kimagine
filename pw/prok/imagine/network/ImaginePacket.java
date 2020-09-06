package pw.prok.imagine.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import pw.prok.imagine.writer.WritableBuf;

public abstract class ImaginePacket {
    public void writePacket(ChannelHandlerContext ctx, WritableBuf buf) {
    }

    public void readPacket(ChannelHandlerContext ctx, WritableBuf buf) {
    }

    public void process(ChannelHandlerContext ctx, EntityPlayer player) {
        final Side side = ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get();
        if (side.isClient()) {
            processClient(ctx, player);
        } else {
            processServer(ctx, player);
        }
    }

    @SideOnly(Side.CLIENT)
    public void processClient(ChannelHandlerContext ctx, EntityPlayer player) {
        new UnsupportedOperationException("Packet not support client execution").printStackTrace();
    }

    public void processServer(ChannelHandlerContext ctx, EntityPlayer player) {
        new UnsupportedOperationException("Packet not support server execution").printStackTrace();
    }
}
