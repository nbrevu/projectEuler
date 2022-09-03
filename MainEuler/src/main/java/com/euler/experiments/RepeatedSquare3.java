package com.euler.experiments;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class RepeatedSquare3 {
	private static DivisorHolder factor(long number,long[] primes)	{
		DivisorHolder result=new DivisorHolder();
		for (long prime:primes)	{
			if (prime*prime>number) break;
			while ((number%prime)==0)	{
				number/=prime;
				result.addFactor(prime,1);
			}
		}
		if (number>1) result.addFactor(number,1);
		return result;
	}
	
	private static long getSquareFactor(DivisorHolder factorisation)	{
		LongIntMap factors=factorisation.getFactorMap();
		long result=1;
		for (LongIntCursor cursor=factors.cursor();cursor.moveNext();) if (cursor.value()>=2) result*=Math.pow(cursor.key(),cursor.value()/2);
		return result;
	}
	
	private static class RepeatedSquareData	{
		public final int basePower;
		public final BigInteger squareFactor;
		public final BigInteger root;
		public final BigInteger square;
		public RepeatedSquareData(int basePower,BigInteger squareFactor,BigInteger root)	{
			this.basePower=basePower;
			this.squareFactor=squareFactor;
			this.root=root;
			square=root.multiply(root);
		}
		public RepeatedSquareData(int basePower,BigInteger squareFactor,long root)	{
			this(basePower,squareFactor,BigInteger.valueOf(root));
		}
	}
	
	private static class SmartRepeatedSquareFinder	{
		private int countAppearances(BigInteger number,BigInteger divisor)	{
			int result=0;
			for (;;)	{
				BigInteger[] division=number.divideAndRemainder(divisor);
				if (!division[1].equals(BigInteger.ZERO)) return result;
				number=division[0];
				++result;
			}
		}
		
		private static OptionalInt findFirstValidPower(long base,long prime)	{
			if (base%prime==0) return OptionalInt.empty();
			int currentPower=0;
			long currentMod=1l;
			long square=prime*prime;
			for (;;)	{
				++currentPower;
				currentMod=(currentMod*base)%square;
				if (currentMod==square-1) return OptionalInt.of(currentPower);
				else if (currentMod==1) return OptionalInt.empty();
			}
		}
		private final long[] primes;
		public SmartRepeatedSquareFinder(int maxValue)	{
			primes=Primes.listLongPrimes(maxValue).stream().mapToLong(Long::longValue).toArray();
		}
		public RepeatedSquareData findFirstRepeatedSquare(long base)	{
			int currentPower=Integer.MAX_VALUE;
			LongSet currentPrimes=HashLongSets.newMutableSet();
			for (long prime:primes)	{
				if (prime>base+1) break;
				OptionalInt oFirstPower=findFirstValidPower(base,prime);
				if (oFirstPower.isEmpty()) continue;
				int firstPower=oFirstPower.getAsInt();
				if (firstPower==currentPower) currentPrimes.add(prime);
				else if (firstPower<currentPower)	{
					currentPower=firstPower;
					currentPrimes.clear();
					currentPrimes.add(prime);
				}
			}
			if (currentPrimes.isEmpty()) throw new IllegalArgumentException("Leider kann ich keine Lösung finden :'(.");
			BigInteger power=BigInteger.valueOf(base).pow(currentPower);
			BigInteger candidate=power.add(BigInteger.ONE);
			BigInteger squareFactor=BigInteger.ONE;
			for (LongCursor cursor=currentPrimes.cursor();cursor.moveNext();)	{
				BigInteger prime=BigInteger.valueOf(cursor.elem());
				int toDivide=countAppearances(candidate,prime)/2;
				squareFactor=squareFactor.multiply(prime.pow(toDivide));
			}
			BigInteger reducedCandidate=candidate.divide(squareFactor);
			long minimumAdditionalFactor=(long)Math.ceil(squareFactor.doubleValue()/Math.sqrt(base)*power.doubleValue()/candidate.doubleValue());
			BigInteger root=reducedCandidate.multiply(BigInteger.valueOf(minimumAdditionalFactor));
			return new RepeatedSquareData(currentPower,squareFactor,root);
		}
	}
	
	private static String toString(BigInteger number,int radix)	{
		if (radix<=36) return number.toString(radix);
		Deque<Long> digits=new ArrayDeque<>();
		BigInteger divisor=BigInteger.valueOf(radix);
		while (number.compareTo(BigInteger.ZERO)>0)	{
			BigInteger[] div=number.divideAndRemainder(divisor);
			number=div[0];
			digits.addFirst(div[1].longValueExact());
		}
		return digits.stream().map(Object::toString).collect(Collectors.joining("_"));
	}
	
	public static void main(String[] args)	{
		/*
		First problem: determine which is the smallest number whose square is a "repeated" number.
		 Is it 36363636364?
		  SOLVED: YES, it is!
		Second problem: determine which is the smallest number whose CUBE is a "repeated" number (with three parts).
		Third problem, probably unsolvable: determine if there is a number whose square has three parts. Or a cube with two parts.
		 Most probably it doesn't exist.
		Fourth problem: determine the smallest number whose square is a "repeated" number, in other bases.
		 In case 10 is a special case, as I suspect, there is a problem 4-bis:
		 Determine the smallest base which is not a multiple of 10 and for which the smallest number is greater than the one for base 10.
		 10 is not a special case! The distribution does not depend as much on small primes as I suspected. That 12 tho.
		ANYWAYS, fifth problem: get the result for 16. Using primes just like I got the result for 10 using 11.
		*/
		{
			// Fourth problem.
			SmartRepeatedSquareFinder finder=new SmartRepeatedSquareFinder(100);
			for (int base=2;base<=1000;++base)	{
				RepeatedSquareData result=finder.findFirstRepeatedSquare(base);
				System.out.println("Base "+base+":");
				System.out.println("\tOrigen="+base+"^"+result.basePower+".");
				System.out.println("\tFactor="+result.squareFactor+".");
				System.out.println("\t"+toString(result.root,base)+"^2="+toString(result.square,base)+".");
				System.out.println("\tEn base 10: "+result.root+"^2="+result.square+".");
			}
		}
	}
}
