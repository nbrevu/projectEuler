package com.euler;

import java.math.BigInteger;
import java.util.stream.IntStream;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.CombinatorialNumberModCache;
import com.google.common.math.IntMath;

public class Euler330_2 {
	private final static int MOD=77_777_777;
	private final static int N=IntMath.pow(10,9);
	
	private static class SequenceCalculator	{
		private final BigInteger[] factorials;
		private final BigInteger[] ds;
		public SequenceCalculator(int maxValue)	{
			int maxD=2*maxValue;
			factorials=new BigInteger[maxD];
			factorials[0]=BigInteger.ONE;
			for (int i=1;i<maxD;++i) factorials[i]=factorials[i-1].multiply(BigInteger.valueOf(i));
			ds=new BigInteger[maxD];
			for (int i=0;i<maxD;++i) ds[i]=calculateD(i);
		}
		private BigInteger calculateD(int n)	{
			BigInteger result=BigInteger.ZERO;
			for (int i=1;i<=n;++i) result=result.add(factorials[n].divide(factorials[i]));
			return result;
		}
		private int[] getDs(int limit,int mod)	{
			BigInteger bigMod=BigInteger.valueOf(mod);
			int[] result=new int[limit];
			for (int i=0;i<limit;++i) result[i]=ds[i].mod(bigMod).intValueExact();
			return result;
		}
		public int[] getCs(int upTo,int mod)	{
			int[] ds=getDs(2*mod,mod);
			int[] cs=new int[upTo];
			CombinatorialNumberModCache combinatorials=new CombinatorialNumberModCache(upTo,mod);
			for (int i=0;i<upTo;++i)	{
				int dIdx=i;
				if (dIdx>=ds.length) dIdx=mod+(dIdx%mod);
				long c=ds[dIdx];
				for (int j=1;j<i;++j) c+=combinatorials.get(i,j)*cs[i-j];
				cs[i]=(int)(c%mod);
			}
			return cs;
		}
	}

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

	/*
	 * Very interesting findings:
	 * 1) for a given prime P, C follows a cycle of length P*(P-1).
	 * 2) for a given prime P, we only need to calculate the first 2P values of D. The sequence is {some P values},{other P values}, and then the
	 * the set {other P values} repeats indefinitely. For example, for P=7 it's {0,1,3,3,6,3,5, 1,2,5,2,2,4,4, 1,2,5,2,2,4,4, 1,2,5,2,2,4,4...}.
	 * This basic scheme, with a calculator of combinatorial numbers with mod, and the Chinese remainder theorem, is enough.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] primes=decompose(MOD);
		int maxPrime=primes[primes.length-1];
		SequenceCalculator calculator=new SequenceCalculator(2*maxPrime);
		int[] residues=new int[primes.length];
		for (int i=0;i<primes.length;++i)	{
			int p=primes[i];
			int cycleLength=p*(p-1);
			int neededValue=N%cycleLength;
			residues[i]=calculator.getCs(1+neededValue,p)[neededValue];
		}
		long result=EulerUtils.solveChineseRemainder(residues[0],primes[0],residues[1],primes[1]);
		long currentProduct=primes[0]*primes[1];
		for (int i=2;i<primes.length;++i)	{
			result=EulerUtils.solveChineseRemainder(result,currentProduct,residues[i],primes[i]);
			currentProduct*=primes[i];
		}
		result=MOD-result;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
