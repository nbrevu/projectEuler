package com.euler;

import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

public class Euler397_8 {
	private final static int K=10;
	private final static int X=100;
	
	private static enum BaseType	{
		A	{
			@Override
			protected long getMinDelta(long bigDelta) {
				return 2-bigDelta;
			}
			@Override
			protected long getMaxDelta(long bigDelta) {
				return 2*K-bigDelta;
			}
			@Override
			public char getName() {
				return 'A';
			}
			@Override
			public long getK(long bigDelta,long smallDelta) {
				return (bigDelta+smallDelta)/2;
			}
		},	B	{
			@Override
			protected long getMinDelta(long bigDelta) {
				return bigDelta-2*K;
			}
			@Override
			protected long getMaxDelta(long bigDelta) {
				return bigDelta-2;
			}
			@Override
			public char getName() {
				return 'B';
			}
			@Override
			public long getK(long bigDelta,long smallDelta) {
				return (bigDelta-smallDelta)/2;
			}
		},	C	{
			@Override
			protected long getMinDelta(long bigDelta) {
				return bigDelta+2;
			}
			@Override
			protected long getMaxDelta(long bigDelta) {
				return bigDelta+2*K;
			}
			@Override
			public char getName() {
				return 'C';
			}
			@Override
			public long getK(long bigDelta,long smallDelta) {
				return (smallDelta-bigDelta)/2;
			}
		};
		protected abstract long getMinDelta(long bigDelta);
		protected abstract long getMaxDelta(long bigDelta);
		public abstract char getName();
		public Range<Long> getRange(long bigDelta)	{
			return Range.closed(getMinDelta(bigDelta),getMaxDelta(bigDelta));
		}
		public abstract long getK(long bigDelta,long smallDelta);
	}
	
	private static enum CaseSubtype	{
		CASE_1	{
			@Override
			public Range<Long> getRange(long y) {
				return Range.atMost(y);
			}
			@Override
			public char getName() {
				return '1';
			}
		},	CASE_2	{
			@Override
			public Range<Long> getRange(long y) {
				return Range.greaterThan(y);
			}
			@Override
			public char getName() {
				return '2';
			}
		};
		public abstract Range<Long> getRange(long y);
		public abstract char getName();
	}
	
	private static enum YConversion	{
		POSITIVE	{
			@Override
			public long convert(long y) {
				return y;
			}
			@Override
			public char getName() {
				return '+';
			}
		},	NEGATIVE	{
			@Override
			public long convert(long y) {
				return -y;
			}
			@Override
			public char getName() {
				return '-';
			}
		};
		public abstract long convert(long y);
		public abstract char getName();
	}
	
	private static Range<Long> intersection(Range<Long> r1,Range<Long> r2)	{
		if ((r1==null)||(r2==null)) return null;
		else if (!r1.isConnected(r2)) return null;
		return r1.intersection(r2);
	}

	private static class CaseCounter	{
		private final static Range<Long> POSITIVES=Range.greaterThan(0l);
		private final BaseType baseType;
		private final CaseSubtype subtype;
		private final YConversion yType;
		public final String name;
		public static CaseCounter[] values()	{
			BaseType[] bs=BaseType.values();
			CaseSubtype[] cs=CaseSubtype.values();
			YConversion[] ys=YConversion.values();
			CaseCounter[] result=new CaseCounter[bs.length*cs.length*ys.length];
			int index=0;
			for (BaseType b:bs) for (CaseSubtype c:cs) for (YConversion y:ys)	{
				result[index]=new CaseCounter(b,c,y);
				++index;
			}
			return result;
		}
		private CaseCounter(BaseType baseType,CaseSubtype subtype,YConversion yType)	{
			this.baseType=baseType;
			this.subtype=subtype;
			this.yType=yType;
			name=String.format("%c%c%c",baseType.getName(),subtype.getName(),yType.getName());
		}
		public Range<Long> getSmallDeltaRange(long y,long bigDelta)	{
			long realY=yType.convert(y);
			Range<Long> range1=POSITIVES;
			Range<Long> range2=baseType.getRange(bigDelta);
			Range<Long> range3=subtype.getRange(realY);
			return intersection(range1,intersection(range2,range3));
		}
		private long countCInternal(long y,long bigDelta,long k,long smallDelta)	{
			long minC=Math.max(y+1-X,smallDelta+1-X);
			long maxC=Math.min(X,y-1+X);
			if (maxC>=minC)	{
				for (long c=minC;c<=maxC;++c)	{
					long a=c-smallDelta;
					long b=y-c;
					System.out.println(String.format("\ty=%d, Δ=%d, delta=%d: k=%d, a=%d, b=%d, c=%d.",y,bigDelta,smallDelta,k,a,b,c));
				}
			}
			return (maxC<minC)?0:(1+maxC-minC);
		}
		public long countCs(long y,long bigDelta)	{
			Range<Long> deltaRange=getSmallDeltaRange(y,bigDelta);
			if (deltaRange==null) return 0l;
			long realY=yType.convert(y);
			long delta=deltaRange.lowerEndpoint();
			if (deltaRange.lowerBoundType()==BoundType.OPEN) ++delta;
			if (((delta-bigDelta)%2)!=0) ++delta;
			long result=0l;
			for (;deltaRange.contains(delta);delta+=2)	{
				long k=baseType.getK(bigDelta,delta);
				if ((k<=0)||(k>K)) throw new IllegalStateException("D:");
				result+=countCInternal(realY,bigDelta,k,delta);
			}
			return result;
		}
	}
	
	// Lots of spurious solutions because I missed a condition.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int maxY=2*X;
		int[] lastPrimes=Primes.lastPrimeSieve(maxY);
		CaseCounter[] counters=CaseCounter.values();
		long result=0l;
		int nextToShow=1000000;
		for (int y=1;y<maxY;++y)	{
			if (y>=nextToShow)	{
				System.out.println(y+"...");
				nextToShow+=1000000;
			}
			// ZUTUN! Manage the y=0 case.
			DivisorHolder divs=(y==1)?new DivisorHolder():DivisorHolder.getFromFirstPrimes(y,lastPrimes);
			divs.powInPlace(2);
			divs.addFactor(2l,1);
			long y2_2=(2l*y)*(long)y;
			long[] qs=divs.getUnsortedListOfDivisors().toLongArray();
			Arrays.sort(qs);
			for (long q:qs)	{
				long p=y2_2/q;
				if (q>p) break;
				long bigDelta=p-q;
				for (CaseCounter counter:counters)	{
					long cs=counter.countCs(y,bigDelta);
					if (cs!=0) System.out.println(String.format("y=%d, Δ=%d, format %s: %d cases.",y,bigDelta,counter.name,cs));
					result+=cs;
				}
			}
		}
		System.out.println(result);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
