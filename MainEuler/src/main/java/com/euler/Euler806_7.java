package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.BigRational;
import com.euler.common.EulerUtils.Pair;

/*
 * Let's try dynamic programming. I'm about 99% sure that this would take too much memory if done directly for 10^5, but...
 * yeah, let's try :).
 * 
 * It DOES work, but it's horribly slow. Like O(n^3*log n) slow or worse.
 * Next stop: try to iterate over all the valid combinations for a given N, and move downwards, maybe pre-generating the values up to N=1000
 * for a meet-in-the-middle-like approach. 
 */
public class Euler806_7 {
	private final static long N=500;
	
	private static class HanoiCombination	{
		public final int left;
		public final int middle;
		public final int right;
		public HanoiCombination(int left,int middle,int right)	{
			this.left=left;
			this.middle=middle;
			this.right=right;
		}
		public boolean isLosing()	{
			return (left^middle^right)==0;
		}
		@Override
		public String toString()	{
			return String.format("{%d,%d,%d}",left,middle,right); 
		}
	}
	
	private static class HanoiCombinationCache	{
		private final List<List<List<HanoiCombination>>> combis;
		public HanoiCombinationCache()	{
			combis=new ArrayList<>();
			HanoiCombination combi0=new HanoiCombination(0,0,0);	// Not actually used, but it "takes space", so to speak.
			List<HanoiCombination> list00=new ArrayList<>();
			list00.add(combi0);
			List<List<HanoiCombination>> list0=new ArrayList<>();
			list0.add(list00);
			combis.add(list0);
		}
		public HanoiCombination get(int left,int middle,int right)	{
			while (left>=combis.size()) combis.add(new ArrayList<>());
			List<List<HanoiCombination>> listA=combis.get(left);
			while (middle>=listA.size()) listA.add(new ArrayList<>());
			List<HanoiCombination> listB=listA.get(middle);
			while (right>=listB.size()) listB.add(new HanoiCombination(left,middle,listB.size()));
			return listB.get(right);
		}
		public HanoiCombination getCanonical(HanoiCombination combi)	{
			int a=combi.left;
			int b=combi.middle;
			int c=combi.right;
			int swap;
			if (a>b)	{
				swap=a;
				a=b;
				b=swap;
			}
			if (b>c)	{
				swap=b;
				b=c;
				c=swap;
			}
			if (a>b)	{
				swap=a;
				a=b;
				b=swap;
			}
			return get(a,b,c);
		}
	}
	
