package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.euler.common.Primes;
import com.google.common.collect.Collections2;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;

public class Euler273 {
	private final static int N=150;
	
	private static int[] getPrimes(int n)	{
		List<Integer> primes=Primes.listIntPrimes(n);
		Collection<Integer> filtered=Collections2.filter(primes,(Integer i)->(i%4)==1);
		return Ints.toArray(filtered);
	}
	
	// Vaaale. Itero por el "índice" (desde 0 hasta 2^N-1, donde N es el número de primos), y para cada índice aplico:
	// https://mathoverflow.net/questions/29644/enumerating-ways-to-decompose-an-integer-into-the-sum-of-two-squares
	private static class OrderedPair	{
		public final long n1;
		public final long n2;
		public OrderedPair(long n1,long n2)	{
			if (n1<n2)	{
				this.n1=n1;
				this.n2=n2;
			}	else	{
				this.n1=n2;
				this.n2=n1;
			}
		}
		public static OrderedPair combine(OrderedPair p1,OrderedPair p2,boolean revert)	{
			long a=p1.n1;
			long b=p1.n2;
			long c=revert?p2.n2:p2.n1;
			long d=revert?p2.n1:p2.n2;
			long n1=a*c+b*d;
			long n2=Math.abs(a*d-b*c);
			return new OrderedPair(n1,n2);
		}
	}
	private static class OrderedPairList	{
		// Wrapper class because Java is very idiot and can't declare arrays of generic objects.
		public final List<OrderedPair> list;
		public OrderedPairList(List<OrderedPair> list)	{
			this.list=list;
		}
		public static OrderedPairList combine(OrderedPairList l1,OrderedPairList l2)	{
			List<OrderedPair> result=new ArrayList<>();
			for (OrderedPair p1:l1.list) for (OrderedPair p2:l2.list)	{
				result.add(OrderedPair.combine(p1,p2,false));
				result.add(OrderedPair.combine(p1,p2,true));
			}
			return new OrderedPairList(result);
		}
	}
	private static class OrderedPairSearcher	{
		private final int[] primes;
		public OrderedPairSearcher(int[] primes)	{
			this.primes=primes;
		}
		public List<OrderedPair> getFullList()	{
			int size=IntMath.pow(2,primes.length);
			List<OrderedPair> result=new ArrayList<>();
			OrderedPairList[] storage=new OrderedPairList[size];
			storage[0]=new OrderedPairList(Collections.emptyList());
			for (int i=1;i<size;++i)	{
				int firstBit=findFirstBit(i);
				int remainder=i-firstBit;
				if (remainder==0) storage[i]=findByBruteForce(primes[IntMath.log2(firstBit,RoundingMode.UNNECESSARY)]);
				else storage[i]=OrderedPairList.combine(storage[firstBit],storage[remainder]);
				result.addAll(storage[i].list);
			}
			return result;
		}
		private int findFirstBit(int in)	{
			int bit=1;
			while ((in&bit)==0) bit+=bit;
			return bit;
		}
		private OrderedPairList findByBruteForce(int prime)	{
			// There should be a single one.
			for (int i=1;;++i)	{
				int i2=i*i;
				for (int j=(i+1);;++j)	{
					int j2=j*j;
					int s=i2+j2;
					if (s==prime) return new OrderedPairList(Collections.singletonList(new OrderedPair(i,j)));
					else if (s>prime) break;
				}
			}
		}
	}
	
	private static long sumFirst(List<OrderedPair> list)	{
		long result=0;
		for (OrderedPair p:list) result+=p.n1;
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] primes=getPrimes(N);
		OrderedPairSearcher searcher=new OrderedPairSearcher(primes);
		List<OrderedPair> pairs=searcher.getFullList();
		long result=sumFirst(pairs);
		long tac=System.nanoTime();
		double seconds=(tac-tic)/1e9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
