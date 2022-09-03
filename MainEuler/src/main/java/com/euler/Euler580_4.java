package com.euler;

import java.math.RoundingMode;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashLongIntMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler580_4 {
	private final static long LIMIT=LongMath.pow(10l,7);
	
	private static int getModifiedMöbiusCoefficient(DivisorHolder decomposition,int[] oldMöbiusValues)	{
		int result=-1;
		for (LongCursor cursor=decomposition.getUnsortedListOfDivisors().cursor();cursor.moveNext();) result-=oldMöbiusValues[(int)(cursor.elem())];
		return result;
	}
	
	private static boolean[] hilbertSieve(int limit)	{
		boolean[] result=new boolean[1+limit];
		for (int i=1;i<=limit;i+=4) result[i]=true;
		for (int i=5;i<=limit;i+=4)	{
			int i2=i*i;
			if (i2>limit) break;
			for (int j=i2;j<=limit;j+=i2) result[j]=false;
		}
		return result;
	}
	
	/*
	 * OK, I see the problem. Things don't work properly regarding numbers like "21^2".
	 * Yet again, inclusion-exclusion needs to be handled carefully. Maybe it won't be as destructive as with #768...
	 */
	public static void main(String[] args)	{
		long s=LongMath.sqrt(LIMIT/4,RoundingMode.DOWN);
		LongIntMap counters=HashLongIntMaps.newMutableMap(2500000);
		counters.addValue(1l,1);
		for (long i=5;i<=LIMIT;i+=4) counters.addValue(i,1);
		long[] lastPrimes=Primes.lastPrimeSieve(LIMIT);
		int[] möbiusCoefficients=new int[1+(int)s];
		long result=LIMIT/4;
		for (long i=5;i<s;i+=4)	{
			int möbius=getModifiedMöbiusCoefficient(DivisorHolder.getFromFirstPrimes(i,lastPrimes),möbiusCoefficients);
			möbiusCoefficients[(int)i]=möbius;
			if (möbius==0) continue;
			long i2=i*i;
			long howMany=LIMIT/(4*i2);
			long rem=LIMIT%(4*i2);
			if (rem>=i2) ++howMany;
			result+=howMany*möbius;
			for (long j=i2;j<=LIMIT;j+=4*i2) counters.addValue(j,möbius);
		}
		IntObjMap<LongSet> shouldHaveBeenZero=HashIntObjMaps.newMutableMap();
		IntObjMap<LongSet> shouldHaveBeenOne=HashIntObjMaps.newMutableMap();
		boolean[] sieved=hilbertSieve((int)LIMIT);
		int sievedResult=0;
		for (int i=0;i<sieved.length;++i) if (sieved[i]) ++sievedResult;
		System.out.println("Sieve: "+sievedResult+".");
		int resultUsingDivisors=0;
		for (long i=1;i<=LIMIT;i+=4)	{
			DivisorHolder divisors=DivisorHolder.getFromFirstPrimes(i,lastPrimes);
			int foundTimes=counters.get(i);
			boolean isExpected=true;
			boolean hasStrikeOne=false;
			for (LongIntCursor cursor=divisors.getFactorMap().cursor();cursor.moveNext();)	{
				long m=(cursor.key())%4;
				int exp=cursor.value();
				if (m==1)	{
					if (exp>1) isExpected=false;
				}	else	{
					if (exp>=4) isExpected=false;
					else if (exp>=2)	{
						if (hasStrikeOne) isExpected=false;
						else hasStrikeOne=true;
					}
				}
				if (!isExpected) break;
			}
			if (isExpected) ++resultUsingDivisors;
			if (isExpected!=sieved[(int)i])	{
				System.out.println("ACHTUNG!!!!! Tengo un valor "+i+" con descomposición "+divisors.getFactorMap()+".");
				System.out.println("\tLa descomposición dice que "+isExpected+", pero en realidad, es "+sieved[(int)i]+".");
				throw new RuntimeException();
			}
			if ((isExpected&&(foundTimes!=1))||(!isExpected&&(foundTimes!=0)))	{
				IntObjMap<LongSet> workingCounter=isExpected?shouldHaveBeenOne:shouldHaveBeenZero;
				LongSet toAdd=workingCounter.computeIfAbsent(foundTimes,(int unused)->HashLongSets.newMutableSet());
				toAdd.add(i);
			}
		}
		System.out.println("Divisors: "+resultUsingDivisors+".");
		System.out.println("Incl. excl.: "+result+".");
		System.out.println("Should have been zero, but wasn't:");
		for (IntObjCursor<LongSet> cursor=shouldHaveBeenZero.cursor();cursor.moveNext();) System.out.println("\t"+cursor.key()+" => "+cursor.value().size()+" DINGE!!!!!");
		System.out.println("Should have been one, but wasn't:");
		for (IntObjCursor<LongSet> cursor=shouldHaveBeenOne.cursor();cursor.moveNext();) System.out.println("\t"+cursor.key()+" => "+cursor.value().size()+" DINGE!!!!!");
	}
}
