package com.euler;

public class Euler760_3 {
	private final static int LIMIT=100_000;
	
	private static long calculateFBruteForce(int n)	{
		long result=0;
		for (int k=0;k<=n;++k)	{
			int nk=n-k;
			result+=k^nk;
			result+=k|nk;
			result+=k&nk;
		}
		return result;
	}
	
	private static class FineFCalculator	{
		private final long[] values;
		
		public FineFCalculator(int limit)	{
			values=new long[1+limit];
		}
		public long getF(int x)	{
			long result=doCalculateF(x);
			values[x]=result;
			return result;
		}
		private long doCalculateF(int x)	{
			long shift=0;
			while ((x&1)==0)	{
				x>>=1;
				++shift;
			}
			if (x==1)	{
				// Power of 2 (primitive value). Note the similarity with the suffix formula.
				return (1l<<(2*shift+1))+(1l<<(shift+1))-(shift<<shift);
			}
			x>>=1;
			long suffixId=(1<<shift);
			long prefixShift=suffixId<<1;
			long normalPrefix=values[x]*prefixShift*(suffixId+1);
			long normalSuffix=(x+1)*values[(int)suffixId];
			long specialPrefix=values[x-1]*prefixShift*(suffixId-1);
			long specialSuffix=x*(values[(int)suffixId]+2*suffixId*(suffixId-3));
			return normalPrefix+normalSuffix+specialPrefix+specialSuffix;
		}
	}
	
	public static void main(String[] args)	{
		FineFCalculator calculator=new FineFCalculator(LIMIT);
		for (int i=1;i<=LIMIT;++i)	{
			long fBruteForce=calculateFBruteForce(i);
			long fFinesse=calculator.getF(i);
			if (fBruteForce!=fFinesse)	{
				String message=String.format("For n=%d I expected %d but the real result is %d.",i,fFinesse,fBruteForce);
				throw new RuntimeException(message);
			}
		}
		for (int i=1;i<=100;++i)	{
			long sum=0;
			for (int j=1;j<=i;++j) sum+=calculator.getF(j);
			System.out.println("Sum for N="+i+": "+sum+".");
		}
	}
}
