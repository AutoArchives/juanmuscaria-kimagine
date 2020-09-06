package pw.prok.imagine.injectors;

import pw.prok.imagine.fan.Fan;
import pw.prok.imagine.fan.FanLoader;
import pw.prok.imagine.inject.AnnotationFieldInjector;
import pw.prok.imagine.inject.Injector;
import pw.prok.imagine.inject.RegisterInjector;

@RegisterInjector(Injector.Phase.Construct)
public class FanInstanceInjector extends AnnotationFieldInjector<Fan.Instance, Object> {
    public FanInstanceInjector() {
        super(Fan.Instance.class, Object.class);
    }

    @Override
    public <V> V inject(Fan.Instance annotation, Class<V> type, Object o, Object... args) throws Exception {
        return FanLoader.getFan(type);
    }
}
