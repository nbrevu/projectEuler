package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.Primes;

public class Euler625 {
	private final static long START=500000000l;
	private final static long END=100000000000l;
	
	private static List<Long> getNumbersOutsideBounds(long start,long end)	{
		List<Long> primes=Primes.listLongPrimes(end/start);
		List<Long> allTheNumbers=new ArrayList<>();
		allTheNumbers.add(1l);
		for (long p:primes)	{
			List<Long> newNumbers=new ArrayList<>();
			for (long n:allTheNumbers) for (;;)	{
				n*=p;
				if (n>end) break;
				newNumbers.add(n);
			}
			allTheNumbers.addAll(newNumbers);
		}
		List<Long> finalSet=new ArrayList<>();
		for (Long n:allTheNumbers) if (n>start) finalSet.add(n);
		return finalSet;
	}
	// This is just a stub...
	private static class GcdSumCalculator	{
		// This class calculates and stores, for each N, the value sum(gcd(i,N),i,N).
		private long[] gcdSum;
		public GcdSumCalculator(int N)	{
			this(N,Primes.firstPrimeSieve(N));
		}
		public GcdSumCalculator(int N,int[] firstPrimes)	{
			gcdSum=new long[1+N];
			gcdSum[0]=0;
			gcdSum[1]=1;
			calculate(gcdSum,firstPrimes);
		}
		private static void calculate(long[] result,int[] firstPrimes)	{
			for (int i=2;i<result.length;++i)	{
				int p=firstPrimes[i];
				if (p==0)	{
					result[i]=2*i-1;
					continue;
				}
				// So p|n. Let's find k, coprime with p, so that n=(p^a)*k. 
				int n=i/p;
				int factor=p;
				while ((n%p)==0)	{
					n/=p;
					factor*=p;
				}
				if (n==1)	{
					// n=p^a. We need a specific formula.
					int prevPower=i/p;
					long prevSum=result[prevPower];
					result[i]=p*prevSum+i-prevPower;
				}	else	{
					// Multiplicity of the SumGcd function comes in handy!
					result[i]=result[n]*result[factor];
				}
			}
		}
		public long getSumGcd(int i)	{
			return gcdSum[i];
		}
		public long[] getSumGcd()	{
			// Yes, this is unsafe, but I don't have that much memory. Sue me.
			return gcdSum;
		}
		public long[] getCumSum()	{
			long[] result=new long[gcdSum.length];
			result[0]=gcdSum[0];
			for (int i=1;i<gcdSum.length;++i) result[i]=result[i-1]+gcdSum[i];
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		List<Long> outsideBounds=getNumbersOutsideBounds(START,END);
		GcdSumCalculator calculator=new GcdSumCalculator((int)START);
		long[] sumGcd=calculator.getSumGcd();
		long[] cumSum=calculator.getCumSum();
		long tac=System.nanoTime();
		double seconds=((double)(tac-tic))/1e9;
		System.out.println(outsideBounds.size());
		System.out.println(sumGcd.length);
		System.out.println(cumSum.length);
		System.out.println("He tardado "+seconds+" segundos en generar los datos tochos.");
		/*
		long maxSum=0;
		int maxIdx=0;
		for (int i=1;i<=START;++i) if (maxSum<=calculator.getSumGcd(i))	{
			maxIdx=i;
			maxSum=calculator.getSumGcd(i);
		}
		System.out.println("Atiende qué gañanazo: f("+maxIdx+")="+maxSum+".");
		long sum=0;
		for (int i=1;i<START;++i)	{
			long newSum=sum+calculator.getSumGcd(i);
			if (newSum<sum)	{
				System.out.println("Al sumar el elemento "+i+" me he pasado del límite.");
				return;
			}
			sum=newSum;
		}
		System.out.println("La suma burrísima es "+sum+".");
		// 1615149201161278240, cabe en un long :).
		 */
	}
}
