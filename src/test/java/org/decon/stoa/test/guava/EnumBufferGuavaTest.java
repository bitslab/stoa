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

import org.decon.stoa.test.data.TestEnum;
import org.decon.stoa.util.EnumBuffer;
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

import junit.framework.TestSuite;

@RunWith(Parameterized.class)
public class EnumBufferGuavaTest {

    @Parameters(name = "{0}")
    public static Iterable<AbstractTester<?>> data() {
        TestSuite suite = new TestSuite(EnumBufferGuavaTest.class.getName());
        suite.addTest(ListTestSuiteBuilder.using(new Generator())
                                          .named("EnumBufferGuavaTest")
                                          .withFeatures(ListFeature.GENERAL_PURPOSE,
                                                  CollectionFeature.ALLOWS_NULL_QUERIES, CollectionFeature.KNOWN_ORDER,
                                                  CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                                                  CollectionSize.ANY)
                                          .createTestSuite());
        return TesterUtil.flatten(suite);
    }

    private final AbstractTester<?> test;

    public EnumBufferGuavaTest(AbstractTester<?> test) {
        this.test = test;
    }

    @Test
    public void test() {
        TesterUtil.runTest(test);
    }

    public static class Generator implements TestListGenerator<TestEnum> {
        private final SampleElements<TestEnum> samples;

        private Generator() {
            samples = new SampleElements<>(TestEnum.enum2, TestEnum.enum3, TestEnum.enum1, TestEnum.enum5, TestEnum.enum4);
        }

        @Override
        public SampleElements<TestEnum> samples() {
            return samples;
        }

        @Override
        public TestEnum[] createArray(int length) {
            return new TestEnum[length];
        }

        @Override
        public Iterable<TestEnum> order(List<TestEnum> insertionOrder) {
            return insertionOrder;
        }

        @Override
        public List<TestEnum> create(Object... elements) {
            EnumBuffer<TestEnum> buffer = EnumBuffer.wrap(TestEnum.class.getEnumConstants());
            Arrays.stream(elements)
                  .forEach((e) -> buffer.add((TestEnum) e));
            return buffer;
        }
    }
}
