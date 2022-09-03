package com.euler;

import java.util.Arrays;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.LongConsumer;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.BaseSquareDecomposition.PrimePowerDecompositionFinder;
import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.euler.common.EulerUtils.LongPair;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

/*
Elapsed 299.7708278 seconds.
2925619196
 */
public class Euler513_3 {
	private final static int L=100_000;
	private final static long N=L*(long)L;
	
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
		long result=0;
		CombinatorialNumberCache combis=new CombinatorialNumberCache(15);
		NavigableMap<Long,BaseSquareDecomposition> decomps=new TreeMap<>();
		decomps.put(1l,new BaseSquareDecomposition(new LongPair(0,1)));
		CaseCounter counter=new CaseCounter(L);
		boolean[] composites=Primes.sieve((int)(N/5));
		for (int p=5;p<composites.length;p+=4) if (!composites[p])	{
			PrimePowerDecompositionFinder finder=new PrimePowerDecompositionFinder(p,combis);
			long limit=N/p;
			decomps.tailMap(limit,false).clear();
			LongObjMap<BaseSquareDecomposition> toAdd=HashLongObjMaps.newMutableMap();
			for (Map.Entry<Long,BaseSquareDecomposition> entry:decomps.entrySet())	{
				long n=entry.getKey();
				BaseSquareDecomposition baseDecomp=entry.getValue();
				for (int i=1;;++i)	{
					n*=p;
					BaseSquareDecomposition primePowDecomp=finder.getFor(i);
					BaseSquareDecomposition newDecomp=baseDecomp.combineWith(primePowDecomp);
					if (n!=p) result+=counter.countCases(newDecomp,n*2<N);
					if (n<limit) toAdd.put(n,newDecomp);
					else break; 
				}
			}
			decomps.putAll(toAdd);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
