package com.euler;

import java.util.Arrays;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.IntStream;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.set.LongSet;

public class Euler699_2 {
	private final static long LIMIT=LongMath.pow(10l,6);
	
	// Special thanks to http://mathworld.wolfram.com/MultiperfectNumber.html for the following lists:
	// http://oeis.org/A000396
	private final static long[] PERFECT_NUMBERS=new long[] {6,28,496,8128,33550336,8589869056l,137438691328l}; // Next: 2305843008139952128.
	// http://oeis.org/A005820. Apparently it's believed that there aren't any more than these six!!
	private final static long[] TRIPERFECT_NUMBERS=new long[] {120,672,523776,459818240,1476304896,51001180160l};
	// http://oeis.org/A027687
	private final static long[] TETRAPERFECT_NUMBERS=new long[] {30240,32760,2178540,23569920,45532800,142990848,1379454720,43861478400l,66433720320l,153003540480l,403031236608l,704575228896l};	// Next: 181742883469056
	// http://oeis.org/A046060
	private final static long[] PENTAPERFECT_NUMBERS=new long[] {14182439040l,31998395520l,518666803200l,13661860101120l,30823866178560l};	// Next: 740344994887680.
	// http://oeis.org/A046061 for hexaperfect numbers. Oops! The first one is already bigger than 10^14: 154345556085770649600.
	
	private static class DecomposedNumber	{
		// "N" is the key and we don't need it here.
		public final long sigma;
		public final DivisorHolder decomposedNumerator;
		public final DivisorHolder decomposedDenominator;
		public DecomposedNumber(long sigma,DivisorHolder decomposedNumerator,DivisorHolder decomposedDenominator)	{
			this.sigma=sigma;
			this.decomposedNumerator=decomposedNumerator;
			this.decomposedDenominator=decomposedDenominator;
		}
		public boolean isPowerOf3()	{
			LongIntMap factors=decomposedDenominator.getFactorMap();
			if (factors.size()!=1) return false;
			for (LongIntCursor cursor=factors.cursor();cursor.moveNext();) if ((cursor.key()!=3)||(cursor.value()<=0)) return false;
			return true;
		}
	}
	
	private static class Factorer	{
		private int[] firstPrimes;
		private int[] primes;
		public Factorer(int maxCache)	{
			firstPrimes=Primes.firstPrimeSieve(maxCache);
			primes=IntStream.range(0,firstPrimes.length).filter((int index)->firstPrimes[index]==0).toArray();
		}
		public DivisorHolder factor(long in)	{
			if (in<firstPrimes.length) return DivisorHolder.getFromFirstPrimes((int)in,firstPrimes);
			for (int p:primes) if ((in%p)==0)	{
				int pow=0;
				do	{
					++pow;
					in/=p;
				}	while ((in%p)==0);
				DivisorHolder result=factor(in);
				result.addFactor(p,pow);
				return result;
			}
			DivisorHolder result=new DivisorHolder();
			result.addFactor(in,1);
			return result;
		}
	}
	
	// Once in a while, streams are actually useful. As in, this is probably the literal best way to do this!
	private final static long[] MULTIPERFECT_NUMBERS=Arrays.stream(new long[][] {PERFECT_NUMBERS,TRIPERFECT_NUMBERS,TETRAPERFECT_NUMBERS,PENTAPERFECT_NUMBERS}).flatMapToLong(Arrays::stream).sorted().toArray();
	
	private static DecomposedNumber getFullFactor(long in,Factorer factorer)	{
		DivisorHolder factors=factorer.factor(in);
		long sigma=factors.getSumOfDivisors();
		long g=EulerUtils.gcd(in,sigma);
		long num=sigma/g;
		long den=in/g;
		return new DecomposedNumber(sigma,factorer.factor(num),factorer.factor(den));
	}
	
	private static NavigableMap<Long,DecomposedNumber> getMultiperfectNumbers(Factorer factorer)	{
		/*
		LongObjMap<long[]> arrays=HashLongObjMaps.newImmutableMapOf(2,PERFECT_NUMBERS,3,TRIPERFECT_NUMBERS,4,TETRAPERFECT_NUMBERS,5,PENTAPERFECT_NUMBERS);
		NavigableMap<Long,DecomposedNumber> result=new TreeMap<>();
		DivisorHolder one=new DivisorHolder();
		for (LongObjCursor<long[]> cursor=arrays.cursor();cursor.moveNext();)	{
			DivisorHolder numerator=factorer.factor(cursor.key());
			for (long multiperfect:cursor.value()) result.put(multiperfect,new DecomposedNumber(cursor.key()*multiperfect,numerator,one));
		}
		return result;
		*/
		NavigableMap<Long,DecomposedNumber> result=new TreeMap<>();
		for (long[] array:Arrays.asList(PERFECT_NUMBERS,TRIPERFECT_NUMBERS,TETRAPERFECT_NUMBERS,PENTAPERFECT_NUMBERS)) for (long n:array) result.put(n,getFullFactor(n,factorer));
		return result;
	}
	
	private static NavigableSet<Long> getPowersOf3UpTo(long in)	{
		NavigableSet<Long> result=new TreeSet<>();
		long current=3;
		while (current<=in)	{
			result.add(current);
			current*=3l;
		}
		return result;
	}
	
	private static boolean canMultiply(long a,long b)	{
		return Long.MAX_VALUE/a>b;
	}
	
	public static void main(String[] args)	{
		long result=0;
		Factorer factorer=new Factorer(IntMath.pow(10,8));
		NavigableMap<Long,DecomposedNumber> queue=new TreeMap<>();
		for (long p:getPowersOf3UpTo(LIMIT)) queue.put(p,getFullFactor(p,factorer));
		while (!queue.isEmpty())	{
			Map.Entry<Long,DecomposedNumber> head=queue.pollFirstEntry();
			DecomposedNumber info=head.getValue();
			if (info==null) continue;
			long n=head.getKey();
			System.out.println(n);
			result+=n;
			LongSet candidates=info.decomposedNumerator.getUnsortedListOfDivisors();
			candidates.removeLong(1);
			for (long p:MULTIPERFECT_NUMBERS) candidates.add(p);
			for (LongCursor cursor=candidates.cursor();cursor.moveNext();)	{
				long value=cursor.elem();
				if (canMultiply(n,value))	{
					long candidate=n*value;
					if ((candidate>=LIMIT)||queue.containsKey(candidate)) continue;
					DecomposedNumber toAdd=getFullFactor(candidate,factorer);
					queue.put(candidate,toAdd.isPowerOf3()?toAdd:null);
				}
			}
		}
		System.out.println(result);
		/*
		 * MIERRRRRDA, sale 25617939 y no 26089287. Pero estoy muy cerca. Creo.
		 */
	}
}
