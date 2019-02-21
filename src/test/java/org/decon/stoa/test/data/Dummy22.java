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

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        if (!super.equals(obj))
            return false;
        Dummy22 other = (Dummy22) obj;
        if (other.byte2 != this.byte2)
            return false;
        if (other.short2 != this.short2)
            return false;
        if (Float.floatToIntBits(other.float2) != Float.floatToIntBits(this.float2))
            return false;
        if (other.long2 != this.long2)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.byte2;
        result = prime * result + this.short2;
        result = prime * result + Float.floatToIntBits(this.float2);
        result = prime * result + (int) (this.long2 ^ (this.long2 >>> 32));
        return result;
    }

    @Override
    public String toString() {
        String result = new ToStringBuilder(this).addAllFields()
                                                 .toString();
        return result;
    }
}
