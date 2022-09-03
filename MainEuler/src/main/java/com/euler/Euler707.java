package com.euler;

import com.google.common.math.LongMath;

public class Euler707 {
	/*
	 * Hypothesis: always a power of 2.
	 * (1,2) -> 2^0.
	 * (3,3) -> 2^8.
	 * (4,4) -> 2^12
	 * (7,11) -> 2^70
	 */
	
	/*
	 * S(4,5) = 1052960 = 1048576 + 4384 = 1048576 + 4096 + 288 = 1048576 + 4096 + 256 + 32
	 * S(4,5) = F(4,1) + F(4,1) + F(4,2) + F(4,3) + F(4,5)
	 * ¿F(4,1)=16=2^4? ¿F(4,2)=256=2^8? ¿F(4,3)=4096=2^12? ¿F(4,5)=2^20?
	 */
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	public static void main(String[] args)	{
		int exp=0;
		long pow=1;
		for (;;)	{
			++exp;
			pow=(2*pow)%MOD;
			if (pow==270016253l)	{
				System.out.println("Found it! exp="+exp+".");	// It happens to be 70.
				return;
			}	else if (pow==1)	{
				System.out.println("No luck :'(.");
				return;
			}
		}
	}
}
