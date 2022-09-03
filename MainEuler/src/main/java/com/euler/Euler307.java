package com.euler;

import java.util.Arrays;
import java.util.Iterator;

import com.euler.common.EulerUtils.FactorialCache;

public class Euler307 {
	/*
	private final static long DEFECTS=20000;
	private final static long CHIPS=1000000;
	
	public static void main(String[] args)	{
		long n=DEFECTS+CHIPS-1;
		long k=DEFECTS;
		BigInteger num=BigInteger.ONE;
		BigInteger den=BigInteger.ONE;
		for (long i=1;i<=k;++i)	{
			num=num.multiply(BigInteger.valueOf(n+1-i));
			den=den.multiply(BigInteger.valueOf(i));
		}
		BigInteger comb=num.divide(den);
		BigInteger mod=num.mod(den);
		System.out.println(comb);
		System.out.println(mod);
	}
	*/
	
	private static class DistributionGenerator implements Iterable<int[]>,Iterator<int[]>	{
		public final int total;
		public final int boxes;
		private int[] distribution;
		private boolean isNextCalculated;
		private boolean isFinished;
		public DistributionGenerator(int total,int boxes)	{
			assert (total>0);
			assert (boxes>0);
			this.total=total;
			this.boxes=boxes;
			distribution=new int[boxes];
			distribution[0]=total;
			isNextCalculated=true;
			isFinished=false;
		}
		@Override
		public boolean hasNext() {
			if (!isNextCalculated) calculateNext();
			return !isFinished;
		}
		@Override
		public int[] next() {
			if (!isNextCalculated) calculateNext();
			isNextCalculated=false;
			return distribution;
		}
		@Override
		public Iterator<int[]> iterator() {
			return this;
		}
		public void calculateNext()	{
			int i=boxes-1;
			while (distribution[i]==0) --i;
			if (i==0)	{
				--distribution[0];
				distribution[1]=1;
			}	else if (i<boxes-1)	{
				--distribution[i];
				distribution[i+1]=1;
			}	else	{
				int carry=distribution[i];
				distribution[i]=0;
				do --i; while ((i>=0)&&distribution[i]==0);
				if (i<0) isFinished=true;
				else	{
					--distribution[i];
					distribution[i+1]=1+carry;
				}
			}
			isNextCalculated=true;
		}
	}
	
	public static void main(String[] args)	{
		int total=0;
		int failed=0;
		FactorialCache cache=new FactorialCache(3);
		for (int[] distr:new DistributionGenerator(3,7))	{
			System.out.println(Arrays.toString(distr));
			int counter=(int)cache.get(3);
			for (int d:distr) counter/=(int)cache.get(d);
			total+=counter;
			for (int defects:distr) if (defects>=3)	{
				failed+=counter;
				break;
			}
		}
		System.out.println(""+failed+" out of "+total+" have failed.");
		double ratio=((double)failed)/((double)total);
		System.out.println("Ratio: "+ratio+".");
	}
}
