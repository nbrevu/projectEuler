package com.euler.common;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils.CombinatorialNumberModCache;

public class BernoulliNumberModCache	{
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
		if (p==i) bernoulli=-bernoulli;	// Special stupid case for B1.
		long undivided=(bernoulli*combinatorials.get(p+1,i))%mod;
		return (undivided*inverses.get(p+1))%mod;
	}
	public long getFaulhaberSum(int p,long n)	{
		// The key step is taking the modulus. For some reason, using BigInteger yields wrong results.
		n=n%mod;
		long currentPower=n;
		long result=0;
		for (int i=1;i<=p+1;++i)	{
			long augend=(getFaulhaberCoefficient(p,i)*currentPower)%mod;
			result=(result+augend)%mod;
			currentPower=(currentPower*n)%mod;
		}
		if (result<0) result+=mod;
		return result;
	}
}