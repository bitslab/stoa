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
package org.decon.stoa;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.function.Supplier;

import org.decon.stoa.util.Heap;

public class Stoa<E> extends AbstractList<E> implements RandomAccess, HugeList<E>, Cloneable {

    private final Defaults defaults;

    private long size;
    private long sizeBound;
    private long capacity;

    private final Supplier<E> instanceFactory;
    private final Class<E>    castClass;

    private final Map<String, Component<E, ?>> components;
    private final Component<E, ?>[]            arrComponents;

    @SuppressWarnings("unchecked")
    public Stoa(Defaults defaults, Supplier<E> instanceFactory, Class<E> castClass,
            Map<String, Component<E, ?>> components) {
        this.defaults = defaults;
        this.size = 0;
        this.sizeBound = defaults.getListSizeBound();
        this.instanceFactory = instanceFactory;
        this.castClass = castClass;
        this.components = Collections.unmodifiableMap(components);
        this.arrComponents = new Component[components.size()];
        {
            int i = 0;
            for (Map.Entry<String, Component<E, ?>> entry : components.entrySet()) {
                arrComponents[i] = entry.getValue();
                i++;
            }
        }
        ensureCapacity(defaults.getListInitialSize());
    }

    public Stoa(Supplier<E> instanceFactory, Class<E> castClass, Map<String, Component<E, ?>> components) {
        this(new Defaults() {
        }, instanceFactory, castClass, components);
    }

    @SuppressWarnings("unchecked")
    public Stoa(Stoa<E> other) {
        this.defaults = other.defaults;
        this.size = other.size;
        this.sizeBound = other.sizeBound;
        this.instanceFactory = other.instanceFactory;
        this.castClass = other.castClass;
        Map<String, Component<E, ?>> newComponents = new HashMap<>();
        for (Map.Entry<String, Component<E, ?>> component : other.components.entrySet()) {
            newComponents.put(component.getKey(), component.getValue()
                                                           .clone());
        }
        this.components = newComponents;
        this.arrComponents = new Component[components.size()];
        {
            int i = 0;
            for (Map.Entry<String, Component<E, ?>> entry : components.entrySet()) {
                arrComponents[i] = entry.getValue();
                i++;
            }
        }
        this.capacity = other.capacity;
    }

    @Override
    public int size() {
        return (int) Math.min(size, Integer.MAX_VALUE);
    }

    @Override
    public long longSize() {
        return size;
    }

    @Override
    public E get(int index) {
        return read(index);
    }

