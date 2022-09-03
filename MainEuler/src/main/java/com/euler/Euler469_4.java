package com.euler;

import java.util.BitSet;
import java.util.Locale;

import com.euler.common.Rational;

public class Euler469_4 {
	private static class Simulator	{
		private final int n;
		private BitSet bits;
		public Simulator(int n)	{
			this.n=n;
			bits=new BitSet(n);
		}
		public Rational simulate()	{
			bits.set(0,n-3);
			return simulateRecursive(2);
		}
		private Rational simulateRecursive(int emptyChairs)	{
			int available=bits.cardinality();
			if (available==0) return new Rational(emptyChairs,n);
			Rational frac=new Rational(1,available);
			Rational result=Rational.ZERO;
			for (int p=bits.nextSetBit(0);p>=0;p=bits.nextSetBit(1+p))	{
				int leftPos=(p==0)?(n-1):(p-1);
				int rightPos=(p==n-1)?0:(p+1);
				boolean left=bits.get(leftPos);
				boolean right=bits.get(rightPos);
				int empty2=emptyChairs;
				if (left) ++empty2;
				if (right) ++empty2;
				bits.clear(leftPos);
				bits.clear(p);
				bits.clear(rightPos);
				result=result.sum(frac.multiply(simulateRecursive(empty2)));
				bits.set(leftPos,left);
				bits.set(p);
				bits.set(rightPos,right);
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		// Oh well. It was indeed easier than it looked.
		for (int i=4;i<=30;++i)	{
			Simulator s=new Simulator(i);
			Rational e=s.simulate();
			System.out.println(String.format(Locale.UK,"E(%d)=%s=%.15f.",i,e.toString(),e.toDouble()));
		}
	}
}
