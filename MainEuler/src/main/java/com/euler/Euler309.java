package com.euler;

import com.euler.common.PythagoreanTriples;

public class Euler309 {
	private final static int LIMIT=1_000_000;
	
	public static void main(String[] args)	{
		PythagoreanTriples.PrimitiveTriplesIterator iterator=new PythagoreanTriples.PrimitiveTriplesIterator();
		long result=0;
		boolean jump=false;
		for (;;)	{
			if (jump)	{
				iterator.nextM();
				jump=false;
			}	else iterator.next();
			long a=iterator.a();
			long b=iterator.b();
			long toAdd=(LIMIT/a)+(LIMIT/b);
			if (toAdd==0)	{
				if (iterator.isSmallestN()) break;
				else jump=true;
			}
			result+=toAdd;
			System.out.println(result+"...");
		}
		System.out.println(result);
	}
}
