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
import java.lang.reflect.Array;
import java.util.Arrays;

import org.decon.stoa.Component;
import org.decon.stoa.util.ExceptionUtils;

public abstract class AbstractArrayComponent<E, T> implements Component<E, T> {

    private final Class<T>     componentClass;
    private final MethodHandle reader;
    private final MethodHandle writer;
    private int                size;

    protected abstract Object getArray();

    protected abstract void setArray(Object array);

    @Override
    public abstract AbstractArrayComponent<E, T> clone();

    public AbstractArrayComponent(Class<T> componentClass, MethodHandle reader, MethodHandle writer) {
        this.componentClass = componentClass;
        this.reader = reader;
        this.writer = writer;
    }

    public AbstractArrayComponent(AbstractArrayComponent<E, T> other) {
        this.componentClass = other.componentClass;
        this.reader = other.reader;
        this.writer = other.writer;
    }

    @Override
    public AbstractArrayComponent<E, T> copyRange(long srcPos, long destPos, long length) {
        System.arraycopy(getArray(), (int) srcPos, getArray(), (int) destPos, (int) length);
        return this;
    }

    @Override
    public AbstractArrayComponent<E, T> fillRangeFromBean(E bean, long fromIndex, long toIndexExclusive) {
        T value;
        try {
            value = (T) reader.invoke(bean);
        } catch (Throwable e) {
            throw ExceptionUtils.sneakyThrow(e);
        }
        return fillRange(value, fromIndex, toIndexExclusive);
    }

    @Override
    @SuppressWarnings("boxing")
    public AbstractArrayComponent<E, T> fillRange(T value, long fromIndex, long toIndexExclusive) {
        if (getComponentClass().equals(boolean.class)) {
            Arrays.fill((boolean[]) getArray(), (int) fromIndex, (int) toIndexExclusive, (Boolean) value);
        } else if (getComponentClass().equals(byte.class)) {
            Arrays.fill((byte[]) getArray(), (int) fromIndex, (int) toIndexExclusive, (Byte) value);
        } else if (getComponentClass().equals(char.class)) {
            Arrays.fill((char[]) getArray(), (int) fromIndex, (int) toIndexExclusive, (Character) value);
        } else if (getComponentClass().equals(short.class)) {
            Arrays.fill((short[]) getArray(), (int) fromIndex, (int) toIndexExclusive, (Short) value);
        } else if (getComponentClass().equals(int.class)) {
            Arrays.fill((int[]) getArray(), (int) fromIndex, (int) toIndexExclusive, (Integer) value);
        } else if (getComponentClass().equals(float.class)) {
            Arrays.fill((float[]) getArray(), (int) fromIndex, (int) toIndexExclusive, (Float) value);
        } else if (getComponentClass().equals(double.class)) {
            Arrays.fill((double[]) getArray(), (int) fromIndex, (int) toIndexExclusive, (Double) value);
        } else if (getComponentClass().equals(long.class)) {
            Arrays.fill((long[]) getArray(), (int) fromIndex, (int) toIndexExclusive, (Long) value);
        } else {
            Arrays.fill((Object[]) getArray(), (int) fromIndex, (int) toIndexExclusive, value);
        }
        return this;
    }

    @Override
    public AbstractArrayComponent<E, T> deleteRange(long fromIndex, long toIndex) {
        final int shift = (int) (toIndex - fromIndex);
        System.arraycopy(getArray(), (int) (fromIndex + shift), getArray(), (int) fromIndex, (int) (size - toIndex));
        size -= shift;
        return this;
    }

    @Override
    public AbstractArrayComponent<E, T> nullifyRange(long fromIndex, long toIndexExclusive) {
        if (getComponentClass().isPrimitive()) {
            return this;
        }
        Arrays.fill((Object[]) getArray(), (int) fromIndex, (int) toIndexExclusive, null);
        return this;
    }

    @Override
    public long longSize() {
        return size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractArrayComponent<E, T> resize(long newSize) {
        int newSizeInt = (int) newSize;
        if (newSizeInt > size) {
            if (getComponentClass().equals(boolean.class)) {
                fillRange((T) Boolean.FALSE, size, newSizeInt);
            } else if (getComponentClass().equals(byte.class)) {
                fillRange((T) Byte.valueOf((byte) 0), size, newSizeInt);
            } else if (getComponentClass().equals(char.class)) {
                fillRange((T) Character.valueOf(' '), size, newSizeInt);
            } else if (getComponentClass().equals(short.class)) {
                fillRange((T) Short.valueOf((short) 0), size, newSizeInt);
            } else if (getComponentClass().equals(int.class)) {
                fillRange((T) Integer.valueOf(0), size, newSizeInt);
            } else if (getComponentClass().equals(float.class)) {
                fillRange((T) Float.valueOf(0.0f), size, newSizeInt);
            } else if (getComponentClass().equals(double.class)) {
                fillRange((T) Double.valueOf(0.0), size, newSizeInt);
            } else if (getComponentClass().equals(long.class)) {
                fillRange((T) Long.valueOf(0L), size, newSizeInt);
            } else {
                fillRange(null, size, newSizeInt);
            }
        }
        size = newSizeInt;
        return this;
    }

    @Override
    public AbstractArrayComponent<E, T> trimToSize() {
        if (size == 0) {
            setArray(Array.newInstance(getComponentClass(), 0));
            return this;
        }
        setArray(copyOf(size));
        return this;
    }

    @Override
    public AbstractArrayComponent<E, T> ensureCapacity(long expectedElements) {
        if (expectedElements > Array.getLength(getArray())) {
            setArray(copyOf((int) expectedElements));
        }
        return this;
    }

    protected Object copyOf(int newLength) {
        int oldLength = Array.getLength(getArray());
        Object ret = Array.newInstance(getComponentClass(), newLength);
        System.arraycopy(getArray(), 0, ret, 0, Math.min(oldLength, newLength));
        return ret;
    }

    @Override
    public Class<T> getComponentClass() {
        return componentClass;
    }

    @Override
    public long maximumCapacity() {
        return Integer.MAX_VALUE - 8;
    }

    @Override
    public long getCapacity() {
        return Array.getLength(getArray());
    }

    protected MethodHandle getReader() {
        return reader;
    }

    protected MethodHandle getWriter() {
        return writer;
    }

    protected int getSize() {
        return size;
    }

    protected void setSize(int size) {
        this.size = size;
    }

}