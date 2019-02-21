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
import java.util.Arrays;

import org.decon.stoa.Component;
import org.decon.stoa.util.BitBuffer;
import org.decon.stoa.util.ExceptionUtils;

public class BoolComponent<E> extends BitBuffer implements Cloneable, Component<E, Boolean> {
    private final MethodHandle reader;
    private final MethodHandle writer;

    public BoolComponent(MethodHandle reader, MethodHandle writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public BoolComponent(BoolComponent<E> other) {
        super(other);
        this.reader = other.reader;
        this.writer = other.writer;
    }

    @Override
    public void beanToArray(E bean, long index) {
        boolean x;
        try {
            x = (boolean) getReader().invokeExact(bean);
        } catch (Throwable e) {
            ExceptionUtils.sneakyThrow(e);
            return;
        }
        writeBit(index, x);
    }

    @Override
    public void insertFromBean(E bean, long index) {
        try {
            boolean x = (boolean) getReader().invokeExact(bean);
            insertBit(index, x);
        } catch (Throwable e) {
            ExceptionUtils.sneakyThrow(e);
            return;
        }
    }

    @Override
    public E arrayToBean(long index, E bean) {
        try {
            E newBean = (E) getWriter().invokeExact(bean, readBit(index));
            if (newBean == null) {
                return bean;
            }
            return newBean;
        } catch (Throwable e) {
            ExceptionUtils.sneakyThrow(e);
            return null;
        }
    }

    @Override
    public BoolComponent<E> nullifyRange(long fromIndex, long toIndexExclusive) {
        return this;
    }

    @Override
    public BoolComponent<E> fillRangeFromBean(E bean, long fromIndex, long toIndexExclusive) {
        try {
            boolean b = (boolean) getReader().invokeExact(bean);
            fillRange(Boolean.valueOf(b), fromIndex, toIndexExclusive);
        } catch (Throwable e) {
            throw ExceptionUtils.sneakyThrow(e);
        }
        return this;
    }

    @Override
    public BoolComponent<E> clone() {
        long[] bits = data();
        BoolComponent<E> ret =
                new BoolComponent<>(getReader(), getWriter(), Arrays.copyOf(bits, bits.length), longSize());
        return ret;
    }

    @Override
    public Class<Boolean> getComponentClass() {
        return boolean.class;
    }

    @Override
    public String toString() {
        return "BoolComponent [bitBuffer=" + super.toString() + "]";
    }

    protected MethodHandle getReader() {
        return reader;
    }

    protected MethodHandle getWriter() {
        return writer;
    }

    private BoolComponent(MethodHandle reader, MethodHandle writer, long[] bits, long size) {
        super(bits, size);
        this.reader = reader;
        this.writer = writer;
    }
}
