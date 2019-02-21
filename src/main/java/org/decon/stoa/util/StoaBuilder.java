/*
 * Copyright (C) 2016 Adarsh Soodan
 * 
 * Stoa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3.0 as 
 * published by the Free Software Foundation.
 *
 * Stoa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3.0 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License version 3.0
 * along with Stoa.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.decon.stoa.util;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.decon.stoa.Component;
import org.decon.stoa.Stoa;
import org.decon.stoa.StoaMap;
import org.decon.stoa.types.BoolComponent;
import org.decon.stoa.types.ByteComponent;
import org.decon.stoa.types.CharacterComponent;
import org.decon.stoa.types.DoubleComponent;
import org.decon.stoa.types.EnumComponent;
import org.decon.stoa.types.FloatComponent;
import org.decon.stoa.types.InlineComponent;
import org.decon.stoa.types.IntegerComponent;
import org.decon.stoa.types.LongComponent;
import org.decon.stoa.types.ObjectComponent;
import org.decon.stoa.types.ShortComponent;
import org.decon.stoa.util.SetAdapter.EmptyValue;

public class StoaBuilder {

    public static <C> BeanInfo beanInfo(Class<C> beanClass) {
        try {
            return Introspector.getBeanInfo(beanClass, Object.class);
        } catch (IntrospectionException e) {
            throw ExceptionUtils.sneakyThrow(e);
        }
    }

    public static <E> Supplier<Stoa<E>> list(BeanInfo info) {
        return list(info, Collections.emptyMap());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Supplier<Stoa<Map<String, Object>>> list(Map<String, Class<?>> keyTypes) {
        return new Lazy<>(() -> {
            Map<String, Supplier<Component>> componentsSuppliers = new HashMap<>();
            keyTypes.forEach((prop, type) -> {
                componentsSuppliers.put(prop, key2mapComponent(prop, type));
            });
            return () -> new Stoa<Map<String, Object>>(() -> new HashMap<>(),
                    (Class<Map<String, Object>>) (Object) Map.class,
                    (Map<String, Component<Map<String, Object>, ?>>) (Object) from(componentsSuppliers));
        });
    }

    public static final Supplier<Stoa<EmptyValue>> emptyValues = () -> new Stoa<EmptyValue>(() -> EmptyValue.singleton,
            EmptyValue.class, Collections.emptyMap());

    public static <E> Supplier<SetAdapter<E>> set(Supplier<Stoa<E>> beanStoaSupplier) {
        return () -> new SetAdapter<E>(map(beanStoaSupplier, emptyValues).get());
    }

    public static <K, V> Supplier<StoaMap<K, V>> map(Supplier<Stoa<K>> keyStoaSupplier,
            Supplier<Stoa<V>> valueStoaSupplier) {
        return () -> new StoaMap<K, V>(keyStoaSupplier, valueStoaSupplier);
    }

    @SuppressWarnings("rawtypes")
    private static ConcurrentHashMap<Class, Supplier<Stoa>> primitiveStoaCache = new ConcurrentHashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <E> Supplier<Stoa<E>> listOfPrimitive(Class<E> primitiveClass) {
        if (!primitiveClass.isPrimitive()) {
            throw new IllegalArgumentException("primitiveClass = " + primitiveClass.toString());
        }
        return (Supplier<Stoa<E>>) (Object) primitiveStoaCache.computeIfAbsent(primitiveClass,
                (ignoreCls) -> new Lazy<>(() -> {
                    final Supplier<E> instanceFactory = (Supplier<E>) zeroFactory.get(primitiveClass);
                    Class boxedClass = instanceFactory.get()
                                                      .getClass();
                    MethodHandle reader = MethodHandles.identity(boxedClass)
                                                       .asType(MethodType.methodType(primitiveClass, Object.class));
                    MethodHandle writer = MethodHandles.dropArguments(MethodHandles.identity(boxedClass), 0, boxedClass)
                                                       .asType(MethodType.methodType(Object.class, Object.class,
                                                               primitiveClass));

                    Map<String, Supplier<Component>> componentSuppliers = new HashMap<>();
                    componentSuppliers.put(primitiveClass.getName(), component(primitiveClass, reader, writer));

                    return () -> new Stoa(instanceFactory, boxedClass, from(componentSuppliers));
                }));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <E> Supplier<Stoa<E>> list(BeanInfo info, Map<String, BeanInfo> inline) {
        return new Lazy<>(() -> {
            Class<E> cls = (Class<E>) info.getBeanDescriptor()
                                          .getBeanClass();
            Map<String, Supplier<Component>> componentSuppliers = componentsMap(info, inline);
            return () -> new Stoa<E>(newInstanceFactory(cls), cls,
                    (Map<String, Component<E, ?>>) (Object) from(componentSuppliers));
        });
    }

    @SuppressWarnings("rawtypes")
    private static Map<String, Supplier<Component>> componentsMap(BeanInfo info, Map<String, BeanInfo> inline) {
        Map<String, Supplier<Component>> ret = new HashMap<>();
        Arrays.stream(info.getPropertyDescriptors())
              .filter(it -> !(it instanceof IndexedPropertyDescriptor))
              .forEach(desc -> ret.put(desc.getName(), type2Component(desc.getPropertyType(), desc.getReadMethod(),
                      desc.getWriteMethod(), inline, desc.getName())));
        return ret;
    }

    @SuppressWarnings({ "rawtypes" })
    private static <T> Supplier<Component> key2mapComponent(String prop, Class<T> type) {
        MethodHandle reader = MethodHandles.insertArguments(mapReader, 1, prop)
                                           .asType(MethodType.methodType(type, Object.class));
        MethodHandle writer = MethodHandles.insertArguments(mapWriter, 1, prop)
                                           .asType(MethodType.methodType(void.class, Object.class, type));
        MethodHandle retNull = MethodHandles.constant(Object.class, null);
        writer = MethodHandles.filterReturnValue(writer, retNull);
        Supplier<Component> componentSupplier = component(type, reader, writer);
        return () -> componentSupplier.get();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <E> Supplier<Component> type2Component(Class<?> componentType, Method reader, Method writer,
            Map<String, BeanInfo> inline, String inlineName) {
        Function<E, Object> freader = (bean) -> {
            try {
                return reader.invoke(bean);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                return ExceptionUtils.sneakyThrow(e);
            }
        };
        BiFunction<E, ?, E> fwriter = (bean, t) -> {
            try {
                writer.invoke(bean, t);
                return bean;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw ExceptionUtils.sneakyThrow(e);
            }
        };
        if (inline.containsKey(inlineName)) {
            // FIXME nestedInline is not working here. Test not written for it.
            Map<String, BeanInfo> nested = new HashMap<>();
            String prefix = inlineName + ".";
            inline.forEach((path, info) -> {
                if (path.startsWith(prefix)) {
                    nested.put(path.substring(prefix.length()), info);
                }
            });
            BeanInfo info = inline.get(inlineName);
            Class<?> cls = info.getBeanDescriptor()
                               .getBeanClass();
            Map<String, Supplier<Component>> componentSupplier = componentsMap(info, nested);
            return () -> new InlineComponent(() -> new Stoa(newInstanceFactory(cls), cls, from(componentSupplier)),
                    freader, fwriter);
        }
        MethodHandle readerMH = unreflect(reader);
        readerMH = readerMH.asType(MethodType.methodType(componentType, Object.class));
        MethodHandle writerMH = unreflect(writer);
        writerMH = writerMH.asType(MethodType.methodType(void.class, Object.class, componentType));
        MethodHandle retNull = MethodHandles.constant(Object.class, null);
        writerMH = MethodHandles.filterReturnValue(writerMH, retNull);
        return component(componentType, readerMH, writerMH);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Supplier<Component> component(Class<?> componentType, MethodHandle reader, MethodHandle writer) {
        if (componentType.equals(boolean.class)) {
            return () -> new BoolComponent<>(reader, writer);
        } else if (componentType.equals(byte.class)) {
            return () -> new ByteComponent<>(reader, writer);
        } else if (componentType.equals(char.class)) {
            return () -> new CharacterComponent<>(reader, writer);
        } else if (componentType.equals(short.class)) {
            return () -> new ShortComponent<>(reader, writer);
        } else if (componentType.equals(int.class)) {
            return () -> new IntegerComponent<>(reader, writer);
        } else if (componentType.equals(float.class)) {
            return () -> new FloatComponent<>(reader, writer);
        } else if (componentType.equals(double.class)) {
            return () -> new DoubleComponent<>(reader, writer);
        } else if (componentType.equals(long.class)) {
            return () -> new LongComponent<>(reader, writer);
        } else if (componentType.isEnum() && componentType.getEnumConstants().length != 0) {
            return () -> new EnumComponent(componentType, (Enum[]) componentType.getEnumConstants(), reader, writer);
        } else {
            return () -> new ObjectComponent<>(componentType, reader, writer);
        }

    }

    private static final Map<Class<?>, Supplier<?>> zeroFactory = new HashMap<>();

    @SuppressWarnings("boxing")
    private static void initSuppliers() {
        zeroFactory.put(boolean.class, () -> false);
        zeroFactory.put(byte.class, () -> (byte) 0);
        zeroFactory.put(short.class, () -> (short) 0);
        zeroFactory.put(char.class, () -> '0');
        zeroFactory.put(int.class, () -> 0);
        zeroFactory.put(float.class, () -> 0.0f);
        zeroFactory.put(long.class, () -> 0L);
        zeroFactory.put(double.class, () -> 0.0);
    }

    static {
        initSuppliers();
    }

    private static MethodHandle mapReader = unreflect(Map.class, "get", Object.class, Object.class);
    private static MethodHandle mapWriter = unreflect(Map.class, "put", Object.class, Object.class, Object.class);

    private static MethodHandle unreflect(Method m) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            return lookup.unreflect(m);
        } catch (IllegalAccessException e) {
            throw ExceptionUtils.sneakyThrow(e);
        }
    }

    private static MethodHandle unreflect(Class<?> cls, String name, Class<?> returnType, Class<?>... parameters) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            return lookup.findVirtual(cls, name, MethodType.methodType(returnType, parameters));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw ExceptionUtils.sneakyThrow(e);
        }
    }

    private static <K, V> Map<K, V> from(Map<K, Supplier<V>> suppliers) {
        Map<K, V> ret = new HashMap<>(suppliers.size());
        suppliers.forEach((k, sv) -> ret.put(k, sv.get()));
        return ret;
    }

    private static <E> Supplier<E> newInstanceFactory(Class<E> cls) {
        return new Lazy<E>(() -> {
            try {
                Constructor<E> constructor = cls.getDeclaredConstructor();
                return () -> {
                    try {
                        return constructor.newInstance();
                    } catch (Exception e) {
                        throw ExceptionUtils.sneakyThrow(e);
                    }
                };
            } catch (Exception e) {
                throw ExceptionUtils.sneakyThrow(e);
            }
        });
    }
}
