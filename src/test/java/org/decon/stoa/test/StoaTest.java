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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.decon.stoa.Stoa;
import org.decon.stoa.test.data.Dummy1;
import org.decon.stoa.test.data.Dummy2;
import org.decon.stoa.test.data.Dummy22;
import org.decon.stoa.test.data.Dummy3;
import org.decon.stoa.test.data.Dummy4;
import org.decon.stoa.util.StoaBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;

@RunWith(Runner.class)
public class StoaTest {
    @SuppressWarnings("unchecked")
    @Property
    public void addAll(Dummy1[] beans) {
        var expected = new ArrayList<Dummy1>();
        var actual = createList();
        var step = Step.proxy(List.class, expected, actual);
        step.addAll(Arrays.asList(beans));

        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Property
    public void addRandom(Dummy1[] beans, int seed) {
        var expected = new ArrayList<Dummy1>();
        var actual = createList();
        var step = Step.proxy(List.class, expected, actual);
        var random = new Random(seed);

        for (var b : beans) {
            int index = random.nextInt(expected.size() + 1); // index can be 1 beyond end of list
            step.add(index, b);
        }
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Property
    public void removeRandom(Dummy1[] beans, int seed) {
        var expected = new ArrayList<Dummy1>();
        var actual = createList();
        var step = Step.proxy(List.class, expected, actual);
        var random = new Random(seed);
        step.addAll(Arrays.asList(beans));

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
    public void setRandom(Dummy1[] target, Dummy1[] src, int seed) {
        if (target.length == 0) {
            return;
        }
        var expected = new ArrayList<Dummy1>();
        var actual = createList();
        var step = Step.proxy(List.class, expected, actual);
        var random = new Random(seed);
        step.addAll(Arrays.asList(target));

        for (var b : src) {
            int index = random.nextInt(target.length);
            step.set(index, b);
        }
        assertEquals(expected, actual);
    }

    @Property
    public void nullifyRandom(Dummy1[] beans, int seed) {
        if (beans.length == 0) {
            return;
        }
        var actual = createList();
        var random = new Random(seed);
        actual.addAll(Arrays.asList(beans));

        var toNullify = random.ints(0, beans.length)
                              .limit(random.nextInt(beans.length + 1))
                              .toArray();

        for (int i : toNullify) {
            actual.nullifyRange(i, i + 1);
        }

        for (int i : toNullify) {
            var it = actual.get(i);

            assertNull(it.getStr1());
            assertNull(it.getNotInline());
            assertNull(it.getInline()
                         .getStr1());
            assertNull(it.getInline()
                         .getStr2());
            assertNull(it.getDerivedInline()
                         .getStr1());
            assertNull(it.getDerivedInline()
                         .getStr2());
            assertNull(it.getNestedInline()
                         .getStr1());
            assertNull(it.getNestedInline()
                         .getStr2());
            assertNull(it.getNestedInline()
                         .getInline()
                         .getStr1());
            assertNull(it.getNestedInline()
                         .getInline()
                         .getStr2());
            assertNull(it.getNotNestedInline()
                         .getStr1());
            assertNull(it.getNotNestedInline()
                         .getStr2());
            assertNull(it.getNotNestedInline()
                         .getInline());
        }

    }

    @Property
    public void exceedSizeBoundAdd(Dummy1 bean1, Dummy1 bean2, Dummy1 bean3) {
        var l = createList();
        l.setSizeBound(2);

        l.add(bean1);
        l.add(bean2);
        try {
            l.add(bean3);
            fail("sizeBound = " + l.getSizeBound() + ", added = " + l.size());
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage()
                        .indexOf("sizeBound") != -1);
        }
    }

    @Property
    public void exceedSizeBoundSet(Dummy1 bean1, Dummy1 bean2, Dummy1 bean3) {
        var l = createList();

        l.add(bean1);
        l.add(bean2);
        l.add(bean3);
        try {
            l.setSizeBound(2);
            fail("sizeBound = " + l.getSizeBound() + ", size = " + l.size());
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage()
                        .indexOf("sizeBound") != -1);
        }
    }

    @Test
    public void sizeBoundEnsureCapacity() {
        var l = createList();

        l.setSizeBound(10);
        l.ensureCapacity(10);
        assertTrue("10 <= l.capacity", 10 <= l.getCapacity());
        try {
            l.ensureCapacity(11);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage()
                        .indexOf("sizeBound") != -1);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
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
        var isEnum = CoreMatchers.instanceOf(long[].class);
        var isObject = CoreMatchers.instanceOf(Object[].class);
        var isInline = CoreMatchers.instanceOf(Stoa.class);

        var l = createList();

        var dummy1props = new HashMap<String, Matcher>();

        dummy1props.putAll(Map.of("str1", isObject, "bool1", isBool, "int1", isInt, "double1", isDouble, "testEnum",
                isEnum, "notInline", isObject, "inline", isInline, "derivedInline", isInline, "partialDerivedInline",
                isInline, "nestedInline", isInline));
        dummy1props.putAll(Map.of("nestedInline.inline", isInline, "notNestedInline", isInline));

        dummy1props.forEach((prop, matcher) -> assertThat(l.data(prop), matcher));

        assertThat(l.data("nestedInline"), isInline);

        var inlineBean3 = (Stoa<Object>) l.data("nestedInline");
        assertThat(inlineBean3.data("inline"), isInline);

        var notNestedBean3 = (Stoa<Object>) l.data("notNestedInline");
        assertThat(notNestedBean3.data("inline"), isObject);

        Stoa<Dummy2> l2 = (Stoa<Dummy2>) l.data("inline");

        var dummy2props = new HashMap<String, Matcher>();

        dummy2props.putAll(Map.of("str1", isObject, "bool1", isBool, "byte1", isByte, "char1", isChar, "short1",
                isShort, "int1", isInt, "float1", isFloat, "double1", isDouble, "long1", isLong, "testEnum1", isEnum));
        dummy2props.putAll(
                Map.of("str2", isObject, "bool2", isBool, "double2", isDouble, "int2", isInt, "testEnum2", isEnum));

        dummy2props.forEach((prop, matcher) -> assertThat(l2.data(prop), matcher));
    }

    @Property
    public void clearSize(Dummy1[] beans) {
        var l = createList();
        l.addAll(Arrays.asList(beans));
        assertEquals(beans.length, l.size());
        l.clear();
        assertEquals(l.size(), 0);
    }

    static BeanInfo dummy1  = StoaBuilder.beanInfo(Dummy1.class);
    static BeanInfo dummy2  = StoaBuilder.beanInfo(Dummy2.class);
    static BeanInfo dummy22 = StoaBuilder.beanInfo(Dummy22.class);

    static BeanInfo partialDummy22;

    static {
        try {
            partialDummy22 = Introspector.getBeanInfo(Dummy22.class, Dummy2.class);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
    static BeanInfo dummy3 = StoaBuilder.beanInfo(Dummy3.class);
    static BeanInfo dummy4 = StoaBuilder.beanInfo(Dummy4.class);

    static Map<String, BeanInfo>  inlineBeans = Map.of("inline", dummy2, "derivedInline", dummy22,
            "partialDerivedInline", partialDummy22, "nestedInline", dummy3, "nestedInline.inline", dummy4,
            "notNestedInline", dummy3);
    static Supplier<Stoa<Dummy1>> factory     = StoaBuilder.list(dummy1, inlineBeans);

    public static Stoa<Dummy1> createList() {
        return factory.get();
    }

}
