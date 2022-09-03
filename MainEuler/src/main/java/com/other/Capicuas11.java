package com.other;

import com.google.common.math.LongMath;

public class Capicuas11 {
	private final static int DIGITS=9;

	private static class CapicuaMaker	{
		private final long MAX_NUMBER;
		private final long[] digits;
		private final long[] reverse;
		private int currentDigits;
		
		public CapicuaMaker(int N)	{
			if (N>9) throw new RuntimeException("A�n no uso BigIntegers para esto, sorry so much.");
			MAX_NUMBER=LongMath.pow(10l,N)-1;
			digits=new long[N];
			reverse=new long[N];
			currentDigits=0;
		}
		
		public void setNumber(long in)	{
			if ((in>MAX_NUMBER)||((in%10)==0))	{
				throw new RuntimeException("No v�lido.");
			}
			int i=0;
			while (in>0)	{
				digits[i]=in%10;
				in/=10;
				++i;
			}
			currentDigits=i;
			for (int j=0;j<currentDigits;++j) reverse[j]=digits[currentDigits-1-j];
		}
		
		public long getCapicua1()	{
			return getCapicua(1);
		}
		
		public long getCapicua2()	{
			return getCapicua(0);
		}
		
		private long getCapicua(int startDigit)	{
			long result=0;
			for (int i=0;i<currentDigits;++i) result=(result*10)+reverse[i];
			for (int i=startDigit;i<currentDigits;++i) result=(result*10)+digits[i];
			return result;
		}
	}
	
	public static void main(String[] args)	{
		CapicuaMaker mk=new CapicuaMaker(DIGITS);
		for (int k=1;k<DIGITS;++k)	{
			long maxN=LongMath.pow(10l,k);
			long total=0;
			long count=0;
			for (long i=1;i<maxN;++i)	{
				if ((i%10)==0) continue;
				mk.setNumber(i);
				long c1=mk.getCapicua1();
				if ((c1%11)==0) ++count;
				long c2=mk.getCapicua2();
				if ((c2%11)!=0) throw new RuntimeException("DAS TRAGEDIA HORRIBLE.");
				++count;
				total+=2;
			}
			double percentage=(double)(count*100)/((double)total);
			System.out.println(""+k+": "+percentage);
		}
	}
}
