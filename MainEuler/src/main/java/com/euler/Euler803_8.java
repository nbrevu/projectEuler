package com.euler;

import java.math.BigInteger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.euler.common.BigMatrix;

public class Euler803_8 {
	private final static String START="PuzzleOne";
	private final static String GOAL="LuckyText";
	private final static int LOW_BITS=18;
	private final static long F=25214903917l;
	private final static long LOW_F=F%(1l<<LOW_BITS);
	private final static long K=11;
	private final static long LOW_MASK=(1l<<LOW_BITS)-1;
	private final static BigInteger MOD=BigInteger.valueOf(1l<<48);
	
	private static int[] cToB(String in)	{
		char[] cs=in.toCharArray();
		int[] result=new int[cs.length];
		for (int i=0;i<cs.length;++i)	{
			char c=cs[i];
			if ((c>='a')&&(c<='z')) result[i]=(c-'a');
			else if ((c>='A')&&(c<='Z')) result[i]=(c-'A'+26);
			else throw new IllegalArgumentException("Invalid character: \""+c+"\".");
		}
		return result;
	}
	
	private static int[] getPossibleLowInitialValues(int[] goal)	{
		IntStream.Builder result=IntStream.builder();
		int maxVal=1<<LOW_BITS;
		for (int i=0;i<maxVal;++i)	{
			long n=i;
			boolean isValid=true;
			for (int j=0;j<goal.length;++j)	{
				if ((n>>16)!=goal[j]%4)	{
					isValid=false;
					break;
				}
				n=(n*LOW_F+K)&LOW_MASK;
			}
			if (isValid) result.accept(i);
		}
		return result.build().toArray();
	}

	private static class Calculator48	{
		private final static long MASK=(1l<<32)-1;
		private final static long SMALL_MASK=(1l<<16)-1;
		private final long f;
		private final long lowF;
		private final long highF;
		private final long k;
		public Calculator48(long f,long k)	{
			this.f=f;
			lowF=f&MASK;
			highF=f>>>32;
			this.k=k;
		}
		public long next(long n)	{
			long lowN=n&MASK;
			long highN=n>>>32;
			long base=(lowN*lowF)+k;
			long carry=base>>>32;
			long lowResult=base&MASK;
			long highResult=lowN*highF+lowF*highN+carry;
			highResult&=SMALL_MASK;
			return (highResult<<32)+lowResult;
		}
		private long[] getPossibleInitialValues(int[] lowCases,int[] goal)	{
			LongStream.Builder result=LongStream.builder();
			long maxVal=1l<<48;
			long incr=52l<<16;
			for (int v0:lowCases)	{
				long initialValue=(v0&SMALL_MASK)+(goal[0]<<16);
				for (long x=initialValue;x<maxVal;x+=incr)	{
					long n=x;
					boolean isValid=true;
					for (int i=0;i<goal.length;++i)	{
						int b=(int)((n>>16)%52);
						if (b!=goal[i])	{
							isValid=false;
							break;
						}
						n=next(n);
					}
					if (isValid) result.accept(x);
				}
			}
			return result.build().toArray();
		}
		public long getN(String goal)	{
			int[] bs=cToB(goal);
			int[] lowValues=getPossibleLowInitialValues(bs);
			long[] validValues=getPossibleInitialValues(lowValues,bs);
			if (validValues.length!=1) throw new IllegalArgumentException("String not found.");
			return validValues[0];
		}
		public int countLowIterationsTo(long a,long b,int bits)	{
			long mask=(1l<<bits)-1;
			long goal=b&mask;
			for (int i=0;;++i)	{
				if ((a&mask)==goal) return i;
				a=next(a);
			}
		}
		public Calculator48 getForRepeatedIterations(long iterations)	{
			BigMatrix mat=new BigMatrix(2);
			mat.assign(0,0,BigInteger.valueOf(f));
			mat.assign(0,1,BigInteger.valueOf(k));
			mat.assign(1,0,BigInteger.ZERO);
			mat.assign(1,1,BigInteger.ONE);
			BigMatrix result=mat.pow(iterations,MOD);
			long newF=result.get(0,0).longValueExact();
			long newK=result.get(0,1).longValueExact();
			return new Calculator48(newF,newK);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Calculator48 baseCalc=new Calculator48(F,K);
		long a0=baseCalc.getN(START);
		long a1=baseCalc.getN(GOAL);
		long lowIterations=baseCalc.countLowIterationsTo(a0,a1,24);
		long a=a0;
		for (int i=0;i<lowIterations;++i) a=baseCalc.next(a);
		long result=lowIterations;
		long blockSize=1<<24;
		Calculator48 expandedCalc=baseCalc.getForRepeatedIterations(blockSize);
		while (a!=a1)	{
			a=expandedCalc.next(a);
			result+=blockSize;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
