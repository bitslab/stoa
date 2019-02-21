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

import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

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

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Dummy2 other = (Dummy2) obj;
        if (this.str1 == null) {
            if (other.str1 != null)
                return false;
        } else if (!this.str1.equals(other.str1))
            return false;
        if (other.bool1 != this.bool1)
            return false;
        if (Double.doubleToLongBits(other.double1) != Double.doubleToLongBits(this.double1))
            return false;
        if (other.int1 != this.int1)
            return false;
        if (this.testEnum1 == null) {
            if (other.testEnum1 != null)
                return false;
        } else if (!this.testEnum1.equals(other.testEnum1))
            return false;
        if (this.str2 == null) {
            if (other.str2 != null)
                return false;
        } else if (!this.str2.equals(other.str2))
            return false;
        if (other.bool2 != this.bool2)
            return false;
        if (Double.doubleToLongBits(other.double2) != Double.doubleToLongBits(this.double2))
            return false;
        if (other.int2 != this.int2)
            return false;
        if (this.testEnum2 == null) {
            if (other.testEnum2 != null)
                return false;
        } else if (!this.testEnum2.equals(other.testEnum2))
            return false;
        if (other.byte1 != this.byte1)
            return false;
        if (other.char1 != this.char1)
            return false;
        if (other.short1 != this.short1)
            return false;
        if (Float.floatToIntBits(other.float1) != Float.floatToIntBits(this.float1))
            return false;
        if (other.long1 != this.long1)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.str1 == null) ? 0 : this.str1.hashCode());
        result = prime * result + (this.bool1 ? 1231 : 1237);
        result = prime * result
                 + (int) (Double.doubleToLongBits(this.double1) ^ (Double.doubleToLongBits(this.double1) >>> 32));
        result = prime * result + this.int1;
        result = prime * result + ((this.testEnum1 == null) ? 0 : this.testEnum1.hashCode());
        result = prime * result + ((this.str2 == null) ? 0 : this.str2.hashCode());
        result = prime * result + (this.bool2 ? 1231 : 1237);
        result = prime * result
                 + (int) (Double.doubleToLongBits(this.double2) ^ (Double.doubleToLongBits(this.double2) >>> 32));
        result = prime * result + this.int2;
        result = prime * result + ((this.testEnum2 == null) ? 0 : this.testEnum2.hashCode());
        result = prime * result + this.byte1;
        result = prime * result + this.char1;
        result = prime * result + this.short1;
        result = prime * result + Float.floatToIntBits(this.float1);
        result = prime * result + (int) (this.long1 ^ (this.long1 >>> 32));
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this);
        b.add("str1", this.str1);
        b.add("bool1", this.bool1);
        b.add("double1", this.double1);
        b.add("int1", this.int1);
        b.add("testEnum1", this.testEnum1);
        b.add("str2", this.str2);
        b.add("bool2", this.bool2);
        b.add("double2", this.double2);
        b.add("int2", this.int2);
        b.add("testEnum2", this.testEnum2);
        b.add("byte1", this.byte1);
        b.add("char1", this.char1);
        b.add("short1", this.short1);
        b.add("float1", this.float1);
        b.add("long1", this.long1);
        return b.toString();
    }
}
