package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.CombinatorialNumberModCache;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler487_2 {
	private final static long BASE=LongMath.pow(10l,12);
	private final static int POWER=10000;
	private final static int LIMIT_MIN=2000000000;
	private final static int LIMIT_MAX=2000002000;
	
	private static class InverseModCache	{
		private final long mod;
		private final Map<Long,Long> cache;
		public InverseModCache(long mod)	{
			this.mod=mod;
			cache=new HashMap<>();
		}
		public long get(long in)	{
			return cache.computeIfAbsent(in,(Long x)->{
				return EulerUtils.modulusInverse(x,mod);
			});
		}
	}
	
	private static class BernoulliNumberModCache	{
		private final long mod;
		// Only EVEN numbers are stored.
		private final List<Long> cache;
		private final CombinatorialNumberModCache combinatorials;
		private final InverseModCache inverses;
		public BernoulliNumberModCache(long mod)	{
			this.mod=mod;
			cache=new ArrayList<>();
			cache.add(1l);	// B0=1.
			combinatorials=new CombinatorialNumberModCache(0,mod);
			inverses=new InverseModCache(mod);
		}
		public long getBernoulli(int i)	{
			if (i==1) return -inverses.get(2l);
			else if ((i%2)==1) return 0l;
			i/=2;	// Even number -> we work with its half.
			if (cache.size()<=i) calculateUpTo(i);
			return cache.get(i);
		}
		private void calculateUpTo(int i)	{
			long minusB1=inverses.get(2l);	// -B1=-(-1/2)=1/2=2^(-1) (in our case we need modular arithmetic).
			while (cache.size()<=i)	{
				int k=cache.size();
				int kk=2*k;
				long result=minusB1;
				for (int j=0;j<k;++j)	{
					int jj=2*j;
					long augend=cache.get(j);
					augend=(augend*combinatorials.get(kk,jj))%mod;
					augend=(augend*inverses.get(kk+1-jj))%mod;
					result=(result-augend)%mod;
				}
				cache.add(result);
			}
		}
		public long getFaulhaberCoefficient(int p,int i)	{
			long bernoulli=getBernoulli(p+1-i);
			if (p==i) bernoulli=-bernoulli;	// Special stupid case.
			long undivided=(bernoulli*combinatorials.get(p+1,i))%mod;
			return (undivided*inverses.get(p+1))%mod;
		}
		public long getFaulhaberSum(int p,long n)	{
			// BigInteger is needed because n could be bigger than 2^63.
			BigInteger bigN=BigInteger.valueOf(n);
			BigInteger bigMod=BigInteger.valueOf(mod);
			BigInteger currentPower=bigN;
			long result=0;
			for (int i=1;i<=p+1;++i)	{
				long augend=(getFaulhaberCoefficient(p,i)*currentPower.longValueExact())%mod;
				result=(result+augend)%mod;
				currentPower=currentPower.multiply(bigN).mod(bigMod);
			}
			if (result<0) result+=mod;
			return result;
		}
	}
	
	// Confirmed: Bernoulli numbers are being calculated correctly.
	private static void assertBernoulli(long numerator,long denominator,long mod,long bernoulli)	{
		long result=(denominator*bernoulli)%mod;
		long diff=(result-numerator)%mod;
		if (diff!=0) throw new RuntimeException("Expected "+numerator+", found "+result+".");
	}
	
	private static void assertBernoullis()	{
		long mod=LongMath.pow(10l,9)+7;
		BernoulliNumberModCache bernoulliCalculator=new BernoulliNumberModCache(mod);
		assertBernoulli(-1,2,mod,bernoulliCalculator.getBernoulli(1));
		assertBernoulli(1,6,mod,bernoulliCalculator.getBernoulli(2));
		assertBernoulli(-1,30,mod,bernoulliCalculator.getBernoulli(4));
		assertBernoulli(1,42,mod,bernoulliCalculator.getBernoulli(6));
		assertBernoulli(-1,30,mod,bernoulliCalculator.getBernoulli(8));
		assertBernoulli(5,66,mod,bernoulliCalculator.getBernoulli(10));
		assertBernoulli(-691,2730,mod,bernoulliCalculator.getBernoulli(12));
		assertBernoulli(7,6,mod,bernoulliCalculator.getBernoulli(14));
		assertBernoulli(-3617,510,mod,bernoulliCalculator.getBernoulli(16));
		assertBernoulli(43867,798,mod,bernoulliCalculator.getBernoulli(18));
		assertBernoulli(-174611,330,mod,bernoulliCalculator.getBernoulli(20));
	}
	
	private static long getResult(long n,int k,long mod)	{
		BernoulliNumberModCache bernoulliCalculator=new BernoulliNumberModCache(mod);
		long s1=bernoulliCalculator.getFaulhaberSum(k,n);
		s1=(s1*((n+1)%mod))%mod;
		long s2=bernoulliCalculator.getFaulhaberSum(k+1,n);
		long result=s1-s2;
		if (result<0) result+=mod;
		System.out.println("Mod="+mod+": result="+result+".");
		return result;
	}
	
	public static void main(String[] args)	{
		// Well, this is not correct :(.
		// "106635546604".
		long tic=System.nanoTime();
		long result=0;
		boolean[] composites=Primes.sieve(LIMIT_MAX);
		for (int i=LIMIT_MIN;i<=LIMIT_MAX;++i) if (!composites[i]) result+=getResult(BASE,POWER,i);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
