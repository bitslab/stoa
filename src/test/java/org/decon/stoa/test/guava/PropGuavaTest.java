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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.decon.stoa.Stoa;
import org.decon.stoa.test.PropTest;
import org.decon.stoa.test.data.Dummy2;
import org.decon.stoa.test.generator.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.testing.AbstractTester;
import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;
import com.pholser.junit.quickcheck.internal.GeometricDistribution;
import com.pholser.junit.quickcheck.internal.generator.GeneratorRepository;
import com.pholser.junit.quickcheck.internal.generator.SimpleGenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import junit.framework.TestSuite;

@RunWith(Parameterized.class)
public class PropGuavaTest {

    @Parameters(name = "{0}")
    public static Iterable<AbstractTester<?>> data() {
        TestSuite suite = new TestSuite(PropGuavaTest.class.getName());
        suite.addTest(ListTestSuiteBuilder.using(new Generator())
                                          .named("PropGuavaTest")
                                          .withFeatures(ListFeature.GENERAL_PURPOSE,
                                                  CollectionFeature.ALLOWS_NULL_QUERIES, CollectionFeature.KNOWN_ORDER,
                                                  CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                                                  CollectionFeature.NON_STANDARD_TOSTRING, CollectionSize.ANY)
                                          .createTestSuite());
        return TesterUtil.flatten(suite);
    }

    private final AbstractTester<?> test;

    public PropGuavaTest(AbstractTester<?> test) {
        this.test = test;
    }

    @Test
    public void test() {
        TesterUtil.runTest(test);
    }

    public static class Generator implements TestListGenerator<Map<String, Object>> {
        private final SampleElements<Map<String, Object>> samples;

        private Generator() {
            GeneratorRepository genRepo = Utils.genRepo.get();
            Map<String, Object>[] arr = createArray(5);
            Random random = new Random();
            SourceOfRandomness src = new SourceOfRandomness(random);
            for (int i = 0; i < arr.length; i++) {
                arr[i] = PropTest.bean2prop(genRepo.type(Dummy2.class)
                                                   .generate(src, new SimpleGenerationStatus(
                                                           new GeometricDistribution(), src, 0)));
                final int i_final = i;
                Map<String, Object> unique = new HashMap<>(arr[i]);
                unique.compute("str1", (k, v) -> v.toString() + i_final); // Ensure Uniqueness of these instances.
                arr[i] = unique;

            }
            samples = new SampleElements<Map<String, Object>>(arr[0], arr[1], arr[2], arr[3], arr[4]);
        }

        @Override
        public SampleElements<Map<String, Object>> samples() {
            return samples;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map<String, Object>[] createArray(int length) {
            return new Map[length];
        }

        @Override
        public Iterable<Map<String, Object>> order(List<Map<String, Object>> insertionOrder) {
            return insertionOrder;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Map<String, Object>> create(Object... elements) {
            Stoa<Map<String, Object>> stoa = PropTest.createList();
            Arrays.stream(elements)
                  .forEach((e) -> stoa.add((Map<String, Object>) e));
            return stoa;
        }
    }

}
