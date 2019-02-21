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
package org.decon.stoa.types;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.decon.stoa.Component;
import org.decon.stoa.HugeList;
import org.decon.stoa.Stoa;

public class InlineComponent<E, T> implements Cloneable, Component<E, T> {
    private final Stoa<T>       stoa;
    private Function<E, T>      reader;
    private BiFunction<E, T, E> writer;

    public InlineComponent(Supplier<Stoa<T>> stoaFactory, Function<E, T> reader, BiFunction<E, T, E> writer) {
        this.stoa = stoaFactory.get();
        this.reader = reader;
        this.writer = writer;
    }

    public InlineComponent(InlineComponent<E, T> other) {
        this.stoa = other.stoa.clone();
        this.reader = other.reader;
        this.writer = other.writer;
    }

    @Override
    public Stoa<T> data() {
        return stoa;
    }

    @Override
    public E arrayToBean(long index, E bean) {
        E ret = getWriter().apply(bean, read(index));
        if (ret == null) {
            return bean;
        }
        return ret;
    }

    @Override
    public void beanToArray(E bean, long index) {
        T value = getReader().apply(bean);
        if (value == null) {
            value = stoa.getInstanceFactory()
                        .get();
        }
        write(index, value);
    }

    @Override
    public void insertFromBean(E bean, long index) {
        T value = getReader().apply(bean);
        if (value == null) {
            value = stoa.getInstanceFactory()
                        .get();
        }
        insert(index, value);
    }

    @Override
    public InlineComponent<E, T> fillRangeFromBean(E bean, long fromIndex, long toIndexExclusive) {
        T value = getReader().apply(bean);
        if (value == null) {
            value = stoa.getInstanceFactory()
                        .get();
        }
        fillRange(value, fromIndex, toIndexExclusive);
        return this;
    }

    @Override
    public InlineComponent<E, T> clone() {
        return new InlineComponent<>(this);
    }

    @Override
    public Class<T> getComponentClass() {
        return stoa.getCastClass();
    }

    public Function<E, T> getReader() {
        return reader;
    }

    public BiFunction<E, T, E> getWriter() {
        return writer;
    }

    @Override
    public T read(long index) {
        return stoa.read(index);
    }

    @Override
    public HugeList<T> write(long index, T value) {
        stoa.write(index, value);
        return this;
    }

    @Override
    public HugeList<T> insert(long index, T value) {
        stoa.insert(index, value);
        return this;
    }

    @Override
    public HugeList<T> nullifyRange(long fromIndex, long toIndexExclusive) {
        stoa.nullifyRange(fromIndex, toIndexExclusive);
        return this;
    }

    @Override
    public HugeList<T> copyRange(long srcPos, long destPos, long length) {
        stoa.copyRange(srcPos, destPos, length);
        return this;
    }

    @Override
    public HugeList<T> fillRange(T value, long fromIndex, long toIndexExclusive) {
        stoa.fillRange(value, fromIndex, toIndexExclusive);
        return this;
    }

    @Override
    public HugeList<T> deleteRange(long fromIndex, long toIndexExclusive) {
        stoa.deleteRange(fromIndex, toIndexExclusive);
        return this;
    }

    @Override
    public long longSize() {
        return stoa.longSize();
    }

    @Override
    public HugeList<T> trimToSize() {
        stoa.trimToSize();
        return this;
    }

    @Override
    public HugeList<T> resize(long newSize) {
        stoa.resize(newSize);
        return this;
    }

    @Override
    public HugeList<T> ensureCapacity(long expectedElements) {
        stoa.ensureCapacity(expectedElements);
        return this;
    }

    @Override
    public long getCapacity() {
        return stoa.getCapacity();
    }

    @Override
    public long longIndexOf(Object o) {
        return stoa.longIndexOf(o);
    }

    @Override
    public long maximumCapacity() {
        return stoa.maximumCapacity();
    }

}
