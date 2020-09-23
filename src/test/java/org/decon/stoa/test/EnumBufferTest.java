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
import java.util.Random;

import org.decon.stoa.test.data.TestEnum;
import org.decon.stoa.util.EnumBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;

@RunWith(Runner.class)
public class EnumBufferTest {
    @Test
    public void readWriteSingle() {
        var eb = EnumBufferTest.createList();
        eb.resize(1);
        eb.set(0, TestEnum.enum4);
        assertEquals(eb.readEnum(0), TestEnum.enum4);
    }

    @Property
    public void readWriteRandom(final TestEnum[] arr) {
        var actual = EnumBufferTest.createList();
        var expected = Arrays.asList(arr);
        actual.addAll(expected);

        assertEquals(expected, actual);
    }

    @Property
    public void insertRandom(final TestEnum[] arr, final TestEnum value, @InRange(min = "0") final int random) {
        final int index = random % (arr.length + 1);

        var actual = EnumBufferTest.createList();
        var expected = new ArrayList<>(Arrays.asList(arr));
        actual.addAll(expected);

        actual.ensureCapacity(arr.length + 1);
        actual.add(index, value);
        expected.add(index, value);
        assertEquals(expected, actual);
    }

    @Property
    public void removeRandom(final TestEnum[] arr, @InRange(min = "0") final int random) {
        if (arr.length == 0) {
            return;
        }
        final int fromIndex = (random % arr.length);
        final int span = (random % (arr.length - fromIndex));
        final int toIndex = (fromIndex + span);

        var actual = EnumBufferTest.createList();
        var expected = new ArrayList<>(Arrays.asList(arr));
        actual.addAll(expected);

        actual.subList(fromIndex, toIndex)
              .clear();
        expected.subList(fromIndex, toIndex)
                .clear();
        assertEquals(expected, actual);
    }

    @Property
    public void copyRandom(final TestEnum[] arr, final int seed) {
        if (arr.length == 0) {
            return;
        }
        var random = new Random(seed);
        final int srcPos = random.nextInt(arr.length);
        final int destPos = random.nextInt(arr.length);
        final int length = random.nextInt((arr.length - Math.max(srcPos, destPos)));

        var actual = EnumBufferTest.createList();
        actual.addAll(Arrays.asList(arr));

        actual.copyRange(srcPos, destPos, length);
        System.arraycopy(arr, srcPos, arr, destPos, length);
        var expected = Arrays.asList(arr);
        assertEquals(expected, actual);
    }

    private static final TestEnum[] constants = TestEnum.class.getEnumConstants();

    public static EnumBuffer<TestEnum> createList() {
        return new EnumBuffer<>(EnumBufferTest.constants);
    }
}
