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
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.decon.stoa.test.data.Dummy2;
import org.decon.stoa.test.data.Dummy4;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;

@RunWith(Runner.class)
public class MapIterTest {

    @SuppressWarnings("unchecked")
    @Property
    public void addRandom(final Dummy4[] keys, final Dummy2[] values, final int seed) {
        if (keys.length == 0 || values.length == 0) {
            return;
        }
        var expected = new HashMap<Dummy4, Dummy2>();
        var actual = MapTest.createMap();
        var step = Step.proxy(Map.class, expected, actual);
        var random = new Random(seed);
        assertEquals(expected, actual);

        Arrays.asList(keys)
              .forEach((key) -> {
                  var value = values[random.nextInt(values.length)];
                  step.put(key, value);
              });

        assertEquals(expected.entrySet(), actual.entrySet());
        assertEquals(expected.entrySet()
                             .size(),
                actual.entrySet()
                      .size());

        actual.entrySet()
              .clear();
        assertNotEquals(expected.entrySet(), actual.entrySet());
        assertNotEquals(expected.entrySet()
                                .size(),
                actual.entrySet()
                      .size());

        expected.clear();
        assertEquals(expected.entrySet(), actual.entrySet());
        assertEquals(expected.entrySet()
                             .size(),
                actual.entrySet()
                      .size());
    }

    @SuppressWarnings("unchecked")
    @Property
    public void overwriteRandom(final Dummy4[] keys, final Dummy2[] values, final int seed) {
        if (keys.length == 0 || values.length == 0) {
            return;
        }
        var expected = new HashMap<Dummy4, Dummy2>();
        var actual = MapTest.createMap();
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
        assertEquals(expected.entrySet(), actual.entrySet());
        assertEquals(expected.entrySet()
                             .size(),
                actual.entrySet()
                      .size());

        actual.entrySet()
              .clear();
        assertNotEquals(expected.entrySet(), actual.entrySet());
        assertNotEquals(expected.entrySet()
                                .size(),
                actual.entrySet()
                      .size());

        expected.clear();
        assertEquals(expected.entrySet(), actual.entrySet());
        assertEquals(expected.entrySet()
                             .size(),
                actual.entrySet()
                      .size());
    }

    @SuppressWarnings("unchecked")
    @Property
    public void removeRandom(final Dummy4[] keys, final Dummy2[] values, final int seed) {
        if (keys.length == 0 || values.length == 0) {
            return;
        }
        var expected = new HashMap<Dummy4, Dummy2>();
        var actual = MapTest.createMap();
        var step = Step.proxy(Map.class, expected, actual);
        var random = new Random(seed);

        Arrays.asList(keys)
              .forEach((key) -> {
                  var value = values[random.nextInt(values.length)];
                  step.put(key, value);
              });

        var iter = actual.entrySet()
                         .iterator();
        while (iter.hasNext()) {
            var entry = iter.next();
            if (random.nextBoolean()) {
                expected.remove(entry.getKey());
                iter.remove();
            }
        }
        assertEquals(expected.entrySet(), actual.entrySet());
        assertEquals(expected.entrySet()
                             .size(),
                actual.entrySet()
                      .size());
    }
}
