package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler721 {
	private final static long MOD=999_999_937l;
	private final static int N=1000;
	
	private static class NumberAndInverse	{
		public final long number;
		public final long inverse;
		public NumberAndInverse(long number,long inverse)	{
			this.number=number;
			this.inverse=inverse;
		}
	}
	
	public static NumberAndInverse[] getFactorialsMod(int upTo,long mod)	{
		NumberAndInverse[] result=new NumberAndInverse[1+upTo];
		result[0]=new NumberAndInverse(1,1);
		for (int i=1;i<=upTo;++i)	{
			long factorial=(result[i-1].number*i)%mod;
			long inverse=EulerUtils.modulusInverse(factorial,mod);
			result[i]=new NumberAndInverse(factorial,inverse);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		NumberAndInverse[] factorials=getFactorialsMod(N*N,MOD);
		long result=0l;
		for (int a=1;a<=N;++a)	{
			int a2=a*a;
			long sqrt=LongMath.sqrt(a,RoundingMode.UP);
			int sumLimit=a2/2;
			long factor=(EulerUtils.expMod(sqrt,a2,MOD)*factorials[a2].number)%MOD;
			long base=(EulerUtils.modulusInverse(sqrt*sqrt,MOD)*a)%MOD;
			long power=1l;
			long augend=0l;
			for (int k=0;k<=sumLimit;++k)	{
				long combinatorial=(factorials[2*k].inverse*factorials[a2-2*k].inverse)%MOD;
				augend+=(combinatorial*power)%MOD;
				power=(power*base)%MOD;
			}
			augend%=MOD;
			result+=(augend*factor)%MOD;
		}
		result*=2;
		result+=LongMath.sqrt(N,RoundingMode.DOWN)-N+MOD;
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
