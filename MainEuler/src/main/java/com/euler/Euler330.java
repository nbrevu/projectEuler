package com.euler;

import java.math.BigInteger;
import java.util.Arrays;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;

public class Euler330 {
	/*-
	private final static int MOD=77_777_777;
	private final static int N=IntMath.pow(10,9);
	*/
	
	private static class SequenceCalculator	{
		private final BigInteger[] factorials;
		private final BigInteger[] c;
		private final BigCombinatorialNumberCache combinatorials;
		public SequenceCalculator(int maxValue)	{
			factorials=new BigInteger[1+maxValue];
			factorials[0]=BigInteger.ONE;
			for (int i=1;i<=maxValue;++i) factorials[i]=factorials[i-1].multiply(BigInteger.valueOf(i));
			c=new BigInteger[1+maxValue];
			combinatorials=new BigCombinatorialNumberCache(1+maxValue);
		}
		public void calculate()	{
			c[0]=BigInteger.ZERO;
			c[1]=calculateD(1);
			for (int i=2;i<c.length;++i)	{
				BigInteger cI=calculateD(i);
				for (int j=1;j<i;++j) cI=cI.add(c[i-j].multiply(combinatorials.get(i,j)));
				c[i]=cI;
			}
		}
		private BigInteger calculateD(int n)	{
			BigInteger result=BigInteger.ZERO;
			for (int i=1;i<=n;++i) result=result.add(factorials[n].divide(factorials[i]));
			return result;
		}
		public int[] getAfterMod(int mod)	{
			BigInteger bigMod=BigInteger.valueOf(mod);
			return Arrays.stream(c).mapToInt((BigInteger n)->n.mod(bigMod).intValueExact()).toArray();
		}
	}
	/*-
	private static int[] decompose(int n)	{
		IntStream.Builder builder=IntStream.builder();
		if ((n%2)==0)	{
			builder.accept(2);
			n/=2;
		}
		if ((n%3)==0)	{
			builder.accept(3);
			n/=3;
		}
		boolean add4=false;
		for (int i=5;;i+=(add4?4:2),add4=!add4) if (i*i>n)	{
			builder.accept(n);
			break;
		}	else if ((n%i)==0)	{
			builder.accept(i);
			n/=i;
		}
		return builder.build().toArray();
	}
	*/
	private static boolean verifyCycleLength(int[] mods,int expectedCycle)	{
		for (int i=expectedCycle;i<mods.length;++i) if (mods[i]!=mods[i-expectedCycle]) return false;
		return true;
	}
	
	/*
	 * Very interesting findings:
	 * 1) for a given prime P, C follows a cycle of length P*(P-1).
	 * 2) for a given prime P, we only need to calculate the first 2P values of D. The sequence is {some P values},{other P values}, and then the
	 * the set {other P values} repeats indefinitely. For example, for P=7 it's {0,1,3,3,6,3,5, 1,2,5,2,2,4,4, 1,2,5,2,2,4,4, 1,2,5,2,2,4,4...}.
	 * This basic scheme, with a calculator of combinatorial numbers with mod, and the Chinese remainder theorem, is enough.
	 */
	public static void main(String[] args)	{
		{
			long tic=System.nanoTime();
			// int[] primes=decompose(MOD);
			int[] primes=new int[] {7,11,13,17,19,23,29};
			int maxPrime=primes[primes.length-1];
			int maxToCalculate=2*maxPrime*(maxPrime-1);
			SequenceCalculator calculator=new SequenceCalculator(maxToCalculate);
			calculator.calculate();
			for (int p:primes)	{
				int cycleLength=p*(p-1);
				boolean isValid=verifyCycleLength(calculator.getAfterMod(p),cycleLength);
				System.out.println("p="+p+": "+(isValid?"correcto :).":"mi nota: puta mierda :(."));
			}
			long tac=System.nanoTime();
			double seconds=1e-9*(tac-tic);
			System.out.println("Elapsed "+seconds+" seconds.");
		}
		{
			int[] primes=new int[] {7,11,13,17,19,23,29};
			SequenceCalculator calculator=new SequenceCalculator(2*28*29);
			for (int p:primes)	{
				int cycle=p*(p-1);
				int[] ds=new int[2*cycle];
				BigInteger bigP=BigInteger.valueOf(p);
				for (int i=0;i<ds.length;++i) ds[i]=calculator.calculateD(i).mod(bigP).intValueExact();
				System.out.println("p="+p+": D="+Arrays.toString(ds)+".");
			}
		}
	}
}
