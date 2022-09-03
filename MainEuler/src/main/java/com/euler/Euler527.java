package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import com.euler.common.BigIntegerUtils.Fraction;
import com.google.common.math.DoubleMath;
import com.google.common.math.LongMath;

public class Euler527 {
	private final static long IN=LongMath.pow(10l,10);
	
	/*
	private static Fraction[] recursiveMode(int N)	{
		Fraction[] res=new Fraction[N];
		res[0]=new Fraction();
		res[1]=new Fraction(1l,1l);
		for (int i=2;i<N;++i)	{
			Fraction augend=new Fraction();
			for (int j=1;j<i;++j) augend=augend.add(res[j].multiply(new Fraction(j,1l)));
			augend=augend.multiply(new Fraction(2l,i*i));
			res[i]=augend.add(new Fraction(1l,1l));
		}
		return res;
	}
	
	private static Fraction[] iterativeMode(int N)	{
		Fraction[] res=new Fraction[N];
		res[0]=new Fraction();
		res[1]=new Fraction(1l,1l);
		for (int i=2;i<N;++i)	{
			int isq=i*i;
			Fraction num=res[i-1].multiply(BigInteger.valueOf(isq-1));
			num=num.add(new Fraction(2*i-1l,1l));
			res[i]=num.divide(BigInteger.valueOf(isq));
		}
		return res;
	}
	*/
	
	private static Fraction getRFrac(int N)	{
		Fraction current=new Fraction(1l,1l);
		for (int i=2;i<=N;++i)	{
			int isq=i*i;
			Fraction num=current.multiply(BigInteger.valueOf(isq-1));
			num=num.add(new Fraction(2*i-1l,1l));
			current=num.divide(BigInteger.valueOf(isq));
		}
		return current;
	}
	
	private static class BCalculator	{
		private final Map<Long,Double> cache;
		public BCalculator()	{
			cache=new HashMap<>();
			cache.put(0l,0.0);
			cache.put(1l,1.0);
		}
		public double getB(long N)	{
			return cache.computeIfAbsent(N,(Long in)->	{
				return actuallyGetB(in);
			});
		}
		private double actuallyGetB(long N)	{
			long i=DoubleMath.roundToLong((double)(N+1)/2.0,RoundingMode.DOWN);
			double Ba=getB(i-1);
			double Bb=getB(N-i);
			return 1+((i-1)*Ba+(N-i)*Bb)/N;
		}
	}
	
	private static double getBDouble(long N)	{
		return new BCalculator().getB(N);
	}
	
	private static double getRDouble(long N)	{
		// See also: https://math.stackexchange.com/questions/1453843/expected-value-of-a-guessing-game
		double current=1.0;
		for (long i=2;i<=N;++i)	{
			double isq=(double)i*(double)i;
			double num=current*(isq-1);
			num+=(2*i-1);
			current=num/isq;
		}
		return current;
	}
	
	public static void main(String[] args)	{
		// This has minuscule precision issues. The solution is correct up to the 6th decimal digits, but the other two are wrong :(.
		/* 11.924120494761645 -> actual answer is 11.92412011. */
		// Also https://github.com/Meng-Gen/ProjectEuler/blob/master/527.py
		long tic=System.nanoTime();
		System.out.println(getRDouble(IN)-getBDouble(IN));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
