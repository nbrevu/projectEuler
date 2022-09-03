package com.euler;

import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import com.euler.common.BigIntegerUtils.Fraction;
import com.euler.common.EulerUtils.IntPermutation;
import com.euler.common.EulerUtils.IntPermutationGenerator;

public class Euler595 {
	private final static int SIZE=10;
	
	private static class PermutationGrouper	{
		private final int size;
		public PermutationGrouper(int size)	{
			this.size=size;
		}
		public BigInteger[] getGroups()	{
			BigInteger[] result=new BigInteger[1+size];
			for (int i=0;i<=size;++i) result[i]=BigInteger.ZERO;
			for (IntPermutation perm:new IntPermutationGenerator(size))	{
				int groups=getGroups(perm.getNumbers());
				result[groups]=BigInteger.ONE.add(result[groups]);
			}
			{
				// DEBUG!
				assert(result[0].equals(BigInteger.ZERO));
				BigInteger sum=BigInteger.ZERO;
				for (int i=1;i<=size;++i) sum=sum.add(result[i]);
				int fact=1;
				for (int i=2;i<=size;++i) fact*=i;
				assert(sum.intValue()==fact);
			}
			return result;
		}
		private int getGroups(int[] array)	{
			int result=size;
			for (int i=1;i<size;++i) if (array[i]==1+array[i-1]) --result;
			return result;
		}
	}
	
	public static void main(String[] args)	{
		Fraction[] result=new Fraction[1+SIZE];
		result[0]=new Fraction();
		result[1]=new Fraction();
		// For each additional case, we need to solve an equation.
		// We define S(x) as the expected amount of shuffles.
		// We also define P(x,y) as the probability that a shuffle of "x" elements results in "y" groups.
		// Then, S(x)=1+[S(x)·P(x,x) + S(x-1)·P(x,x-1) + ... + S(1)·P(x,1)].
		// But if G(x,y) is the grouping that we get with the previous function, then P(x,y) = G(x,y)/x!
		// So x!·S(x)=x!+[S(x)·G(x,x) + S(x-1)·G(x,x-1) + ... + S(1)·G(x,1)].
		// Therefore S(x) = [x! + S(x-1)·G(x,x-1) + ... + S(1)·G(x,1)]/(x!-G(x,x)).
		int fact=1;
		for (int i=2;i<=SIZE;++i)	{
			fact*=i;
			Fraction base=new Fraction(BigInteger.valueOf(fact),BigInteger.ONE);
			BigInteger[] groups=new PermutationGrouper(i).getGroups();
			{
				// DEBUG!
				for (int j=0;j<groups.length;++j) System.out.println("G("+i+","+j+")="+groups[j].toString()+".");
				System.out.println();
			}
			for (int j=1;j<i;++j) base=base.add(result[j].multiply(groups[j]));
			result[i]=base.divide(BigInteger.valueOf(fact).subtract(groups[i]));
		}
		MathContext mc=new MathContext(20,RoundingMode.HALF_UP);
		for (int i=2;i<=SIZE;++i) System.out.println(""+i+": "+result[i]+"="+(result[i].asBigDecimal(mc).toString())+".");
	}
}
