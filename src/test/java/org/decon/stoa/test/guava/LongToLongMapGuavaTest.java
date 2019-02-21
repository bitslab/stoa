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
package org.decon.stoa.test.guava;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import org.decon.stoa.StoaMap;
import org.decon.stoa.util.StoaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.testing.AbstractTester;
import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;

import junit.framework.TestSuite;

@RunWith(Parameterized.class)
public class LongToLongMapGuavaTest {

    @Parameters(name = "{0}")
    public static Iterable<AbstractTester<?>> data() {
        TestSuite suite = new TestSuite(LongToLongMapGuavaTest.class.getName());
        suite.addTest(MapTestSuiteBuilder.using(new Generator())
                                         .named("LongToLongMapGuavaTest")
                                         .withFeatures(CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                                                 MapFeature.GENERAL_PURPOSE, MapFeature.ALLOWS_ANY_NULL_QUERIES,
                                                 CollectionSize.ANY)
                                         .createTestSuite());
        return TesterUtil.flatten(suite);
    }

    private final AbstractTester<?> test;

    public LongToLongMapGuavaTest(AbstractTester<?> test) {
        this.test = test;
    }

    @Test
    public void test() {
        TesterUtil.runTest(test);
    }

    public static class Generator implements TestMapGenerator<Long, Long> {
        private final SampleElements<Map.Entry<Long, Long>> samples;

        @SuppressWarnings("boxing")
        private Generator() {
            Map.Entry<Long, Long>[] arr = createArray(5);
            Random random = new Random();
            long[] keys = random.longs()
                                .distinct()
                                .limit(5)
                                .toArray();
            long[] values = random.longs()
                                  .limit(5)
                                  .toArray();
            for (int i = 0; i < arr.length; i++) {
                Long key = keys[i];
                Long value = values[i];
                Map.Entry<Long, Long> entry = Collections.singletonMap(key, value)
                                                         .entrySet()
                                                         .iterator()
                                                         .next();
                arr[i] = entry;
            }
            samples = new SampleElements<Map.Entry<Long, Long>>(arr[0], arr[1], arr[2], arr[3], arr[4]);
        }

        @Override
        public SampleElements<Map.Entry<Long, Long>> samples() {
            return samples;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map.Entry<Long, Long>[] createArray(int length) {
            return new Map.Entry[length];
        }

        @Override
        public Iterable<Map.Entry<Long, Long>> order(List<Map.Entry<Long, Long>> insertionOrder) {
            return insertionOrder;
        }

        private static Supplier<StoaMap<Long, Long>> supplier =
                StoaBuilder.map(StoaBuilder.listOfPrimitive(long.class), StoaBuilder.listOfPrimitive(long.class));

        @Override
        public Map<Long, Long> create(Object... elements) {
            StoaMap<Long, Long> map = supplier.get();
            Arrays.stream(elements)
                  .forEach((e) -> {
                      @SuppressWarnings("unchecked")
                      Map.Entry<Long, Long> entry = (Map.Entry<Long, Long>) e;
                      map.put(entry.getKey(), entry.getValue());
                  });
            return map;
        }

        @Override
        public Long[] createKeyArray(int length) {
            return new Long[length];
        }

        @Override
        public Long[] createValueArray(int length) {
            return new Long[length];
        }

    }

}
