package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.google.common.math.IntMath;

public class Euler778 {
	private final static int R=234567;
	private final static int M=765432;
	
	private final static int BASE=10;
	private final static int HOW_MANY_DIGITS=IntMath.log10(M+1,RoundingMode.UP);
	private final static BigInteger BIG_BASE=BigInteger.valueOf(BASE);
	
	private static class SingleDigitDistribution	{
		private final BigInteger[] counters;
		private SingleDigitDistribution(BigInteger[] counters)	{
			this.counters=counters;
		}
		public static SingleDigitDistribution getUpToNumber(int number,int digit)	{
			// If "number" is 765432 and "digit" is "2", "remaining" is 32, "thisDigit" is 4 and "bigPart" is 765.
			int basePower=IntMath.pow(BASE,digit);
			int remaining=number%basePower;
			int firstPart=number/basePower;
			int thisDigit=firstPart%BASE;
			int bigPart=firstPart/BASE;
			BigInteger[] result=new BigInteger[BASE];
			BigInteger prev=BigInteger.valueOf(basePower*(1+bigPart));
			for (int i=0;i<thisDigit;++i) result[i]=prev;
			result[thisDigit]=BigInteger.valueOf(basePower*bigPart+remaining+1);
			BigInteger next=BigInteger.valueOf(basePower*bigPart);
			for (int i=thisDigit+1;i<BASE;++i) result[i]=next;
			return new SingleDigitDistribution(result);
		}
		public static SingleDigitDistribution product(SingleDigitDistribution d1,SingleDigitDistribution d2)	{
			BigInteger[] result=new BigInteger[BASE];
			for (int i=0;i<BASE;++i) result[i]=BigInteger.ZERO;
			for (int i=0;i<BASE;++i) for (int j=0;j<BASE;++j)	{
				int digit=(i*j)%BASE;
				result[digit]=result[digit].add(d1.counters[i].multiply(d2.counters[j]));
			}
			return new SingleDigitDistribution(result);
		}
		public BigInteger condense()	{
			BigInteger result=BigInteger.ZERO;
			for (int i=1;i<BASE;++i) result=result.add(BigInteger.valueOf(i).multiply(counters[i]));
			return result;
		}
	}
	
	private static class CompleteDistribution	{
		private final SingleDigitDistribution[] digits;
		private CompleteDistribution(SingleDigitDistribution[] digits)	{
			this.digits=digits;
		}
		public static CompleteDistribution partition(int number)	{
			SingleDigitDistribution[] result=new SingleDigitDistribution[HOW_MANY_DIGITS];
			for (int i=0;i<HOW_MANY_DIGITS;++i) result[i]=SingleDigitDistribution.getUpToNumber(number,i);
			return new CompleteDistribution(result);
		}
		public static CompleteDistribution product(CompleteDistribution d1,CompleteDistribution d2)	{
			SingleDigitDistribution[] result=new SingleDigitDistribution[HOW_MANY_DIGITS];
			for (int i=0;i<HOW_MANY_DIGITS;++i) result[i]=SingleDigitDistribution.product(d1.digits[i],d2.digits[i]);
			return new CompleteDistribution(result);
		}
		public BigInteger condense()	{
			BigInteger result=BigInteger.ZERO;
			BigInteger currentBase=BigInteger.ONE;
			for (int i=0;i<HOW_MANY_DIGITS;++i)	{
				result=result.add(digits[i].condense().multiply(currentBase));
				currentBase=currentBase.multiply(BIG_BASE);
			}
			return result;
		}
		public CompleteDistribution power(int exp)	{
			CompleteDistribution current=this;
			CompleteDistribution result=null;
			while (exp>0)	{
				if ((exp%2)==1)	{
					if (result==null) result=current;
					else result=product(result,current);
				}
				current=product(current,current);
				exp/=2;
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		CompleteDistribution base=CompleteDistribution.partition(M);
		CompleteDistribution finalResult=base.power(R);
		BigInteger result=finalResult.condense();
		System.out.println(result);
		System.out.println(result.mod(BigInteger.valueOf(1_000_000_009l)));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
