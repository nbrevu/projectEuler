package com.euler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler531 {
	private final static int LIMIT=1005000;
	private final static int START=1000000;
	
	private final static int[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
	
	private static void addFactor(Map<Long,Integer> map,long prime)	{
		Integer cur=map.get(prime);
		int next=1+((cur==null)?0:cur.intValue());
		map.put(prime,next);
	}

	private static class Factors	{
		public final Map<Long,Integer> factors;
		public Factors(int n)	{
			Map<Long,Integer> divs=new HashMap<>();
			for (;;)	{
				int prime=firstPrimes[n];
				if (prime==0)	{
					addFactor(divs,n);
					break;
				}
				addFactor(divs,prime);
				n/=prime;
			}
			factors=Collections.unmodifiableMap(divs);
		}
	}
	
	private static class NumberAndTotient	{
		public final int n;
		public final Factors nFactors;
		public final int tot;
		public NumberAndTotient(int n)	{
			this.n=n;
			nFactors=new Factors(n);
			long tmpTot=n;
			for (long prime:nFactors.factors.keySet()) {
				tmpTot/=prime;
				tmpTot*=prime-1;
			}
			tot=(int)tmpTot;
		}
	}
	
	private static NumberAndTotient[] getInitialData(int start,int limit)	{
		NumberAndTotient[] result=new NumberAndTotient[limit-start];
		for (int i=start;i<limit;++i) result[i-start]=new NumberAndTotient(i);
		return result;
	}
	
	private static long solve(NumberAndTotient n1,NumberAndTotient n2)	{
		long gcd=EulerUtils.gcd(n1.n,n2.n);
		long mod1=n1.tot%gcd;
		long mod2=n2.tot%gcd;
		if (mod1!=mod2) return 0l;
		else return mod1+gcd*EulerUtils.solveChineseRemainder(n1.tot/gcd,n1.n/gcd,n2.tot/gcd,n2.n/gcd);
	}
	
	public static void main(String[] args)	{
		long sum=0l;
		NumberAndTotient[] data=getInitialData(START,LIMIT);
		int N=data.length;
		for (int i=0;i<N-1;++i) for (int j=i+1;j<N;++j) sum+=solve(data[i],data[j]);
		System.out.println(sum);
	}
}
