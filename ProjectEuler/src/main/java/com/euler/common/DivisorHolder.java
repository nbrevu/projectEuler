package com.euler.common;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.LongConsumer;

import com.google.common.base.Optional;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class DivisorHolder	{
	private LongIntMap factors;
	public DivisorHolder()	{
		factors=HashLongIntMaps.newMutableMap();
	}
	private DivisorHolder(LongIntMap factors)	{
		this.factors=HashLongIntMaps.newMutableMap(factors);
	}
	public void addFactor(long prime,int power)	{
		Integer prevPow=factors.get(prime);
		int newVal=power+((prevPow==null)?0:prevPow.intValue());
		factors.put(prime,newVal);
	}
	public void removeFactor(long prime)	{
		int pow=factors.get(prime);
		if (pow==1) factors.remove(prime);
		else factors.put(prime,pow-1);
	}
	public long getAmountOfDivisors()	{
		long prod=1;
		for (int pow:factors.values()) prod*=1+pow;
		return prod;
	}
	public long getDivisorSum()	{
		long prod=1;
		LongIntCursor cursor=factors.cursor();
		while (cursor.moveNext())	{
			long prime=cursor.key();
			int power=cursor.value();
			prod*=(LongMath.pow(prime,power+1)-1)/(prime-1);
		}
		return prod;
	}
	public LongIntMap getFactorMap()	{
		return factors;
	}
	public Decomposer getDecomposer()	{
		return new Decomposer(this);
	}
	public int removeSinglePrimes()	{
		// Removes every non-repeated primes, returns the amount of removed primes.
		LongSet toRemove=HashLongSets.newMutableSet();
		factors.forEach((long key,int value)->{
			if (value==1) toRemove.add(key);
		});
		toRemove.forEach((LongConsumer)factors::remove);
		return toRemove.size();
	}
	public LongSet getUnsortedListOfDivisors()	{
		LongSet result=HashLongSets.newMutableSet((int)getAmountOfDivisors());
		result.add(1);
		factors.forEach((long prime,int power)->{
			LongSet toAdd=HashLongSets.newMutableSet(result.size()*power);
			long currentFactor=prime;
			for (int i=1;i<=power;++i)	{
				for (long existing:result) toAdd.add(currentFactor*existing);
				currentFactor*=prime;
			}
			result.addAll(toAdd);
		});
		return result;
	}
	public long[] getSortedListOfDivisors()	{
		long[] result=getUnsortedListOfDivisors().toLongArray();
		Arrays.sort(result);
		return result;
	}
	@Override
	public int hashCode()	{
		return factors.hashCode();
	}
	@Override
	public boolean equals(Object other)	{
		return factors.equals(((DivisorHolder)other).factors);
	}
	@Override
	public String toString()	{
		if (factors.isEmpty()) return "1";
		StringBuilder sb=new StringBuilder();
		boolean first=true;
		SortedMap<Long,Integer> sorted=new TreeMap<>(factors);
		for (Map.Entry<Long,Integer> entry:sorted.entrySet())	{
			if (first) first=false;
			else sb.append(" · ");
			sb.append(entry.getKey()).append('^').append(entry.getValue());
		}
		return sb.toString();
	}
	public DivisorHolder pow(int n)	{
		// Negative numbers are allowed. This is quirky but it actually works to our advantage (see problem 650).
		DivisorHolder result=new DivisorHolder();
		if (n==0) return result;
		factors.forEach((long key,int value)->result.addFactor(key,n*value));
		return result;
	}
	// Use this after operations that might have resulted in empty powers.
	public void clean()	{
		LongSet toRemove=HashLongSets.newMutableSet();
		factors.forEach((long key,int value)->{ 
			if (value==0) toRemove.add(key);
		});
		toRemove.forEach((LongConsumer)factors::remove);
	}
	public int getTotient()	{
		int result=1;
		LongIntCursor cursor=factors.cursor();
		while (cursor.moveNext())	{
			result*=cursor.key()-1;
			for (int i=1;i<cursor.value();++i) result*=cursor.key();
		}
		return result;
	}
	public static DivisorHolder getFromFirstPrimes(int n,int[] firstPrimes)	{
		DivisorHolder result=new DivisorHolder();
		for (;;)	{
			int prime=firstPrimes[n];
			if (prime==0)	{
				result.addFactor(n,1);
				return result;
			}	else if (prime==1) return result;
			result.addFactor(prime,1);
			n/=prime;
		}
	}
	public static DivisorHolder getFromFirstPrimes(long n,long[] firstPrimes)	{
		DivisorHolder result=new DivisorHolder();
		for (;;)	{
			long prime=firstPrimes[(int)n];
			if (prime==0)	{
				result.addFactor((int)n,1);
				return result;
			}	else if (prime==1) return result;
			result.addFactor((int)prime,1);
			n/=prime;
		}
	}
	public static DivisorHolder combine(DivisorHolder d1,DivisorHolder d2)	{
		DivisorHolder result=new DivisorHolder(d1.factors);
		for (Map.Entry<Long,Integer> entry:d2.factors.entrySet()) result.addFactor(entry.getKey(),entry.getValue());
		return result;
	}
	public static DivisorHolder divide(DivisorHolder dividend,DivisorHolder divisor)	{
		DivisorHolder result=new DivisorHolder(dividend.factors);
		divisor.factors.forEach((long key,int value)->{
			int existing=result.factors.getOrDefault(key,0);
			int toRemove=value;
			// Negative numbers might appear. We don't really care, each problem knows whether this makes sense or not.
			if (existing==toRemove) result.factors.remove(key);
			else result.factors.put(key,existing-toRemove);
		});
		return result;
	}
	public static DivisorHolder getLcm(Collection<DivisorHolder> numbers)	{
		DivisorHolder result=new DivisorHolder();
		for (DivisorHolder holder:numbers)	{
			LongIntCursor cursor=holder.factors.cursor();
			while (cursor.moveNext()) result.factors.compute(cursor.key(),(long key,int elem)->Math.max(elem,cursor.value()));
		}
		return result;
	}
	public long getAsLong()	{
		// Assumes that the results fits in a long (i.e. 63 bits).
		long result=1l;
		LongIntCursor cursor=factors.cursor();
		while (cursor.moveNext()) result*=LongMath.pow(cursor.key(),cursor.value());
		return result;
	}
	public static class Decomposition	{
		public int[] factor1;
		public int[] factor2;
		public Decomposition(int[] factor1,int[] factor2)	{
			// ACHTUNG! This doesn't COPY the array. Keep in mind.
			this.factor1=factor1;
			this.factor2=factor2;
		}
		public long[] getFactors()	{
			long prod1=1;
			long prod2=1;
			for (int i=0;i<factor1.length;++i)	{
				prod1*=1+factor1[i];
				prod2*=1+factor2[i];
			}
			return new long[]{prod1,prod2};
		}
		public boolean sameAmountOfFactors()	{
			long[] prods=getFactors();
			return prods[0]==prods[1];
		}
		public Optional<Integer> neededPowersOf2()	{
			long[] prods=getFactors();
			if (prods[0]<prods[1]) prods=new long[]{prods[1],prods[0]};
			if ((prods[0]%prods[1])!=0) return Optional.absent();
			long q=prods[0]/prods[1];
			if (LongMath.isPowerOfTwo(q)) return Optional.of(LongMath.log2(q,RoundingMode.UNNECESSARY));
			else return Optional.absent();
		}
	}
	public static class Decomposer implements Iterable<Decomposition>,Iterator<Decomposition>	{
		// In this case we're more interested in accessing through indices, therefore we use a pair of
		// arrays instead of a map. As ugly as this may look, the alternative looks way uglier.
		private final int howManyFactors;
		private final long[] factors;
		private final int[] powers;
		private final double[] primeLogs;
		private int[] currentStatus;
		private int[] oppositeStatus;
		private boolean isCalculated;
		private boolean isFinished;
		private final double halfLog;
		public Decomposer(DivisorHolder source)	{
			SortedMap<Long,Integer> divs=new TreeMap<>(source.getFactorMap());
			howManyFactors=divs.size();
			int i=0;
			factors=new long[howManyFactors];
			powers=new int[howManyFactors];
			primeLogs=new double[howManyFactors];
			for (Map.Entry<Long,Integer> entry:divs.entrySet())	{
				factors[i]=entry.getKey();
				powers[i]=entry.getValue();
				primeLogs[i]=Math.log((double)factors[i]);
				++i;
			}
			currentStatus=new int[howManyFactors];
			oppositeStatus=Arrays.copyOf(powers,howManyFactors);
			isCalculated=true;
			isFinished=false;
			halfLog=calculateLog(powers)/2.0;
		}
		@Override
		public boolean hasNext() {
			if (isCalculated) return true;
			calculateNext();
			return !isFinished;
		}
		@Override
		public Decomposition next() {
			if (!isCalculated) calculateNext();
			isCalculated=false;
			return new Decomposition(currentStatus,oppositeStatus);
		}
		@Override
		public Iterator<Decomposition> iterator() {
			return this;
		}
		private double calculateLog(int[] fPowers) {
			double result=0.0;
			for (int i=0;i<howManyFactors;++i) result+=primeLogs[i]*fPowers[i];
			return result;
		}
		private void calculateNext()	{
			do	{
				int i=howManyFactors-1;
				for (;i>=0;--i) if (currentStatus[i]<powers[i])	{
					++currentStatus[i];
					--oppositeStatus[i];
					break;
				}
				if (i<0)	{
					isFinished=true;
					break;
				}
				for (++i;i<howManyFactors;++i)	{
					currentStatus[i]=0;
					oppositeStatus[i]=powers[i];
				}
			}	while (calculateLog(currentStatus)>=halfLog);
			isCalculated=true;
		}
	}
}
