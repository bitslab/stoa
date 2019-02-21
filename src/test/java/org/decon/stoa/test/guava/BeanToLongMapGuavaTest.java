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

import org.decon.stoa.Stoa;
import org.decon.stoa.StoaMap;
import org.decon.stoa.test.data.Dummy2;
import org.decon.stoa.test.generator.Utils;
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
import com.pholser.junit.quickcheck.internal.GeometricDistribution;
import com.pholser.junit.quickcheck.internal.generator.GeneratorRepository;
import com.pholser.junit.quickcheck.internal.generator.SimpleGenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import junit.framework.TestSuite;

@RunWith(Parameterized.class)
public class BeanToLongMapGuavaTest {

    @Parameters(name = "{0}")
    public static Iterable<AbstractTester<?>> data() {
        TestSuite suite = new TestSuite(BeanToLongMapGuavaTest.class.getName());
        suite.addTest(MapTestSuiteBuilder.using(new Generator())
                                         .named("BeanToLongMapGuavaTest")
                                         .withFeatures(CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                                                 MapFeature.GENERAL_PURPOSE, MapFeature.ALLOWS_ANY_NULL_QUERIES,
                                                 CollectionSize.ANY)
                                         .createTestSuite());
        return TesterUtil.flatten(suite);
    }

    private final AbstractTester<?> test;

    public BeanToLongMapGuavaTest(AbstractTester<?> test) {
        this.test = test;
    }

    @Test
    public void test() {
        TesterUtil.runTest(test);
    }

    public static class Generator implements TestMapGenerator<Dummy2, Long> {
        private final SampleElements<Map.Entry<Dummy2, Long>> samples;

        private Generator() {
            GeneratorRepository genRepo = Utils.genRepo.get();
            Map.Entry<Dummy2, Long>[] arr = createArray(5);
            SourceOfRandomness src = new SourceOfRandomness(new Random());
            for (int i = 0; i < arr.length; i++) {
                Long value = genRepo.type(long.class)
                                    .generate(src, new SimpleGenerationStatus(new GeometricDistribution(), src, 0));
                Dummy2 key = genRepo.type(Dummy2.class)
                                    .generate(src, new SimpleGenerationStatus(new GeometricDistribution(), src, 0));
                Map.Entry<Dummy2, Long> entry = Collections.singletonMap(key, value)
                                                           .entrySet()
                                                           .iterator()
                                                           .next();
                arr[i] = entry;
                arr[i].getKey()
                      .setInt1(arr[0].getKey()
                                     .getInt1()
                               + 1);
            }
            samples = new SampleElements<Map.Entry<Dummy2, Long>>(arr[0], arr[1], arr[2], arr[3], arr[4]);
        }

        @Override
        public SampleElements<Map.Entry<Dummy2, Long>> samples() {
            return samples;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map.Entry<Dummy2, Long>[] createArray(int length) {
            return new Map.Entry[length];
        }

        @Override
        public Iterable<Map.Entry<Dummy2, Long>> order(List<Map.Entry<Dummy2, Long>> insertionOrder) {
            return insertionOrder;
        }

        private Supplier<Stoa<Dummy2>> keysFactory  = StoaBuilder.list(StoaBuilder.<Dummy2>beanInfo(Dummy2.class));
        private Supplier<Stoa<Long>>   longsFactory = StoaBuilder.listOfPrimitive(long.class);

        private Supplier<StoaMap<Dummy2, Long>> supplier = StoaBuilder.map(keysFactory, longsFactory);

        @Override
        public Map<Dummy2, Long> create(Object... elements) {
            StoaMap<Dummy2, Long> map = supplier.get();
            Arrays.stream(elements)
                  .forEach((e) -> {
                      @SuppressWarnings("unchecked")
                      Map.Entry<Dummy2, Long> entry = (Map.Entry<Dummy2, Long>) e;
                      map.put(entry.getKey(), entry.getValue());
                  });
            return map;
        }

        @Override
        public Dummy2[] createKeyArray(int length) {
            return new Dummy2[length];
        }

        @Override
        public Long[] createValueArray(int length) {
            return new Long[length];
        }

    }

}
