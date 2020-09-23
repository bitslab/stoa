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

    @SuppressWarnings("boxing")
    @Override
    public int hashCode() {
        return Objects.hash(bool1, derivedInline, double1, inline, int1, nestedInline, notInline, notNestedInline,
                partialDerivedInline, str1, testEnum);
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
        return bool1 == other.bool1 && Objects.equals(derivedInline, other.derivedInline)
               && Double.doubleToLongBits(double1) == Double.doubleToLongBits(other.double1)
               && Objects.equals(inline, other.inline) && int1 == other.int1
               && Objects.equals(nestedInline, other.nestedInline) && Objects.equals(notInline, other.notInline)
               && Objects.equals(notNestedInline, other.notNestedInline)
               && Objects.equals(partialDerivedInline, other.partialDerivedInline) && Objects.equals(str1, other.str1)
               && testEnum == other.testEnum;
    }

    @SuppressWarnings("boxing")
    @Override
    public String toString() {
        return String.format(
                "Dummy1 [str1=%s, bool1=%s, double1=%s, int1=%s, testEnum=%s, notInline=%s, inline=%s, derivedInline=%s, partialDerivedInline=%s, nestedInline=%s, notNestedInline=%s]",
                str1, bool1, double1, int1, testEnum, notInline, inline, derivedInline, partialDerivedInline,
                nestedInline, notNestedInline);
    }

}
