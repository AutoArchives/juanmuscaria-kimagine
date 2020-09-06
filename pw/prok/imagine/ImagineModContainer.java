package pw.prok.imagine;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import pw.prok.imagine.fan.FanLoader;
import pw.prok.imagine.inject.Injector;

import java.util.Map;

public class ImagineModContainer extends DummyModContainer {
    public static final String MODID = "kimagine";
    public static ImagineModContainer INSTANCE;

    @SidedProxy(serverSide = "pw.prok.imagine.ImaginePoxy", clientSide = "pw.prok.imagine.client.ImagineClientProxy")
    private ImagineProxy proxy;

    public static ImagineProxy proxy() {
        return INSTANCE.proxy;
    }

    private ModMetadata mMetadata;

    public ImagineModContainer() {
        super(createContainerMetadata());
        mMetadata = getMetadata();
        INSTANCE = this;
    }

    private static ModMetadata createContainerMetadata() {
        ModMetadata metadata = new ModMetadata();
        metadata.modId = MODID;
        metadata.name = "KImagine";
        metadata.version = "0.2";
        return metadata;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    public static void registerChildMod(ModContainer modContainer) {
        final ModMetadata metadata = modContainer.getMetadata();
        metadata.parent = ImagineModContainer.MODID;
        metadata.parentMod = ImagineModContainer.INSTANCE;
        ImagineModContainer.INSTANCE.mMetadata.childMods.add(modContainer);
    }

    @Subscribe
    public void constructMod(FMLConstructionEvent event) {
        Injector.inject(this, Injector.Phase.Construct);
        FanLoader.migrate(FanLoader.State.Loaded);
    }

    @NetworkCheckHandler
    public boolean checkModLists(Map<String, String> modList, Side side) {
        return true;
    }

    @Subscribe
    public void onPreInit(FMLPreInitializationEvent event) {
        Injector.inject(this, Injector.Phase.PreInit);
        FanLoader.migrate(FanLoader.State.PreInitialized);
    }

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        Injector.inject(this, Injector.Phase.Init);
        FanLoader.migrate(FanLoader.State.Initialized);
    }

    @Subscribe
    public void onPostInit(FMLPostInitializationEvent event) {
        Injector.inject(this, Injector.Phase.PostInit);
        FanLoader.migrate(FanLoader.State.PostInitialized);
    }
}