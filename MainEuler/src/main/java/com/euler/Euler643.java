package com.euler;

import java.util.Random;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.euler.common.SumOfTotientCalculator;
import com.google.common.math.LongMath;

public class Euler643 {
	private static class TotientCalculator	{
		private final int[] firstPrimes;
		public TotientCalculator(int max)	{
			firstPrimes=Primes.firstPrimeSieve(1+max);
		}
		public long getTotient(int in)	{
			return DivisorHolder.getFromFirstPrimes(in,firstPrimes).getTotient();
		}
	}
	
	private static boolean isPowerOfTwo(int in)	{
		if (in==0) return false;
		while ((in%2)==0) in/=2;
		return in==1;
	}
	
	private static long totientBasedCounter(int in,TotientCalculator totCalc)	{
		if ((in%2)==1) return 0;
		long result=totCalc.getTotient(in/2);
		if ((in%4)==0) result+=result;
		if (isPowerOfTwo(in)) --result;
		return result;
	}
	
	private static int bruteForceBasedCounter(int in)	{
		if ((in%2)==1) return 0;
		int result=0;
		for (int i=2;i<in;i+=2) if (isPowerOfTwo(EulerUtils.gcd(i,in))) ++result;
		return result;
	}
	
	public static void main(String[] args)	{
		{
			Random rand=new Random(System.currentTimeMillis());
			for (int i=6;i<=9;++i)	{
				long limit=LongMath.pow(10l,i);
				SumOfTotientCalculator base=SumOfTotientCalculator.getWithoutMod();
				long realValue=base.getTotientSum(limit);
				for (int j=0;j<10;++j)	{
					long mod=rand.nextInt(90000000)+10000000;
					SumOfTotientCalculator withMod=SumOfTotientCalculator.getWithMod(mod);
					long modValue=withMod.getTotientSum(limit);
					if ((realValue%mod)!=modValue) throw new RuntimeException("Mierda. Con i="+i+", esperaba "+(realValue%mod)+" y me ha salido "+mod+".");
				}
			}
		}
		{
			long mod=1_000_000_007l;
			SumOfTotientCalculator calc=SumOfTotientCalculator.getWithMod(mod);
			for (int i=6;i<=11;++i)	{
				long limit=LongMath.pow(10l,i);
				long tic=System.nanoTime();
				long result=calc.getTotientSum(limit);
				long tac=System.nanoTime();
				double seconds=(tac-tic)*1e-9;
				System.out.println("S(10^"+i+") = "+result+".");
				System.out.println("Elapsed "+seconds+" seconds.");
			}
		}
		{
			SumOfTotientCalculator sumCalc=SumOfTotientCalculator.getWithoutMod();
			TotientCalculator totCalc=new TotientCalculator(1000000);
			int ok=0;
			for (int i=2;i<=1000000;++i)	{
				long diff=sumCalc.getTotientSum(i)-sumCalc.getTotientSum(i-1);
				long realValue=totCalc.getTotient(i);
				if (diff!=realValue) System.out.println("D'OH! Para i="+i+" esperaba "+realValue+" pero la diferencia que me sale es "+diff+".");
				else ++ok;
			}
			System.out.println(ok+" resultados correctos.");
		}
		{
			int limit=20000;
			TotientCalculator totCalc=new TotientCalculator(limit);
			for (int i=2;i<=limit;i+=2)	{
				long res1=totientBasedCounter(i,totCalc);
				int res2=bruteForceBasedCounter(i);
				if (res1!=res2) System.out.println("D'OH! Para i="+i+" esperaba "+res2+" pero me ha salido "+res1+".");
			}
		}
	}
}
