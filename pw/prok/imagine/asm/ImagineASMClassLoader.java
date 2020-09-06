package pw.prok.imagine.asm;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImagineASMClassLoader extends URLClassLoader {
    public static ImagineASMClassLoader ASM_CLASSLOADER = new ImagineASMClassLoader(Launch.classLoader);

    public ImagineASMClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    public List<URL> getSources() {
        List<URL> sources = new ArrayList<>();
        putSources(this, sources);
        return sources;
    }

    private static void putSources(ClassLoader classLoader, List<URL> sources) {
        if (classLoader instanceof LaunchClassLoader) {
            sources.addAll(((LaunchClassLoader) classLoader).getSources());
        } else if (classLoader instanceof URLClassLoader) {
            Collections.addAll(sources, ((URLClassLoader) classLoader).getURLs());
        }
        classLoader = classLoader.getParent();
        if (classLoader != null) {
            putSources(classLoader, sources);
        }
    }
}
