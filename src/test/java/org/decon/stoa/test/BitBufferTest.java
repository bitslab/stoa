/**
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
package org.decon.stoa.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.decon.stoa.util.BitBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;

@RunWith(Runner.class)
public class BitBufferTest {
    @SuppressWarnings("boxing")
    @Property(shrink = false)
    public void transferWord(final int seed) {
        final Random random = new Random(seed);
        final BitBuffer src = new BitBuffer();
        {
            int nextInt = random.nextInt(1000);
            src.resize(nextInt + 1000);
        }

        final Function<BitBuffer, BitBuffer> fillRandom = (BitBuffer buf) -> {
            for (int i = 0; (i < (buf.size() / 4)); i++) {
                buf.writeBit(random.nextInt(src.size()), true);
            }
            return buf;
        };
        fillRandom.apply(src);

        final BiConsumer<Integer, BiConsumer<? super BitBuffer, ? super Integer>> test = (Integer wordSize,
                BiConsumer<? super BitBuffer, ? super Integer> transfer) -> {
            BitBuffer target = new BitBuffer();
            target.resize(src.size());
            final BitBuffer orig = fillRandom.apply(target)
                                             .clone();
            final int rand = random.nextInt((src.size() - wordSize));
            transfer.accept(target, Integer.valueOf(rand));
            orig.removeRange(rand, (rand + wordSize));
            target.removeRange(rand, (rand + wordSize));
            assertEquals(target, orig);
        };
        final BiConsumer<BitBuffer, Integer> byteTx = (BitBuffer target, Integer rand) -> {
            target.writeByte(rand, src.readByte(rand));
            assertEquals(target.readByte(rand), src.readByte(rand));
        };
        final BiConsumer<BitBuffer, Integer> shortTx = (BitBuffer target, Integer rand) -> {
            target.writeShort(rand, src.readShort(rand));
            assertEquals(target.readShort(rand), src.readShort(rand));
        };
        final BiConsumer<BitBuffer, Integer> intTx = (BitBuffer target, Integer rand) -> {
            target.writeInt(rand, src.readInt(rand));
            assertEquals(target.readInt(rand), src.readInt(rand));
        };
        final BiConsumer<BitBuffer, Integer> longTx = (BitBuffer target, Integer rand) -> {
            target.writeLong(rand, src.readLong(rand));
            assertEquals(target.readLong(rand), src.readLong(rand));
        };

        for (int i = 0; (i < (src.size() / 16)); i++) {
            test.accept(8, byteTx);
            test.accept(16, shortTx);
            test.accept(32, intTx);
            test.accept(64, longTx);
        }
    }

    @SuppressWarnings("boxing")
    @Test
    public void readWriteSingleBit() {
        final BitBuffer bits = new BitBuffer();
        bits.resize(64);
        bits.writeBit(10, true);
        bits.writeBit(63, true);

        final BiConsumer<Integer, Integer> test = (Integer i, Integer b) -> {
            assertEquals(bits.readByte((i).intValue()), (b).intValue());
        };

        test.accept(0, 0);
        test.accept(8, 4);
        test.accept(9, 2);
        test.accept(16, 0);
        test.accept(24, 0);
        test.accept(32, 0);
        test.accept(48, 0);
        test.accept(56, -128);

        assertEquals(0, bits.readByte(0));
        assertEquals(4, bits.readByte(8));
        assertEquals(2, bits.readByte(9));
        assertEquals(0, bits.readByte(16));
        assertEquals(0, bits.readByte(24));
        assertEquals(0, bits.readByte(32));
        assertEquals(0, bits.readByte(48));
        assertEquals((-128), bits.readByte(56));
        bits.resize(128);
        bits.writeBit(67, true);
        bits.writeBit(127, true);
        assertEquals(8, bits.readByte(64));
        assertEquals(2, bits.readByte(66));
        assertEquals(0, bits.readByte(68));
        assertEquals(0, bits.readByte(68));
        assertEquals((-128), bits.readByte(120));
        bits.writeByte(8, ((byte) 9));
        assertEquals(9, bits.readByte(8));
        assertEquals(4, bits.readByte(9));
    }

    @SuppressWarnings("boxing")
    @Property
    public void readWriteRandom(final boolean[] barr) {
        Boolean[] boxedBarr = new Boolean[barr.length];
        for (int i = 0; i < barr.length; i++) {
            boxedBarr[i] = barr[i];
        }
        var expected = Arrays.asList(boxedBarr);
        var actual = new BitBuffer();
        actual.addAll(expected);

        assertEquals(expected, actual);
    }

    @SuppressWarnings("boxing")
    @Property
    public void insertRandom(final boolean[] barr, final boolean value, @InRange(min = "0") final int random) {
        final int index = (random % (barr.length + 1));

        Boolean[] boxedBarr = new Boolean[barr.length];
        for (int i = 0; i < barr.length; i++) {
            boxedBarr[i] = barr[i];
        }
        var expected = new ArrayList<>(Arrays.asList(boxedBarr));
        var actual = new BitBuffer();
        actual.addAll(expected);

        actual.add(index, value);
        expected.add(index, value);
        assertEquals(expected, actual);
    }

    @SuppressWarnings("boxing")
    @Property
    public void removeRandom(final boolean[] barr, @InRange(min = "0") final int random) {
        if (barr.length == 0) {
            return;
        }
        final int fromIndex = (random % barr.length);
        final int span = (random % (barr.length - fromIndex));
        final int toIndex = (fromIndex + span);

        Boolean[] boxedBarr = new Boolean[barr.length];
        for (int i = 0; i < barr.length; i++) {
            boxedBarr[i] = barr[i];
        }

        var expected = new ArrayList<>(Arrays.asList(boxedBarr));
        var actual = new BitBuffer();
        actual.addAll(expected);

        actual.subList(fromIndex, toIndex)
              .clear();
        expected.subList(fromIndex, toIndex)
                .clear();
        assertEquals(expected, actual);
    }

    @SuppressWarnings("boxing")
    @Property
    public void copyRandom(final boolean[] barr, final int seed) {
        if (barr.length == 0) {
            return;
        }
        var random = new Random(seed);
        final int srcPos = random.nextInt(barr.length);
        final int destPos = random.nextInt(barr.length);
        final int length = random.nextInt((barr.length - Math.max(srcPos, destPos)));

        Boolean[] boxedBarr = new Boolean[barr.length];
        for (int i = 0; i < barr.length; i++) {
            boxedBarr[i] = barr[i];
        }

        var actual = new BitBuffer();
        actual.addAll(Arrays.asList(boxedBarr));

        actual.copyRange(srcPos, destPos, length);
        System.arraycopy(barr, srcPos, barr, destPos, length);
        boxedBarr = new Boolean[barr.length];
        for (int i = 0; i < barr.length; i++) {
            boxedBarr[i] = barr[i];
        }
        var expected = Arrays.asList(boxedBarr);

        assertEquals(expected, actual);
    }

    @SuppressWarnings("boxing")
    @Property
    public void nextSetBit(final boolean[] barr) {
        Boolean[] boxedBarr = new Boolean[barr.length];
        for (int i = 0; i < barr.length; i++) {
            boxedBarr[i] = barr[i];
        }

        var actual = new BitBuffer();
        actual.addAll(Arrays.asList(boxedBarr));

        final List<Integer> setBits = new ArrayList<>();
        for (int i = 0; i < barr.length; i++) {
            if (barr[i]) {
                setBits.add(i);
            }
        }

        final List<Integer> next = new ArrayList<>();
        for (long i = 0; i < actual.size(); i++) {
            final long j = actual.nextSetBit(i);
            if ((j != -1)) {
                next.add((int) j);
                i = j;
            }
        }

        assertEquals(setBits, next);
    }

    @SuppressWarnings("boxing")
    @Property
    public void nextUnsetBit(final boolean[] barr) {
        Boolean[] boxedBarr = new Boolean[barr.length];
        for (int i = 0; i < barr.length; i++) {
            boxedBarr[i] = barr[i];
        }

        var actual = new BitBuffer();
        actual.addAll(Arrays.asList(boxedBarr));

        final List<Integer> unsetBits = new ArrayList<>();
        for (int i = 0; i < barr.length; i++) {
            if (!barr[i]) {
                unsetBits.add(i);
            }
        }

        final List<Integer> next = new ArrayList<>();
        for (long i = 0; i < actual.size(); i++) {
            final long j = actual.nextUnsetBit(i);
            if ((j != -1)) {
                next.add((int) j);
                i = j;
            }
        }

        assertEquals(unsetBits, next);
    }
}
