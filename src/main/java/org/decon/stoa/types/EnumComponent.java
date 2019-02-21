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

import java.lang.invoke.MethodHandle;

import org.decon.stoa.Component;
import org.decon.stoa.util.EnumBuffer;
import org.decon.stoa.util.ExceptionUtils;

public class EnumComponent<E, T extends Enum<T>> extends EnumBuffer<T> implements Cloneable, Component<E, T> {
    private final Class<T>     componentClass;
    private final MethodHandle reader;
    private final MethodHandle writer;

    public EnumComponent(Class<T> componentClass, T[] constants, MethodHandle reader, MethodHandle writer) {
        super(constants);
        this.componentClass = componentClass;
        this.reader = reader;
        this.writer = writer;
    }

    public EnumComponent(EnumComponent<E, T> other) {
        super(other);
        this.componentClass = other.componentClass;
        this.reader = other.reader;
        this.writer = other.writer;
    }

    @Override
    public void beanToArray(E bean, long index) {
        T x;
        try {
            x = (T) getReader().invoke(bean);
        } catch (Throwable e) {
            ExceptionUtils.sneakyThrow(e);
            return;
        }
        writeEnum(index, x);
    }

    @Override
    public void insertFromBean(E bean, long index) {
        try {
            T x = (T) getReader().invoke(bean);
            insertEnum(index, x);
        } catch (Throwable e) {
            ExceptionUtils.sneakyThrow(e);
            return;
        }
    }

    @Override
    public E arrayToBean(long index, E bean) {
        try {
            E newBean = (E) getWriter().invoke(bean, readEnum(index));
            if (newBean == null) {
                return bean;
            }
            return newBean;
        } catch (Throwable e) {
            throw ExceptionUtils.sneakyThrow(e);
        }
    }

    @Override
    public EnumComponent<E, T> fillRangeFromBean(E bean, long fromIndex, long toIndexExclusive) {
        try {
            T x = (T) getReader().invoke(bean);
            fillRange(x, fromIndex, toIndexExclusive);
        } catch (Throwable e) {
            throw ExceptionUtils.sneakyThrow(e);
        }
        return this;
    }

    @Override
    public EnumComponent<E, T> clone() {
        return new EnumComponent<>(this);
    }

    @Override
    public Class<T> getComponentClass() {
        return componentClass;
    }

    protected MethodHandle getReader() {
        return reader;
    }

    protected MethodHandle getWriter() {
        return writer;
    }

}
