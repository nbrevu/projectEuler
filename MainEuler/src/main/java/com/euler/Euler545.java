package com.euler;

import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.collect.ImmutableSet;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.set.LongSet;

public class Euler545 {
	private final static int LIMIT=3000000;	// Originally I used 1000000000, but this is very close. For 1e9 the run time is 14.011149626 seconds.
	private final static int N=IntMath.pow(10,5);
	
	private final static class Sieve308 extends EulerUtils.MultiplicativeSieve	{
		/*
		 * So we need to test a number X. We assume that all divisors of X have been positively tested (otherwise X would have been sieved).
		 * This means that we need to test all the divisors of 308*X to see whether any one them verifies N+1=prime. 
		 * For EVEN numbers we need to consider all the divisors because a*X is always even so all divisors have been tested.
		 * For ODD numbers, if there are multiples of three we will also need to test every number.
		 * However, if the numbers are of the form <3a+1>, multiplying by a <3b+2> number will result in a <3c+2> number, so adding one we will
		 * always get a multiple of 3. Therefore for <3a+1> we only test <3b+1> numbers. Following a similar reasoning, for <3a+2> we only need
		 * to test <3b+2> numbers.
		 */
		private final static int[] ALL_DIVS=new int[]{1,2,4,7,11,14,22,28,44,77,154,308};
		private final static int[] DIVS_FOR_3N_PLUS_1=new int[]{1,4,7,22,28,154};
		private final static int[] DIVS_FOR_3N_MINUS_1=new int[]{2,11,14,44,77,308};
		private final static Set<Integer> ACCEPTABLE_PRIMES=ImmutableSet.of(2,3,5,23,29);
		private final List<Integer> primes;
		public Sieve308(int n) {
			super(n);
			primes=Primes.listIntPrimes((int)(1+LongMath.sqrt(308*(long)n,RoundingMode.UP)));
		}
		@Override
		protected boolean test(int in) {
			for (long i:getAllDivisors(in)) if (i!=1) for (int j:getFactorsToTest((int)i))	{
				int toTest=((int)i)*j+1;
				if (!ACCEPTABLE_PRIMES.contains(toTest)&&isPrime(toTest)) return true;
			}
			return false;
		}
		private static int[] getFactorsToTest(int in)	{
			if ((in%2)==0) return ALL_DIVS;
			else switch (in%3)	{
				case 0:return ALL_DIVS;
				case 1:return DIVS_FOR_3N_PLUS_1;
				case 2:return DIVS_FOR_3N_MINUS_1;
				default:throw new RuntimeException("No, Java, I don't expect a different result than 0, 1 or 2 if I get the remainder modulo 3.");
			}
		}
		private LongSet getAllDivisors(int in)	{
			DivisorHolder factors=factor(in);
			return factors.getUnsortedListOfDivisors();
		}
		private DivisorHolder factor(int in)	{
			DivisorHolder result=new DivisorHolder();
			for (int prime:primes) if ((in%prime)==0)	{
				int powCounter=0;
				do	{
					in/=prime;
					++powCounter;
				}	while ((in%prime)==0);
				result.addFactor(prime,powCounter);
			}	else if (prime*prime>in)	{
				if (in>1) result.addFactor(in,1);
				break;
			}
			return result;
		}
		private boolean isPrime(int in)	{
			for (int prime:primes) if (prime==in) return true;
			else if ((in%prime)==0) return false;
			else if (prime*prime>in) return true;
			return true;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Sieve308 sieve=new Sieve308(LIMIT);
		int unsieved=sieve.getNthUnsieved(N-1);
		long tac=System.nanoTime();
		System.out.println(308*unsieved);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
