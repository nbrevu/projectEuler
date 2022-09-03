package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.google.common.math.LongMath;

public class Euler723 {
	private static class NumberPair	{
		public final long a;
		public final long b;
		public NumberPair(long a,long b)	{
			this.a=a;
			this.b=b;
		}
	}
	
	private static class Counter	{
		public final int totalCount;
		public final int pythagoreanCount;
		public Counter(int totalCount,int pythagoreanCount)	{
			this.totalCount=totalCount;
			this.pythagoreanCount=pythagoreanCount;
		}
	}
	
	private static List<NumberPair> getSquareSums(long sum)	{
		// Generates the pairs with a, b positive (or possibly b=0) and a>b. Some reordering must be applied to get all the combinations.
		long sqrt2=LongMath.sqrt(sum/2,RoundingMode.UP);
		long sqrt=LongMath.sqrt(sum,RoundingMode.DOWN);
		List<NumberPair> result=new ArrayList<>();
		for (long a=sqrt2;a<=sqrt;++a)	{
			long a2=a*a;
			long b2=sum-a2;
			long b=LongMath.sqrt(b2,RoundingMode.DOWN);
			if ((b<=a)&&(b*b==b2)) result.add(new NumberPair(a,b));
		}
		return result;
	}
	
	private static List<NumberPair> expand(NumberPair base)	{
		List<NumberPair> result=expandSign1(base);
		result=expandSign2(result,base);
		return expandReorder(result,base);
	}
	
	private static List<NumberPair> expandSign1(NumberPair base)	{
		return Arrays.asList(base,new NumberPair(-base.a,base.b));
	}
	
	private static List<NumberPair> expandSign2(List<NumberPair> base,NumberPair baseNumber)	{
		if (baseNumber.b==0) return base;
		List<NumberPair> result=new ArrayList<>(2*base.size());
		for (NumberPair p:base)	{
			result.add(p);
			result.add(new NumberPair(p.a,-p.b));
		}
		return result;
	}
	
	private static List<NumberPair> expandReorder(List<NumberPair> base,NumberPair baseNumber)	{
		if (baseNumber.a==baseNumber.b) return base;
		List<NumberPair> result=new ArrayList<>(2*base.size());
		for (NumberPair p:base)	{
			result.add(p);
			result.add(new NumberPair(p.b,p.a));
		}
		return result;
	}
	
	private static List<NumberPair> expand(List<NumberPair> baseResults)	{
		List<NumberPair> result=new ArrayList<>(8*baseResults.size());
		for (NumberPair base:baseResults) result.addAll(expand(base));
		return result;
	}
	
	private static Counter countCases(long squaredRadius)	{
		List<NumberPair> allPairs=expand(getSquareSums(squaredRadius));
		allPairs.sort(Comparator.comparingDouble((NumberPair np)->Math.atan2(np.b,np.a)));
		int N=allPairs.size();
		int totalCases=0;
		int pythagoreanCases=0;
		long expectedSum=8*squaredRadius;
		for (int i=0;i<N;++i) for (int j=i+1;j<N;++j) for (int k=j+1;k<N;++k) for (int l=k+1;l<N;++l)	{
			++totalCases;
			if (isPythagorean(allPairs.get(i),allPairs.get(j),allPairs.get(k),allPairs.get(l),expectedSum)) ++pythagoreanCases;
		}
		return new Counter(totalCases,pythagoreanCases);
	}
	
	private static boolean isPythagorean(NumberPair p1,NumberPair p2,NumberPair p3,NumberPair p4,long expectedSum)	{
		boolean result=getSegmentLengthSquared(p1,p2)+getSegmentLengthSquared(p2,p3)+getSegmentLengthSquared(p3,p4)+getSegmentLengthSquared(p4,p1)==expectedSum;
		logPythagoreanCase(p1,p2,p3,p4,result);
		return result;
	}
	
	private static void logPythagoreanCase(NumberPair p1,NumberPair p2,NumberPair p3,NumberPair p4,boolean isPythagorean)	{
		if ((((p1.a+p3.a)==0)&&((p1.b+p3.b)==0))||(((p2.a+p4.a)==0)&&((p2.b+p4.b)==0)))	{
			if (!isPythagorean) throw new RuntimeException("What a very good soup!");
		}	else if (isPythagorean)	{
			long l1=getSegmentLengthSquared(p1,p2);
			long l2=getSegmentLengthSquared(p2,p3);
			long l3=getSegmentLengthSquared(p3,p4);
			long l4=getSegmentLengthSquared(p4,p1);
			System.out.println(String.format("Non-standard case! Square lengths are (%d,%d,%d,%d).",l1,l2,l3,l4));
			System.out.println(String.format("\t(%d,%d)-(%d,%d)-(%d,%d)-(%d,%d).",p1.a,p1.b,p2.a,p2.b,p3.a,p3.b,p4.a,p4.b));
		}
	}
	
	private static long getSegmentLengthSquared(NumberPair p1,NumberPair p2)	{
		long dx=p1.a-p2.a;
		long dy=p1.b-p2.b;
		return dx*dx+dy*dy;
	}
	
	public static void main(String[] args)	{
		// long[] someNumbers=new long[] {1,5,13,17,25,29,65,85,125,169,221,325,1625};
		long[] someNumbers=new long[] {25};
		for (long n:someNumbers)	{
			Counter results=countCases(n);
			System.out.println("For N="+n+" there are "+results.totalCount+" inscribed quadrilaterals, "+results.pythagoreanCount+" of which are pythagorean.");
		}
	}
}