    @Override
    public E read(long index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index + " where size = " + size);
        }
        E bean = castClass.cast(instanceFactory.get());
        for (Component<E, ?> component : arrComponents) {
            bean = component.arrayToBean(index, bean);
        }
        return bean;
    }

    @Override
    public E set(int index, E bean) {
        E ret = read(index);
        write(index, bean);
        return ret;
    }

    @Override
    public Stoa<E> write(long index, E bean) {
        castClass.cast(bean);
        for (Component<E, ?> component : arrComponents) {
            component.beanToArray(bean, index);
        }
        return this;
    }

    @Override
    public void add(int index, E element) {
        insert(index, element);
    }

    @Override
    public Stoa<E> insert(long index, E bean) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(index + " where size=" + size);
        }
        castClass.cast(bean);
        if (size >= sizeBound) {
            throw new IllegalArgumentException("size = " + size + ", sizeBound = " + sizeBound);
        }
        ensureCapacity(size + 1);
        for (Component<E, ?> component : arrComponents) {
            component.insertFromBean(bean, index);
        }
        size++;
        modCount++;
        return this;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(index + " where size=" + size);
        }
        int csize = c.size();
        {
            int srcPos = index;
            int length = (int) (size - srcPos);
            int destPos = srcPos + csize;
            resize(size + csize);
            copyRange(srcPos, destPos, length);
        }
        Iterator<? extends E> it = c.iterator();
        for (int i = 0; i < csize; i++) {
            set(index + i, it.next());
        }
        return csize != 0;
    }

    @Override
    public E remove(int index) {
        E bean = get(index);
        for (Component<E, ?> component : arrComponents) {
            component.deleteRange(index, index + 1);
        }
        size--;
        modCount++;
        return bean;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        Heap.heapSort(this, c);
    }

    /**
     * Set "reference fields" of bean to null. Primitive fields are not touched. Useful in conserving memory.
     */
    @Override
    public Stoa<E> nullifyRange(long fromIndex, long toIndexExclusive) {
        if (fromIndex == toIndexExclusive) {
            return this;
        }
        if (fromIndex < 0 || fromIndex >= size() || toIndexExclusive < 0 || toIndexExclusive > size()
            || fromIndex > toIndexExclusive) {
            throw new IndexOutOfBoundsException(
                    "fromIndex = " + fromIndex + " - " + "toIndex = " + toIndexExclusive + " where size = " + size);
        }
        for (Component<E, ?> component : arrComponents) {
            component.nullifyRange(fromIndex, toIndexExclusive);
        }
        return this;
    }

    public Object data(String path) {
        if (path.indexOf('.') == -1) {
            return components.get(path)
                             .data();
        }
        String[] arrs = path.split("\\.", 2);
        String head = arrs[0];
        String tail = arrs[1];
        Stoa<?> inline = (Stoa<?>) components.get(head)
                                             .data();
        return inline.data(tail);
    }

    public Object readComponent(String path, long index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(index + " where size=" + size);
        }
        if (path.indexOf('.') == -1) {
            return components.get(path)
                             .read(index);
        }
        String[] arrs = path.split("\\.", 2);
        String head = arrs[0];
        String tail = arrs[1];
        Stoa<?> inline = (Stoa<?>) components.get(head)
                                             .data();
        return inline.readComponent(tail, index);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object writeComponent(String path, long index, Object value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(index + " where size=" + size);
        }
        if (path.indexOf('.') == -1) {
            Component c = components.get(path);
            Object ret = c.read(index);
            c.write(index, value);
            return ret;
        }
        String[] arrs = path.split("\\.", 2);
        String head = arrs[0];
        String tail = arrs[1];
        Stoa<?> inline = (Stoa<?>) components.get(head)
                                             .data();
        return inline.writeComponent(tail, index, value);
    }

    @Override
    public Stoa<E> resize(long newSize) {
        if (newSize >= sizeBound) {
            throw new IllegalArgumentException("newSize = " + newSize + ", sizeBound = " + sizeBound);
        }
        ensureCapacity(newSize);
        for (Component<E, ?> component : arrComponents) {
            component.resize(newSize);
        }
        size = newSize;
        modCount++;
        return this;
    }

    @Override
    public Stoa<E> trimToSize() {
        for (Component<E, ?> component : arrComponents) {
            component.trimToSize();
        }
        capacity = size;
        modCount++;
        return this;
    }

    /**
     * List is not resized for this operation.
     */
    @Override
    public Stoa<E> copyRange(long srcPos, long destPos, long length) {
        if (srcPos == destPos) {
            return this;
        }
        if (srcPos < 0 || (srcPos + length) > size || destPos < 0 || (destPos + length) > size) {
            throw new IndexOutOfBoundsException("srcPos = " + srcPos + " - " + "destPos = " + destPos + " length = "
                                                + length + " where size = " + size);
        }
        for (Component<E, ?> component : arrComponents) {
            component.copyRange(srcPos, destPos, length);
        }
        return this;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Stoa<E> fillRange(E value, long fromIndex, long toIndexExclusive) {
        for (Component component : arrComponents) {
            component.fillRangeFromBean(value, fromIndex, toIndexExclusive);
        }
        return this;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        deleteRange(fromIndex, toIndex);
    }

    @Override
    public Stoa<E> deleteRange(long fromIndex, long toIndex) {
        if (fromIndex == toIndex) {
            return this;
        }
        if (fromIndex < 0 || fromIndex >= size() || toIndex < 0 || toIndex > size() || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException(
                    "fromIndex = " + fromIndex + " - " + "toIndex = " + toIndex + " where size = " + size);
        }
        for (Component<E, ?> component : arrComponents) {
            component.deleteRange(fromIndex, toIndex);
        }
        size -= (toIndex - fromIndex);
        modCount++;
        return this;
    }

    @Override
    public Stoa<E> ensureCapacity(long expectedElements) {
        if (expectedElements > sizeBound) {
            throw new IndexOutOfBoundsException(
                    "expectedElements = " + expectedElements + " where sizeBound = " + sizeBound);
        }
        if (expectedElements <= capacity) {
            return this;
        }
        final long grow = Math.max(defaults.getListMinEmpty(), (long) (size * defaults.getListResizeFactor()));
        final long newCapacity = (int) Math.max(expectedElements, size + grow);

        for (Component<E, ?> component : arrComponents) {
            component.ensureCapacity(newCapacity);
        }
        capacity = newCapacity;
        modCount++;
        return this;
    }

    public Supplier<E> getInstanceFactory() {
        return instanceFactory;
    }

    public Class<E> getCastClass() {
        return castClass;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    public long getSizeBound() {
        return sizeBound;
    }

    public Stoa<E> setSizeBound(long sizeBound) {
        if (size > sizeBound) {
            throw new IllegalArgumentException(
                    "size = " + size + ", given sizeBound = " + sizeBound + ", current sizeBound = " + this.sizeBound);
        }
        this.sizeBound = sizeBound;
        return this;
    }

    public Map<String, Component<E, ?>> getComponents() {
        return components;
    }

    @Override
    public long longIndexOf(Object o) {
        for (long i = 0; i < size; i++) {
            E e = read(i);
            if (e == null && o == null) {
                return i;
            } else if (o == null || e == null) {
                return -1;
            } else if (o.equals(e)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public long maximumCapacity() {
        long max = Long.MAX_VALUE;
        for (Component<E, ?> component : arrComponents) {
            max = Math.min(max, component.maximumCapacity());
        }
        return max;
    }

    @Override
    public Stoa<E> clone() {
        return new Stoa<E>(this);
    }

}
