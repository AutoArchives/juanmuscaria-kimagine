package pw.prok.imagine.inject;

import pw.prok.imagine.util.Array;

import java.util.*;

public abstract class Injector<State extends Injector.InjectorState> implements IInjector<State> {

    private static final InjectorState[] NULL_STATES = new InjectorState[0];

    public static void registerInjector(Class<? extends IInjector<?>> clazz) {
        IInjector<?> injector = Creator.creator(clazz).build();
        Phase[] phases = clazz.getAnnotation(RegisterInjector.class).value();
        registerInjector(injector, phases);
    }

    public enum Phase {
        Construct, PreInit, Init, PostInit
    }

    public static class InjectorState {
        private IInjector mInjector;
    }

    private static Map<Phase, Set<IInjector<?>>> sInjectors = new EnumMap<Phase, Set<IInjector<?>>>(Phase.class);
    private static Map<Phase, Map<Class<?>, InjectorState[]>> sState = new EnumMap<Phase, Map<Class<?>, InjectorState[]>>(Phase.class);

    public static void registerInjector(IInjector<?> injector, Phase... phases) {
        for (Phase phase : phases) {
            Set<IInjector<?>> injectors = sInjectors.get(phase);
            if (injectors == null) sInjectors.put(phase, injectors = new HashSet<>());
            injectors.add(injector);

            Map<Class<?>, InjectorState[]> states = sState.get(phase);
            if (states == null) {
                states = new HashMap<>();
                sState.put(phase, states);
            }
            for (Map.Entry<Class<?>, InjectorState[]> entry : states.entrySet()) {
                InjectorState state = injector.parseClass(entry.getKey());
                if (state != null) {
                    entry.setValue(Array.mergeArrays(entry.getValue(), new InjectorState[]{state}));
                }
            }
        }
    }

    public static <Type> InjectorState[] queryStates(Class<Type> clazz, Phase phase) {
        Map<Class<?>, InjectorState[]> mapStates = sState.get(phase);
        if (mapStates == null) {
            mapStates = new HashMap<>();
            sState.put(phase, mapStates);
        }
        InjectorState[] states = mapStates.get(clazz);
        if (states != null) return states;

        Set<IInjector<?>> injectors = sInjectors.get(phase);
        if (injectors != null) {
            Set<InjectorState> statesSet = new HashSet<InjectorState>();
            for (IInjector<?> injector : injectors) {
                InjectorState state = injector.parseClass(clazz);
                if (state != null) {
                    state.mInjector = injector;
                    statesSet.add(state);
                }
            }
            mapStates.put(clazz, states = statesSet.toArray(new InjectorState[statesSet.size()]));
        } else {
            mapStates.put(clazz, states = NULL_STATES);
        }
        return states == NULL_STATES ? null : states;
    }

    public static <Type> void inject(Type t, Phase phase, Object... args) {
        if (t == null) return;
        injectInternal(t, phase, t.getClass(), args);
    }

    private static <Type> void injectInternal(Type t, Phase phase, Class<?> clazz, Object... args) {
        final Class<?> superclass = clazz.getSuperclass();
        if (superclass != Object.class) {
            injectInternal(t, phase, superclass, args);
        }
        InjectorState[] states = queryStates(t.getClass(), phase);
        if (states == null) return;
        for (InjectorState state : states) {
            IInjector injector = state.mInjector;
            if (!injector.inject(state, t, args)) {
                throw new RuntimeException("Failed to inject into object " + t + " for class state " + clazz);
            }
        }
    }

    @Override
    public <T> IConstructorBuilder<T, ?> create(Class<T> clazz) {
        return Creator.creator(clazz);
    }

    @Override
    public <T> IConstructorBuilder<T, ?> create(String className) {
        return Creator.creator(className);
    }
}
