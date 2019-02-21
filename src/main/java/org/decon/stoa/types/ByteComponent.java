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

public class ByteComponent<E> extends AbstractArrayComponent<E, Byte> implements Cloneable {
    private byte[] array;

    public ByteComponent(MethodHandle reader, MethodHandle writer) {
        super(byte.class, reader, writer);
        this.array = new byte[0];
    }

    public ByteComponent(ByteComponent<E> other) {
        super(other);
        this.array = other.array.clone();
    }

    @Override
    public void beanToArray(E bean, long index) {
        byte x;
        try {
            x = (byte) getReader().invokeExact(bean);
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
            byte x = (byte) getReader().invokeExact(bean);
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
    public byte[] data() {
        return array;
    }

    @SuppressWarnings("boxing")
    @Override
    public Byte read(long index) {
        return array[(int) index];
    }

    @Override
    public ByteComponent<E> write(long index, Byte value) {
        array[(int) index] = value.byteValue();
        return this;
    }

    @Override
    public ByteComponent<E> insert(long index, Byte value) {
        int intIndex = (int) index;
        System.arraycopy(array, intIndex, array, intIndex + 1, getSize() - intIndex);
        array[intIndex] = value.byteValue();
        return this;
    }

    @Override
    public long longIndexOf(Object o) {
        if (o == null || !(o instanceof Byte)) {
            return -1;
        }
        byte x = ((Byte) o).byteValue();
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
        this.array = (byte[]) array;
    }

    @Override
    public ByteComponent<E> clone() {
        ByteComponent<E> ret = new ByteComponent<>(getReader(), getWriter());
        ret.array = Arrays.copyOf(array, getSize());
        ret.setSize(getSize());
        return ret;
    }

}
