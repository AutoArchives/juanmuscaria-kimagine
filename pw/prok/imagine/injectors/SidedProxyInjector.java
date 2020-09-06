package pw.prok.imagine.injectors;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.relauncher.Side;
import pw.prok.imagine.inject.AnnotationFieldInjector;
import pw.prok.imagine.inject.Injector;
import pw.prok.imagine.inject.RegisterInjector;

@RegisterInjector(Injector.Phase.Construct)
public class SidedProxyInjector extends AnnotationFieldInjector<SidedProxy, Object> {
    public SidedProxyInjector() {
        super(SidedProxy.class, Object.class);
    }

    @Override
    public <V> V inject(SidedProxy annotation, Class<V> type, Object o, Object... args) throws Exception {
        Side side = FMLCommonHandler.instance().getSide();
        if (side.isClient() && !"".equals(annotation.clientSide())) {
            return (V) create(annotation.clientSide()).build();
        } else if (!"".equals(annotation.serverSide())) {
            return (V) create(annotation.serverSide()).build();
        } else {
            return create(type).build();
        }
    }
}
