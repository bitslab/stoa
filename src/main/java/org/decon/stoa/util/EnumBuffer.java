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

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.RandomAccess;

import org.decon.stoa.HugeList;

public class EnumBuffer<E extends Enum<E>> extends AbstractList<E> implements RandomAccess, HugeList<E>, Cloneable {
    protected final E[]       constants;
    protected final int       bitsPerConstant;
    protected final BitBuffer bitBuffer;

    public static <E extends Enum<E>> EnumBuffer<E> wrap(E[] constants) {
        if (constants.length == 0) {
            throw new IllegalArgumentException("constants array is empty");
        }
        for (E e : constants) {
            if (!Enum.class.isAssignableFrom(e.getClass())) {
                throw new IllegalArgumentException(
                        "constants array contains non-enum object -> " + e + " of class " + e.getClass());
            }
        }
        return new EnumBuffer<E>(constants);
    }

    public EnumBuffer(E[] constants) {
        this.constants = constants;
        int hob = Integer.highestOneBit(constants.length) == constants.length ? constants.length
                : (Integer.highestOneBit(constants.length) << 1);
        this.bitsPerConstant = Integer.numberOfTrailingZeros(hob);
        this.bitBuffer = new BitBuffer();
    }

    public EnumBuffer(EnumBuffer<E> other) {
        this.constants = other.constants;
        this.bitsPerConstant = other.bitsPerConstant;
        this.bitBuffer = other.bitBuffer.clone();
    }

    public E readEnum(long index) {
        checkIndex(index, 0);
        long l = bitBuffer.readSomeBits(index * bitsPerConstant, bitsPerConstant);
        return constants[(int) l];
    }

    public E writeEnum(long index, E e) {
        checkIndex(index, 0);
        long l = bitBuffer.readSomeBits(index * bitsPerConstant, bitsPerConstant);
        E ret = constants[(int) l];
        bitBuffer.writeSomeBits(index * bitsPerConstant, bitsPerConstant, e.ordinal());
        return ret;
    }

    public void insertEnum(long index, E e) {
        if (index < 0 || index > longSize()) {
            throw new IndexOutOfBoundsException("index = " + index + " size = " + longSize());
        }
        resize(longSize() + 1);
        copyRange(index, index + 1, longSize() - index - 1);
        writeEnum(index, e);
    }

    @Override
    public EnumBuffer<E> copyRange(long srcPos, long destPos, long length) {
        if (srcPos < 0 || (srcPos + length) > longSize() || destPos < 0 || (destPos + length) > longSize()) {
            throw new IndexOutOfBoundsException(
                    "srcPos = " + srcPos + " destPos = " + destPos + " length = " + length + " size = " + longSize());
        }
        if (destPos == srcPos || length == 0) {
            return this;
        }
        bitBuffer.copyRange(srcPos * bitsPerConstant, destPos * bitsPerConstant, length * bitsPerConstant);
        return this;
    }

    @Override
    public EnumBuffer<E> fillRange(E e, long fromIndex, long toIndexExclusive) {
        if (fromIndex >= longSize() || toIndexExclusive > longSize() || fromIndex > toIndexExclusive) {
            throw new IndexOutOfBoundsException(
                    "fromIndex = " + fromIndex + " toIndexExclusive = " + toIndexExclusive + " size = " + longSize());
        }
        long length = toIndexExclusive - fromIndex;
        for (int i = 0; i < Math.min(length, 20); i++) {
            writeEnum(fromIndex + i, e);
        }
        if (length > 20) {
            long span = 20;
            while (span * 2 < length) {
                copyRange(fromIndex, fromIndex + span, span);
                span *= 2;
            }
            copyRange(fromIndex, fromIndex + span, length - span);
        }
        return this;
    }

    @Override
    public EnumBuffer<E> deleteRange(long fromIndex, long toIndexExclusive) {
        if (fromIndex == toIndexExclusive) {
            return this;
        }
        if (fromIndex >= longSize() || toIndexExclusive > longSize() || fromIndex > toIndexExclusive) {
            throw new IndexOutOfBoundsException(
                    "fromIndex = " + fromIndex + " toIndexExclusive = " + toIndexExclusive + " size = " + longSize());
        }
        bitBuffer.deleteRange(fromIndex * bitsPerConstant, toIndexExclusive * bitsPerConstant);
        return this;
    }

    @Override
    public void removeRange(int fromIndex, int toIndexExclusive) {
        deleteRange(fromIndex, toIndexExclusive);
        ++modCount;
    }

    public long[] data() {
        return bitBuffer.data();
    }

    @Override
    public int size() {
        return (int) Math.min(longSize(), Integer.MAX_VALUE);
    }

