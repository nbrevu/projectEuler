package com.euler;

import com.euler.common.EulerUtils;

public class Euler792_7 {
	private final static int N=10000;
	private final static int MAX_DIGITS=31;
	
	private static class ModPow2	{
		public long oddPart;
		public int exp;
		public ModPow2(long oddPart,int exp)	{
			this.oddPart=oddPart;
			this.exp=exp;
		}
		public ModPow2 copy()	{
			return new ModPow2(oddPart,exp);
		}
	}
	
	private static class ModCalculator	{
		private final int maxPower;
		private final long mod;
		private final long mask;
		private final ModPow2 modData;
		private long currentN;
		public ModCalculator(int maxPower)	{
			this.maxPower=maxPower;
			mod=1l<<maxPower;
			mask=mod-1;
			modData=new ModPow2(1l,0);
			currentN=0;
		}
		private void increase(long amount)	{
			long product1=1;
			long product2=1;
			for (int i=1;i<=amount;++i)	{
				++currentN;
				++modData.exp;
				long n_=currentN+1;
				product1*=2*currentN+1;
				product1&=mask;
				while ((n_&1)==0)	{
					--modData.exp;
					n_>>=1;
				}
				product2*=n_;
				product2&=mask;
			}
			modData.oddPart*=product1;
			modData.oddPart&=mask;
			modData.oddPart*=EulerUtils.modulusInverse(product2,mod);
			modData.oddPart&=mask;
		}
		private int getExtraValue()	{
			long currentValue=(modData.oddPart<<modData.exp)&mask;
			ModPow2 combi=modData.copy();
			int result=0;
			long a=1;
			while ((currentValue&1)==0)	{
				++result;
				long num=currentN-a+1;
				while ((num&1)==0)	{
					++combi.exp;
					num>>=1;
				}
				num&=mask;
				long denom=currentN+a+1;
				while ((denom&1)==0)	{
					--combi.exp;
					denom>>=1;
				}
				denom&=mask;
				combi.oddPart*=num;
				combi.oddPart&=mask;
				combi.oddPart*=EulerUtils.modulusInverse(denom,mod);
				combi.oddPart&=mask;
				long longCombi=(combi.oddPart<<combi.exp)&mask;
				currentValue=longCombi-(currentValue>>1);
				++a;
			}
			if (result>=maxPower) throw new IllegalArgumentException("T'has pasao, macho t'has pasao.");
			return result;
		}
		public long getU(long n)	{
			increase(n-currentN);
			return n+2+getExtraValue();
		}
	}
	
	/*
	2500500025183625
	Elapsed 1814.3571435000001 seconds.
	
	Fuck, it's wrong :(. It's all because of 4373.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		ModCalculator calculator=new ModCalculator(MAX_DIGITS);
		long result=0;
		for (long i=1;i<=N;++i)	{
			long cube=i*i*i;
			long u=calculator.getU(cube);
			result+=u;
			System.out.println(String.format("u(%d^3)=%d=%d+%d.",i,u,cube,u-cube));
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
