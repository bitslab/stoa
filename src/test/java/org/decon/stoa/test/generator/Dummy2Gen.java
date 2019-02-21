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

import org.decon.stoa.test.data.Dummy2;

public class Dummy2Gen extends BeanGen<Dummy2> {
    public Dummy2Gen() {
        super(Dummy2.class);
    }

    @Override
    public boolean canShrink(final Object o) {
        return false;
    }

    @Override
    public boolean canRegisterAsType(final Class<?> type) {
        return Dummy2.class.equals(type);
    }
}
