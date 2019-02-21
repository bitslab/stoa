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

public class ObjectComponent<E, T> extends AbstractArrayComponent<E, T> implements Cloneable {
    private Object[] array;

    public ObjectComponent(Class<T> componentClass, MethodHandle reader, MethodHandle writer) {
        super(componentClass, reader, writer);
        this.array = new Object[0];
    }

    public ObjectComponent(ObjectComponent<E, T> other) {
        super(other);
        this.array = other.array.clone();
    }

    @Override
    public void beanToArray(E bean, long index) {
        Object x;
        try {
            x = getReader().invoke(bean);
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
            Object x = getReader().invoke(bean);
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
            E newBean = (E) getWriter().invoke(bean, array[(int) index]);
            if (newBean == null) {
                return bean;
            }
            return newBean;
        } catch (Throwable e) {
            throw ExceptionUtils.sneakyThrow(e);
        }
    }

    @Override
    public Object[] data() {
        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T read(long index) {
        return (T) array[(int) index];
    }

    @Override
    public ObjectComponent<E, T> write(long index, T value) {
        array[(int) index] = value;
        return this;
    }

    @Override
    public ObjectComponent<E, T> insert(long index, T value) {
        int intIndex = (int) index;
        System.arraycopy(array, intIndex, array, intIndex + 1, getSize() - intIndex);
        array[intIndex] = value;
        return this;
    }

    @Override
    public long longIndexOf(Object o) {
        if (o != null && !getComponentClass().isInstance(o)) {
            return -1;
        }
        for (int i = 0; i < getSize(); i++) {
            Object a = array[i];
            if (a == null && o == null) {
                return i;
            } else if (o == null) {
                continue;
            } else if (o.equals(a)) {
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
        this.array = (Object[]) array;
    }

    @Override
    public ObjectComponent<E, T> clone() {
        ObjectComponent<E, T> ret = new ObjectComponent<>(getComponentClass(), getReader(), getWriter());
        ret.array = Arrays.copyOf(array, getSize());
        ret.setSize(getSize());
        return ret;
    }

}
