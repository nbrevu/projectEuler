package com.euler;

import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.LongPair;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler264 {
	public final static long N=100000;
	
	private static class SquareSumFinder	{
		private final long[] lastPrimes;
		private final LongObjMap<BaseSquareDecomposition> baseCases;
		public SquareSumFinder(long limit)	{
			lastPrimes=Primes.lastPrimeSieve(limit);
			baseCases=HashLongObjMaps.newMutableMap();
		}
		private BaseSquareDecomposition getFromSinglePrime(long prime,int power)	{
			if (prime==2l)	{
				long base=LongMath.pow(2l,power/2);
				return ((power%2)==1)?new BaseSquareDecomposition(base,base):new BaseSquareDecomposition(0,base);
			}	else if ((prime%4)==3) return new BaseSquareDecomposition(0,LongMath.pow(prime,power/2));
			else if (power==1) return new BaseSquareDecomposition(EulerUtils.hermiteAlgorithm(prime));
			else return baseCases.get(prime).combineWith(baseCases.get(LongMath.pow(prime,power-1)));
		}
		public BaseSquareDecomposition getAllDecompositions(long value)	{
			// Assumes that every valid number below "value" is already known, therefore no recursive calls to computeIfAbsent.
			// This might not be true if I find that I can filter by multiples of 5. 
			DivisorHolder holder=DivisorHolder.getFromFirstPrimes(value,lastPrimes);
			LongIntMap decomp=holder.getFactorMap();
			for (LongIntCursor cursor=decomp.cursor();cursor.moveNext();) if (((cursor.key()%4)==3)&&((cursor.value()%2)==1)) return BaseSquareDecomposition.EMPTY;
			return baseCases.computeIfAbsent(value,(long n)->	{
				LongIntCursor cursor=decomp.cursor();
				cursor.moveNext();
				long aPrime=cursor.key();
				int aPower=cursor.value();
				if (decomp.size()==1) return getFromSinglePrime(aPrime,aPower);
				else	{
					long f1=LongMath.pow(aPrime,aPower);
					long f2=n/f1;
					return baseCases.get(f1).combineWith(baseCases.get(f2));
				}
			});
		}
	}
	
	private static LongObjMap<LongSet> getYToXMap(List<LongPair> points)	{
		LongObjMap<LongSet> result=HashLongObjMaps.newMutableMap();
		for (LongPair p:points) result.computeIfAbsent(p.y,(long unused)->HashLongSets.newMutableSet()).add(p.x);
		return result;
	}
	
	private static double hypotenuse(long ax,long bx,long ay,long by)	{
		long dx=bx-ax;
		long dy=by-ay;
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		// The highest value we actually need is 343040465.
		long maxR2=LongMath.divide(N*N,20,RoundingMode.UP);
		SquareSumFinder finder=new SquareSumFinder(maxR2);
		double result=0;
		for (long n=5;n<=maxR2;n+=2)	{
			// The only valid decompositions are those with n%5=0, but I'm still interested in storing the decompositions.
			BaseSquareDecomposition decomp=finder.getAllDecompositions(n);
			if ((n%10)!=5) continue;
			List<LongPair> combinations=decomp.getAllCombinations();
			LongObjMap<LongSet> sortedCombinations=getYToXMap(combinations);
			for (int ia=0;ia<combinations.size();++ia)	{
				LongPair a=combinations.get(ia);
				if (a.y>=0) break;
				long maxB=-a.y/2;
				for (int ib=ia+1;ib<combinations.size();++ib)	{
					LongPair b=combinations.get(ib);
					if (b.y>maxB) break;
					long cy=-a.y-b.y;
					long cx=5-a.x-b.x;
					if ((cy==b.y)&&(cx<=b.x)) continue;
					LongSet cSet=sortedCombinations.get(cy);
					if ((cSet!=null)&&cSet.contains(cx))	{
						double perimeter=hypotenuse(a.x,b.x,a.y,b.y)+hypotenuse(a.x,cx,a.y,cy)+hypotenuse(cx,b.x,cy,b.y);
						if (perimeter<=N) result+=perimeter;
					}
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.4f",result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
