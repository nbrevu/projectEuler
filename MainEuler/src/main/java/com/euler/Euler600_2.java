package com.euler;

public class Euler600_2 {
	private final static long GOAL=100;
	
	// La base está bien, pero hay que tener en cuenta las congruencias por rotación.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long halfGoal=(GOAL-2)/2;
		long result=0;
		for (long a=1;a<halfGoal;++a)	{
			for (long b=1;b<halfGoal;++b)	{
				long maxC=Math.min(halfGoal,a+b);
				for (long c=1;c<maxC;++c)	{
					long d=a+b-c;
					long baseDiff=Math.abs((b+d)-(c+a))/2;
					long missing=GOAL-(a+b+c+d);
					long count=(missing-baseDiff)/2;
					if (count>0)	{
						System.out.println("a="+a+"; b="+b+"; c="+c+"; d="+d+" => "+count);
						result+=count;
					}
				}
			}
			System.out.println(a+"...");
		}
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");	// Interminable :D.
	}
}
