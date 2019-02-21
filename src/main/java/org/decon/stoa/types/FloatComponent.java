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

public class FloatComponent<E> extends AbstractArrayComponent<E, Float> implements Cloneable {
    private float[] array;

    public FloatComponent(MethodHandle reader, MethodHandle writer) {
        super(float.class, reader, writer);
        this.array = new float[0];
    }

    public FloatComponent(FloatComponent<E> other) {
        super(other);
        this.array = other.array.clone();
    }

    @Override
    public void beanToArray(E bean, long index) {
        float x;
        try {
            x = (float) getReader().invokeExact(bean);
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
            float x = (float) getReader().invokeExact(bean);
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
    public float[] data() {
        return array;
    }

    @SuppressWarnings("boxing")
    @Override
    public Float read(long index) {
        return array[(int) index];
    }

    @Override
    public FloatComponent<E> write(long index, Float value) {
        array[(int) index] = value.floatValue();
        return this;
    }

    @Override
    public FloatComponent<E> insert(long index, Float value) {
        int intIndex = (int) index;
        System.arraycopy(array, intIndex, array, intIndex + 1, getSize() - intIndex);
        array[intIndex] = value.floatValue();
        return this;
    }

    @Override
    public long longIndexOf(Object o) {
        if (o == null || !(o instanceof Float)) {
            return -1;
        }
        float x = ((Float) o).floatValue();
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
        this.array = (float[]) array;
    }

    @Override
    public FloatComponent<E> clone() {
        FloatComponent<E> ret = new FloatComponent<>(getReader(), getWriter());
        ret.array = Arrays.copyOf(array, getSize());
        ret.setSize(getSize());
        return ret;
    }

}
