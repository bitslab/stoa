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
package org.decon.stoa.test.data;

import java.util.Objects;

public class Dummy4 {
    private String str1;

    private boolean bool1;

    private double double1;

    private int int1;

    private String str2;

    private boolean bool2;

    private double double2;

    private int int2;

    private TestEnum testEnum;

    public String getStr1() {
        return this.str1;
    }

    public void setStr1(final String str1) {
        this.str1 = str1;
    }

    public boolean isBool1() {
        return this.bool1;
    }

    public void setBool1(final boolean bool1) {
        this.bool1 = bool1;
    }

    public double getDouble1() {
        return this.double1;
    }

    public void setDouble1(final double double1) {
        this.double1 = double1;
    }

    public int getInt1() {
        return this.int1;
    }

    public void setInt1(final int int1) {
        this.int1 = int1;
    }

    public String getStr2() {
        return this.str2;
    }

    public void setStr2(final String str2) {
        this.str2 = str2;
    }

    public boolean isBool2() {
        return this.bool2;
    }

    public void setBool2(final boolean bool2) {
        this.bool2 = bool2;
    }

    public double getDouble2() {
        return this.double2;
    }

    public void setDouble2(final double double2) {
        this.double2 = double2;
    }

    public int getInt2() {
        return this.int2;
    }

    public void setInt2(final int int2) {
        this.int2 = int2;
    }

    public TestEnum getTestEnum() {
        return this.testEnum;
    }

    public void setTestEnum(final TestEnum testEnum) {
        this.testEnum = testEnum;
    }

    @SuppressWarnings("boxing")
    @Override
    public int hashCode() {
        return Objects.hash(bool1, bool2, double1, double2, int1, int2, str1, str2, testEnum);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Dummy4 other = (Dummy4) obj;
        return bool1 == other.bool1 && bool2 == other.bool2
               && Double.doubleToLongBits(double1) == Double.doubleToLongBits(other.double1)
               && Double.doubleToLongBits(double2) == Double.doubleToLongBits(other.double2) && int1 == other.int1
               && int2 == other.int2 && Objects.equals(str1, other.str1) && Objects.equals(str2, other.str2)
               && testEnum == other.testEnum;
    }

    @SuppressWarnings("boxing")
    @Override
    public String toString() {
        return String.format(
                "Dummy4 [str1=%s, bool1=%s, double1=%s, int1=%s, str2=%s, bool2=%s, double2=%s, int2=%s, testEnum=%s]",
                str1, bool1, double1, int1, str2, bool2, double2, int2, testEnum);
    }

}
