package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler776_2 {
	private final static int BASE=10;
	private final static BigInteger N=new BigInteger("123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ",36);
	private final static int PRECISION=12;
	
	private final static BigInteger BIG_BASE=BigInteger.valueOf(BASE);
	private final static List<Integer> DIGITS=extractDigits(N);
	
	private static List<Integer> extractDigits(BigInteger n)	{
		List<Integer> result=new LinkedList<>();
		while (n.signum()>0)	{
			BigInteger[] division=n.divideAndRemainder(BIG_BASE);
			result.add(0,division[1].intValueExact());
			n=division[0];
		}
		return result;
	}
	
	/*
	 * Basic object for information exchange. One of these represents a group of numbers, presumably numbers whose digit sum is the same value,
	 * and only the total count and sum are needed.
	 */
	private static class CountAndSum	{
		private BigInteger count;
		private BigInteger sum;
		public CountAndSum()	{
			this(BigInteger.ZERO,BigInteger.ZERO);
		}
		public CountAndSum(BigInteger count,BigInteger sum)	{
			this.count=count;
			this.sum=sum;
		}
		public void accumulate(CountAndSum other)	{
			count=count.add(other.count);
			sum=sum.add(other.sum);
		}
		private CountAndSum addDigit(int digit)	{
			BigInteger newSum=BIG_BASE.multiply(sum).add(BigInteger.valueOf(digit).multiply(count));
			return new CountAndSum(count,newSum);
		}
	}
	
	/*
	 * This is a pack of three "CountAndSum" objects, because we need special treatment depending on whether the numbers summarised in the
	 * CountAndSum objects are below, equal to, or above certain limit. I expect the "equalTo" case to be 0 in most but not all cases.
	 */
	private static class Counters	{
		private final CountAndSum beforeLimit;
		private final CountAndSum equalToLimit;
		private final CountAndSum afterLimit;
		public Counters()	{
			beforeLimit=new CountAndSum();
			equalToLimit=new CountAndSum();
			afterLimit=new CountAndSum();
		}
		public void accumulateBefore(CountAndSum toAccumulate)	{
			beforeLimit.accumulate(toAccumulate);
		}
		public void accumulateEqualTo(CountAndSum toAccumulate)	{
			equalToLimit.accumulate(toAccumulate);
		}
		public void accumulateAfter(CountAndSum toAccumulate)	{
			afterLimit.accumulate(toAccumulate);
		}
		public void accumulate(Counters counters)	{
			accumulateBefore(counters.beforeLimit);
			accumulateEqualTo(counters.equalToLimit);
			accumulateAfter(counters.afterLimit);
		}
		public BigInteger getSumLessOrEqualToLimit()	{
			return beforeLimit.sum.add(equalToLimit.sum);
		}
		/*
		 * The second parameter indicates the behaviour of the values equal to the limit.
		 * If (digitCompareToLimit<0), then the "equalToLimit" counters are accumulated into beforeLimit.
		 * If (digitCompareToLimit==0), then the "equalToLimit" counters are accumulated into equalToLimit.
		 * If (digitCompareToLimit>0), then the "equalToLimit" counters are accumulated into afterLimit.
		 */
		private Counters addDigit(int digit,int digitCompareToLimit)	{
			CountAndSum newBeforeLimit=beforeLimit.addDigit(digit);
			CountAndSum newEqualToLimit=equalToLimit.addDigit(digit);
			CountAndSum newAfterLimit=afterLimit.addDigit(digit);
			Counters result=new Counters();
			result.accumulateBefore(newBeforeLimit);
			if (digitCompareToLimit<0) result.accumulateBefore(newEqualToLimit);
			else if (digitCompareToLimit==0) result.accumulateEqualTo(newEqualToLimit);
			else result.accumulateAfter(newEqualToLimit);
			result.accumulateAfter(newAfterLimit);
			return result;
		}
	}
	
	// Represents all the numbers with a fixed amount of digits.
	private static class GenerationalCounters	{
		private final IntObjMap<Counters> countersByDigitSum;
		private GenerationalCounters(IntObjMap<Counters> countersByDigitSum)	{
			this.countersByDigitSum=countersByDigitSum;
		}
		public static GenerationalCounters getZeroCase()	{
			Counters zero=new Counters();
			zero.accumulateEqualTo(new CountAndSum(BigInteger.ONE,BigInteger.ZERO));
			IntObjMap<Counters> result=HashIntObjMaps.newImmutableMapOf(0,zero);
			return new GenerationalCounters(result);
		}
		public GenerationalCounters evolve(int limitDigit)	{
			IntObjMap<Counters> result=HashIntObjMaps.newMutableMap();
			for (IntObjCursor<Counters> cursor=countersByDigitSum.cursor();cursor.moveNext();)	{
				int prevCount=cursor.key();
				Counters elements=cursor.value();
				for (int i=0;i<BASE;++i)	{
					int newCount=prevCount+i;
					if (newCount==0) continue;
					int compare=i-limitDigit;
					Counters nextElements=elements.addDigit(i,compare);
					result.merge(newCount,nextElements,(Counters existing,Counters newOne)->	{
						existing.accumulate(newOne);
						return existing;
					});
				}
			}
			return new GenerationalCounters(result);
		}
	}
	
	/*
	 * Represents all the counters with a common sum of digits. The list includes cases where the amount of digits is below the limit, and the
	 * final object, possibly null if it doesn't have representation with the final amount of digits, represents the edge case where the "below,
	 * equal or above" conditions must be respected.
	 */
	private static class FinalCounters	{
		private final List<Counters> incompleteCounters;
		private Counters completeCounter;
		public FinalCounters()	{
			incompleteCounters=new ArrayList<>();
			completeCounter=null;
		}
		public void addIncompleteCounter(Counters counter)	{
			incompleteCounters.add(counter);
		}
		public void setCompleteCounter(Counters counter)	{
			completeCounter=counter;
		}
		public Counters condense()	{
			Counters result=new Counters();
			for (Counters c:incompleteCounters)	{
				result.accumulateBefore(c.beforeLimit);
				result.accumulateBefore(c.equalToLimit);
				result.accumulateBefore(c.afterLimit);
			}
			if (completeCounter!=null)	{
				result.accumulateBefore(completeCounter.beforeLimit);
				result.accumulateEqualTo(completeCounter.equalToLimit);
				result.accumulateAfter(completeCounter.afterLimit);
			}
			return result;
		}
	}
	
	private static class FullData	{
		private final List<Integer> digits;
		private final IntObjMap<FinalCounters> totalCountersPerDigitSum;
		private final GenerationalCounters[] countersPerDigitAmount;
		public FullData(List<Integer> digits)	{
			this.digits=digits;
			totalCountersPerDigitSum=HashIntObjMaps.newMutableMap();
			countersPerDigitAmount=new GenerationalCounters[1+digits.size()];
		}
		public void fill()	{
			countersPerDigitAmount[0]=GenerationalCounters.getZeroCase();
			for (int i=0;i<digits.size();++i)	{
				countersPerDigitAmount[i+1]=countersPerDigitAmount[i].evolve(digits.get(i).intValue());
				for (IntObjCursor<Counters> cursor=countersPerDigitAmount[i+1].countersByDigitSum.cursor();cursor.moveNext();)	{
					int sum=cursor.key();
					Counters counters=cursor.value();
					FinalCounters target=totalCountersPerDigitSum.computeIfAbsent(sum,(int unused)->new FinalCounters());
					if (i==digits.size()-1) target.setCompleteCounter(counters);
					else target.addIncompleteCounter(counters);
				}
			}
		}
		public IntObjMap<Counters> getSummary()	{
			IntObjMap<Counters> result=HashIntObjMaps.newMutableMap();
			for (IntObjCursor<FinalCounters> cursor=totalCountersPerDigitSum.cursor();cursor.moveNext();) result.put(cursor.key(),cursor.value().condense());
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		FullData data=new FullData(DIGITS);
		data.fill();
		IntObjMap<Counters> dataPerDigitSum=data.getSummary();
		BigDecimal result=BigDecimal.ZERO;
		MathContext context=new MathContext(50);
		for (IntObjCursor<Counters> cursor=dataPerDigitSum.cursor();cursor.moveNext();)	{
			if (cursor.key()==0) continue;
			BigDecimal num=new BigDecimal(cursor.value().getSumLessOrEqualToLimit());
			BigDecimal den=BigDecimal.valueOf(cursor.key());
			result=result.add(num.divide(den,context));
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println(String.format(Locale.UK,"%."+PRECISION+"e",result.doubleValue()));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
