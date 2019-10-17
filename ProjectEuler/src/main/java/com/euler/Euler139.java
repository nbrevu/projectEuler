package com.euler;

import com.euler.common.PythagoreanTriples;
import com.euler.common.PythagoreanTriples.SimplePythagoreanTriple;
import com.euler.common.Timing;
import com.google.common.math.LongMath;

public class Euler139 {
	private final static long LIMIT=LongMath.pow(10l,8);
	
	private static long solve()	{
		long result=0;
		for (SimplePythagoreanTriple triple:PythagoreanTriples.getSimpleTriplesWithPerimeterLimit(LIMIT)) if ((triple.c%(triple.a-triple.b))==0) result+=LIMIT/(triple.a+triple.b+triple.c);
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler139::solve);
	}
}
