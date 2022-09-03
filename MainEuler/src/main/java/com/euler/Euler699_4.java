package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;

public class Euler699_4 {
	private final static long LIMIT=LongMath.pow(10l,14);
	
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
		public final long n;
		public final long den;
		public final DivisorHolder decomposedNumerator;
		public final DivisorHolder decomposedDenominator;
		public DecomposedNumber(long n,long den,DivisorHolder decomposedNumerator,DivisorHolder decomposedDenominator)	{
			this.n=n;
			this.den=den;
			this.decomposedNumerator=decomposedNumerator;
			this.decomposedDenominator=decomposedDenominator;
		}
		public boolean isPowerOf3()	{
			LongIntMap factors=decomposedDenominator.getFactorMap();
			if (factors.size()!=1) return false;
			for (LongIntCursor cursor=factors.cursor();cursor.moveNext();) if ((cursor.key()!=3)||(cursor.value()<=0)) return false;
			return true;
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(n);
		}
		@Override
		public boolean equals(Object other)	{
			DecomposedNumber dOther=(DecomposedNumber)other;
			return n==dOther.n;
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
	private final static long[] MULTIPERFECT_NUMBERS=Stream.of(PERFECT_NUMBERS,TRIPERFECT_NUMBERS,TETRAPERFECT_NUMBERS,PENTAPERFECT_NUMBERS).flatMapToLong(Arrays::stream).sorted().toArray();
	
	private static DecomposedNumber getFullFactor(long in,Factorer factorer)	{
		DivisorHolder factors=factorer.factor(in);
		long sigma=factors.getSumOfDivisors();
		long g=EulerUtils.gcd(in,sigma);
		long num=sigma/g;
		long den=in/g;
		return new DecomposedNumber(in,den,factorer.factor(num),factorer.factor(den));
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
	
	private static List<List<DecomposedNumber>> sortByDenominator(Factorer factorer,int maxNumber)	{
		List<List<DecomposedNumber>> result=new ArrayList<>(1+maxNumber);
		for (int i=0;i<=maxNumber;++i) result.add(null);
		int million=1000000;
		boolean add2=true;
		// Skips multiples of 3.
		for (int i=2;i<=maxNumber;i+=add2?2:1,add2=!add2)	{
			if (i>=million)	{
				System.out.println(i+"...");
				million+=1000000;
			}
			DecomposedNumber decomposed=getFullFactor(i,factorer);
			if (decomposed.den!=i)	{
				int index=(int)decomposed.den;
				List<DecomposedNumber> toAdd=result.get(index);
				if (toAdd==null)	{
					toAdd=new ArrayList<>();
					result.set(index,toAdd);
				}
				toAdd.add(decomposed);
			}
		}
		return result;
	}
	
	private static class AppendedCollection<T> implements Iterable<T>	{
		private final Collection<T> base;
		private final T newElement;
		private class CustomIterator implements Iterator<T>	{
			private final Iterator<T> baseIterator=base.iterator();
			private boolean hasNewElementPassed=false;
			@Override
			public boolean hasNext() {
				return baseIterator.hasNext()||!hasNewElementPassed;
			}
			@Override
			public T next() {
				if (baseIterator.hasNext()) return baseIterator.next();
				else if (!hasNewElementPassed)	{
					hasNewElementPassed=true;
					return newElement;
				}	else throw new NoSuchElementException();
			}
		}
		public AppendedCollection(Collection<T> base,T newElement)	{
			this.base=base;
			this.newElement=newElement;
		}
		@Override
		public Iterator<T> iterator() {
			return new CustomIterator();
		}
	}
	
	public static void main(String[] args)	{
		long result=0;
		int internalLimit=5*IntMath.pow(10,7);
		Factorer factorer=new Factorer(internalLimit);
		System.out.println("Descomposición gorda...");
		List<List<DecomposedNumber>> precalc=sortByDenominator(factorer,internalLimit);
		System.out.println("Y ahora lo chiquitico.");
		List<DecomposedNumber> perfects=Arrays.stream(MULTIPERFECT_NUMBERS).mapToObj((long l)->getFullFactor(l,factorer)).collect(Collectors.toUnmodifiableList());
		precalc.set(1,perfects);
		NavigableMap<Long,DecomposedNumber> queue=new TreeMap<>();
		for (long p:getPowersOf3UpTo(LIMIT)) queue.put(p,getFullFactor(p,factorer));
		System.out.println("AL TURRÓN.");
		while (!queue.isEmpty())	{
			Map.Entry<Long,DecomposedNumber> head=queue.pollFirstEntry();
			DecomposedNumber info=head.getValue();
			if (info==null) continue;
			long n=head.getKey();
			System.out.println(n);
			result+=n;
			for (LongCursor cursor=info.decomposedNumerator.getUnsortedListOfDivisors().cursor();cursor.moveNext();)	{
				List<DecomposedNumber> baseList=Collections.emptyList();
				if (cursor.elem()<precalc.size())	{
					List<DecomposedNumber> actualList=precalc.get((int)cursor.elem());
					if (actualList!=null) baseList=actualList;
				}
				Iterable<DecomposedNumber> appended=(cursor.elem()==1)?baseList:new AppendedCollection<>(baseList,getFullFactor(cursor.elem(),factorer));
				for (DecomposedNumber candidate:appended)	{
					long value=candidate.n;
					if (canMultiply(n,value)&&EulerUtils.areCoprime(n,value))	{
						long newNumber=n*value;
						if ((newNumber>=LIMIT)||queue.containsKey(newNumber)) continue;
						DecomposedNumber toAdd=getFullFactor(newNumber,factorer);
						queue.put(newNumber,toAdd.isPowerOf3()?toAdd:null);
					}
				}
			}
			// 35253867850204884
			// 35554388176167540
			// 35744861302268532
			// I'm pretty sure that I'm missing very few numbers :(.
		}
		System.out.println(result);
	}
}
