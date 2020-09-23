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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import org.decon.stoa.test.data.Dummy4;
import org.decon.stoa.util.SetAdapter;
import org.decon.stoa.util.StoaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;

@RunWith(Runner.class)
public class SetTest {
    @SuppressWarnings("unchecked")
    @Property
    public void addRandom(Dummy4[] elements, int seed) {
        var expected = new HashSet<Dummy4>();
        var actual = createSet();
        var step = Step.proxy(Set.class, expected, actual);
        var random = new Random(seed);
        assertEquals(expected, actual);

        for (int i = 0; i < elements.length / 2; i++) {
            var index = random.nextInt(elements.length);
            step.add(elements[index]);
        }
        assertEquals(expected, actual);
        step.clear();
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Property
    public void removeRandom(Dummy4[] elements, int seed) {
        var expected = new HashSet<Dummy4>();
        var actual = createSet();
        var step = Step.proxy(Set.class, expected, actual);
        var random = new Random(seed);

        step.addAll(Arrays.asList(elements));

        for (int i = 0; i < elements.length / 2; i++) {
            var index = random.nextInt(elements.length);
            step.remove(elements[index]);
        }

        assertEquals(expected, actual);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test(expected = ClassCastException.class)
    public void addWrongClass() {
        var map = (SetAdapter<Object>) (Object) createSet();
        var wrongClass = new ArrayList();
        map.add(wrongClass);
    }

    static Supplier<SetAdapter<Dummy4>> factory = StoaBuilder.set(StoaBuilder.list(StoaBuilder.beanInfo(Dummy4.class)));

    public static SetAdapter<Dummy4> createSet() {
        return factory.get();
    }

}
