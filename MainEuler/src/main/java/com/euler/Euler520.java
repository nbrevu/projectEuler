package com.euler;

import java.util.Arrays;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler520 {
	// For such a relatively simple problem, I've taken my time to get it right...
	private final static long MOD=LongMath.pow(10l,9)+123l;
	private final static int TOTAL_CASES=IntMath.pow(2,5)*IntMath.pow(3,5);	// 7776.
	private final static int MAX_POWER_OF_2=39;
	
	private static class Identifier	{
		private final int[] oddNumbers;	// 0: not present. 1: odd amount. 2: even amount.
		private final boolean[] evenNumbers;
		public Identifier(int[] oddNumbers,boolean[] evenNumbers)	{
			this.oddNumbers=oddNumbers;
			this.evenNumbers=evenNumbers;
		}
		public static Identifier getIdentifierForSingleDigit(int digit)	{
			int[] oddNumbers=new int[5];
			boolean[] evenNumbers=new boolean[5];
			if (digit==0) return new Identifier(oddNumbers,evenNumbers);
			else if ((digit%2)==0) evenNumbers[digit/2]=true;
			else oddNumbers[digit/2]=1;
			return new Identifier(oddNumbers,evenNumbers);
		}
		public int getIdentifierIndex()	{
			int result=0;
			for (int i=0;i<5;++i) result=3*result+oddNumbers[i];
			for (int i=0;i<5;++i) result=2*result+(evenNumbers[i]?1:0);
			return result;
		}
		public static Identifier getFromIdentifierIndex(int index)	{
			boolean[] evenNumbers=new boolean[5];
			for (int i=4;i>=0;--i)	{
				evenNumbers[i]=((index%2)==1);
				index/=2;
			}
			int[] oddNumbers=new int[5];
			for (int i=4;i>=0;--i)	{
				oddNumbers[i]=index%3;
				index/=3;
			}
			return new Identifier(oddNumbers,evenNumbers);
		}
		/*
		 * This is used to put i1 "above" i2. This is important, since because of the special treatment for 0, we need to handle a slightly
		 * different case depending on whether i2 has odd digits or not.
		 */
		public static Identifier combine(Identifier i1,Identifier i2,long maxI2Digits)	{
			int[] oddNumbers=new int[5];
			for (int i=0;i<5;++i)	{
				int sum=i1.oddNumbers[i]+i2.oddNumbers[i];
				oddNumbers[i]=(sum>=3)?(sum-2):sum;
			}
			boolean[] evenNumbers=new boolean[5];
			for (int i=0;i<5;++i) evenNumbers[i]=i1.evenNumbers[i]^i2.evenNumbers[i];
			boolean extraOddZerosAdded=(((maxI2Digits%2l)==1l)!=i2.hasOddDigits());
			if (extraOddZerosAdded) evenNumbers[0]=!evenNumbers[0];
			return new Identifier(oddNumbers,evenNumbers);
		}
		public Identifier addOddZeros()	{
			boolean[] evenNumbers2=Arrays.copyOf(evenNumbers,evenNumbers.length);
			evenNumbers2[0]=!evenNumbers2[0];
			return new Identifier(oddNumbers,evenNumbers2);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(oddNumbers)+Arrays.hashCode(evenNumbers);
		}
		@Override
		public boolean equals(Object other)	{
			Identifier iOther=(Identifier)other;
			return Arrays.equals(oddNumbers,iOther.oddNumbers)&&Arrays.equals(evenNumbers,iOther.evenNumbers);
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			for (int i=0;i<5;++i) sb.append(oddNumbers[i]).append(' ');
			for (int i=0;i<5;++i) sb.append(evenNumbers[i]?'T':'F').append(' ');
			return sb.toString();
		}
		public boolean hasOddDigits()	{
			int oddSum=0;
			for (int i=0;i<5;++i)	{
				oddSum+=oddNumbers[i];
				if (evenNumbers[i]) ++oddSum;
			}
			return (oddSum%2)==1;
		}
		public boolean isSimber()	{
			for (int i=0;i<5;++i) if (evenNumbers[i]||(oddNumbers[i]==2)) return false;
			return true;
		}
	}
	
	private static class IdentifierCache	{
		private final IntObjMap<Identifier> cache;
		public IdentifierCache()	{
			cache=HashIntObjMaps.newMutableMap();
			for (int i=0;i<TOTAL_CASES;++i)	{
				Identifier id=Identifier.getFromIdentifierIndex(i);
				cache.put(i,id);
			}
		}
		public Identifier get(int index)	{
			return cache.get(index);
		}
	}
	
	private static class SimberSummary	{
		private final static IdentifierCache CACHE=new IdentifierCache();
		private final long amountOfDigits;
		private final long[] counters;
		private SimberSummary(long amountOfDigits,long[] counters)	{
			this.amountOfDigits=amountOfDigits;
			this.counters=counters;
		}
		public static SimberSummary getInitial()	{
			long[] counters=new long[TOTAL_CASES];
			for (int i=1;i<10;++i) counters[Identifier.getIdentifierForSingleDigit(i).getIdentifierIndex()]=1l;
			return new SimberSummary(1l,counters);
		}
		public long getSimberCount(long mod)	{
			long result=0l;
			for (int i=0;i<TOTAL_CASES;++i) if (CACHE.get(i).isSimber()) result=(result+counters[i])%mod;
			return result;
		}
		public static SimberSummary combine(SimberSummary s1,SimberSummary s2,long mod)	{
			long[] counters=new long[TOTAL_CASES];
			for (int i=0;i<TOTAL_CASES;++i) if (s1.counters[i]!=0l)	{
				Identifier id1=CACHE.get(i);
				for (int j=0;j<TOTAL_CASES;++j) if (s2.counters[j]!=0l)	{
					Identifier id2=CACHE.get(j);
					Identifier newId=Identifier.combine(id1,id2,s2.amountOfDigits);
					int newIndex=newId.getIdentifierIndex();
					counters[newIndex]=(counters[newIndex]+s1.counters[i]*s2.counters[j])%mod;
				}
			}
			for (int i=0;i<TOTAL_CASES;++i) counters[i]=(counters[i]+s2.counters[i])%mod;
			if ((s2.amountOfDigits%2)==0) for (int i=0;i<TOTAL_CASES;++i) counters[i]=(counters[i]+s1.counters[i])%mod;
			else for (int i=0;i<TOTAL_CASES;++i)	{
				int toWrite=CACHE.get(i).addOddZeros().getIdentifierIndex();
				counters[toWrite]=(counters[toWrite]+s1.counters[i])%mod;
			}
			return new SimberSummary(s1.amountOfDigits+s2.amountOfDigits,counters);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long sum=0l;
		SimberSummary previous=SimberSummary.getInitial();	// For Q(1).
		for (int i=1;i<=MAX_POWER_OF_2;++i)	{
			SimberSummary current=SimberSummary.combine(previous,previous,MOD);	// For Q(2^i).
			previous=current;
			sum=(sum+current.getSimberCount(MOD))%MOD;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
