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

    private float float1;

    private long long1;

    private short short1;

    private char char1;

    private byte byte1;

    private int int1;

    private String str2;

    private boolean bool2;

    private double double2;

    private int int2;

    private float float2;

    private long long2;

    private short short2;

    private char char2;

    private byte byte2;

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

    public float getFloat1() { return this.float1; }
    public long  getLong1() { return this.long1; }
    public short getShort1() { return this.short1; }
    public char  getChar1() { return this.char1; }
    public byte  getByte1() { return this.byte1; }

    public void setFloat1(final float float1) { this.float1 = float1; }
    public void setLong1(final long long1){ this.long1 = long1; }
    public void setShort1(final short short1) { this.short1 = short1; }
    public void setChar1(final char char1){ this.char1 = char1; }
    public void setByte1(final byte byte1){ this.byte1 = byte1; }

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

    public float getFloat2() { return this.float2; }
    public long  getLong2() { return this.long2; }
    public short getShort2() { return this.short2; }
    public char  getChar2() { return this.char2; }
    public byte  getByte2() { return this.byte2; }

    public void setFloat2(final float float2) { this.float2 = float2; }
    public void setLong2(final long long2){ this.long2 = long2; }
    public void setShort2(final short short2) { this.short2 = short2; }
    public void setChar2(final char char2){ this.char2 = char2; }
    public void setByte2(final byte byte2){ this.byte2 = byte2; }

    public TestEnum getTestEnum() {
        return this.testEnum;
    }

    public void setTestEnum(final TestEnum testEnum) {
        this.testEnum = testEnum;
    }

    @SuppressWarnings("boxing")
    @Override
    public int hashCode() {
        return Objects.hash(bool1, bool2, double1, double2, int1, int2, str1, str2, long1, long2, float1, float2, short1, short2, char1, char2, byte1, byte2, testEnum);
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
               && char1 == other.char1 && char2 == other.char2
               && short1 == other.short1 && short2 == other.short2
               && byte1 == other.byte1 && byte2 == other.byte2
               && long1 == other.long1 && long2 == other.long2
               && float1 == other.float1 && float2 == other.float2
               && testEnum == other.testEnum;
    }

    @SuppressWarnings("boxing")
    @Override
    public String toString() {
        return String.format(
                "Dummy4 [str1=%s, bool1=%s, double1=%s, int1=%s, str2=%s, bool2=%s, double2=%s, int2=%s," +
                        "char1=%s, char2=%s, " +
                        "short1=%s, short2=%s, " +
                        "byte1=%s, byte2=%s, " +
                        "long1=%s, long2=%s, " +
                        "float1=%s, float2=%s, " +
                        " testEnum=%s]",
                str1, bool1, double1, int1, str2, bool2, double2, int2, char1, char2, short1, short2, byte1, byte2, long1, long2, float1, float2, testEnum);
    }

}
