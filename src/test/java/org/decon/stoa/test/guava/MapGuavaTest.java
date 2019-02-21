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

import org.decon.stoa.StoaMap;
import org.decon.stoa.test.MapTest;
import org.decon.stoa.test.data.Dummy2;
import org.decon.stoa.test.data.Dummy4;
import org.decon.stoa.test.generator.Utils;
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
public class MapGuavaTest {

    @Parameters(name = "{0}")
    public static Iterable<AbstractTester<?>> data() {
        TestSuite suite = new TestSuite(MapGuavaTest.class.getName());
        suite.addTest(MapTestSuiteBuilder.using(new Generator())
                                         .named("MapGuavaTest")
                                         .withFeatures(CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                                                 MapFeature.GENERAL_PURPOSE, MapFeature.ALLOWS_ANY_NULL_QUERIES,
                                                 CollectionSize.ANY)
                                         .createTestSuite());
        return TesterUtil.flatten(suite);
    }

    private final AbstractTester<?> test;

    public MapGuavaTest(AbstractTester<?> test) {
        this.test = test;
    }

    @Test
    public void test() {
        TesterUtil.runTest(test);
    }

    public static class Generator implements TestMapGenerator<Dummy4, Dummy2> {
        private final SampleElements<Map.Entry<Dummy4, Dummy2>> samples;

        private Generator() {
            GeneratorRepository genRepo = Utils.genRepo.get();
            Map.Entry<Dummy4, Dummy2>[] arr = createArray(5);
            SourceOfRandomness src = new SourceOfRandomness(new Random());
            for (int i = 0; i < arr.length; i++) {
                Dummy4 key = genRepo.type(Dummy4.class)
                                    .generate(src, new SimpleGenerationStatus(new GeometricDistribution(), src, 0));
                Dummy2 value = genRepo.type(Dummy2.class)
                                      .generate(src, new SimpleGenerationStatus(new GeometricDistribution(), src, 0));
                Map.Entry<Dummy4, Dummy2> entry = Collections.singletonMap(key, value)
                                                             .entrySet()
                                                             .iterator()
                                                             .next();
                arr[i] = entry;
                entry.getKey()
                     .setInt1(arr[0].getKey()
                                    .getInt1()
                              + i); // Ensure Uniqueness of keys.
            }
            samples = new SampleElements<Map.Entry<Dummy4, Dummy2>>(arr[0], arr[1], arr[2], arr[3], arr[4]);
        }

        @Override
        public SampleElements<Map.Entry<Dummy4, Dummy2>> samples() {
            return samples;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map.Entry<Dummy4, Dummy2>[] createArray(int length) {
            return new Map.Entry[length];
        }

        @Override
        public Iterable<Map.Entry<Dummy4, Dummy2>> order(List<Map.Entry<Dummy4, Dummy2>> insertionOrder) {
            return insertionOrder;
        }

        @Override
        public Map<Dummy4, Dummy2> create(Object... elements) {
            StoaMap<Dummy4, Dummy2> map = MapTest.createMap();
            Arrays.stream(elements)
                  .forEach((e) -> {
                      @SuppressWarnings("unchecked")
                      Map.Entry<Dummy4, Dummy2> entry = (Map.Entry<Dummy4, Dummy2>) e;
                      map.put(entry.getKey(), entry.getValue());
                  });
            return map;
        }

        @Override
        public Dummy4[] createKeyArray(int length) {
            return new Dummy4[length];
        }

        @Override
        public Dummy2[] createValueArray(int length) {
            return new Dummy2[length];
        }

    }

}
