package com.euler;

public class Euler732 {
	private final static int N=5;
	
	private static class PseudoRandomGenerator	{
		private long r;
		public PseudoRandomGenerator()	{
			r=1l;
		}
		public int next()	{
			long result=(r%101)+50;
			r=(r*5l)%1_000_000_007l;
			return (int)result;
		}
	}
	private static class Troll	{
		public final int h;
		public final int l;
		public final int q;
		public Troll(int h,int l,int q)	{
			this.h=h;
			this.l=l;
			this.q=q;
		}
		public int totalHeight()	{
			return h+l;
		}
	}
	
	public static void main(String[] args)	{
		PseudoRandomGenerator gen=new PseudoRandomGenerator();
		Troll[] trolls=new Troll[N];
		int totalH=0;
		for (int i=0;i<N;++i)	{
			int h=gen.next();
			int l=gen.next();
			int q=gen.next();
			trolls[i]=new Troll(h,l,q);
			totalH+=h;
		}
		int d=(int)(Math.ceil(Math.sqrt(0.5)*totalH));
		int result=0;
		int maxDistr=(1<<N);
		for (int i=0;i<maxDistr;++i)	{
			int shoulderHeight=0;
			int q=0;
			for (int j=0;j<N;++j) if (((1<<j)&i)!=0) shoulderHeight+=trolls[j].h;
			for (int j=0;j<N;++j) if (((1<<j)&i)==0) if (shoulderHeight+trolls[j].totalHeight()>=d) q+=trolls[j].q;
			result=Math.max(result,q);
		}
		/*
		 * Ok, I see. The column is not fixed. Some trolls may exit the hole despite not being so tall.
		 * For example, at the start certain small troll might escape with a tower made out of certain trolls. This troll might not be
		 * tall enough to climb the "final" tower, but when all the trolls are there, some additional trolls might help him escape.
		 * 
		 * For N=5, trolls 1, 3 and 4 can escape.
		 * The heights of trolls 0 and 2 adds to 172.
		 * The total height is 372. So, a troll should be able to escape as long as its total height is greater than, or equal to, 200.
		 * This is valid for trolls 3 and 4, but nor for troll 1.
		 * However troll 1 can still escape. He just needs to do it before 3 and 4 go.
		 * So, there are three sets of trolls:
		 * 1) The trolls that take part in the final tower.
		 * 2) The last troll who escapes, which is the only one for which arm length matters.
		 * 3) The rest of the trolls who escape.
		 * 
		 * Now, from the basic perspective it might make sense to just divide the set in two and assume that the last troll can always escape,
		 * but it's probably better from a computational standpoint to iterate over the "last" troll, because then, for each case, we have a
		 * relatively simple knapsack problem.
		 * 
		 * I wonder whether I can use something like this for 750. Most probably not.
		 */
		System.out.println(String.format("D=%d.",d));
		System.out.println(result);
	}
}
