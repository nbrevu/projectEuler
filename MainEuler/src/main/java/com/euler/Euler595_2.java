package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;
import com.euler.common.BigIntegerUtils.BigFactorialCache;
import com.euler.common.BigIntegerUtils.Fraction;

public class Euler595_2 {
	private final static int SIZE=200;
	
	private static BigCombinatorialNumberCache COMBS=new BigCombinatorialNumberCache(1);
	private static BigFactorialCache FACTS=new BigFactorialCache(1);

	// Many thanks to https://oeis.org/A010027!
	private static class PermutationGrouper	{
		private final int size;
		public PermutationGrouper(int size)	{
			this.size=size;
		}
		public BigInteger[] getGroups()	{
			// U(n,k)=(k+1)!*binom(n,k)*Sum((-1)^i/i!, i=0..k+1)/n
			// Here, n=size, and k=i.
			BigInteger[] result=new BigInteger[1+size];
			result[0]=BigInteger.ZERO;
			for (int i=1;i<=size;++i)	{
				BigInteger num=FACTS.get(i+1).multiply(COMBS.get(size,i));
				Fraction frac=new Fraction();
				for (int j=0;j<=(i+1);++j)	{
					Fraction baseFrac=new Fraction(BigInteger.valueOf(1),FACTS.get(j));
					if ((j%2)==0) frac=frac.add(baseFrac);
					else frac=frac.subtract(baseFrac);
				}
				frac=frac.divide(BigInteger.valueOf(size));
				Fraction res1=frac.multiply(num);
				assert (res1.denominator.equals(BigInteger.ONE));
				result[i]=res1.numerator;
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
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
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
		for (int i=2;i<=SIZE;++i)	{
			System.out.println(""+i+"...");
			Fraction base=new Fraction(FACTS.get(i),BigInteger.ONE);
			BigInteger[] groups=new PermutationGrouper(i).getGroups();
			for (int j=1;j<i;++j) base=base.add(result[j].multiply(groups[j]));
			result[i]=base.divide(FACTS.get(i).subtract(groups[i]));
		}
		// We must subtract one to the actual result. Therefore, we subtract the denominator to the numerator. 
		Fraction preResult=result[SIZE];
		Fraction actualResult=new Fraction(preResult.numerator.subtract(preResult.denominator),preResult.denominator);
		System.out.println(actualResult.toString());
		System.out.println(""+actualResult.numerator.bitLength()+" bits in the numerator, "+actualResult.denominator.bitLength()+" bits in the denominator.");
		MathContext mc=new MathContext(20,RoundingMode.HALF_UP);
		BigDecimal decResult=actualResult.asBigDecimal(mc);
		System.out.println(decResult);
		long tac=System.nanoTime();
		double seconds=((double)(tac-tic))/1e9;
		System.out.println("Calculated in "+seconds+" seconds.");
	}
}
