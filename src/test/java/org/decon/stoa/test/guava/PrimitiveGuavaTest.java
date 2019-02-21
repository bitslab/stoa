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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.decon.stoa.Stoa;
import org.decon.stoa.util.StoaBuilder;
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
public class PrimitiveGuavaTest {

    @SuppressWarnings({ "rawtypes", "unchecked", "boxing" })
    @Parameters(name = "{0}")
    public static Iterable<AbstractTester<?>> data() {
        TestSuite suite = new TestSuite(PrimitiveGuavaTest.class.getName());
        List<Generator> gens = new ArrayList<PrimitiveGuavaTest.Generator>();
        gens.add(new Generator(byte.class, Byte.class,
                new SampleElements<>((byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4)));
        gens.add(new Generator(char.class, Character.class, new SampleElements<>('a', 'b', 'c', 'd', 'e')));
        gens.add(new Generator(short.class, Short.class,
                new SampleElements<>((short) 0, (short) 1, (short) 2, (short) 3, (short) 4)));
        gens.add(new Generator(int.class, Integer.class, new SampleElements<>(0, 1, 2, 3, 4)));
        gens.add(new Generator(float.class, Float.class, new SampleElements<>(0f, 1f, 2f, 3f, 4f)));
        gens.add(new Generator(double.class, Double.class, new SampleElements<>(0d, 1d, 2d, 3d, 4d)));
        gens.add(new Generator(long.class, Long.class, new SampleElements<>(0L, 1L, 2L, 3L, 4L)));

        for (Generator gen : gens) {
            suite.addTest(ListTestSuiteBuilder.using(gen)
                                              .named("PrimitiveGuavaTest")
                                              .withFeatures(ListFeature.GENERAL_PURPOSE,
                                                      CollectionFeature.ALLOWS_NULL_QUERIES,
                                                      CollectionFeature.KNOWN_ORDER,
                                                      CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                                                      CollectionSize.ANY)
                                              .createTestSuite());
        }
        return TesterUtil.flatten(suite);
    }

    private final AbstractTester<?> test;

    public PrimitiveGuavaTest(AbstractTester<?> test) {
        this.test = test;
    }

    @Test
    public void test() {
        TesterUtil.runTest(test);
    }

    public static class Generator<E, Ejava> implements TestListGenerator<Ejava> {
        @SuppressWarnings("unused")
        private final Class<E>              primitiveClass;
        private final Supplier<Stoa<Ejava>> factory;
        private final Class<Ejava>          boxedClass;
        private final SampleElements<Ejava> samples;

        @SuppressWarnings("unchecked")
        private Generator(Class<E> primitiveClass, Class<Ejava> boxedClass, SampleElements<Ejava> samples) {
            super();
            this.primitiveClass = primitiveClass;
            this.factory = (Supplier<Stoa<Ejava>>) (Object) StoaBuilder.listOfPrimitive(primitiveClass);
            this.boxedClass = boxedClass;
            this.samples = samples;
        }

        @Override
        public SampleElements<Ejava> samples() {
            return samples;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Ejava[] createArray(int length) {
            return (Ejava[]) Array.newInstance(boxedClass, length);
        }

        @Override
        public Iterable<Ejava> order(List<Ejava> insertionOrder) {
            return insertionOrder;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Ejava> create(Object... elements) {
            Stoa<Ejava> stoa = factory.get();
            Arrays.stream(elements)
                  .forEach((e) -> stoa.add((Ejava) e));
            return stoa;
        }
    }
}
