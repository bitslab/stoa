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
package org.decon.stoa.test.generator;

import org.decon.stoa.test.data.Dummy1;
import org.decon.stoa.test.data.Dummy22;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

public class Dummy1Gen extends BeanGen<Dummy1> {
    public Dummy1Gen() {
        super(Dummy1.class);
    }

    @Override
    public Dummy1 generate(SourceOfRandomness random, GenerationStatus status) {
        Dummy1 ret = super.generate(random, status);

        Dummy22 full = ret.getPartialDerivedInline();
        Dummy22 partial = new Dummy22();
        partial.setByte2(full.getByte2());
        partial.setShort2(full.getShort2());
        partial.setFloat2(full.getFloat2());
        partial.setLong2(full.getLong2());

        ret.setPartialDerivedInline(partial);
        return ret;
    }

    @Override
    public boolean canShrink(final Object o) {
        return false;
    }

    @Override
    public boolean canRegisterAsType(final Class<?> type) {
        return Dummy1.class.equals(type);
    }
}
