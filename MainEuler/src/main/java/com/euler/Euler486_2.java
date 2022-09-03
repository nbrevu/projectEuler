package com.euler;

import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;

public class Euler486_2 {
	/*
	 * Ok, the "reverse" has a simple pattern (excluding cases 5 and 6): I(x)={32,32,32,34,36,34}.
	 * If we extend I to cases <=6, we get:
	 * I(0)=1.
	 * I(1)=2.
	 * I(2)=4.
	 * I(3)=8.
	 * I(4)=16.
	 * I(5)=24.
	 * I(6)=30.
	 * 
	 * This allows us to calculate the sum of the inverse:
	 * SI(0)=1.
	 * SI(1)=3.
	 * SI(2)=7.
	 * SI(3)=15.
	 * SI(4)=31.
	 * SI(5)=55.
	 * SI(6)=85.
	 * SI(7)=117.	//+32.
	 * SI(8)=149.	//+32.
	 * SI(9)=181.	//+32.
	 * SI(10)=215.	//+34.
	 * SI(11)=251.	//+36.
	 * And from now, the series has some periodicity according to modulus 6:
	 * SI(6n) = 200n-115.
	 * SI(6n+1) = 200n-83.
	 * SI(6n+2) = 200n-51.
	 * SI(6n+3) = 200n-19.
	 * SI(6n+4) = 200n+15.
	 * SI(6n+5) = 200n+51.
	 * Of course, the total amount of strings is a power of 2.
	 * Then, sumTotal(n) = 2^(n+1)-1.
	 * 
	 * And so we get a CLOSED form of F(n), valid for n>=6, which depends on n%6:
	 * 
	 * F(6n)   = 2^(6n+1) - 200n + 114.
	 * F(6n+1) = 2^(6n+2) - 200n + 82.
	 * F(6n+2) = 2^(6n+3) - 200n + 50.
	 * F(6n+3) = 2^(6n+4) - 200n + 18.
	 * F(6n+4) = 2^(6n+5) - 200n - 16.
	 * F(6n+5) = 2^(6n+6) - 200n - 52.
	 * 
	 * And now we have a slight (or maybe huge) problem: the modulus.
	 * 87654321 = 3^2 * 1997 * 4877 -> tot(87654321) = 2*3*1996*4876 = 58394976.
	 * As per Euler's theorem, 2^6n repeats each 58394976 values.
	 * However, 200n repeats each 87654321 values.
	 * 
	 * The total period is something like 58394976*87654321/3, which has 16 digits.
	 * A possibility is: count for each one of the 58394976 "exponential" moduli, then use arithmetic to look for the inverses, which by the way,
	 * MUST exist. Modular inverses, yay.
	 */
	
	private final static long MOD=87654321l;	// = 3^2*1977*4877.
	// private final static long MOD_TOTIENT=2*3*1996*4876;
	
	public static void main(String[] args)	{
		DivisorHolder holder=new DivisorHolder();
		for (long p:new long[] {2,3,1996,4876}) holder.addFactor(p,1);
		SortedSet<Long> divisors=new TreeSet<>(holder.getUnsortedListOfDivisors());
		// It seems that: 64^9732496 mod 87654321=1.
		for (long d:divisors)	{
			System.out.println(String.format("%d^%d mod %d=%d",2l,d,MOD,EulerUtils.expMod(2l,d,MOD)));
			System.out.println(String.format("%d^%d mod %d=%d",64l,d,MOD,EulerUtils.expMod(64l,d,MOD)));
		}
	}
}
