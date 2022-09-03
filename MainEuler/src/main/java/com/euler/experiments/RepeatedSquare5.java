package com.euler.experiments;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;

public class RepeatedSquare5 {
	/*
	 * This is like RepeatedSquare2, but using Pollard's Rho.
	 * 
	 * Bad news: Pollard's rho doesn't work very well when numbers contain squares. The first suggestion, ZUTUN, is to use a function different
	 * than x^2+1: x^2+2, x^2+3... but do I need to detect the infinite loop?
	 */
	private static BigInteger getSquareFactor(ObjIntMap<BigInteger> factors)	{
		BigInteger result=BigInteger.ONE;
		for (ObjIntCursor<BigInteger> cursor=factors.cursor();cursor.moveNext();) if (cursor.value()>=2) result=result.multiply(cursor.key().pow(cursor.value()/2));
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
	}
	
	private static class RepeatedSquareFinder	{
		private final PollardsRho factorFinder;
		public RepeatedSquareFinder()	{
			factorFinder=new PollardsRho();
		}
		public RepeatedSquareData findFirstRepeatedSquare(long base)	{
			BigInteger bigBase=BigInteger.valueOf(base);
			BigInteger currentPow=bigBase;
			for (int pow=1;;++pow,currentPow=currentPow.multiply(bigBase))	{
				BigInteger candidate=currentPow.add(BigInteger.ONE);
				ObjIntMap<BigInteger> factors=factorFinder.factor(candidate);
				BigInteger squareFactor=getSquareFactor(factors);
				if (squareFactor.compareTo(BigInteger.ONE)>0)	{
					BigInteger reducedCandidate=candidate.divide(squareFactor);
					long minimumAdditionalFactor=(long)Math.ceil(squareFactor.doubleValue()/Math.sqrt(base)*currentPow.doubleValue()/candidate.doubleValue());
					return new RepeatedSquareData(pow,squareFactor,reducedCandidate.multiply(BigInteger.valueOf(minimumAdditionalFactor)));
				}
			}
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
			RepeatedSquareFinder finder=new RepeatedSquareFinder();
			for (int base=2;base<=50;++base)	{
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
