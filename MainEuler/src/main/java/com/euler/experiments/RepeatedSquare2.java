package com.euler.experiments;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;

public class RepeatedSquare2 {
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
		public final long squareFactor;
		public final BigInteger root;
		public final BigInteger square;
		public RepeatedSquareData(int basePower,long squareFactor,long root)	{
			this.basePower=basePower;
			this.squareFactor=squareFactor;
			this.root=BigInteger.valueOf(root);
			square=this.root.multiply(this.root);
		}
	}
	
	private static class RepeatedSquareFinder	{
		private final long[] primes;
		private final long maxValue;
		public RepeatedSquareFinder(long maxValue)	{
			primes=Primes.listLongPrimes(LongMath.sqrt(maxValue,RoundingMode.UP)).stream().mapToLong(Long::longValue).toArray();
			this.maxValue=maxValue;
		}
		public Optional<RepeatedSquareData> findFirstRepeatedSquare(long base)	{
			long currentPow=base;
			for (int pow=1;currentPow<=maxValue;++pow,currentPow*=base)	{
				long candidate=currentPow+1;
				DivisorHolder factorisation=factor(candidate,primes);
				long squareFactor=getSquareFactor(factorisation);
				if (squareFactor>1)	{
					long reducedCandidate=candidate/squareFactor;
					long minimumAdditionalFactor=(long)Math.ceil(squareFactor/Math.sqrt(base)*((double)currentPow/(double)candidate));
					return Optional.of(new RepeatedSquareData(pow,squareFactor,reducedCandidate*minimumAdditionalFactor));
				}
			}
			return Optional.empty();
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
			long maxValue=LongMath.pow(10l,17);
			RepeatedSquareFinder finder=new RepeatedSquareFinder(maxValue);
			for (int base=2;base<=50;++base)	{
				Optional<RepeatedSquareData> result=finder.findFirstRepeatedSquare(base);
				System.out.println("Base "+base+":");
				if (result.isPresent())	{
					RepeatedSquareData data=result.get();
					System.out.println("\tOrigen="+base+"^"+data.basePower+".");
					System.out.println("\tFactor="+data.squareFactor+".");
					System.out.println("\t"+toString(data.root,base)+"^2="+toString(data.square,base)+".");
					System.out.println("\tEn base 10: "+data.root+"^2="+data.square+".");
				}	else System.out.println("\tLeider kann ich für diese Zahl keine Lösung finden. Es gefällt mir überhaupt nicht. Überhaupt nicht!!!!!");
			}
		}
	}
}
