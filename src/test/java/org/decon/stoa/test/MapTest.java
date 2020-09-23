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
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import org.decon.stoa.StoaMap;
import org.decon.stoa.test.data.Dummy2;
import org.decon.stoa.test.data.Dummy4;
import org.decon.stoa.util.StoaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;

@RunWith(Runner.class)
public class MapTest {
    @SuppressWarnings("unchecked")
    @Property
    public void addRandomValue(Dummy4[] keys, Dummy2[] values, int seed) {
        if (keys.length == 0 || values.length == 0) {
            return;
        }
        var expected = new HashMap<Dummy4, Dummy2>();
        var actual = createMap();
        var step = Step.proxy(Map.class, expected, actual);
        var random = new Random(seed);
        assertEquals(expected, actual);

        Arrays.asList(keys)
              .forEach((key) -> {
                  var value = values[random.nextInt(values.length)];
                  step.put(key, value);
              });
        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
        actual.clear();
        assertNotEquals(expected, actual);
        assertNotEquals(expected.size(), actual.size());
        expected.clear();
        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
    }

    @SuppressWarnings("unchecked")
    @Property
    public void addRandomKey(Dummy4[] keys, Dummy2[] values, int seed) {
        if (keys.length == 0 || values.length == 0) {
            return;
        }
        var expected = new HashMap<Dummy4, Dummy2>();
        var actual = createMap();
        var step = Step.proxy(Map.class, expected, actual);
        var random = new Random(seed);
        assertEquals(expected, actual);

        Arrays.asList(values)
              .forEach((value) -> {
                  var key = keys[random.nextInt(keys.length)];
                  step.put(key, value);
              });
        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
        actual.clear();
        assertNotEquals(expected, actual);
        assertNotEquals(expected.size(), actual.size());
        expected.clear();
        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
    }

    @SuppressWarnings("unchecked")
    @Property
    public void overwriteRandomValue(Dummy4[] keys, Dummy2[] values, int seed) {
        if (keys.length == 0 || values.length == 0) {
            return;
        }
        var expected = new HashMap<Dummy4, Dummy2>();
        var actual = createMap();
        var step = Step.proxy(Map.class, expected, actual);
        var random = new Random(seed);

        Arrays.asList(keys)
              .forEach((key) -> {
                  var value = values[random.nextInt(values.length)];
                  step.put(key, value);
              });
        Arrays.asList(keys)
              .forEach((key) -> {
                  var value = values[random.nextInt(values.length)];
                  step.put(key, value);
              });
        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
        actual.clear();
        assertNotEquals(expected, actual);
        assertNotEquals(expected.size(), actual.size());
        expected.clear();
        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
    }

    @SuppressWarnings("unchecked")
    @Property
    public void removeRandom(Dummy4[] keys, Dummy2[] values, int seed) {
        if (keys.length == 0 || values.length == 0) {
            return;
        }
        var expected = new HashMap<Dummy4, Dummy2>();
        var actual = createMap();
        var step = Step.proxy(Map.class, expected, actual);
        var random = new Random(seed);

        Arrays.asList(keys)
              .forEach((key) -> {
                  var value = values[random.nextInt(values.length)];
                  step.put(key, value);
              });

        for (int i = 1; i < keys.length / 2; i++) {
            var key = keys[random.nextInt(keys.length)];
            step.remove(key);
        }
        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ClassCastException.class)
    public void addWrongClassKey() {
        StoaMap<Object, Object> map = (StoaMap<Object, Object>) (Object) createMap();
        var wrongClass = "";
        var value = new Dummy4();
        map.put(wrongClass, value);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ClassCastException.class)
    public void addWrongClassValue() {
        StoaMap<Object, Object> map = (StoaMap<Object, Object>) (Object) createMap();
        var wrongClass = "";
        var key = new Dummy2();
        map.put(key, wrongClass);
    }

    static final Supplier<StoaMap<Dummy4, Dummy2>> factory = StoaBuilder.map(
            StoaBuilder.list(StoaBuilder.beanInfo(Dummy4.class)), StoaBuilder.list(StoaBuilder.beanInfo(Dummy2.class)));

    public static StoaMap<Dummy4, Dummy2> createMap() {
        return factory.get();
    }

}
