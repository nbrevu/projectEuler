package com.euler;

import com.euler.common.Timing;

public class Euler154 {
	private final static int LIMIT=200000;
	private final static int MIN_POWER=12;
	
	private static int getPowersInNumber(int in,int base)	{
		int result=0;
		do	{	// Yes: do. Assumes that "in" is already known to be a multiple of "base".
			in/=base;
			++result;
		}	while ((in%base)==0);
		return result;
	}
	
	private static int[] getPowersInFactorials(int limit,int base)	{
		int[] result=new int[1+limit];
		int cumResult=0;
		for (int i=base;i<result.length;i+=base)	{
			cumResult+=getPowersInNumber(i,base);
			int nextI=Math.min(i+base,result.length);
			for (int k=i;k<nextI;++k) result[k]=cumResult;
		}
		return result;
	}
	
	private static long solve()	{
		long result=0;
		int[] calc2=getPowersInFactorials(LIMIT,2);
		int[] calc5=getPowersInFactorials(LIMIT,5);
		int pow2=calc2[LIMIT]-MIN_POWER;
		int pow5=calc5[LIMIT]-MIN_POWER;
		for (int i=2;i<65625;++i)	{
			int minJ=(i<12502)?31252:i;
			int maxJ=1+((LIMIT-i)/2);
			int li=LIMIT-i;
			int pow2i=pow2-calc2[i];
			int pow5i=pow5-calc5[i];
			for (int j=minJ;j<maxJ;++j)	{
				int k=li-j;
				if ((calc5[j]+calc5[k]<=pow5i)&&(calc2[j]+calc2[k]<=pow2i)) result+=((i==j)||(j==k))?3:6;
				// if ((calc2[j]+calc2[k]<=pow2i)&&(calc5[j]+calc5[k]<=pow5i)) result+=((i==j)||(j==k))?3:6;
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler154::solve);
	}
}