	private static class HanoiSequence	{
		private static Pair<BigInteger,BigInteger> combineSumAndCount(Pair<BigInteger,BigInteger> a,Pair<BigInteger,BigInteger> b)	{
			return new Pair<>(a.first.add(b.first),a.second.add(b.second));
		}
		private final Map<HanoiCombination,Pair<BigInteger,BigInteger>> countAndSums;
		private HanoiSequence(Map<HanoiCombination,Pair<BigInteger,BigInteger>> countAndSums)	{
			this.countAndSums=countAndSums;
		}
		public static HanoiSequence getInitial(HanoiCombinationCache cache)	{
			HanoiCombination combi0=cache.get(1,0,0);
			HanoiCombination combi1=cache.get(0,0,1);
			Pair<BigInteger,BigInteger> sum0=new Pair<>(BigInteger.ONE,BigInteger.ZERO);
			Pair<BigInteger,BigInteger> sum1=new Pair<>(BigInteger.ONE,BigInteger.ONE);
			Map<HanoiCombination,Pair<BigInteger,BigInteger>> countAndSums=Map.of(combi0,sum0,combi1,sum1);
			return new HanoiSequence(countAndSums);
		}
		public HanoiSequence evolve(HanoiCombinationCache cache,BigInteger power2)	{
			Map<HanoiCombination,Pair<BigInteger,BigInteger>> result=new HashMap<>();
			for (Map.Entry<HanoiCombination,Pair<BigInteger,BigInteger>> entry:countAndSums.entrySet())	{
				HanoiCombination oldKey=entry.getKey();
				Pair<BigInteger,BigInteger> oldValue=entry.getValue();
				{
					// First half.
					HanoiCombination newKey=cache.get(oldKey.left+1,oldKey.right,oldKey.middle);
					result.merge(newKey,oldValue,HanoiSequence::combineSumAndCount);
				}
				{
					// Second half (displaced).
					BigInteger newSum=oldValue.first.multiply(power2).add(oldValue.second);
					Pair<BigInteger,BigInteger> newValue=new Pair<>(oldValue.first,newSum);
					HanoiCombination newKey=cache.get(oldKey.middle,oldKey.left,oldKey.right+1);
					result.merge(newKey,newValue,HanoiSequence::combineSumAndCount);
				}
			}
			return new HanoiSequence(result);
		}
		public Pair<BigInteger,BigInteger> accumulateValidCases()	{
			BigInteger count=BigInteger.ZERO;
			BigInteger sum=BigInteger.ZERO;
			for (Map.Entry<HanoiCombination,Pair<BigInteger,BigInteger>> entry:countAndSums.entrySet())	{
				HanoiCombination key=entry.getKey();
				if (key.isLosing())	{
					Pair<BigInteger,BigInteger> value=entry.getValue();
					count=count.add(value.first);
					sum=sum.add(value.second);
				}
			}
			return new Pair<>(count,sum);
		}
		public Map<HanoiCombination,BigInteger> getLosingOccurrencesMap()	{
			Map<HanoiCombination,BigInteger> result=new HashMap<>();
			for (Map.Entry<HanoiCombination,Pair<BigInteger,BigInteger>> entry:countAndSums.entrySet())	{
				HanoiCombination numbers=entry.getKey();
				if (!numbers.isLosing()) continue;
				BigInteger count=entry.getValue().first;
				result.put(numbers,count);
			}
			return result;
		}
	}
	
	private static class InnerSummaryInfo	{
		private final Map<HanoiCombination,BigInteger> subcases;
		public InnerSummaryInfo()	{
			subcases=new HashMap<>();
		}
		public void addCase(HanoiCombination combi,BigInteger count)	{
			subcases.put(combi,count);
		}
		public BigRational getQuotient()	{
			BigRational result=null;
			for (Map.Entry<HanoiCombination,BigInteger> entry:subcases.entrySet())	{
				HanoiCombination key=entry.getKey();
				int divisor=key.left+key.right;
				BigRational quotient=new BigRational(entry.getValue(),BigInteger.valueOf(divisor));
				if (result==null) result=quotient;
				else if (!result.equals(quotient)) throw new IllegalStateException("Hypothesis disproved.");
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		HanoiCombinationCache cache=new HanoiCombinationCache();
		HanoiSequence current=HanoiSequence.getInitial(cache);
		BigInteger pow2=BigInteger.ONE;
		for (int i=2;i<=N;++i)	{
			pow2=pow2.add(pow2);
			current=current.evolve(cache,pow2);
			if ((i%2)==0)	{
				Pair<BigInteger,BigInteger> results=current.accumulateValidCases();
				System.out.println(String.format("For N=%d there are %s solutions adding up to %s.",i,results.first,results.second));
				Map<HanoiCombination,BigInteger> losingMap=current.getLosingOccurrencesMap();
				System.out.println(String.format("\tThe distribution of subcases is: %s.",losingMap.toString()));
				if ((i%4)!=0) continue;	// Odd cases are too odd to be interesting.
				Map<HanoiCombination,InnerSummaryInfo> innerMap=new HashMap<>();
				for (Map.Entry<HanoiCombination,BigInteger> entry:losingMap.entrySet())	{
					HanoiCombination combi=entry.getKey();
					HanoiCombination canonical=cache.getCanonical(combi);
					BigInteger count=entry.getValue();
					innerMap.computeIfAbsent(canonical,(HanoiCombination unused)->new InnerSummaryInfo()).addCase(combi,count);
				}
				for (Map.Entry<HanoiCombination,InnerSummaryInfo> entry:innerMap.entrySet())	{
					InnerSummaryInfo info=entry.getValue();
					BigRational quotient=info.getQuotient();
					System.out.println(String.format("\t\t%s => %s.",info.subcases,quotient));
				}
			}
		}
	}
}
