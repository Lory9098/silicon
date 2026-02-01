package org.silicon;

public class BitUtils {
    public static float halfToFloat(short h) {
        int bits = h & 0xFFFF;
        
        int sign = (bits & 0x8000) << 16;
        int exp  = (bits >>> 10) & 0x1F;
        int mant = bits & 0x03FF;
        
        if (exp == 0) {
            if (mant == 0) {
                // zero
                return Float.intBitsToFloat(sign);
            }
            // subnormal
            while ((mant & 0x0400) == 0) {
                mant <<= 1;
                exp--;
            }
            exp++;
            mant &= ~0x0400;
        } else if (exp == 31) {
            // Inf / NaN
            return Float.intBitsToFloat(sign | 0x7F800000 | (mant << 13));
        }
        
        exp = exp - 15 + 127;
        mant <<= 13;
        
        return Float.intBitsToFloat(sign | (exp << 23) | mant);
    }
    
    public static short floatToHalf(float f) {
        int bits = Float.floatToIntBits(f);
        
        int sign = (bits >>> 16) & 0x8000;
        int exp  = ((bits >>> 23) & 0xFF) - 127 + 15;
        int mant = bits & 0x7FFFFF;
        
        if (exp <= 0) {
            if (exp < -10) return (short) sign;
            mant = (mant | 0x800000) >> (1 - exp);
            return (short) (sign | (mant >> 13));
        }
        
        if (exp >= 31) {
            return (short) (sign | 0x7C00); // Inf
        }
        
        return (short) (sign | (exp << 10) | (mant >> 13));
    }
}
