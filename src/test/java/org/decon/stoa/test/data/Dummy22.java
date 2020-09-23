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

public class Dummy22 extends Dummy2 {
    private byte byte2;

    private short short2;

    private float float2;

    private long long2;

    public byte getByte2() {
        return this.byte2;
    }

    public void setByte2(final byte byte2) {
        this.byte2 = byte2;
    }

    public short getShort2() {
        return this.short2;
    }

    public void setShort2(final short short2) {
        this.short2 = short2;
    }

    public float getFloat2() {
        return this.float2;
    }

    public void setFloat2(final float float2) {
        this.float2 = float2;
    }

    public long getLong2() {
        return this.long2;
    }

    public void setLong2(final long long2) {
        this.long2 = long2;
    }

    @SuppressWarnings("boxing")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(byte2, float2, long2, short2);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Dummy22 other = (Dummy22) obj;
        return byte2 == other.byte2 && Float.floatToIntBits(float2) == Float.floatToIntBits(other.float2)
               && long2 == other.long2 && short2 == other.short2;
    }

    @SuppressWarnings("boxing")
    @Override
    public String toString() {
        return String.format("Dummy22 [byte2=%s, short2=%s, float2=%s, long2=%s]", byte2, short2, float2, long2);
    }

}
