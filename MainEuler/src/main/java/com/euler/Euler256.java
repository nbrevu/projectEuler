package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;

public class Euler256 {
	private final static int LIMIT=100000000;
	private final static int GOAL=200;
	
	private static boolean isTatamiFreeWithMOdd(int m,int n)	{
		int den=m-1;
		for (int j=0;;++j)	{
			int num=n-2*j;
			if (num<(den*j)) return true;
			if ((num%den)==0)	{
				int q=num/den;
				// If q>=j, we've found a tatami. If q<j, there's no point in keeping on.
				return q<j;
			}
		}
	}
	
	private static boolean isCombNumberNonZero(int num,int den,int k)	{
		// Returns true if n=(num/den) is an integer and n>=k.
		if ((num%den)!=0) return false;
		/*
		int n=num/den;
		return n>=j;
		*/
		return num>=(den*k);
	}
	
	private static boolean isTatamiFreeWithMEven(int m,int n)	{
		int den=m-1;
		for (int j=0;;++j)	{
			int num=n-2*j;
			if (num<(den*j)) return true;
			if (isCombNumberNonZero(num,den,j)) return false;
			if (isCombNumberNonZero(num-1,den,j)) return false;
			num-=m;
			if (isCombNumberNonZero(num+2,den,j)) return false;
			if (isCombNumberNonZero(num,den,j)) return false;
		}
	}
	
	private static boolean isTatamiFree(int m,int n)	{
		if (m>n) throw new IllegalArgumentException();
		else if (m<=2) return false;
		else if ((m%2)==1) return isTatamiFreeWithMOdd(m,n);
		else return isTatamiFreeWithMEven(m,n);
	}
	
	private static class NumberProduct implements Comparable<NumberProduct>	{
		public final int a;
		public final int b;
		public final static NumberProduct BASE=new NumberProduct(1,1);
		public NumberProduct(int a,int b)	{
			this.a=a;
			this.b=b;
		}
		public List<NumberProduct> getChildren(int prime,int power)	{
			List<NumberProduct> result=new ArrayList<>();
			NumberProduct current=new NumberProduct(a,b*IntMath.pow(prime,power));
			result.add(current);
			for (int i=1;i<=power;++i)	{
				current=new NumberProduct(current.a*prime,current.b/prime);
				result.add(current);
			}
			return result;
		}
		@Override
		public int compareTo(NumberProduct o) {
			return a-o.a;
		}
	}
	
	private static class PrimeInformation	{
		private static int[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
		private final Map<Integer,Integer> divisors;
		public PrimeInformation(int number)	{
			divisors=new HashMap<>();
			while (number>=1)	{
				int prime=firstPrimes[number];
				if (prime==0)	{
					EulerUtils.increaseCounter(divisors,number);
					return;
				}	else	{
					EulerUtils.increaseCounter(divisors,prime);
					number/=prime;
				}
			}
		}
		public int getAmountOfDivisors()	{
			int result=1;
			for (int power:divisors.values()) result*=1+power;
			return result;
		}
		public List<NumberProduct> getCuratedListOfDecompositions()	{
			List<NumberProduct> current=Arrays.asList(NumberProduct.BASE);
			for (Map.Entry<Integer,Integer> primeFactor:divisors.entrySet())	{
				List<NumberProduct> next=new ArrayList<>(current.size()*(1+primeFactor.getValue()));
				for (NumberProduct prev:current) next.addAll(prev.getChildren(primeFactor.getKey(),primeFactor.getValue()));
				current=next;
			}
			current.sort(null);
			int N=current.size();
			if ((N%2)==0) return current.subList(0,N/2);
			else return current.subList(0,(1+N)/2);
		}
	}
	
	public static void main(String[] args)	{
		int minDivs=2*GOAL-1;
		for (int i=2;i<=LIMIT;i+=2)	{
			PrimeInformation info=new PrimeInformation(i);
			if (info.getAmountOfDivisors()<=minDivs) continue;
			List<NumberProduct> decomps=info.getCuratedListOfDecompositions();
			int s=decomps.size();
			int total=0;
			for (int j=0;j<s;++j)	{
				if (total+s-j<GOAL) break;
				NumberProduct np=decomps.get(j);
				if (isTatamiFree(np.a,np.b))	{
					++total;
					if (total>GOAL) break;
				}
			}
			if (total==GOAL)	{
				System.out.println(i);
				return;
			}
		}
	}
}
