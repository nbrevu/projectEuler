package com.euler;

import java.math.RoundingMode;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

// PROBLEM NOT FOUND.
public class Euler404 {
	private final static long N=30000l;
	
	/*
	 * Sample case:
	 * a=209=11*19.
	 * b=247=13*19.
	 * c=286=2*11*13.
	 * SUSPICIOUS.
	 * 
	 * Apparently I need 1/b^2 + 1/c^2 = 5/(4a^2).
	 * 
	 * 4a^2b^2 + 4a^2c^2 = 5b^2c^2
	 * 
	 * All the PRIMITIVE cases follow a similar pattern:
	 * A = x*y, and then there is an additional factor z. Then, either b=x*z and c=2*y*z, OR b=2*x*z and c=y*z.
	 * I don't known whether x or y must be primes (they are in every case I've seen), but z might not.
	 * Found a case where a has three factors. 
		a=14839, b=16046, c=23617.
			a=11^1 · 19^1 · 71^1.
			b=2^1 · 71^1 · 113^1.
			c=11^1 · 19^1 · 113^1.
	 * Only some primes appear. 5 never appears alone, but it appears as part of z. 3 or 7 never appear. 11, 13, 17 and 19 appear.
	 * 23 doesn't appear. 29, 31, 37 and 41 do. 43 and 47 don't. 53 does, so do 59 and 61. 67 doesn't. And so on... I don't see a clear pattern.
	 */
	public static void main(String[] args)	{
		long[] firstPrimes=Primes.firstPrimeSieve(2*N);
		long result=0;
		long primitives=0;
		for (long b=1;b<=2*N;++b) for (long c=b;c<=2*N;++c)	{
			if (((b%2)==1)&&((c%2)==1)) continue;
			long bb=b*b;
			long cc=c*c;
			long sumSq=bb+cc;
			long d=5*bb*cc;
			if ((d%sumSq)!=0) continue;
			long q=d/sumSq;
			if ((q%4)!=0) continue;
			long a2=q/4;
			long a=LongMath.sqrt(a2,RoundingMode.DOWN);
			if ((c<2*a)&&(a*a==a2)&&(a<N))	{
				System.out.println(String.format("a=%d, b=%d, c=%d.",a,b,c));
				if (EulerUtils.gcd(a,EulerUtils.gcd(b,c))==1)	{
					System.out.println(String.format("\ta=%s.",DivisorHolder.getFromFirstPrimes(a,firstPrimes).toString()));
					System.out.println(String.format("\tb=%s.",DivisorHolder.getFromFirstPrimes(b,firstPrimes).toString()));
					System.out.println(String.format("\tc=%s.",DivisorHolder.getFromFirstPrimes(c,firstPrimes).toString()));
					++primitives;
				}	else System.out.println("\t<Non primitive>.");
				++result;
			}
		}
		System.out.println(String.format("%d (%d primitives).",result,primitives));
	}
}
