package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler397_3 {
	private final static long K=10;
	private final static long X=1000;
	
	private static enum KType	{
		SUM,DIFF1,DIFF2;
		
		public static KType getFromValues(long k,long diff,long delta)	{
			long kk=k+k;
			if (diff+delta==kk) return SUM;
			else if (diff-delta==kk) return DIFF1;
			else if (delta-diff==kk) return DIFF2;
			else throw new IllegalStateException(":(");
		}
	}
	
	private static class Summaries	{
		private final SortedMap<Long,LongSet> xsPerK;
		private final SortedMap<Long,LongSet> ysPerK;
		private final SortedMap<Long,LongSet> deltasPerK;
		private final SortedMap<Long,LongSet> diffsPerK;
		private final SortedMap<Long,Map<KType,Long>> kTypesPerK;
		public Summaries()	{
			xsPerK=new TreeMap<>();
			ysPerK=new TreeMap<>();
			deltasPerK=new TreeMap<>();
			diffsPerK=new TreeMap<>();
			kTypesPerK=new TreeMap<>();
		}
		public void displayRectangle(long a,long b,long c,long k)	{
			long x=-a+2*b+3*c;
			long y=b+c;
			long delta2=x*x-8*y*y;
			if (delta2<0)	{
				throw new IllegalStateException(":(");
			}
			long delta=LongMath.sqrt(delta2,RoundingMode.UNNECESSARY);
			if (((x-delta)%2)!=0) throw new IllegalStateException(":(");
			long p=x+delta;
			long q=x-delta;
			if (p*q!=8*y*y) throw new IllegalStateException(":(");
			long diff=c-a;
			if (((diff-delta)%2)!=0) throw new IllegalStateException(":(");
			KType type=KType.getFromValues(k,diff,delta);
			EulerUtils.increaseCounter(kTypesPerK.computeIfAbsent(k,(Long unused)->new EnumMap<>(KType.class)),type,1l);
			if ((2*k!=Math.abs(diff-delta))&&(2*k!=diff+delta)) throw new IllegalStateException(":(");
			System.out.println(String.format("x=%d, y=%d, p=%d, q=%d, delta=%d, c-a=%d, type=%s.",x,y,p,q,delta,diff,type.name()));
			if ((y==0)&&(p!=2*k)) throw new IllegalStateException(":(");
			xsPerK.computeIfAbsent(k,(Long unused)->HashLongSets.newMutableSet()).add(x);
			ysPerK.computeIfAbsent(k,(Long unused)->HashLongSets.newMutableSet()).add(y);
			deltasPerK.computeIfAbsent(k,(Long unused)->HashLongSets.newMutableSet()).add(delta);
			diffsPerK.computeIfAbsent(k,(Long unused)->HashLongSets.newMutableSet()).add(diff);
		}
		public void displaySummary()	{
			for (Map.Entry<Long,LongSet> entry:xsPerK.entrySet())	{
				long[] xs=entry.getValue().toLongArray();
				Arrays.sort(xs);
				System.out.println(String.format("Valid xs for k=%d: %s.",entry.getKey().longValue(),Arrays.toString(xs)));
			}
			for (Map.Entry<Long,LongSet> entry:ysPerK.entrySet())	{
				long[] ys=entry.getValue().toLongArray();
				Arrays.sort(ys);
				System.out.println(String.format("Valid ys for k=%d: %s.",entry.getKey().longValue(),Arrays.toString(ys)));
			}
			for (Map.Entry<Long,LongSet> entry:deltasPerK.entrySet())	{
				long[] deltas=entry.getValue().toLongArray();
				Arrays.sort(deltas);
				System.out.println(String.format("Valid deltas for k=%d: %s.",entry.getKey().longValue(),Arrays.toString(deltas)));
			}
			for (Map.Entry<Long,LongSet> entry:diffsPerK.entrySet())	{
				long[] diffs=entry.getValue().toLongArray();
				Arrays.sort(diffs);
				System.out.println(String.format("Valid diffs for k=%d: %s.",entry.getKey().longValue(),Arrays.toString(diffs)));
			}
			for (Map.Entry<Long,Map<KType,Long>> entry:kTypesPerK.entrySet())	{
				long k=entry.getKey();
				Map<KType,Long> innerMap=entry.getValue();
				long sum=innerMap.getOrDefault(KType.SUM,0l);
				long diff1=innerMap.getOrDefault(KType.DIFF1,0l);
				long diff2=innerMap.getOrDefault(KType.DIFF2,0l);
				System.out.println(String.format("For k=%d: %d cases where diff+delta=2k, %d cases where diff-delta=2k, %d cases where delta-diff=2k.",k,sum,diff1,diff2));
			}
		}
	}
	
	public static void main(String[] args)	{
		int result=0;
		int countA=0;
		int countB=0;
		int countC=0;
		int doubleResult=0;
		Summaries s=new Summaries();
		for (long k=1;k<=K;++k) for (long a=-X;a<=X;++a) for (long b=a+1;b<=X;++b) for (long c=b+1;c<=X;++c)	{
			long ax=k*a;
			long ay=a*a;
			long bx=k*b;
			long by=b*b;
			long cx=k*c;
			long cy=c*c;
			long abx=bx-ax;
			long bcx=cx-bx;
			long acx=cx-ax;
			long aby=by-ay;
			long bcy=cy-by;
			long acy=cy-ay;
			long ab2=abx*abx+aby*aby;
			long bc2=bcx*bcx+bcy*bcy;
			long ac2=acx*acx+acy*acy;
			long abbc=abx*bcx+aby*bcy;
			long bcac=bcx*acx+bcy*acy;
			long abac=abx*acx+aby*acy;
			long abbc2=abbc*abbc;
			long bcac2=bcac*bcac;
			long abac2=abac*abac;
			int counts=0;
			if ((abbc<0)&&(abbc2*2==ab2*bc2))	{	// Note the sign!
				System.out.println(String.format("Type B triangle: k=%d, a=%d, b=%d, c=%d.",k,a,b,c));
				s.displayRectangle(a,b,c,k);
				++countB;
				++counts;
			}
			if ((bcac>0)&&(bcac2*2==bc2*ac2))	{
				System.out.println(String.format("Type C triangle: k=%d, a=%d, b=%d, c=%d.",k,a,b,c));
				s.displayRectangle(a,c,b,k);
				++countC;
				++counts;
			}
			if ((abac>0)&&(abac2*2==ab2*ac2))	{
				System.out.println(String.format("Type A triangle: k=%d, a=%d, b=%d, c=%d.",k,a,b,c));
				s.displayRectangle(b,a,c,k);
				++countA;
				++counts;
			}
			if (counts>2) throw new IllegalArgumentException("Mira, no sé qué has hecho, pero está MAL.");
			if (counts>1)	{
				System.out.println(":O");
				++doubleResult;
			}
			if (counts>0) ++result;
		}
		System.out.println(result);
		System.out.println(String.format("Double results: %d.",doubleResult));
		System.out.println(String.format("Counts for: A=%d, B=%d, C=%d.",countA,countB,countC));
		s.displaySummary();
	}
}
