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

public interface Defaults {

    default int getListInitialSize() {
        return 8;
    }

    default int getListSizeBound() {
        return Integer.MAX_VALUE - 8;
    }

    default int getMapInitialSize() {
        return 32;
    }

    default double getMapLoadFactor() {
        return 0.85;
    }

    default int getListMinEmpty() {
        return 8;
    }

    default int getMapMinEmpty() {
        return 8;
    }

    default double getListResizeFactor() {
        return 0.5;
    }

    default double getMapResizeFactor() {
        return 0.5;
    }
}
