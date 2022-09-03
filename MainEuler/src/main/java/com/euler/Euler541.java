package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.padic.RationalPAdicArithmetic;
import com.euler.common.padic.RationalPAdicArithmetic.PAdicRational;

public class Euler541 {
	private final static int PRIME=3;
	
	// First version. Brute force of sorts.
	public static void main(String[] args)	{
		List<Long> js=new ArrayList<>();
		js.add(0l);
		RationalPAdicArithmetic calculator=new RationalPAdicArithmetic(PRIME,50);
		long currentLastJ=PRIME-1;
		PAdicRational sum=calculator.getInteger(0);
		for (long i=1;i<=currentLastJ;++i)	{
			sum=calculator.add(sum,calculator.inverse(i));
			if (sum.v>0)	{
				System.out.println("Found element in J: "+i+".");
				js.add(i);
				currentLastJ=PRIME*(i+1)-1;
			}
		}
		System.out.println("J="+js+".");
	}
}
