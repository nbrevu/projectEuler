package com.euler;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler801_3 {
	private final static long LIMIT=100l;
	
	public static void main(String[] args)	{
		long[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
		long[] primes=Primes.listLongPrimesAsArray(LIMIT);
		long result=0;
		for (long p:primes)	{
			long thisResult=0;
			long lim=p*(p-1);
			SortedMap<Long,Long> gs=new TreeMap<>();
			for (int x=1;x<=lim;++x)	{
				long g=0;
				for (int y=1;y<=lim;++y)	{
					long p1=EulerUtils.expMod(x,y,p);
					long p2=EulerUtils.expMod(y,x,p);
					if (p1==p2)	{
						++g;
					}
				}
				thisResult+=g;
				EulerUtils.increaseCounter(gs,g,1l);
			}
			System.out.println(String.format("f(%d)=%d=%d*%d.",p,thisResult,p-1,thisResult/(p-1)));
			System.out.println(String.format("Distribution of g for %d: %s.",p,gs));
			DivisorHolder p_1Decomp=DivisorHolder.getFromFirstPrimes(p-1,firstPrimes);
			long[] p_1Divs=p_1Decomp.getUnsortedListOfDivisors().toLongArray();
			Arrays.sort(p_1Divs);
			if (p_1Divs.length!=gs.size()) throw new IllegalStateException("Ay qué corcho con el tío Paco.");
			for (long d:p_1Divs)	{
				long k=d*(p-1);
				if (!gs.containsKey(k)) throw new IllegalStateException("Ay qué corcho con el tío Paco.");
				// AND THERE IS IT :D.
				// https://oeis.org/A007434
				System.out.println(String.format("\tg(%d,%d)=g(%d,%d*%d)=g(%d,(%d)^2/%d)=%d.",p,k,p,p-1,d,p,p-1,(p-1)/d,gs.get(k).longValue()));
			}
			result+=thisResult;
		}
		System.out.println(result);
	}
}
