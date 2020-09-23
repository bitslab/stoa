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

public class Dummy2 {
    private String str1;

    private boolean bool1;

    private double double1;

    private int int1;

    private TestEnum testEnum1;

    private String str2;

    private boolean bool2;

    private double double2;

    private int int2;

    private TestEnum testEnum2;

    private byte byte1;

    private char char1;

    private short short1;

    private float float1;

    private long long1;

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

    public TestEnum getTestEnum1() {
        return this.testEnum1;
    }

    public void setTestEnum1(final TestEnum testEnum1) {
        this.testEnum1 = testEnum1;
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

    public TestEnum getTestEnum2() {
        return this.testEnum2;
    }

    public void setTestEnum2(final TestEnum testEnum2) {
        this.testEnum2 = testEnum2;
    }

    public byte getByte1() {
        return this.byte1;
    }

    public void setByte1(final byte byte1) {
        this.byte1 = byte1;
    }

    public char getChar1() {
        return this.char1;
    }

    public void setChar1(final char char1) {
        this.char1 = char1;
    }

    public short getShort1() {
        return this.short1;
    }

    public void setShort1(final short short1) {
        this.short1 = short1;
    }

    public float getFloat1() {
        return this.float1;
    }

    public void setFloat1(final float float1) {
        this.float1 = float1;
    }

    public long getLong1() {
        return this.long1;
    }

    public void setLong1(final long long1) {
        this.long1 = long1;
    }

    @SuppressWarnings("boxing")
    @Override
    public int hashCode() {
        return Objects.hash(bool1, bool2, byte1, char1, double1, double2, float1, int1, int2, long1, short1, str1, str2,
                testEnum1, testEnum2);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Dummy2 other = (Dummy2) obj;
        return bool1 == other.bool1 && bool2 == other.bool2 && byte1 == other.byte1 && char1 == other.char1
               && Double.doubleToLongBits(double1) == Double.doubleToLongBits(other.double1)
               && Double.doubleToLongBits(double2) == Double.doubleToLongBits(other.double2)
               && Float.floatToIntBits(float1) == Float.floatToIntBits(other.float1) && int1 == other.int1
               && int2 == other.int2 && long1 == other.long1 && short1 == other.short1
               && Objects.equals(str1, other.str1) && Objects.equals(str2, other.str2) && testEnum1 == other.testEnum1
               && testEnum2 == other.testEnum2;
    }

    @SuppressWarnings("boxing")
    @Override
    public String toString() {
        return String.format(
                "Dummy2 [str1=%s, bool1=%s, double1=%s, int1=%s, testEnum1=%s, str2=%s, bool2=%s, double2=%s, int2=%s, testEnum2=%s, byte1=%s, char1=%s, short1=%s, float1=%s, long1=%s]",
                str1, bool1, double1, int1, testEnum1, str2, bool2, double2, int2, testEnum2, byte1, char1, short1,
                float1, long1);
    }

}
