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
package org.decon.stoa.test.generator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.decon.stoa.util.ExceptionUtils;
import org.decon.stoa.util.Lazy;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

public class BeanGen<T> extends Generator<T> {
    private final Constructor<T>            constructor;
    private final List<Supplier<GenSet<T>>> genSet;

    @SuppressWarnings("unchecked")
    public BeanGen(final Class<T> type) {
        super(type);
        genSet = new ArrayList<>();
        try {
            BeanInfo info = Introspector.getBeanInfo(type, Object.class);
            constructor = (Constructor<T>) info.getBeanDescriptor()
                                               .getBeanClass()
                                               .getDeclaredConstructor();
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                Lazy<GenSet<T>> lambda = new Lazy<>(() -> {
                    Generator<?> generator = gen().type(pd.getPropertyType());
                    Method writer = pd.getWriteMethod();
                    return () -> (bean, random, status) -> {
                        try {
                            writer.invoke(bean, generator.generate(random, status));
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            throw ExceptionUtils.sneakyThrow(e);
                        }
                    };
                });
                genSet.add(lambda);
            }
        } catch (IntrospectionException | NoSuchMethodException | SecurityException e) {
            throw ExceptionUtils.sneakyThrow(e);
        }
    }

    @Override
    public boolean canRegisterAsType(Class<?> type) {
        return true;
    }

    @Override
    public T generate(SourceOfRandomness random, GenerationStatus status) {
        try {
            T ret = constructor.newInstance();
            for (Supplier<GenSet<T>> g : genSet) {
                g.get()
                 .apply(ret, random, status);
            }
            return ret;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException e) {
            throw ExceptionUtils.sneakyThrow(e);
        }
    }

    @FunctionalInterface
    private static interface GenSet<T> {
        public void apply(T bean, SourceOfRandomness random, GenerationStatus status);
    }

}
