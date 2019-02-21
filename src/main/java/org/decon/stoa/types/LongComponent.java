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

import org.decon.stoa.util.ExceptionUtils;

public class LongComponent<E> extends AbstractArrayComponent<E, Long> implements Cloneable {
    private long[] array;

    public LongComponent(MethodHandle reader, MethodHandle writer) {
        super(long.class, reader, writer);
        this.array = new long[0];
    }

    public LongComponent(LongComponent<E> other) {
        super(other);
        this.array = other.array.clone();
    }

    @Override
    public void beanToArray(E bean, long index) {
        long x;
        try {
            x = (long) getReader().invokeExact(bean);
        } catch (Throwable e) {
            ExceptionUtils.sneakyThrow(e);
            return;
        }
        array[(int) index] = x;
    }

    @Override
    public void insertFromBean(E bean, long index) {
        int intIndex = (int) index;
        try {
            long x = (long) getReader().invokeExact(bean);
            System.arraycopy(array, intIndex, array, intIndex + 1, getSize() - intIndex);
            array[(int) index] = x;
            setSize(getSize() + 1);
        } catch (Throwable e) {
            ExceptionUtils.sneakyThrow(e);
            return;
        }
    }

    @Override
    public E arrayToBean(long index, E bean) {
        try {
            E newBean = (E) getWriter().invokeExact(bean, array[(int) index]);
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
    public long[] data() {
        return array;
    }

    @SuppressWarnings("boxing")
    @Override
    public Long read(long index) {
        return array[(int) index];
    }

    @Override
    public LongComponent<E> write(long index, Long value) {
        array[(int) index] = value.longValue();
        return this;
    }

    @Override
    public LongComponent<E> insert(long index, Long value) {
        int intIndex = (int) index;
        System.arraycopy(array, intIndex, array, intIndex + 1, getSize() - intIndex);
        array[intIndex] = value.longValue();
        return this;
    }

    @Override
    public long longIndexOf(Object o) {
        if (o == null || !(o instanceof Long)) {
            return -1;
        }
        long x = ((Long) o).longValue();
        for (int i = 0; i < getSize(); i++) {
            if (array[i] == x) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected Object getArray() {
        return array;
    }

    @Override
    protected void setArray(Object array) {
        this.array = (long[]) array;
    }

    @Override
    public LongComponent<E> clone() {
        LongComponent<E> ret = new LongComponent<>(getReader(), getWriter());
        ret.array = Arrays.copyOf(array, getSize());
        ret.setSize(getSize());
        return ret;
    }

}
