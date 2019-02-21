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
package org.decon.stoa.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

public class SetAdapter<E> extends AbstractSet<E> {

    private final Map<E, EmptyValue> backingMap;

    public SetAdapter(Map<E, EmptyValue> backingMap) {
        this.backingMap = backingMap;
    }

    @Override
    public int size() {
        return backingMap.size();
    }

    @Override
    public boolean add(E element) {
        EmptyValue previous = backingMap.put(element, EmptyValue.singleton);
        return previous == null;
    }

    @Override
    public boolean remove(Object element) {
        EmptyValue previous = backingMap.remove(element);
        return previous != null;
    }

    @Override
    public boolean contains(Object element) {
        return backingMap.containsKey(element);
    }

    @Override
    public Iterator<E> iterator() {
        return new SetIterator(this);
    }

    @Override
    public void clear() {
        backingMap.clear();
    }

    public Map<E, EmptyValue> getBackingMap() {
        return backingMap;
    }

    final private class SetIterator implements Iterator<E> {

        private final Iterator<Map.Entry<E, EmptyValue>> mapIter;

        public SetIterator(SetAdapter<E> set) {
            this.mapIter = set.backingMap.entrySet()
                                         .iterator();
        }

        @Override
        public boolean hasNext() {
            return mapIter.hasNext();
        }

        @Override
        public E next() {
            return mapIter.next()
                          .getKey();
        }

        @Override
        public void remove() {
            mapIter.remove();
        }

    }

    public static class EmptyValue {
        public static final EmptyValue singleton = new EmptyValue();
        public static final BeanInfo   info;
        static {
            try {
                info = Introspector.getBeanInfo(EmptyValue.class, Object.class);
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
