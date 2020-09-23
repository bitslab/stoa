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
package org.decon.stoa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.decon.stoa.Stoa;
import org.decon.stoa.test.data.Dummy2;
import org.decon.stoa.test.data.TestEnum;
import org.decon.stoa.util.StoaBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;

@RunWith(Runner.class)
public class PropTest {
    @SuppressWarnings("unchecked")
    @Property
    public void addAll(Dummy2[] beans) {
        var expected = new ArrayList<Dummy2>();
        var actual = createList();
        var step = Step.proxy(List.class, expected, actual);
        var props = Arrays.asList(beans)
                          .stream()
                          .map(PropTest::bean2prop)
                          .collect(Collectors.toList());
        step.addAll(props);

        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Property
    public void addRandom(Dummy2[] beans, int seed) {
        var expected = new ArrayList<Dummy2>();
        var actual = createList();
        var step = Step.proxy(List.class, expected, actual);
        assertEquals(expected, actual);
        var random = new Random(seed);

        var props = Arrays.asList(beans)
                          .stream()
                          .map(PropTest::bean2prop)
                          .collect(Collectors.toList());
        for (var p : props) {
            int index = random.nextInt(expected.size() + 1); // index can be 1 beyond end of list
            step.add(index, p);
        }
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Property
    public void removeRandom(Dummy2[] beans, int seed) {
        var expected = new ArrayList<Dummy2>();
        var actual = createList();
        var step = Step.proxy(List.class, expected, actual);
        var props = Arrays.asList(beans)
                          .stream()
                          .map(PropTest::bean2prop)
                          .collect(Collectors.toList());
        step.addAll(props);
        var random = new Random(seed);

        var toRemove = IntStream.range(0, random.nextInt(beans.length + 1))
                                .map((it) -> random.nextInt(beans.length - it))
                                .toArray();
        for (var i : toRemove) {
            step.remove(i);
        }

        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ClassCastException.class)
    public void addWrongClass() {
        List<String> l = (List<String>) (Object) createList();
        l.add("wrongClass");
    }

    @SuppressWarnings("unchecked")
    @Property
    public void setRandom(Dummy2[] target, Dummy2[] src, int seed) {
        if (target.length == 0) {
            return;
        }
        var expected = new ArrayList<Dummy2>();
        var actual = createList();
        var step = Step.proxy(List.class, expected, actual);
        var props = Arrays.asList(target)
                          .stream()
                          .map(PropTest::bean2prop)
                          .collect(Collectors.toList());
        step.addAll(props);
        var random = new Random(seed);

        var srcProps = Arrays.asList(src)
                             .stream()
                             .map(PropTest::bean2prop)
                             .collect(Collectors.toList());
        for (var p : srcProps) {
            int index = random.nextInt(target.length);
            step.set(index, p);
        }
        assertEquals(expected, actual);
    }

    @Property
    public void nullifyRandom(Dummy2[] beans, int seed) {
        if (beans.length == 0) {
            return;
        }
        var actual = createList();
        var props = Arrays.asList(beans)
                          .stream()
                          .map(PropTest::bean2prop)
                          .collect(Collectors.toList());
        actual.addAll(props);
        var random = new Random(seed);

        var toNullify = random.ints(0, beans.length)
                              .limit(random.nextInt(beans.length + 1))
                              .toArray();

        for (int i : toNullify) {
            actual.nullifyRange(i, i + 1);
        }

        for (int i : toNullify) {
            assertNull(actual.get(i)
                             .get("str1"));
        }
    }

    @Test
    public void checkInlineAndInternalArray() {
        var isBool = CoreMatchers.instanceOf(long[].class);
        var isByte = CoreMatchers.instanceOf(byte[].class);
        var isChar = CoreMatchers.instanceOf(char[].class);
        var isShort = CoreMatchers.instanceOf(short[].class);
        var isInt = CoreMatchers.instanceOf(int[].class);
        var isFloat = CoreMatchers.instanceOf(float[].class);
        var isDouble = CoreMatchers.instanceOf(double[].class);
        var isLong = CoreMatchers.instanceOf(long[].class);
        var isObject = CoreMatchers.instanceOf(Object[].class);
        var l = createList();
        var dummy2props = Map.of("str1", isObject, "bool1", isBool, "byte1", isByte, "char1", isChar, "short1", isShort,
                "int1", isInt, "float1", isFloat, "double1", isDouble, "long1", isLong);

        dummy2props.forEach((prop, matcher) -> assertThat(l.data(prop), matcher));
    }

    @Property
    public void clearSize(Dummy2[] beans) {
        var l = createList();
        var props = Arrays.asList(beans)
                          .stream()
                          .map(PropTest::bean2prop)
                          .collect(Collectors.toList());

        l.addAll(props);
        assertEquals(beans.length, l.size());
        l.clear();
        assertEquals(l.size(), 0);
    }

    static Supplier<Stoa<Map<String, Object>>> factory = StoaBuilder.list(Map.of("str1", String.class, "int1",
            int.class, "bool1", boolean.class, "double1", double.class, "testEnum1", TestEnum.class, "byte1",
            byte.class, "char1", char.class, "short1", short.class, "float1", float.class, "long1", long.class));

    public static Stoa<Map<String, Object>> createList() {
        return factory.get();
    }

    @SuppressWarnings("boxing")
    public static Map<String, Object> bean2prop(Dummy2 it) {
        return Map.of("str1", it.getStr1(), "int1", it.getInt1(), "bool1", it.isBool1(), "double1", it.getDouble1(),
                "testEnum1", it.getTestEnum1(), "byte1", it.getByte1(), "char1", it.getChar1(), "short1",
                it.getShort1(), "float1", it.getFloat1(), "long1", it.getLong1());
    }
}
