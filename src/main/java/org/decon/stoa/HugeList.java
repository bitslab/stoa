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

public interface HugeList<T> {

    T read(long index);

    HugeList<T> write(long index, T value);

    HugeList<T> insert(long index, T value);

    HugeList<T> nullifyRange(long fromIndex, long toIndexExclusive);

    HugeList<T> copyRange(long srcPos, long destPos, long length);

    HugeList<T> fillRange(T value, long fromIndex, long toIndexExclusive);

    HugeList<T> deleteRange(long fromIndex, long toIndexExclusive);

    long longSize();

    HugeList<T> trimToSize();

    HugeList<T> resize(long newSize);

    HugeList<T> ensureCapacity(long expectedElements);

    long getCapacity();

    HugeList<T> clone();

    long longIndexOf(Object o);

    long maximumCapacity();
}