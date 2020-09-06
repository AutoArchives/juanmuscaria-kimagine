package pw.prok.imagine.fan;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModClassLoader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import pw.prok.imagine.inject.Injector;

public class FanEventHandler {
    private final ModCandidate mCandidate;
    private final ModContainer mContainer;
    private final Object mMod;
    private final Fan mFan;

    public FanEventHandler(ModCandidate candidate, ModContainer container, Object mod, Fan fan) {
        mCandidate = candidate;
        mContainer = container;
        mMod = mod;
        mFan = fan;
    }

    @Subscribe
    public void constructMod(FMLConstructionEvent event) {
        try {
            ModClassLoader modClassLoader = event.getModClassLoader();
            modClassLoader.addFile(mContainer.getSource());
            modClassLoader.clearNegativeCacheFor(mCandidate.getClassList());

            NetworkRegistry.INSTANCE.register(mContainer, mContainer.getClass(), null, event.getASMHarvestedData());
            Injector.inject(mMod, Injector.Phase.Construct, FMLCommonHandler.instance().getSide());
        } catch (Throwable e) {
            throw new IllegalStateException("Cannot construct fan", e);
        }
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
        Injector.inject(mMod, Injector.Phase.PreInit, event);
    }

    @Subscribe
    public void init(FMLInitializationEvent event) {
        Injector.inject(mMod, Injector.Phase.Init, event);
    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent event) {
        Injector.inject(mMod, Injector.Phase.PostInit, event);
    }
}
