package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.LongStream;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.BaseSquareDecomposition.PrimePowerDecompositionFinder;
import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.euler.common.EulerUtils.LongPair;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler311_3 {
	public final static long N=10_000_000_000l;
	
	private static void addFactor(SortedSet<Long> result,long factor,long max)	{
		Set<Long> toAdd=new HashSet<>();
		for (Long value:result)	{
			long product=value*factor;
			if (product>max) break;
			do	{
				toAdd.add(product);
				product*=factor;
			}	while (product<=max);
		}
		result.addAll(toAdd);
	}
	
	private static long[] getMultipliers(long[] firstPrimes,long max)	{
		SortedSet<Long> result=new TreeSet<>();
		result.add(1l);
		addFactor(result,2l,max);
		for (long i=3;i<firstPrimes.length;i+=4) if (firstPrimes[(int)i]==0) addFactor(result,i,max);
		return result.stream().mapToLong(Long::longValue).toArray();
	}
	
	private static class SquareSumFinder	{
		private final long[] multipliers;
		private final long[] primes4k1;
		private final CombinatorialNumberCache combinatorials;
		private final long maxSquareSum;
		public SquareSumFinder(long n)	{
			maxSquareSum=n/4;
			long maxPrime=maxSquareSum/5;
			long maxMultiplier=maxSquareSum/52;
			long[] firstPrimes=Primes.firstPrimeSieve(maxPrime);
			multipliers=getMultipliers(firstPrimes,maxMultiplier);
			LongStream.Builder builder=LongStream.builder();
			for (int i=5;i<firstPrimes.length;i+=4) if (firstPrimes[i]==0l) builder.accept(i);
			primes4k1=builder.build().toArray();
			combinatorials=new CombinatorialNumberCache((int)Math.ceil(Math.log(n)/Math.log(5)));
		}
		private static boolean isTriangleInvalid(long a,long b,long c)	{
			return (a>=b+c)||(b>=c+a)||(c>=a+b);
		}
		private static boolean isQuadrilateralInvalid(long ab,long bc,long cd,long ad,long aoco,long bodo)	{
			return (ab<1)||(bc<=ab)||(cd<=bc)||(ad<=cd)||(aoco>bodo)||((ab*ab+bc*bc+cd*cd+ad*ad)>N);
		}
		private long countValidCases(BaseSquareDecomposition decomp,BaseSquareDecomposition decomp2,long multiplier)	{
			NavigableMap<Long,Long> as=new TreeMap<>();
			for (LongPair pair:decomp2.getBaseCombinations()) if (pair.x!=pair.y) as.put(pair.x,pair.y);
			long result=0l;
			for (LongPair pair:decomp.getBaseCombinations())	{
				long diff=pair.y-pair.x;
				NavigableMap<Long,Long> subMap=as.tailMap(diff,false);
				long validCases=subMap.size();
				if (validCases>=2)	{
					Long[] keys=subMap.keySet().toArray(Long[]::new);
					for (int i=0;i<validCases;++i) for (int j=i+1;j<validCases;++j) for (int k=0;k<multiplier;++k)	{
						long x=pair.x*multipliers[k];
						long y=pair.y*multipliers[k];
						long a=keys[i]*multipliers[k];
						long b=keys[j]*multipliers[k];
						long c=subMap.get(keys[j])*multipliers[k];
						long d=subMap.get(keys[i])*multipliers[k];
						long n=a*a+b*b+c*c+d*d;
						double num=(x*x+y*y-a*a);
						double den=2*x*y;
						double cos=num/den;
						System.out.println(String.format(Locale.UK,"Some quadrilateral: AB=%d, BC=%d, CD=%d, DA=%d, AO=CO=%d, BO=DO=%d, N=%d, cos α=%f.",a,b,c,d,x,y,n,cos));
						if (isTriangleInvalid(x,y,a)||isTriangleInvalid(x,y,b)||isTriangleInvalid(x,y,c)||isTriangleInvalid(x,y,d)) System.out.println("\tWAS PASSIERT?????");
						if (isQuadrilateralInvalid(a,b,c,d,x,y)) System.out.println("AJÁ.");
					}
					result+=(validCases*(validCases-1))/2;
				}
			}
			return result*multiplier;
		}
		private long getMultiplier(long n)	{
			int position=Arrays.binarySearch(multipliers,LongMath.sqrt(maxSquareSum/n,RoundingMode.DOWN));
			if (position>=0) return position+1;
			else return -1-position;
		}
		private long countQuadrilaterals(long n,BaseSquareDecomposition decomp)	{
			// Try to rewrite to debug, when I'm not suddenly tired.
			BaseSquareDecomposition decomp2=decomp.scramble();
			long result=countValidCases(decomp,decomp2,getMultiplier(n));
			if (n*2<=maxSquareSum) result+=countValidCases(decomp2,decomp2.scramble(),getMultiplier(2*n));
			return result;
		}
		public long countAllQuadrilaterals()	{
			SortedMap<Long,BaseSquareDecomposition> decomps=new TreeMap<>();
			long result=0;
			for (long prime:primes4k1)	{
				decomps.entrySet().removeIf((Map.Entry<Long,BaseSquareDecomposition> entry)->entry.getKey().longValue()*prime>maxSquareSum);
				PrimePowerDecompositionFinder finder=new PrimePowerDecompositionFinder(prime,combinatorials);
				Map<Long,BaseSquareDecomposition> toAdd=new HashMap<>();
				long primePower=prime;
				for (int exp=1;primePower<maxSquareSum;++exp,primePower*=prime)	{
					BaseSquareDecomposition primePowerDecomps=finder.getFor(exp);
					toAdd.put(primePower,primePowerDecomps);
					for (Map.Entry<Long,BaseSquareDecomposition> other:decomps.entrySet())	{
						long number=primePower*other.getKey();
						if (number>maxSquareSum) break;
						toAdd.put(number,other.getValue().combineWith(primePowerDecomps));
					}
				}
				for (Map.Entry<Long,BaseSquareDecomposition> entry:toAdd.entrySet()) result+=countQuadrilaterals(entry.getKey(),entry.getValue());
				decomps.putAll(toAdd);
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SquareSumFinder finder=new SquareSumFinder(N);
		long result=finder.countAllQuadrilaterals();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
