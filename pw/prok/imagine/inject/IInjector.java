package pw.prok.imagine.inject;

public interface IInjector<State extends Injector.InjectorState> {
    State parseClass(Class<?> clazz);

    boolean inject(State state, Object o, Object... args);

    <T> IConstructorBuilder<T, ?> create(Class<T> clazz);

    <T> IConstructorBuilder<T, ?> create(String className);
}
