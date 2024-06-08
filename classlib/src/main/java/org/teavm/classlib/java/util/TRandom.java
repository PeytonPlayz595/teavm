/*
 *  Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util;

import org.teavm.classlib.impl.RandomUtils;
import org.teavm.classlib.java.io.TSerializable;
import org.teavm.classlib.java.lang.TObject;
import org.teavm.classlib.java.util.random.TRandomGenerator;

public class TRandom extends TObject implements TRandomGenerator, TSerializable {
    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;

    private long seed = 69;
    
    /** A stored gaussian value for nextGaussian() */
    private double storedGaussian;

    /** Whether storedGuassian value is valid */
    private boolean haveStoredGaussian;

    public TRandom() {
        this((long)(Math.random() * 9007199254740991.0));
    }

    public TRandom(long seed) {
        setSeed(seed);
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    private static long initialScramble(long seed) {
	return (seed ^ multiplier) & mask;
    }

    protected int next(int bits) {
	seed = (seed * multiplier + addend) & mask;
	return (int) (seed >>> (48 - bits));
    }

    @Override
    public void nextBytes(byte[] bytes) {
	for (int i = 0, len = bytes.length; i < len;)
		for (int rnd = nextInt(), n = Math.min(len - i, Integer.SIZE / Byte.SIZE); n-- > 0; rnd >>= Byte.SIZE)
			bytes[i++] = (byte) rnd;
    }

    @Override
    public int nextInt() {
	return next(32);
    }

    @Override
    public int nextInt(int n) {
	int r = next(31);
	int m = n - 1;
	if ((n & m) == 0) {
	    r = (int) ((n * (long) r) >> 31);
        } else {
            for (int u = r; u - (r = u % n) + m < 0; u = next(31))
		    ;
	}
	return r;
    }

    @Override
    public long nextLong() {
	return ((long) (next(32)) << 32) + next(32);
    }
    
    @Override
    public boolean nextBoolean() {
	return next(1) != 0;
    }

    @Override
    public float nextFloat() {
	return next(24) / ((float) (1 << 24));
    }

    @Override
    public double nextDouble() {
	return (((long) (next(26)) << 27) + next(27)) * DOUBLE_UNIT;
    }

    /**
     * Generate a random number with Gaussian distribution:
     * centered around 0 with a standard deviation of 1.0.
     */
    @Override
    public double nextGaussian() {
        /*
         * This implementation uses the polar method to generate two gaussian
         * values at a time. One is returned, and the other is stored to be returned
         * next time.
         */
        if (haveStoredGaussian) {
            haveStoredGaussian = false;
            return storedGaussian;
        }

        double[] pair = RandomUtils.pairGaussian(this::nextDouble);
        haveStoredGaussian = true;
        storedGaussian = pair[1];

        return pair[0];
    }
}
