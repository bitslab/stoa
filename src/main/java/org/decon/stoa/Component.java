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
package org.decon.stoa;

public interface Component<E, T> extends HugeList<T> {

    Class<T> getComponentClass();

    void beanToArray(E bean, long index);

    E arrayToBean(long index, E bean);

    void insertFromBean(E bean, long index);

    Component<E, T> fillRangeFromBean(E bean, long fromIndex, long toIndexExclusive);

    Object data();

    @Override
    Component<E, T> clone();

}
