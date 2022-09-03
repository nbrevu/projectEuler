package com.euler;

import java.util.Arrays;
import java.util.function.LongConsumer;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils.LongPair;
import com.euler.common.Primes;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

// This is crude and slow, but it works.
public class Euler513_2 {
	private final static int L=100000;
	private final static long N=L*(long)L;
	
	private final static int FIRST_PRIME_SIZE=1_000_000_000;
	
	private static class CaseCounter	{
		private static void addFactors(LongSet existing,long p,long limit)	{
			LongSet toAdd=HashLongSets.newMutableSet();
			for (LongCursor cursor=existing.cursor();cursor.moveNext();)	{
				long val=cursor.elem()*p;
				while (val<limit)	{
					toAdd.add(val);
					val*=p;
				}
			}
			toAdd.forEach((LongConsumer)(existing::add));
		}
		private static int[] getMultipliers(long[] factorsArray,int l)	{
			int[] result=new int[1+l];
			int writeIndex=0;
			for (int i=0;i<factorsArray.length;++i)	{
				for (int j=writeIndex;j<factorsArray[i];++j) result[j]=i;
				writeIndex=(int)factorsArray[i];
			}
			for (int j=writeIndex;j<result.length;++j) result[j]=factorsArray.length;
			return result;
		}
		private final int l;
		private final int[] multipliers;
		public CaseCounter(int l)	{
			this.l=l;
			boolean[] composites=Primes.sieve(l);
			LongSet factors=HashLongSets.newMutableSet();
			factors.add(1l);
			addFactors(factors,2l,l);
			for (int i=3;i<composites.length;i+=4) if (!composites[i]) addFactors(factors,i,l);
			long[] factorsArray=factors.toLongArray();
			Arrays.sort(factorsArray);
			multipliers=getMultipliers(factorsArray,l);
		}
		private long countCases(BaseSquareDecomposition decompXy,BaseSquareDecomposition decompAb)	{
			int result=0;
			for (LongPair ab:decompAb.getBaseCombinations())	{
				long a=ab.x;
				if (a==0) continue;
				long b=ab.y;
				for (LongPair xy:decompXy.getBaseCombinations())	{
					long x=xy.x;
					long y=xy.y;
					long x2=x+x;
					if ((x2<=l)&&(b<=x2)&&(a+b>x2)) result+=multipliers[(int)(l/x2)];
					if (x!=y)	{
						long y2=y+y;
						if ((y2<=l)&&(b<=y2)&&(a+b>y2)) result+=multipliers[(int)(l/y2)];
					}
				}
			}
			return result;
		}
		public long countCases(BaseSquareDecomposition decompXy,boolean countDouble)	{
			BaseSquareDecomposition decompAb=decompXy.scramble();
			long result=countCases(decompXy,decompAb);
			if (countDouble) result+=countCases(decompAb,decompXy.scale(2l));
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		PrimeDecomposer primeDecomposer=new StandardPrimeDecomposer(FIRST_PRIME_SIZE);
		long result=0;
		SumSquareDecomposer decomposer=new SumSquareDecomposer();
		CaseCounter counter=new CaseCounter(L);
		long show=100_000_000l;
		for (long i=5;i<=N;i+=4)	{
			if (i>=show)	{
				System.out.println(show+"...");
				show+=100_000_000;
			}
			DivisorHolder decomp=primeDecomposer.decompose(i);
			boolean isValid=true;
			int expSum=0;
			for (LongIntCursor cursor=decomp.getFactorMap().cursor();cursor.moveNext();) if ((cursor.key()&3)==3) isValid=false;
			else expSum+=cursor.value();
			if ((!isValid)||(expSum<2)) continue;
			BaseSquareDecomposition decompXy=decomposer.getFor(decomp);
			if (decompXy.getBaseCombinations().size()>10000) System.out.println(String.format("ACHTUNG! For n=%d I have %d combinations.",i,decompXy.getBaseCombinations().size()));
			result+=counter.countCases(decompXy,i*2<=N);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
