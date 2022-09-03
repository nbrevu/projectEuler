package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Euler369_2 {
	// Ooooh, está mal. A ver qué tengo que arreglar.
	private final static long[] VARIATIONS_13=new long[14];
	static	{
		VARIATIONS_13[1]=13l;
		for (int i=2;i<=13;++i) VARIATIONS_13[i]=VARIATIONS_13[i-1]*(14-i);
	}
	private final static long[] FACTORIALS=new long[14];
	static	{
		FACTORIALS[0]=1l;
		for (int i=1;i<=13;++i) FACTORIALS[i]=i*FACTORIALS[i-1];
	}
	
	private static class SuitDistribution	{
		// This is UNORDERED in terms of suits. It's ordered in terms of how many cards of each suit are there.
		// For example, [1,3,3,6] is valid. [0,1,2,10] is not, because every suit must have at least one card. [1,2,7,4] is neither, because it's
		// not ordered.
		private final int[] distribution;
		public SuitDistribution()	{
			distribution=new int[]{1,1,1,1};	// Default case.
		}
		private SuitDistribution(int[] distribution)	{
			this.distribution=distribution;
		}
		public int get(int index)	{
			return distribution[index];
		}
		@Override
		public boolean equals(Object other)	{
			try	{
				SuitDistribution sOther=(SuitDistribution)other;
				return Arrays.equals(distribution,sOther.distribution);
			}	catch (ClassCastException exc)	{
				return false;
			}
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(distribution);
		}
		@Override
		public String toString()	{
			return Arrays.toString(distribution);
		}
		public void getChildren(Set<SuitDistribution> destination)	{
			for (int i=0;i<3;++i) if (distribution[i]<distribution[i+1]) destination.add(increaseOne(i));
			destination.add(increaseOne(3));
		}
		private SuitDistribution increaseOne(int position)	{
			int[] result=Arrays.copyOf(distribution,4);
			++result[position];
			return new SuitDistribution(result);
		}
		private static Set<SuitDistribution> getAllChildren(Set<SuitDistribution> in)	{
			Set<SuitDistribution> result=new HashSet<>();
			for (SuitDistribution distr:in) distr.getChildren(result);
			return result;
		}
		public long getMultiplier()	{
			// WARNING! A HUGE BATTLESHIP "BLACK FUCKING MAGIC" IS APPROACHING FAST.
			// The equality pattern is coded as a 3-bit number, with bit K corresponding to whether distribution[K] equals distribution[K+1].
			// If the pattern equals 7, all the digits are the same.
			// If the pattern is 3 or 6 (011 / 110) then there are three equal numbers.
			// If the pattern is 5 (101), there are two pairs of numbers.
			// If there is a single bit active (1, 2, 4) there is a pair of numbers.
			// If the pattern is 0, there aren't any repeated numbers.
			
			// NOTE FROM ME FROM THE FUTURE (i.e. a lot of time after I wrote the code): there is a more readable way to do this: use BitSet and
			// cardinality().
			int equality=0;
			for (int i=0;i<3;++i)	{
				equality*=2;
				if (distribution[i]==distribution[i+1]) ++equality;
			}
			switch (equality)	{
				case 0:
					return 24l;
				case 1:case 2:case 4:
					return 12l;
				case 3:case 6:
					return 4l;
				case 5:
					return 6l;
				case 7:
					return 1l;
				default:throw new IllegalStateException();
			}
		}
		public long getCombinations()	{
			long result=0;
			for (NumbersDistribution nd:NumbersDistribution.getForSuitDistribution(this)) result+=nd.getCombinations();
			return result*getMultiplier();
		}
	}
	
	private static class NumbersDistribution	{
		private static class NumbersForASuit	{
			public final int reused;
			public final int added;
			public NumbersForASuit(int reused,int added)	{
				this.reused=reused;
				this.added=added;
			}
			public static List<NumbersForASuit> forNumber(int in)	{
				List<NumbersForASuit> result=new ArrayList<>();
				for (int i=1;i<=in;++i) result.add(new NumbersForASuit(in-i,i));
				return result;
			}
			@Override
			public String toString()	{
				return "{"+reused+","+added+"}";
			}
		}
		private final NumbersForASuit[] distribution;
		private NumbersDistribution(NumbersForASuit[] distribution)	{
			this.distribution=distribution;
		}
		@Override
		public String toString()	{
			return Arrays.toString(distribution);
		}
		public static List<NumbersDistribution> getForSuitDistribution(SuitDistribution sd)	{
			List<NumbersDistribution> result=new ArrayList<>();
			NumbersForASuit n0=new NumbersForASuit(0,sd.get(0));
			List<NumbersForASuit> l1=NumbersForASuit.forNumber(sd.get(1));
			List<NumbersForASuit> l2=NumbersForASuit.forNumber(sd.get(2));
			List<NumbersForASuit> l3=NumbersForASuit.forNumber(sd.get(3));
			for (NumbersForASuit n1:l1) for (NumbersForASuit n2:l2) for (NumbersForASuit n3:l3)	{
				NumbersForASuit[] array=new NumbersForASuit[]{n0,n1,n2,n3};
				result.add(new NumbersDistribution(array));
			}
			return result;
		}
		private int getTotalUsed()	{
			int result=0;
			for (int i=0;i<4;++i) result+=distribution[i].added;
			return result;
		}
		private long getMultiplier()	{
			long result=1;
			int currentlyUsing=0;
			for (int i=0;i<4;++i)	{
				result*=getCombinations(currentlyUsing,distribution[i].reused);
				currentlyUsing+=distribution[i].added;
			}
			return result;
		}
		private long getCombinations(int n,int k)	{
			if (n<k) return 0l;
			return FACTORIALS[n]/(FACTORIALS[k]*FACTORIALS[n-k]);
		}
		private long getDivisor()	{
			long result=1;
			for (int i=0;i<4;++i) result*=FACTORIALS[distribution[i].added];
			return result;
		}
		public long getCombinations()	{
			return (VARIATIONS_13[getTotalUsed()]*getMultiplier())/getDivisor();
		}
	}
	
	public static void main(String[] args)	{
		long sum=0;
		Set<SuitDistribution> current=Collections.singleton(new SuitDistribution());
		for (int i=4;i<=13;++i)	{
			long tmpSum=0;
			for (SuitDistribution sd:current)	{
				long combs=sd.getCombinations();
				sum+=combs;
				tmpSum+=combs;
				System.out.println(""+sd+" => "+combs+".");
			}
			System.out.println("\t\tTMP para i="+i+": "+tmpSum+".");
			System.out.println();
			current=SuitDistribution.getAllChildren(current);
		}
		System.out.println(sum);
	}
}
