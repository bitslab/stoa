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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.RandomAccess;

import org.decon.stoa.HugeList;

public class BitBuffer extends AbstractList<Boolean> implements RandomAccess, HugeList<Boolean>, Cloneable {

    private long[] bits;
    private long   size; // TODO Implement sizeBound like Stoa

    public BitBuffer() {
        this(new long[0], 0);
    }

    public BitBuffer(long[] bits, long size) {
        this.bits = bits;
        this.size = size;
    }

    public BitBuffer(BitBuffer other) {
        this(other.bits.clone(), other.size);
    }

    public boolean readBit(long index) {
        checkIndex(index, 0);
        int wordIndex = (int) (index / 64);
        int bitIndex = (int) (index % 64);
        return (bits[wordIndex] & (0x01L << bitIndex)) != 0;
    }

    public boolean writeBit(long index, boolean b) {
        checkIndex(index, 0);
        int wordIndex = (int) (index / 64);
        int bitIndex = (int) (index % 64);
        boolean previous = (bits[wordIndex] & (0x01L << bitIndex)) != 0;
        if (b) {
            bits[wordIndex] |= (0x01L << bitIndex);
        } else {
            bits[wordIndex] &= ~(0x01L << bitIndex);
        }
        return previous;
    }

    public void insertBit(long index, boolean bit) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index = " + index + " size = " + size);
        }
        ensureCapacity(size + 1);
        int wordIndex = (int) (index / 64);
        int bitIndex = (int) (index % 64);
        for (int i = (int) (size / 64); i > wordIndex; --i) {
            long word = bits[i];
            word <<= 1;
            word |= ((bits[i - 1] & (0x1L << 63)) >>> 63);
            bits[i] = word;
        }
        long word = bits[wordIndex];
        long msbMask = mask(bitIndex, 64);
        long lsbMask = ~msbMask;
        word = (word & lsbMask) | ((word & msbMask) << 1);
        if (bit) {
            word |= (0x01L << bitIndex);
        }
        bits[wordIndex] = word;
        ++size;
    }

    @Override
    public BitBuffer copyRange(long srcPos, long destPos, long length) {
        if (srcPos < 0 || (srcPos + length) > size || destPos < 0 || (destPos + length) > size) {
            throw new IndexOutOfBoundsException(
                    "srcPos = " + srcPos + " destPos = " + destPos + " length = " + length + " size = " + size);
        }
        if (destPos == srcPos || length == 0) {
            return this;
        }
        if (destPos < srcPos) {
            int longs = (int) ((length / 64) * 64);
            long span = srcPos - destPos;
            for (long i = srcPos; i <= srcPos + longs - 64; i += 64) {
                writeLong(i - span, readLong(i));
            }
            for (long i = srcPos + longs; i < srcPos + length; i++) {
                writeBit(i - span, readBit(i));
            }
        } else {
            int longs = (int) ((length / 64) * 64);
            long span = destPos - srcPos;
            for (long i = srcPos + length - 64; i >= srcPos + length - longs; i -= 64) {
                writeLong(i + span, readLong(i));
            }
            for (long i = srcPos + length - longs - 1; i >= srcPos; i--) {
                writeBit(i + span, readBit(i));
            }
        }
        return this;
    }

    @Override
    public BitBuffer fillRange(Boolean value, long fromIndex, long toIndexExclusive) {
        boolean b = value.booleanValue();
        if (fromIndex >= size || toIndexExclusive > size || fromIndex > toIndexExclusive) {
            throw new IndexOutOfBoundsException(
                    "fromIndex = " + fromIndex + " toIndexExclusive = " + toIndexExclusive + " size = " + size);
        }
        int startWord = (int) (fromIndex / 64);
        int endWord = (int) (toIndexExclusive / 64);
        if (startWord != endWord) {
            if (startWord + 1 <= endWord - 1) {
                long l = b ? ~0L : 0L;
                Arrays.fill(bits, startWord + 1, endWord - 1, l);
            }
            for (long i = fromIndex; i < (startWord + 1) * 64; i++) {
                writeBit(i, b);
            }
            for (long i = endWord * 64; i < toIndexExclusive; i++) {
                writeBit(i, b);
            }
        } else {
            for (long i = fromIndex; i < toIndexExclusive; i++) {
                writeBit(i, b);
            }
        }
        return this;
    }

    @Override
    public BitBuffer deleteRange(long fromIndex, long toIndexExclusive) {
        if (fromIndex == toIndexExclusive) {
            return this;
        }
        if (fromIndex >= size || toIndexExclusive > size || fromIndex > toIndexExclusive) {
            throw new IndexOutOfBoundsException(
                    "fromIndex = " + fromIndex + " toIndexExclusive = " + toIndexExclusive + " size = " + size);
        }
        if (fromIndex == 0 && toIndexExclusive >= size) {
            size = 0;
            return this;
        }
        copyRange(toIndexExclusive, fromIndex, size - toIndexExclusive);
        size -= (toIndexExclusive - fromIndex);
        return this;
    }

    @Override
    public void removeRange(int fromIndex, int toIndexExclusive) {
        deleteRange(fromIndex, toIndexExclusive);
        ++modCount;
    }

    private long mask(int startInclusive, int endExclusive) {
        if (startInclusive == endExclusive) {
            return 0x0L;
        }
        long ret = ~0x0L;
        ret >>>= startInclusive;
        ret <<= startInclusive;
        ret <<= (64 - endExclusive);
        ret >>>= (64 - endExclusive);
        return ret;
    }

    public long nextSetBit(long fromIndexInclusive) {
        if (fromIndexInclusive >= size) {
            return -1;
        }
        int bitStart = (int) (fromIndexInclusive % 64);
        long startMask = mask(bitStart, 64);
        int start = (int) (fromIndexInclusive / 64);
        if ((bits[start] & startMask) != 0) {
            long word = bits[start] & startMask;
            long ret = (start * 64) + Long.numberOfTrailingZeros(word);
            if (ret < size) {
                return ret;
            }
            return -1;
        }
        int end = (int) ((size - 1) / 64);
        for (int i = start + 1; i < end; i++) {
            if (bits[i] != 0) {
                long ret = (i * 64) + Long.numberOfTrailingZeros(bits[i]);
                if (ret < size) {
                    return ret;
                }
                return -1;
            }
        }
        return -1;
    }

    public long nextUnsetBit(long fromIndexInclusive) {
        if (fromIndexInclusive >= size) {
            return -1;
        }
        int bitStart = (int) (fromIndexInclusive % 64);
        int start = (int) (fromIndexInclusive / 64);
        long startWord = bits[start] >> bitStart;
        if (~startWord != 0) {
            long ret = (start * 64) + Long.numberOfTrailingZeros(~startWord) + bitStart;
            if (ret < size) {
                return ret;
            }
            return -1;
        }
        int end = (int) ((size - 1) / 64);
        for (int i = start + 1; i < end; i++) {
            if (~bits[i] != 0) {
                long ret = (i * 64) + Long.numberOfTrailingZeros(~bits[i]);
                if (ret < size) {
                    return ret;
                }
                return -1;
            }
        }
        return -1;
    }

    public long[] data() {
        return bits;
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
    public BitBuffer trimToSize() {
        if (size == 0) {
            bits = new long[0];
            return this;
        }
        int newLength = (int) ((size / 64) + Math.min(size % 64, 1));
        if (newLength < bits.length) {
            bits = Arrays.copyOf(bits, newLength);
        }
        return this;
    }

    @Override
    public BitBuffer resize(long newSize) {
        ensureCapacity(newSize);
        long oldSize = size;
        size = newSize;
        if (newSize > oldSize) {
            fillRange(Boolean.FALSE, oldSize, newSize);
        }
        return this;
    }

    @Override
    public BitBuffer ensureCapacity(long expectedElements) {
        int newLength = (int) ((expectedElements / 64) + Math.min(expectedElements % 64, 1));
        if (newLength > bits.length) {
            bits = Arrays.copyOf(bits, newLength);
        }
        return this;
    }

    public byte readByte(long index) {
        checkIndex(index, 7);
        int bitStart = (int) (index % 64);
        if (bitStart <= 56) {
            return BitUtils.readNonOverlapByte(bits, (int) (index / 64), bitStart);
        }
        return BitUtils.readOverlapByte(bits, (int) (index / 64), bitStart);
    }

    public short readShort(long index) {
        checkIndex(index, 15);
        int bitStart = (int) (index % 64);
        if (bitStart <= 48) {
            return BitUtils.readNonOverlapShort(bits, (int) (index / 64), bitStart);
        }
        return BitUtils.readOverlapShort(bits, (int) (index / 64), bitStart);
    }

    public int readInt(long index) {
        checkIndex(index, 31);
        int bitStart = (int) (index % 64);
        if (bitStart <= 32) {
            return BitUtils.readNonOverlapInt(bits, (int) (index / 64), bitStart);
        }
        return BitUtils.readOverlapInt(bits, (int) (index / 64), bitStart);
    }

    public long readLong(long index) {
        checkIndex(index, 63);
        int bitStart = (int) (index % 64);
        if (bitStart == 0) {
            return bits[(int) (index / 64)];
        }
        return BitUtils.readOverlapLong(bits, (int) (index / 64), bitStart);
    }

    public long readSomeBits(long index, int numBits) {
        if (numBits > 64 || numBits < 0) {
            throw new IllegalArgumentException("numBits = " + numBits);
        }
        checkIndex(index, numBits - 1);
        int bitStart = (int) (index % 64);
        long l;
        if (bitStart <= 64 - numBits) {
            l = bits[(int) (index / 64)];
        } else {
            l = BitUtils.readOverlapLong(bits, (int) (index / 64), bitStart);
        }
        l >>>= bitStart;
        l <<= (64 - numBits);
        l >>>= (64 - numBits);
        return l;
    }

    public void writeByte(long index, byte value) {
        checkIndex(index, 7);
        int bitStart = (int) (index % 64);
        if (bitStart <= 56) {
            BitUtils.writeNonOverlapByte(bits, (int) (index / 64), bitStart, value);
            return;
        }
        BitUtils.writeOverlapByte(bits, (int) (index / 64), bitStart, value);
    }

    public void writeShort(long index, short value) {
        checkIndex(index, 15);
        int bitStart = (int) (index % 64);
        if (bitStart <= 48) {
            BitUtils.writeNonOverlapShort(bits, (int) (index / 64), bitStart, value);
            return;
        }
        BitUtils.writeOverlapShort(bits, (int) (index / 64), bitStart, value);
    }

    public void writeInt(long index, int value) {
        checkIndex(index, 31);
        int bitStart = (int) (index % 64);
        if (bitStart <= 32) {
            BitUtils.writeNonOverlapInt(bits, (int) (index / 64), bitStart, value);
            return;
        }
        BitUtils.writeOverlapInt(bits, (int) (index / 64), bitStart, value);
    }

    public void writeLong(long index, long value) {
        checkIndex(index, 63);
        int bitStart = (int) (index % 64);
        if (bitStart == 0) {
            bits[(int) (index / 64)] = value;
            return;
        }
        BitUtils.writeOverlapLong(bits, (int) (index / 64), bitStart, value);
    }

    public void writeSomeBits(long index, int numBits, long value) {
        if (numBits > 64 || numBits < 0) {
            throw new IllegalArgumentException("numBits = " + numBits);
        }
        checkIndex(index, numBits - 1);
        int bitStart = (int) (index % 64);
        int word = (int) (index / 64);
        if (bitStart <= 64 - numBits) {
            long mask = (1L << numBits) - 1;
            mask <<= bitStart;
            mask = ~mask;

            long l = bits[word];
            l &= mask;
            l |= (value << bitStart) & ~mask;
            bits[word] = l;
        } else {
            long mask1 = (1L << numBits) - 1;
            mask1 <<= bitStart;
            mask1 = ~mask1;
            long mask2 = ~0L;
            mask2 <<= (numBits - (64 - bitStart));

            long l1 = bits[word];
            long l2 = bits[word + 1];
            l1 &= mask1;
            l1 |= (value << bitStart) & ~mask1;
            l2 &= mask2;
            l2 |= (value >>> (64 - bitStart)) & ~mask2;

            bits[word] = l1;
            bits[word + 1] = l2;
        }
    }

    @Override
    public BitBuffer clone() {
        return new BitBuffer(this);
    }

    private void checkIndex(long index, long offset) {
        if (index + offset < 0 || index + offset >= size) {
            throw new IndexOutOfBoundsException("index = " + index + " offset = " + offset + " size = " + size);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() == obj.getClass()) {
            BitBuffer other = (BitBuffer) obj;
            if (other.size != this.size) {
                return false;
            }
            for (int i = 0; i < size / 64; i++) {
                if (bits[i] != other.bits[i]) {
                    return false;
                }
            }
            for (long i = (size / 64) * 64; i < (size / 64) * 64 + (size % 64); i++) {
                if (readBit(i) != other.readBit(i)) {
                    return false;
                }
            }
            return true;
        }
        return super.equals(obj);
    }

    @Override
    public Boolean get(int index) {
        return Boolean.valueOf(readBit(index));
    }

    @Override
    public Boolean read(long index) {
        return Boolean.valueOf(readBit(index));
    }

    @Override
    public Boolean set(int index, Boolean element) {
        return Boolean.valueOf(writeBit(index, element.booleanValue()));
    }

    @Override
    public BitBuffer write(long index, Boolean element) {
        writeBit(index, element.booleanValue());
        return this;
    }

    @Override
    public void add(int index, Boolean element) {
        insertBit(index, element.booleanValue());
        ++modCount;
    }

    @Override
    public Boolean remove(int index) {
        boolean ret = readBit(index);
        removeRange(index, index + 1);
        ++modCount;
        return Boolean.valueOf(ret);
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof Boolean) {
            boolean bit = ((Boolean) o).booleanValue();
            long next;
            if (bit) {
                next = nextSetBit(0);
            } else {
                next = nextUnsetBit(0);
            }
            if (next > Integer.MAX_VALUE) {
                next = -1;
            }
            return (int) next;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        // TODO Auto-generated method stub
        return super.lastIndexOf(o);
    }

    @Override
    public void clear() {
        deleteRange(0, size);
        ++modCount;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Boolean) {
            boolean bit = ((Boolean) o).booleanValue();
            if (bit) {
                return nextSetBit(0) != -1;
            }
            return nextUnsetBit(0) != -1;
        }
        return false;
    }

    @Override
    public Object[] toArray() {
        if (size > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException("size = " + size + " > Integer.MAX_VALUE");
        }
        Object[] ret = new Object[(int) size];
        Arrays.fill(ret, Boolean.FALSE);
        for (int next = (int) nextSetBit(0); next != -1; next = (int) nextSetBit(next)) {
            ret[next] = Boolean.TRUE;
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (size > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException("size = " + size + " > Integer.MAX_VALUE");
        }
        if (longSize() == 0) {
            if (a.length != 0) {
                a[0] = null;
            }
            return a;
        }
        if (!a.getClass()
              .getComponentType()
              .isAssignableFrom(Boolean.class)) {
            throw new ArrayStoreException(a.getClass()
                                           .getComponentType()
                    + " != " + Boolean.class);
        }
        Boolean[] ba;
        if (a.length >= size) {
            ba = (Boolean[]) a;
            for (int i = 0; i < size; i++) {
                ba[i] = Boolean.FALSE;
            }
            if (ba.length > size) {
                ba[(int) size] = null;
            }
        } else {
            ba = new Boolean[(int) size];
            Arrays.fill(ba, Boolean.FALSE);
        }

        for (int next = (int) nextSetBit(0); next != -1; next = (int) nextSetBit(next)) {
            ba[next] = Boolean.TRUE;
        }
        return (T[]) ba;
    }

    @Override
    public BitBuffer insert(long index, Boolean value) {
        insertBit(index, value.booleanValue());
        return this;
    }

    @Override
    public long longIndexOf(Object o) {
        if (o instanceof Boolean) {
            boolean bit = ((Boolean) o).booleanValue();
            if (bit) {
                return nextSetBit(0);
            }
            return nextUnsetBit(0);
        }
        return -1;
    }

    @Override
    public long maximumCapacity() {
        return 64L * (Integer.MAX_VALUE - 8);
    }

    @Override
    public long getCapacity() {
        return 64L * bits.length;
    }

    @Override
    public BitBuffer nullifyRange(long fromIndex, long toIndexExclusive) {
        return this;
    }

}
