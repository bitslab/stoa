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

public class Dummy1 {
    private String str1;

    private boolean bool1;

    private double double1;

    private int int1;

    private TestEnum testEnum;

    private Dummy2 notInline;

    private Dummy2 inline;

    private Dummy22 derivedInline;

    private Dummy22 partialDerivedInline;

    private Dummy3 nestedInline;

    private Dummy3 notNestedInline;

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

    public TestEnum getTestEnum() {
        return this.testEnum;
    }

    public void setTestEnum(final TestEnum testEnum) {
        this.testEnum = testEnum;
    }

    public Dummy2 getNotInline() {
        return this.notInline;
    }

    public void setNotInline(final Dummy2 notInline) {
        this.notInline = notInline;
    }

    public Dummy2 getInline() {
        return this.inline;
    }

    public void setInline(final Dummy2 inline) {
        this.inline = inline;
    }

    public Dummy22 getDerivedInline() {
        return this.derivedInline;
    }

    public void setDerivedInline(final Dummy22 derivedInline) {
        this.derivedInline = derivedInline;
    }

    public Dummy22 getPartialDerivedInline() {
        return this.partialDerivedInline;
    }

    public void setPartialDerivedInline(final Dummy22 partialDerivedInline) {
        this.partialDerivedInline = partialDerivedInline;
    }

    public Dummy3 getNestedInline() {
        return this.nestedInline;
    }

    public void setNestedInline(final Dummy3 nestedInline) {
        this.nestedInline = nestedInline;
    }

    public Dummy3 getNotNestedInline() {
        return this.notNestedInline;
    }

    public void setNotNestedInline(final Dummy3 notNestedInline) {
        this.notNestedInline = notNestedInline;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (bool1 ? 1231 : 1237);
        result = prime * result + ((derivedInline == null) ? 0 : derivedInline.hashCode());
        long temp;
        temp = Double.doubleToLongBits(double1);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((inline == null) ? 0 : inline.hashCode());
        result = prime * result + int1;
        result = prime * result + ((nestedInline == null) ? 0 : nestedInline.hashCode());
        result = prime * result + ((notInline == null) ? 0 : notInline.hashCode());
        result = prime * result + ((notNestedInline == null) ? 0 : notNestedInline.hashCode());
        result = prime * result + ((partialDerivedInline == null) ? 0 : partialDerivedInline.hashCode());
        result = prime * result + ((str1 == null) ? 0 : str1.hashCode());
        result = prime * result + ((testEnum == null) ? 0 : testEnum.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Dummy1 other = (Dummy1) obj;
        if (bool1 != other.bool1)
            return false;
        if (derivedInline == null) {
            if (other.derivedInline != null)
                return false;
        } else if (!derivedInline.equals(other.derivedInline))
            return false;
        if (Double.doubleToLongBits(double1) != Double.doubleToLongBits(other.double1))
            return false;
        if (inline == null) {
            if (other.inline != null)
                return false;
        } else if (!inline.equals(other.inline))
            return false;
        if (int1 != other.int1)
            return false;
        if (nestedInline == null) {
            if (other.nestedInline != null)
                return false;
        } else if (!nestedInline.equals(other.nestedInline))
            return false;
        if (notInline == null) {
            if (other.notInline != null)
                return false;
        } else if (!notInline.equals(other.notInline))
            return false;
        if (notNestedInline == null) {
            if (other.notNestedInline != null)
                return false;
        } else if (!notNestedInline.equals(other.notNestedInline))
            return false;
        if (partialDerivedInline == null) {
            if (other.partialDerivedInline != null)
                return false;
        } else if (!partialDerivedInline.equals(other.partialDerivedInline))
            return false;
        if (str1 == null) {
            if (other.str1 != null)
                return false;
        } else if (!str1.equals(other.str1))
            return false;
        if (testEnum != other.testEnum)
            return false;
        return true;
    }

    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this);
        b.add("str1", this.str1);
        b.add("bool1", this.bool1);
        b.add("double1", this.double1);
        b.add("int1", this.int1);
        b.add("testEnum", this.testEnum);
        b.add("notInline", this.notInline);
        b.add("inline", this.inline);
        b.add("derivedInline", this.derivedInline);
        b.add("partialDerivedInline", this.partialDerivedInline);
        b.add("nestedInline", this.nestedInline);
        b.add("notNestedInline", this.notNestedInline);
        return b.toString();
      }

}
