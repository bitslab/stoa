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

public class BitUtils {

    public static byte readNonOverlapByte(long[] array, int word, int bitStart) {
        long l = array[word];
        l >>>= bitStart;
        return (byte) l;
    }

    public static byte readOverlapByte(long[] array, int word, int bitStart) {
        long l1 = array[word];
        long l2 = array[word + 1];
        l1 >>>= bitStart;
        byte ret = (byte) l1;
        l2 <<= (64 - bitStart);
        ret |= (byte) l2;
        return ret;
    }

    public static short readNonOverlapShort(long[] array, int word, int bitStart) {
        long l = array[word];
        l >>>= bitStart;
        return (short) l;
    }

    public static short readOverlapShort(long[] array, int word, int bitStart) {
        long l1 = array[word];
        long l2 = array[word + 1];
        l1 >>>= bitStart;
        short ret = (short) l1;
        l2 <<= (64 - bitStart);
        ret |= (short) l2;
        return ret;
    }

    public static int readNonOverlapInt(long[] array, int word, int bitStart) {
        long l = array[word];
        l >>>= bitStart;
        return (int) l;
    }

    public static int readOverlapInt(long[] array, int word, int bitStart) {
        long l1 = array[word];
        long l2 = array[word + 1];
        l1 >>>= bitStart;
        int ret = (int) l1;
        l2 <<= (64 - bitStart);
        ret |= (int) l2;
        return ret;
    }

    public static long readOverlapLong(long[] array, int word, int bitStart) {
        long l1 = array[word];
        long l2 = array[word + 1];
        l1 >>>= bitStart;
        long ret = l1;
        l2 <<= (64 - bitStart);
        ret |= l2;
        return ret;
    }

    public static void writeNonOverlapByte(long[] array, int word, int bitStart, byte value) {
        long mask = (1L << 8) - 1;
        mask <<= bitStart;
        mask = ~mask;

        long l = array[word];
        l &= mask;
        l |= Byte.toUnsignedLong(value) << bitStart;
        array[word] = l;
    }

    public static void writeOverlapByte(long[] array, int word, int bitStart, byte value) {
        long mask1 = (1L << 8) - 1;
        mask1 <<= bitStart;
        mask1 = ~mask1;
        long mask2 = ~0L;
        mask2 <<= (8 - (64 - bitStart));

        long l1 = array[word];
        long l2 = array[word + 1];
        l1 &= mask1;
        l1 |= Byte.toUnsignedLong(value) << bitStart;
        l2 &= mask2;
        l2 |= Byte.toUnsignedLong(value) >>> (64 - bitStart);

        array[word] = l1;
        array[word + 1] = l2;
    }

    public static void writeNonOverlapShort(long[] array, int word, int bitStart, short value) {
        long mask = (1L << 16) - 1;
        mask <<= bitStart;
        mask = ~mask;

        long l = array[word];
        l &= mask;
        l |= Short.toUnsignedLong(value) << bitStart;
        array[word] = l;
    }

    public static void writeOverlapShort(long[] array, int word, int bitStart, short value) {
        long mask1 = (1L << 16) - 1;
        mask1 <<= bitStart;
        mask1 = ~mask1;
        long mask2 = ~0L;
        mask2 <<= (16 - (64 - bitStart));

        long l1 = array[word];
        long l2 = array[word + 1];
        l1 &= mask1;
        l1 |= Short.toUnsignedLong(value) << bitStart;
        l2 &= mask2;
        l2 |= Short.toUnsignedLong(value) >>> (64 - bitStart);

        array[word] = l1;
        array[word + 1] = l2;
    }

    public static void writeNonOverlapInt(long[] array, int word, int bitStart, int value) {
        long mask = (1L << 32) - 1;
        mask <<= bitStart;
        mask = ~mask;

        long l = array[word];
        l &= mask;
        l |= Integer.toUnsignedLong(value) << bitStart;
        array[word] = l;
    }

    public static void writeOverlapInt(long[] array, int word, int bitStart, int value) {
        long mask1 = (1L << 32) - 1;
        mask1 <<= bitStart;
        mask1 = ~mask1;
        long mask2 = ~0L;
        mask2 <<= (32 - (64 - bitStart));

        long l1 = array[word];
        long l2 = array[word + 1];
        l1 &= mask1;
        l1 |= Integer.toUnsignedLong(value) << bitStart;
        l2 &= mask2;
        l2 |= Integer.toUnsignedLong(value) >>> (64 - bitStart);

        array[word] = l1;
        array[word + 1] = l2;
    }

    public static void writeNonOverlapLong(long[] array, int word, long value) {
        array[word] = value;
    }

    public static void writeOverlapLong(long[] array, int word, int bitStart, long value) {
        long mask1 = ~0L;
        mask1 <<= bitStart;
        long mask2 = mask1;
        mask1 = ~mask1;

        long l1 = array[word];
        long l2 = array[word + 1];
        l1 &= mask1;
        l1 |= value << bitStart;
        l2 &= mask2;
        l2 |= value >>> (64 - bitStart);

        array[word] = l1;
        array[word + 1] = l2;
    }

}