    @Override
    public long longSize() {
        return bitBuffer.longSize() / bitsPerConstant;
    }

    @Override
    public EnumBuffer<E> trimToSize() {
        bitBuffer.trimToSize();
        return this;
    }

    @Override
    public EnumBuffer<E> resize(long newSize) {
        bitBuffer.resize(newSize * bitsPerConstant);
        return this;
    }

    @Override
    public EnumBuffer<E> ensureCapacity(long expectedElements) {
        bitBuffer.ensureCapacity(expectedElements * bitsPerConstant);
        return this;
    }

    @Override
    public EnumBuffer<E> clone() {
        EnumBuffer<E> ret = new EnumBuffer<E>(constants, bitsPerConstant, bitBuffer.clone());
        return ret;
    }

    private void checkIndex(long index, long offset) {
        if (index + offset < 0 || index + offset >= longSize()) {
            throw new IndexOutOfBoundsException("index = " + index + " size = " + longSize());
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() == obj.getClass()) {
            EnumBuffer<E> other = (EnumBuffer<E>) obj;
            return Arrays.equals(other.constants, this.constants) && other.bitBuffer.equals(this.bitBuffer);
        }
        return super.equals(obj);
    }

    @Override
    public E get(int index) {
        return readEnum(index);
    }

    @Override
    public E read(long index) {
        return readEnum(index);
    }

    @Override
    public E set(int index, E e) {
        return writeEnum(index, e);
    }

    @Override
    public EnumBuffer<E> write(long index, E e) {
        writeEnum(index, e);
        return this;
    }

    @Override
    public void add(int index, E e) {
        insertEnum(index, e);
        ++modCount;
    }

    @Override
    public E remove(int index) {
        E ret = readEnum(index);
        removeRange(index, index + 1);
        ++modCount;
        return ret;
    }

    @Override
    public int indexOf(Object o) {
        long index = longIndexOf(o);
        if (index > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) index;
    }

    @Override
    public int lastIndexOf(Object o) {
        // TODO Auto-generated method stub
        return super.lastIndexOf(o);
    }

    @Override
    public void clear() {
        deleteRange(0, longSize());
        ++modCount;
    }

    @Override
    public boolean contains(Object o) {
        long index = longIndexOf(o);
        if (index > Integer.MAX_VALUE) {
            return false;
        }
        return index != -1;
    }

    @Override
    public Object[] toArray() {
        if (longSize() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException("size = " + longSize() + " > Integer.MAX_VALUE");
        }
        Object[] ret = new Object[(int) longSize()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = readEnum(i);
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (longSize() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException("size = " + longSize() + " > Integer.MAX_VALUE");
        }
        if (longSize() == 0) {
            if (a.length != 0) {
                a[0] = null;
            }
            return a;
        }
        if (!a.getClass()
              .getComponentType()
              .isAssignableFrom(constants[0].getClass())) {
            throw new ArrayStoreException(a.getClass()
                                           .getComponentType()
                    + " != " + constants[0].getClass());
        }
        T[] ret;
        if (a.length >= longSize()) {
            ret = a;
        } else {
            ret = (T[]) Array.newInstance(a.getClass()
                                           .getComponentType(),
                    (int) longSize());
        }
        for (int i = 0; i < longSize(); i++) {
            ret[i] = (T) readEnum(i);
        }
        if (ret.length > longSize()) {
            ret[(int) longSize()] = null;
        }
        return ret;
    }

    @Override
    public EnumBuffer<E> insert(long index, E e) {
        insertEnum(index, e);
        return this;
    }

    @Override
    public long longIndexOf(Object o) {
        int c = -1;
        for (int i = 0; i < constants.length; i++) {
            if (constants[i].equals(o)) {
                c = i;
                break;
            }
        }
        if (c == -1) {
            return -1;
        }
        for (long i = 0; i < longSize(); i++) {
            E e = readEnum(i);
            if (e.equals(o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public long maximumCapacity() {
        return (64L * (Integer.MAX_VALUE - 8)) / bitsPerConstant;
    }

    @Override
    public long getCapacity() {
        return bitBuffer.getCapacity() / bitsPerConstant;
    }

    @Override
    public EnumBuffer<E> nullifyRange(long fromIndex, long toIndexExclusive) {
        return this;
    }

    public E[] getConstants() {
        return constants;
    }

    public int getBitsPerConstant() {
        return bitsPerConstant;
    }

    public BitBuffer getBitBuffer() {
        return bitBuffer;
    }

    protected EnumBuffer(E[] constants, int bitsPerConstant, BitBuffer bitBuffer) {
        this.constants = constants;
        this.bitsPerConstant = bitsPerConstant;
        this.bitBuffer = bitBuffer;
    }

}
