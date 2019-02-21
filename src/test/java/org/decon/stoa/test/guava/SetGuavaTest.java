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
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.decon.stoa.test.SetTest;
import org.decon.stoa.test.data.Dummy4;
import org.decon.stoa.test.generator.Utils;
import org.decon.stoa.util.SetAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.testing.AbstractTester;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestSetGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.SetFeature;
import com.pholser.junit.quickcheck.internal.GeometricDistribution;
import com.pholser.junit.quickcheck.internal.generator.GeneratorRepository;
import com.pholser.junit.quickcheck.internal.generator.SimpleGenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import junit.framework.TestSuite;

@RunWith(Parameterized.class)
public class SetGuavaTest {

    @Parameters(name = "{0}")
    public static Iterable<AbstractTester<?>> data() {
        TestSuite suite = new TestSuite(SetGuavaTest.class.getName());
        suite.addTest(SetTestSuiteBuilder.using(new Generator())
                                         .named("SetGuavaTest")
                                         .withFeatures(SetFeature.GENERAL_PURPOSE,
                                                 CollectionFeature.ALLOWS_NULL_QUERIES, CollectionSize.ANY)
                                         .createTestSuite());
        return TesterUtil.flatten(suite);
    }

    private final AbstractTester<?> test;

    public SetGuavaTest(AbstractTester<?> test) {
        this.test = test;
    }

    @Test
    public void test() {
        TesterUtil.runTest(test);
    }

    public static class Generator implements TestSetGenerator<Dummy4> {
        private final SampleElements<Dummy4> samples;

        private Generator() {
            GeneratorRepository genRepo = Utils.genRepo.get();
            Dummy4[] arr = createArray(5);
            Random random = new Random();
            SourceOfRandomness src = new SourceOfRandomness(random);
            for (int i = 0; i < arr.length; i++) {
                arr[i] = genRepo.type(Dummy4.class)
                                .generate(src, new SimpleGenerationStatus(new GeometricDistribution(), src, 0));
                arr[i].setInt1(arr[0].getInt1() + i); // Ensure Uniqueness of these instances.
            }
            samples = new SampleElements<Dummy4>(arr[0], arr[1], arr[2], arr[3], arr[4]);
        }

        @Override
        public SampleElements<Dummy4> samples() {
            return samples;
        }

        @Override
        public Dummy4[] createArray(int length) {
            return new Dummy4[length];
        }

        @Override
        public Iterable<Dummy4> order(List<Dummy4> insertionOrder) {
            return insertionOrder;
        }

        @Override
        public Set<Dummy4> create(Object... elements) {
            SetAdapter<Dummy4> set = SetTest.createSet();
            Arrays.stream(elements)
                  .forEach((e) -> set.add((Dummy4) e));
            return set;
        }
    }

}
