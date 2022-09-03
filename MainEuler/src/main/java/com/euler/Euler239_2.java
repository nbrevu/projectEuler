package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Euler239_2 {
	private final static int TOTAL=100;
	private final static int PRIMES=25;
	private final static int DERANGED=22;
	
	private static class FactorialCache	{
		private List<BigInteger> cache;
		public FactorialCache(int precalculation)	{
			cache=new ArrayList<>();
			cache.add(BigInteger.ONE);
			addToCache(1,precalculation);
		}
		private void addToCache(int start,int end)	{
			BigInteger last=cache.get(cache.size()-1);
			for (int i=start;i<=end;++i)	{
				last=last.multiply(BigInteger.valueOf(i));
				cache.add(last);
			}
		}
		public BigInteger get(int index)	{
			if (index>=cache.size()) addToCache(cache.size(),index);
			return cache.get(index);
		}
	}
	
	private static class CombinatorialNumberCache	{
		private List<List<BigInteger>> cache;
		public CombinatorialNumberCache(int precalculation)	{
			cache=new ArrayList<>();
			cache.add(Arrays.asList(BigInteger.ONE));
			addToCache(1,precalculation);
		}
		private void addToCache(int start,int end)	{
			List<BigInteger> lastRow=cache.get(cache.size()-1);
			for (int i=start;i<=end;++i)	{
				List<BigInteger> nextRow=new ArrayList<>(lastRow.size()+1);
				nextRow.add(BigInteger.ONE);
				for (int j=1;j<i;++j) nextRow.add(lastRow.get(j).add(lastRow.get(j-1)));
				nextRow.add(BigInteger.ONE);
				cache.add(nextRow);
				lastRow=nextRow;
			}
		}
		public BigInteger get(int n,int k)	{
			if (k>n) return BigInteger.ZERO;
			if (n>=cache.size()) addToCache(cache.size(),n);
			return cache.get(n).get(k);
		}
		public BigInteger get(int n,int k1,int k2)	{
			// n!/(k1!*k2!*(n-k1-k2)!) = n!/(k1!*(n-k1)!) * (n-k1)!/(k2!*(n-k1-k2)!)
			return get(n,k1).multiply(get(n-k1,k2));
		}
		public BigInteger get(int n,int k1,int k2,int k3)	{
			// Same, although one step more.
			return get(n,k1).multiply(get(n-k1,k2,k3));
		}
	}
	
	private static class DerangementCache	{
		private List<BigInteger> cache;
		public DerangementCache(int precalculation)	{
			cache=new ArrayList<>();
			cache.add(BigInteger.ONE);
			cache.add(BigInteger.ZERO);
			addToCache(2,precalculation);
		}
		private void addToCache(int start,int end)	{
			BigInteger prev=cache.get(cache.size()-1);
			BigInteger prev2=cache.get(cache.size()-2);
			for (int i=start;i<=end;++i)	{
				BigInteger newVal=prev.add(prev2).multiply(BigInteger.valueOf(i-1));
				cache.add(newVal);
				prev2=prev;
				prev=newVal;
			}
		}
		public BigInteger get(int index)	{
			if (index>=cache.size()) addToCache(cache.size(),index);
			return cache.get(index);
		}
	}
	
	public static void main(String[] args)	{
		// Dividimos los primos en: 3 + a + b + c, con 3+a+b+c=25.
		// Dividimos los no primos en: b + c + x, con c+x=75. 
		// Tenemos dos grados de libertad: a y b.
		// Además, tiene que ser b<=c, por tanto b<=(22-a-b), por tanto b<=(22-a)/2.
		// Los primos que hay en "a" son primos que representan un derangement. Están todos, pero en posiciones bailongas.
		// "b" representa los primos que están en posiciones P tales que el primo P NO está en posición prima.
		// A su vez, en las posiciones "b" sólo hay no primos.
		// "c" representa los primos que están en posiciones no primas.
		// Van a hacer falta también "c" no primos que se muevan a posiciones primas.
		
		// Esto sigue estando mal :(.
		CombinatorialNumberCache combinatorials=new CombinatorialNumberCache(TOTAL);
		FactorialCache factorials=new FactorialCache(TOTAL);
		DerangementCache derangeds=new DerangementCache(DERANGED);
		BigInteger sum=BigInteger.ZERO;
		int fixed=PRIMES-DERANGED;
		int nonPrimes=TOTAL-PRIMES;
		for (int a=0;a<=DERANGED;++a) if (a!=1)	{
			int limB=(DERANGED-a)/2;
			for (int b=0;b<=limB;++b)	{
				int c=DERANGED-a-b;
				BigInteger distributePrimes=combinatorials.get(PRIMES,fixed,a,b);
				BigInteger distributeComposites=combinatorials.get(nonPrimes,b,c);
				BigInteger distributePrimesPositionsA=derangeds.get(a);
				BigInteger distributePrimesPositionsB=factorials.get(b);
				BigInteger distributePrimesPositionsC=factorials.get(c);
				BigInteger distributeCompositePositions=factorials.get(nonPrimes);
				BigInteger total=distributePrimes.multiply(distributeComposites).multiply(distributePrimesPositionsA).multiply(distributePrimesPositionsB).multiply(distributePrimesPositionsC).multiply(distributeCompositePositions);
				sum=sum.add(total);
			}
		}
		BigInteger denominator=factorials.get(TOTAL);
		System.out.println(sum);
		System.out.println(denominator);
	}
}
