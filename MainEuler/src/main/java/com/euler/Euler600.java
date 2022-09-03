package com.euler;

public class Euler600 {
	private final static long GOAL=55106;
	
	// La base está bien, pero hay que tener en cuenta las congruencias por rotación.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long halfGoal=(GOAL-2)/2;
		long result=0;
		for (long a=1;a<halfGoal;++a) for (long b=a;b<halfGoal;++b) for (long c=b;c<halfGoal;++c)	{
			long d=a+b-c;
			if (d<=0) break;	// Next b.
			long baseDiff=Math.abs((b+d)-(c+a))/2;
			long missing=GOAL-(a+b+c+d);
			long count=(missing-baseDiff)/2;
			if (count>0)	{
				// System.out.println("a="+a+"; b="+b+"; c="+c+"; d="+d+" => "+count);
				result+=count;
			}
		}
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");	// Interminable :D.
	}
}
