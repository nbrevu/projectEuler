package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.FactorialCacheMod;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler374_3 {
	private final static long MOD=982451653l;
	private final static long LIMIT=LongMath.pow(10l,14);
	
	private static class InverseSumAccumulator	{
		private final LongLongMap inverseCache;
		private final long prime;
		private long currentValue;
		private long currentStart;
		private long currentEnd;
		public InverseSumAccumulator(long prime)	{
			inverseCache=HashLongLongMaps.newMutableMap();
			this.prime=prime;
			currentValue=0;
			currentStart=0;
			currentEnd=0;
		}
		private long calculateInverse(long q)	{
			return EulerUtils.modulusInverse(q,prime);
		}
		public long getInverse(long q)	{
			return inverseCache.computeIfAbsent(q,this::calculateInverse);
		}
		// End is NOT included.
		public long getInverseSum(long start,long end)	{
			if (start>2) return getInverseSumFromScratch(start,end);
			if ((start>=currentEnd)||(currentStart>=end)||(currentStart==currentEnd)) return getInverseSumFromScratch(start,end);
			adjustStart(start);
			adjustEnd(end);
			return currentValue;
		}
		private long getInverseSumFromScratch(long start,long end)	{
			long result=0;
			for (long i=start;i<end;++i) result+=getInverse(i);
			currentStart=start;
			currentEnd=end;
			currentValue=result%prime;
			return currentValue;
		}
		private void adjustStart(long start)	{
			if (start==currentStart) return;
			else if (start<currentStart) for (long i=start;i<currentStart;++i) currentValue=(currentValue+getInverse(i)%prime);
			else for (long i=currentStart;i<start;++i) currentValue=(currentValue+prime-getInverse(i))%prime;
			currentValue%=prime;
			currentStart=start;
		}
		private void adjustEnd(long end)	{
			if (end==currentEnd) return;
			else if (end<currentEnd) for (long i=end;i<currentEnd;++i) currentValue=(currentValue+prime-getInverse(i))%prime;
			else for (long i=currentEnd;i<end;++i) currentValue=(currentValue+getInverse(i))%prime;
			currentValue%=prime;
			currentEnd=end;
		}
	}
	
	private static class MaximumProductCalculator	{
		private final InverseSumAccumulator accumulator;
		private final FactorialCacheMod factorials;
		private final long prime;
		public MaximumProductCalculator(long prime)	{
			accumulator=new InverseSumAccumulator(prime);
			factorials=new FactorialCacheMod(500000,prime);
			this.prime=prime;
		}
		public long getFullAddend(int n)	{
			// This gets the sum of f(n)*m(n) for the "triangular" batch [n(n+1)/2 .. (n+1)(n+2)/2).
			if (n==1) return 3l;
			return (getPartialSum(n,n-2)+getPenultimateSubaddend(n)+getLastSubaddend(n))%prime;
		}
		public long getIncompleteAddend(int n,int lastL)	{
			if ((lastL<0)||(lastL>n)||(n<=1)) throw new IllegalArgumentException();
			else if (lastL==n) return getFullAddend(n);
			else if (lastL==n-1) return (getPartialSum(n,n-2)+getPenultimateSubaddend(n))%prime;
			else return getPartialSum(n,lastL);
		}
		private long getPartialSum(int n,int lastL)	{
			long factor=(factorials.get(n+1)*(n-1))%prime;
			long sum=accumulator.getInverseSum(n-lastL,n+1);
			return (factor*sum)%prime;
		}
		private long getPenultimateSubaddend(int n)	{
			long nn=n;	// Casting to long to prevent overflow!
			long prod1=(((nn+2)*(nn-1)/2))%prime;
			return (prod1*factorials.get(n))%prime;
		}
		private long getLastSubaddend(int n)	{
			return (factorials.get(n+1)*n)%prime;
		}
	}
	
	private static class TriangularDecomposition	{
		public final long m;
		public final long l;
		private TriangularDecomposition(long m,long l)	{
			this.m=m;
			this.l=l;
		}
		private static TriangularDecomposition getFor(long n)	{
			long m=(LongMath.sqrt(8*n+1,RoundingMode.DOWN)-1)/2;
			long tri=(m*(m+1))/2;
			long l=n-tri;
			return new TriangularDecomposition(m,l);
		}
	}
	
	private static long getSum(long in)	{
		TriangularDecomposition decomp=TriangularDecomposition.getFor(in);
		MaximumProductCalculator calculator=new MaximumProductCalculator(MOD);
		long result=0;
		for (int i=1;i<(int)(decomp.m);++i) result=(result+calculator.getFullAddend(i))%MOD;
		result+=calculator.getIncompleteAddend((int)decomp.m,(int)decomp.l);
		result%=MOD;
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=getSum(LIMIT);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result+".");
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
