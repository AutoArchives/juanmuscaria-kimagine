package pw.prok.imagine;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.Launch;
import pw.prok.imagine.asm.ImagineASMClassTransformer;
import pw.prok.imagine.asm.ImagineRemapper;
import pw.prok.imagine.asm.Transformer;
import pw.prok.imagine.fan.Fan;
import pw.prok.imagine.fan.FanLoader;
import pw.prok.imagine.fastdiscover.FastDiscoverer;
import pw.prok.imagine.inject.Creator;
import pw.prok.imagine.inject.IInjector;
import pw.prok.imagine.inject.Injector;
import pw.prok.imagine.inject.RegisterInjector;

import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class ImagineLoadingPlugin implements IFMLLoadingPlugin {
    public static boolean DEV;
    public static File MC_HOME;
    public static String MC_VERSION = "1.7.10";

    static {
        System.err.println("Loading plugin initialized!");
        Launch.classLoader.addTransformerExclusion("pw.prok.imagine.");
        // ModContainerFactory.instance().registerContainerType(Type.getType(Fan.class), FanModContainer.class);
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ImagineASMClassTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return ImagineModContainer.class.getName();
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {
        DEV = !((boolean) map.get("runtimeDeobfuscationEnabled"));
        MC_HOME = (File) map.get("mcLocation");
        ImagineRemapper.setupDeobfuscationData("/srg_deobfuscation_1.7.10.lzma", "/method_deobfuscation_1.7.10.lzma", "/field_deobfuscation_1.7.10.lzma");

        FastDiscoverer.discoverClassloader(Launch.classLoader, FastDiscoverer.DATA_SCANNER);
        FastDiscoverer.discover(Launch.classLoader, MC_HOME, MC_VERSION, FastDiscoverer.DATA_SCANNER);

        for (String className : FastDiscoverer.DISCOVER_DATA.getClassesForAnnotation(RegisterInjector.class.getName())) {
            Injector.registerInjector(Creator.<IInjector<?>>creator(Launch.classLoader, className).clazz());
        }

        for (String className : FastDiscoverer.DISCOVER_DATA.getClassesForAnnotation(Transformer.RegisterTransformer.class.getName())) {
            ImagineASMClassTransformer.addTransformer(Creator.<Transformer>creator(Launch.classLoader, className).atLeast(Transformer.class).build());
        }

        for (String className : FastDiscoverer.DISCOVER_DATA.getClassesForAnnotation(Fan.class.getName())) {
            FanLoader.loadFan(Creator.creator(Launch.classLoader, className).clazz());
        }
        FanLoader.migrate(FanLoader.State.Found);
    }

    @Override
    public String getAccessTransformerClass() {
        return ImagineAccessTransformer.class.getName();
    }
}
