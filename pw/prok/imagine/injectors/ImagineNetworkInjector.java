package pw.prok.imagine.injectors;

import pw.prok.imagine.inject.AnnotationFieldInjector;
import pw.prok.imagine.inject.Injector;
import pw.prok.imagine.inject.RegisterInjector;
import pw.prok.imagine.network.ImagineNetwork;
import pw.prok.imagine.network.Network;

@RegisterInjector(Injector.Phase.Init)
public class ImagineNetworkInjector extends AnnotationFieldInjector<Network, ImagineNetwork> {
    public ImagineNetworkInjector() {
        super(Network.class, ImagineNetwork.class);
    }

    @Override
    public <V extends ImagineNetwork> V inject(Network annotation, Class<V> type, Object o, Object... args) throws Exception {
        return create(type).arg(annotation.value()).build();
    }
}
