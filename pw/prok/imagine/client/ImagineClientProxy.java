package pw.prok.imagine.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import pw.prok.imagine.ImagineProxy;

public class ImagineClientProxy extends ImagineProxy {
    @Override
    public EntityPlayer obtainPlayer(INetHandler handler) {
        EntityPlayer player = super.obtainPlayer(handler);
        if (player == null) {
            player = Minecraft.getMinecraft().thePlayer;
        }
        return player;
    }
}
