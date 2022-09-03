package com.euler;

import com.google.common.math.LongMath;

public class Euler778_2 {
	private final static long R=234567l;
	private final static long M=765432l;
	private final static long MOD=1_000_000_009l;
	
	private final static int BASE=10;
	private final static int HOW_MANY_DIGITS=(int)Math.ceil(Math.log(M+1)/Math.log(BASE));
	
	private static class SingleDigitDistribution	{
		private final long[] counters;
		private SingleDigitDistribution(long[] counters)	{
			this.counters=counters;
		}
		public static SingleDigitDistribution getUpToNumber(long number,int digit)	{
			long basePower=LongMath.pow(BASE,digit);
			long remaining=number%basePower;
			long firstPart=number/basePower;
			int thisDigit=(int)(firstPart%BASE);
			long bigPart=firstPart/BASE;
			long[] result=new long[BASE];
			long prev=(basePower*(1+bigPart))%MOD;
			for (int i=0;i<thisDigit;++i) result[i]=prev;
			result[thisDigit]=(basePower*bigPart+remaining+1)%MOD;
			long next=(basePower*bigPart)%MOD;
			for (int i=thisDigit+1;i<BASE;++i) result[i]=next;
			return new SingleDigitDistribution(result);
		}
		public static SingleDigitDistribution product(SingleDigitDistribution d1,SingleDigitDistribution d2)	{
			long[] result=new long[BASE];
			for (int i=0;i<BASE;++i) result[i]=0;
			for (int i=0;i<BASE;++i) for (int j=0;j<BASE;++j)	{
				int digit=(i*j)%BASE;
				result[digit]=(result[digit]+(d1.counters[i]*d2.counters[j]))%MOD;
			}
			return new SingleDigitDistribution(result);
		}
		public long condense()	{
			long result=0;
			for (int i=1;i<BASE;++i) result+=counters[i]*i;
			return result%MOD;
		}
	}
	
	private static class CompleteDistribution	{
		private final SingleDigitDistribution[] digits;
		private CompleteDistribution(SingleDigitDistribution[] digits)	{
			this.digits=digits;
		}
		public static CompleteDistribution partition(long number)	{
			SingleDigitDistribution[] result=new SingleDigitDistribution[HOW_MANY_DIGITS];
			for (int i=0;i<HOW_MANY_DIGITS;++i) result[i]=SingleDigitDistribution.getUpToNumber(number,i);
			return new CompleteDistribution(result);
		}
		public static CompleteDistribution product(CompleteDistribution d1,CompleteDistribution d2)	{
			SingleDigitDistribution[] result=new SingleDigitDistribution[HOW_MANY_DIGITS];
			for (int i=0;i<HOW_MANY_DIGITS;++i) result[i]=SingleDigitDistribution.product(d1.digits[i],d2.digits[i]);
			return new CompleteDistribution(result);
		}
		public long condense()	{
			long result=0;
			long currentBase=1;
			for (int i=0;i<HOW_MANY_DIGITS;++i)	{
				result+=digits[i].condense()*currentBase;
				currentBase*=BASE;
				result%=MOD;
				currentBase%=MOD;
			}
			return result;
		}
		public CompleteDistribution power(long exp)	{
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
		long result=finalResult.condense();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
