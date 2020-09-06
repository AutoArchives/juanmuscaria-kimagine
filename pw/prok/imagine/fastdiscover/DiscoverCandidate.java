package pw.prok.imagine.fastdiscover;

import net.minecraft.launchwrapper.LaunchClassLoader;
import pw.prok.imagine.asm.ImagineASMClassLoader;

import java.io.InputStream;
import java.net.URL;

public abstract class DiscoverCandidate implements Iterable<InputStream> {
    public abstract void injectClassLoader(LaunchClassLoader classLoader);
}
