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
package org.decon.stoa.util;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
    private Supplier<Supplier<T>> supplier;
    private Supplier<T>           computed = null;

    public Lazy(Supplier<Supplier<T>> supplier) {
        this.supplier = supplier;
    }

    @Override
    public synchronized T get() {
        if (computed == null) {
            computed = supplier.get();
            supplier = null;
        }
        return computed.get();
    }
}
