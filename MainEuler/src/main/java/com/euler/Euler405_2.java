package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler405_2 {
	/*
	 * Wow, this was much more fun that I expected!
	 * 
	 * So: after some quick simulations, I got a sequence of valid values (see Euler405). From it, I got a working recurrence relation
	 * (see euler405 in Matlab): f(n) = 2/5*4^n - 4/3*2^n + 1 - 1/15*(-1)^n.
	 * 
	 * This is fastidious, so why not multiply times 15? We can just multiply times the modulus inverse and get the right result afterwards!
	 * 15*f(n) = 6*4^n - 20*2^n + 15 - (-1)^n.
	 * 
	 * This would be workable if not for that stupidly big exponent, 10^(10^18). But that's what Fermat's little theorem is for!
	 * Instead of using 10^(10^18) we can use 10^(10^18)%phi(17^7). Of course, since 17 is prime, phi(17^7) is 16*17^6.
	 * Sooo... we use modular exponentiation to calculate 10^(10^18)%phi(17^7), and we use the result as exponent in the formula for f(n). Solved!
	 * 
	 * It has some mathematical analysis to be performed beforehand, but the final execution time is very short thanks to the marvels of binary
	 * exponentiation. Isn't it beautiful, that we're given N=10^(10^18) and the run time ends having O(log(log(N)))?
	 */
	private final static long MOD=LongMath.pow(17l,7);
	private final static long POW=LongMath.pow(10l,18);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long modTotient=16*LongMath.pow(17l,6);
		long pow10=EulerUtils.expMod(10l,POW,modTotient);
		long inverse=EulerUtils.modulusInverse(15l,MOD);
		long mainResult=6*EulerUtils.expMod(4l,pow10,MOD)+(MOD-20)*EulerUtils.expMod(2l,pow10,MOD)+15-1;
		long result=(inverse*(mainResult%MOD))%MOD;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
