package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler721_3 {
	private final static long MOD=999_999_937l;
	private final static int N=5_000_000;
	
	private static class QuadraticRationals	{
		private final Instance ONE=instance(1l,0l);
		private final long x;
		private final long mod;
		public QuadraticRationals(long x,long mod)	{
			this.x=x;
			this.mod=mod;
		}
		public class Instance	{
			private final long a;
			private final long b;
			private Instance(long a,long b)	{
				this.a=a;
				this.b=b;
			}
		}
		public Instance instance(long a,long b)	{
			return new Instance(a,b);
		}
		public Instance product(Instance i1,Instance i2)	{
			long aa=(i1.a*i2.a)%mod;
			long bb=(i1.b*i2.b)%mod;
			long ab=(i1.a*i2.b)%mod;
			long ba=(i1.b*i2.a)%mod;
			long a=(aa+(bb*x))%mod;
			long b=(ab+ba)%mod;
			return instance(a,b);
		}
		public Instance binaryExponentiation(Instance i1,long exp)	{
			Instance result=ONE;
			Instance power=i1;
			while (exp>0)	{
				if ((exp&1)!=0) result=product(result,power);
				power=product(power,power);
				exp>>=1;
			}
			return result;
		}
	}
	
	/*
	 * Calculates (S(a)+floor(S(a)))^(a^2), with S(x) being the square root function.
	 * 
	 * Ideally it uses g=(S(a)+floor(S(a)))^(a^2) and h=(S(a)-floor(S(a)))^(a^2); however, the coefficient is always the same!
	 * We can just calculate g, then multiply times two, then subtract 1. FAST!
	 */
	private static long calculateF(long a,long mod)	{
		long exp=a*a;
		long ceilSqrt=LongMath.sqrt(a,RoundingMode.UP);
		if (ceilSqrt*ceilSqrt==a)	{
			// Perfect square, calculated directly using standard binary exponentiation.
			return EulerUtils.expMod(ceilSqrt+ceilSqrt,exp,mod);
		}
		QuadraticRationals rationals=new QuadraticRationals(a,mod);
		QuadraticRationals.Instance g1=rationals.instance(ceilSqrt,1);
		QuadraticRationals.Instance g=rationals.binaryExponentiation(g1,exp);
		return (2*g.a-1)%mod;
	}
	
	public static void main(String[] args)	{
		long result=0l;
		long tic=System.nanoTime();
		for (int i=1;i<=N;++i) result=result+calculateF(i,MOD);
		// "result" is not going to overflow because there aren't any multiplications. We can calculate the final mod at the end.
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
