package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.math.IntMath;

public class Euler254 {
	private final static long GOAL=150;
	
	private final static class DigitCombination implements Comparable<DigitCombination>	{
		private final static long[] FACTORIALS={1,1,2,6,24,120,720,5040,40320,362880};
		private final static BigInteger FACTORIAL_9_BIG=BigInteger.valueOf(FACTORIALS[9]);
		private final List<Integer> digits;
		private final long[] factorialRepresentation;
		public DigitCombination(List<Integer> digits)	{
			/* These checks don't really apply because of postconditions from the calling methods. */
			/*
			if (digits.isEmpty()) throw new IllegalArgumentException();
			if (digits.get(0)==0) throw new IllegalArgumentException();
			for (int i:digits) if (i<0||i>=10) throw new IllegalArgumentException();
			*/
			this.digits=digits;
			factorialRepresentation=calculateFactorialRepresentation();
		}
		public long getDigitCount()	{
			return digits.size();
		}
		private static long list2Num(List<Integer> digits)	{
			long result=0l;
			for (int i:digits) result=(10*result)+(long)i;
			return result;
		}
		private long[] calculateFactorialRepresentationUsingLong()	{
			long[] result=new long[10];
			long initialNumber=list2Num(digits);
			result[9]=initialNumber/FACTORIALS[9];
			addResidualRepresentation(result,initialNumber%FACTORIALS[9]);
			return result;
		}
		private long[] calculateFactorialRepresentationUsingBigInteger()	{
			long[] result=new long[10];
			StringBuilder initialNumberBuilder=new StringBuilder();
			for (int i:digits) initialNumberBuilder.append(i);
			BigInteger initialNumber=new BigInteger(initialNumberBuilder.toString());
			BigInteger[] divResult=initialNumber.divideAndRemainder(FACTORIAL_9_BIG);
			result[9]=divResult[0].longValue();
			addResidualRepresentation(result,divResult[1].longValue());
			return result;
		}
		private static void addResidualRepresentation(long[] array,long residue)	{
			for (int i=8;i>0;--i)	{
				array[i]=residue/FACTORIALS[i];
				residue%=FACTORIALS[i];
			}
		}
		private long[] calculateFactorialRepresentation()	{
			return (getDigitCount()>18)?calculateFactorialRepresentationUsingBigInteger():calculateFactorialRepresentationUsingLong();
		}
		private static long sumArray(long[] array)	{
			long sum=0;
			for (long l:array) sum+=l;
			return sum;
		}
		private static long sumWeightedArray(long[] array)	{
			long sum=0;
			for (int i=0;i<array.length;++i) sum+=array[i]*(long)i;
			return sum;
		}
		public long getFactorialDigits()	{
			return sumArray(factorialRepresentation);
		}
		public long getFactorialDigitSum()	{
			return sumWeightedArray(factorialRepresentation);
		}
		@Override
		public int compareTo(DigitCombination o) {
			long digitDiff=getFactorialDigits()-o.getFactorialDigits();
			if (digitDiff<0) return -1;
			else if (digitDiff>0) return 1;
			for (int i=0;i<10;++i) if (factorialRepresentation[i]>o.factorialRepresentation[i]) return -1;
			else if (factorialRepresentation[i]<o.factorialRepresentation[i]) return 1;
			return 0;
		}
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			for (int i=0;i<10;++i) for (long j=0;j<factorialRepresentation[i];++j) sb.append(i);
			return sb.toString();
		}
	}
	
	private final static class FixedLengthCombinationGenerator implements Iterator<DigitCombination>	{
		private final int length;
		private final int sum;
		private int[] digits;
		private boolean isStateValid;
		public FixedLengthCombinationGenerator(int length,int sum)	{
			this.length=length;
			this.sum=sum;
			digits=new int[length];
			createFirstCombination();
			isStateValid=true;
		}
		@Override
		public boolean hasNext() {
			return isStateValid;
		}
		@Override
		public DigitCombination next() {
			if (!isStateValid) throw new NoSuchElementException();
			DigitCombination result=createFromList();
			isStateValid=createNextCombination();
			return result;
		}
		private void createFirstCombination()	{
			int diffAllNines=sum-9*(length-1);
			if (diffAllNines>=1)	{
				digits[0]=diffAllNines;
				for (int i=1;i<length;++i) digits[i]=9;
			}	else	{
				digits[0]=1;
				distributeRemainder(1,sum-1);
			}
		}
		private void distributeRemainder(int initialDigit,int remainder)	{
			for (int i=initialDigit;i<length;++i) digits[i]=0;
			for (int i=length-1;remainder>0;--i)	{
				if (remainder>=9)	{
					digits[i]=9;
					remainder-=9;
				}	else	{
					digits[i]=remainder;
					remainder=0;
				}
			}
		}
		private boolean createNextCombination()	{
			int lastNonZero=length-1;
			while (digits[lastNonZero]==0) --lastNonZero;
			if (lastNonZero==0) return false;
			int remainder=digits[lastNonZero]-1;
			int lastNonNine=lastNonZero-1;
			while (digits[lastNonNine]==9)	{
				--lastNonNine;
				if (lastNonNine<0) return false;
				remainder+=9;
			}
			++digits[lastNonNine];
			distributeRemainder(1+lastNonNine,remainder);
			return true;
		}
		private DigitCombination createFromList()	{
			List<Integer> list=new ArrayList<>(digits.length);
			for (int d:digits) list.add(d);
			return new DigitCombination(list);
		}
	}
	
	// The numbers must verify the following:
	// 1) The sum of digits must always be equal to the starting sum.
	// 2) The order in which the numbers are generated must be increasing (as positive numbers).  
	// That is, for 5 we must generate: 5, 14, 23, 32, 41, 50, 104, 113, 122, 131, 140, 203, 212, 221, 230, 302, 311, 320, 410...
	// Since zeros at the end are allowed (how would we get, say, 5040 otherwise?), this iterator never ends!
	private static class CombinationGenerator implements Iterator<DigitCombination>	{
		private int sum;
		private FixedLengthCombinationGenerator currentGenerator;
		private int currentLength;
		public CombinationGenerator(int sum)	{
			this.sum=sum;
			currentLength=IntMath.divide(sum,9,RoundingMode.CEILING);
			currentGenerator=new FixedLengthCombinationGenerator(currentLength,sum);
		}
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public DigitCombination next() {
			if (currentGenerator.hasNext()) return currentGenerator.next();
			++currentLength;
			currentGenerator=new FixedLengthCombinationGenerator(currentLength,sum);
			return currentGenerator.next();
		}
	}
	
	public static void main(String[] args)	{
		long sum=0;
		for (int i=1;i<=GOAL;++i)	{
			CombinationGenerator generator=new CombinationGenerator(i);
			DigitCombination bestCombination=generator.next();
			int digitLimit;
			if (i<10) digitLimit=i;
			else if (i<40) digitLimit=1+IntMath.sqrt(i,RoundingMode.CEILING);
			else if (i<70) digitLimit=1+IntMath.divide(i,9,RoundingMode.CEILING);
			else digitLimit=IntMath.divide(i,9,RoundingMode.CEILING);
			for (;;)	{
				DigitCombination comb=generator.next();
				if (comb.getDigitCount()>digitLimit) break;
				if (bestCombination.compareTo(comb)>0) bestCombination=comb;
			}
			System.out.println("sg("+i+")="+bestCombination.getFactorialDigitSum());
			sum+=bestCombination.getFactorialDigitSum();
		}

		System.out.println(sum);

	}
}
