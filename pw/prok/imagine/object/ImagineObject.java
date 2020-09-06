package pw.prok.imagine.object;


import com.google.common.collect.Sets;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import pw.prok.imagine.api.Member;
import pw.prok.imagine.api.Pair;
import pw.prok.imagine.inject.Creator;
import pw.prok.imagine.object.nbt.INBTHandler;
import pw.prok.imagine.reflect.AnnotationFilter;
import pw.prok.imagine.reflect.GetSetMethodFilter;
import pw.prok.imagine.reflect.IMemberScanCallback;
import pw.prok.imagine.reflect.ImagineReflect;
import pw.prok.imagine.writer.WritableBuf;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

public class ImagineObject {
    private static final ObjectState EMPTY_STATE = new ObjectState(null, 0, null, null, null);

    private static Map<Class<?>, ObjectState> sStates = new HashMap<Class<?>, ObjectState>();

    private static Map<Class<?>, INBTHandler<?, ?>> sNBTHandlers = new HashMap<Class<?>, INBTHandler<?, ?>>();

    static {
        System.out.println("Loaded!");
    }

    private static <T> ObjectState getState(Class<T> clazz) {
        ObjectState state = sStates.get(clazz);
        if (state == null) {
            sStates.put(clazz, state = parseState(clazz));
        }
        if (state == EMPTY_STATE) return null;
        return state;
    }

    public static <T> T copy(T object) {
        if (object == null) return null;
        Class<T> clazz = (Class<T>) object.getClass();
        ObjectState state = getState(clazz);
        T newObject = Creator.creator(clazz).build();
        if (state == null) return newObject;
        try {
            for (int i = 0; i < state.mSize; i++) {
                Object value = state.get(i, object);
                state.set(i, newObject, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy object", e);
        }
        return newObject;
    }

    public static <T> NBTBase nbt(T object) {
        if (object == null) return null;
        if (object instanceof NBTBase) return (NBTBase) object;
        Class<T> clazz = (Class<T>) object.getClass();
        ObjectState state = getState(clazz);
        if (state == null) return null;
        NBTTagCompound nbt = state.newNBT();
        try {
            for (int i = 0; i < state.mSize; i++) {
                Object value = state.get(i, object);
                //state.set(i, newObject, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create nbt", e);
        }

        return nbt;
    }

    public static <T> void write(T object, WritableBuf buf) {
        buf.writeClass(object.getClass());
        buf.writeNBT(nbt(object));
    }

    private static final MethodHandler METHOD_HANDLER = new MethodHandler();
    private static final FieldHandler FIELD_HANDLER = new FieldHandler();

    private static <T> ObjectState parseState(Class<T> clazz) {
        final Map<String, Pair<Pair<IHandler<?>, Object>, Pair<IHandler<?>, Object>>> members = new HashMap<String, Pair<Pair<IHandler<?>, Object>, Pair<IHandler<?>, Object>>>();
        ImagineReflect.create().addGetSetFilter(true, true).addAnnotationFilter(Member.class, false, true, true).scanMembers(clazz, new IMemberScanCallback<T>() {
            @Override
            public void scanMethod(Class<T> mainClass, Class<? super T> childClass, Method method) {
                Member member = method.getAnnotation(Member.class);
                String name = member.name();
                if (name.length() == 0) {
                    name = GetSetMethodFilter.getVarName(method);
                }
                final boolean get = GetSetMethodFilter.isGetMethod(method);
                Pair<Pair<IHandler<?>, Object>, Pair<IHandler<?>, Object>> memberPair = members.get(name);
                if (memberPair == null) {
                    members.put(name, memberPair = new Pair<>());
                }
                Pair<IHandler<?>, Object> handlerPair = new Pair<IHandler<?>, Object>(METHOD_HANDLER, method);
                if (get) {
                    memberPair.first(handlerPair);
                } else {
                    memberPair.second(handlerPair);
                }
            }

            @Override
            public void scanField(Class<T> mainClass, Class<? super T> childClass, Field field) {
                Member member = field.getAnnotation(Member.class);
                String name = member.name();
                if (name.length() == 0) {
                    name = field.getName();
                }
                Pair<Pair<IHandler<?>, Object>, Pair<IHandler<?>, Object>> memberPair = members.get(name);
                if (memberPair == null) {
                    members.put(name, memberPair = new Pair<>());
                }
                Pair<IHandler<?>, Object> handlerPair = new Pair<IHandler<?>, Object>(FIELD_HANDLER, field);
                if (member.load()) {
                    memberPair.first(handlerPair);
                }
                if (member.save()) {
                    memberPair.second(handlerPair);
                }
            }
        });
        final int size = members.size();
        if (size == 0) {
            return EMPTY_STATE;
        }
        final String[] names = new String[size];
        final Pair<IHandler<?>, Object>[] getHandlers = (Pair<IHandler<?>, Object>[]) new Pair[size];
        final Pair<IHandler<?>, Object>[] setHandlers = (Pair<IHandler<?>, Object>[]) new Pair[size];
        int i = 0;
        SortedSet<Map.Entry<String, Pair<Pair<IHandler<?>, Object>, Pair<IHandler<?>, Object>>>> set = Sets.newTreeSet(new Comparator<Map.Entry<String, Pair<Pair<IHandler<?>, Object>, Pair<IHandler<?>, Object>>>>() {
            @Override
            public int compare(Map.Entry<String, Pair<Pair<IHandler<?>, Object>, Pair<IHandler<?>, Object>>> o1, Map.Entry<String, Pair<Pair<IHandler<?>, Object>, Pair<IHandler<?>, Object>>> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        set.addAll(members.entrySet());
        for (Map.Entry<String, Pair<Pair<IHandler<?>, Object>, Pair<IHandler<?>, Object>>> p : set) {
            names[i] = p.getKey();
            getHandlers[i] = p.getValue().first();
            setHandlers[i] = p.getValue().second();
            i++;
        }
        return new ObjectState(clazz, size, names, getHandlers, setHandlers);
    }

    private static final class ObjectState {
        private final Class<?> mClass;
        private final int mSize;
        private final String[] mNames;
        private final Pair<IHandler<?>, Object>[] mGetHandlers;
        private final Pair<IHandler<?>, Object>[] mSetHandlers;

        public ObjectState(Class<?> clazz, int size, String[] names, Pair<IHandler<?>, Object>[] getHandlers, Pair<IHandler<?>, Object>[] setHandlers) {
            mClass = clazz;
            mSize = size;
            mNames = names;
            mGetHandlers = getHandlers;
            mSetHandlers = getHandlers;
        }

        public Object get(int i, Object o) throws Exception {
            Pair<IHandler<?>, Object> p = mGetHandlers[i];
            IHandler handler = p.first();
            return handler.getData(o, p.second());
        }

        public void set(int i, Object o, Object value) throws Exception {
            Pair<IHandler<?>, Object> p = mSetHandlers[i];
            IHandler handler = p.first();
            handler.putData(o, p.second(), value);
        }

        public NBTTagCompound newNBT() {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString("imagine:class", mClass.getName());
            return nbt;
        }
    }
}
