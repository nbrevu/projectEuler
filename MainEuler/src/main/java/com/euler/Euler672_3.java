package com.euler;

import com.euler.common.LongMatrix;
import com.google.common.math.LongMath;

public class Euler672_3 {
	private final static int K=1_000_000_000;
	private final static long MOD=1_117_117_717l;
	
	private final static int BASE=7;
	private final static long INITIAL_NUMBER=(LongMath.pow(BASE,10)-1)/11;
	private final static int EXTRA_ITERATIONS=(K/10)-1;
	
	private static int[] getTriangDiffs(int base)	{
		int[] result=new int[base];
		result[0]=0;
		for (int i=1;i<result.length;++i) result[i]=result[i-1]+BASE-i;
		return result;
	}
	
	private static int[] extractDigits(long in)	{
		int numDigits=(int)Math.ceil(Math.log(in+1)/Math.log(BASE));
		int[] result=new int[numDigits];
		for (int i=result.length-1;i>=0;--i)	{
			result[i]=(int)(in%7);
			in/=7;
		}
		return result;
	}
	
	/*
	 * This assumes a space where the elements are [x, g(x+1), sum(g(1..x)), 1]. That 1 is useful for additions and constants.
	 */
	private static LongMatrix[] getMatrices(int base)	{
		int[] triangDigits=getTriangDiffs(base);
		LongMatrix[] result=new LongMatrix[base];
		for (int i=0;i<base;++i)	{
			LongMatrix matrix=new LongMatrix(4);
			matrix.assign(0,0,base);
			matrix.assign(0,3,i);
			matrix.assign(1,1,1);
			matrix.assign(1,3,base-1-i);
			matrix.assign(2,0,triangDigits[base-1]);
			matrix.assign(2,1,i);
			matrix.assign(2,2,base);
			matrix.assign(2,3,triangDigits[i]-base+1);
			matrix.assign(3,3,1);
			result[i]=matrix;
		}
		return result;
	}
	
	private static long[] getInitialState(int base,int initialDigit)	{
		long initialSum=0;
		for (int i=2;i<=initialDigit;++i) initialSum+=base-i;
		return new long[] {initialDigit,base-1-initialDigit,initialSum,1};
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] digits=extractDigits(INITIAL_NUMBER);
		long[] initialState=getInitialState(BASE,digits[0]);
		LongMatrix[] matrices=getMatrices(BASE);
		LongMatrix bigTransform=matrices[0];
		for (int digit:digits) bigTransform=matrices[digit].multiply(bigTransform,MOD);
		LongMatrix expTransform=bigTransform.pow(EXTRA_ITERATIONS,MOD);
		LongMatrix additionalTransform=matrices[digits[1]];
		for (int i=2;i<digits.length;++i) additionalTransform=matrices[digits[i]].multiply(additionalTransform,MOD);
		LongMatrix finalTransform=expTransform.multiply(additionalTransform);
		long[] finalState=finalTransform.multiply(initialState,MOD);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(finalState[2]);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
