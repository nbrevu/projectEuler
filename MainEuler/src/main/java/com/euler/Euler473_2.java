package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler473_2 {
	private final static long LIMIT=LongMath.pow(10l,10);
	
	private final static double PHI=(1.0+Math.sqrt(5.0))/2.0;
	private final static int MAX_PHIGITS=(int)Math.floor(Math.log((double)LIMIT)/Math.log(PHI));
	
	private static class IntSqrt5Combination	{
		public final long intPart;
		public final long sqrt5Part;
		public final long denominator;
		private IntSqrt5Combination(long intPart,long sqrt5Part,long denominator)	{
			this.intPart=intPart;
			this.sqrt5Part=sqrt5Part;
			this.denominator=denominator;
		}
		public final static IntSqrt5Combination PHI=new IntSqrt5Combination(1l,1l,2l);
		public final static IntSqrt5Combination PHI_MINUS_ONE=new IntSqrt5Combination(-1l,1l,2l);
		public final static IntSqrt5Combination ONE=new IntSqrt5Combination(1l,0l,1l);
		public final static IntSqrt5Combination ZERO=new IntSqrt5Combination(0l,0l,1l);
		public IntSqrt5Combination multiply(IntSqrt5Combination other)	{
			long newIntPart=intPart*other.intPart+5*sqrt5Part*other.sqrt5Part;
			long newSqrt5Part=intPart*other.sqrt5Part+other.intPart*sqrt5Part;
			long newDenominator=denominator*other.denominator;
			long gcd=Math.abs(getGcd(newIntPart,newSqrt5Part,newDenominator));
			return new IntSqrt5Combination(newIntPart/gcd,newSqrt5Part/gcd,newDenominator/gcd);
		}
		public IntSqrt5Combination add(IntSqrt5Combination other)	{
			long newIntPart=intPart*other.denominator+other.intPart*denominator;
			long newSqrt5Part=sqrt5Part*other.denominator+other.sqrt5Part*denominator;
			long newDenominator=denominator*other.denominator;
			long gcd=Math.abs(getGcd(newIntPart,newSqrt5Part,newDenominator));
			return new IntSqrt5Combination(newIntPart/gcd,newSqrt5Part/gcd,newDenominator/gcd);
		}
		private static long getGcd(long a,long b,long c)	{
			return getGcd(a,getGcd(b,c));
		}
		private static long getGcd(long a,long b)	{
			if ((a==0)&&(b==0)) return 1l;
			else if (a==0) return b;
			else if (b==0) return a;
			else return EulerUtils.gcd(a,b);
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append('(');
			sb.append(intPart);
			sb.append(" + ");
			sb.append(sqrt5Part);
			sb.append("*sqrt(5))/");
			sb.append(denominator);
			return sb.toString();
		}
	}
	
	private static void generateSumsRecursive(IntSqrt5Combination current,IntSqrt5Combination[] elements,int curIndex,List<Long> result)	{
		for (int i=curIndex;i<elements.length;++i)	{
			IntSqrt5Combination next=current.add(elements[i]);
			if ((next.intPart>0)&&(next.sqrt5Part==0)) result.add(next.intPart);
			generateSumsRecursive(next,elements,i+2,result);
		}
	}
	
	private static List<Long> generateSums(IntSqrt5Combination[] elements)	{
		List<Long> result=new ArrayList<>();
		result.add(1l);	// Special case.
		IntSqrt5Combination base=IntSqrt5Combination.ZERO;
		generateSumsRecursive(base,elements,1,result);
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		IntSqrt5Combination[] positivePowers=new IntSqrt5Combination[MAX_PHIGITS];
		IntSqrt5Combination[] negativePowers=new IntSqrt5Combination[1+MAX_PHIGITS];
		positivePowers[0]=IntSqrt5Combination.PHI;
		negativePowers[0]=IntSqrt5Combination.PHI_MINUS_ONE;
		for (int i=1;i<MAX_PHIGITS;++i)	{
			positivePowers[i]=positivePowers[i-1].multiply(IntSqrt5Combination.PHI);
			negativePowers[i]=negativePowers[i-1].multiply(IntSqrt5Combination.PHI_MINUS_ONE);
		}
		negativePowers[MAX_PHIGITS]=negativePowers[MAX_PHIGITS-1].multiply(IntSqrt5Combination.PHI_MINUS_ONE);
		IntSqrt5Combination[] finalParts=new IntSqrt5Combination[1+MAX_PHIGITS];
		finalParts[0]=IntSqrt5Combination.ONE;
		for (int i=1;i<=MAX_PHIGITS;++i) finalParts[i]=positivePowers[i-1].add(negativePowers[i]);
		List<Long> results=new ArrayList<>(generateSums(finalParts));
		long sum=0;
		for (long s:results) if (s<=LIMIT) sum+=s;
		long tac=System.nanoTime();
		System.out.println(sum);
		System.out.println("Found "+results.size()+" numbers.");
		double seconds=((tac-tic)/1e9);
		System.out.println("Found the result in "+seconds+" seconds.");
	}
}
