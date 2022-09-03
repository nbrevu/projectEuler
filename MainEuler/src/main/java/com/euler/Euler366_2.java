package com.euler;

import java.util.Arrays;
import java.util.stream.Stream;

import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler366_2 {
	private static class TmpZeckendorf	{
		private final static long[] FIBONACCIS=new long[] {1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597};
		private final static TmpZeckendorf END=new TmpZeckendorf(0,0);
		private final long remaining;
		private final long currentFibo;
		public TmpZeckendorf(long remaining,long toAdd)	{
			this.remaining=remaining;
			this.currentFibo=toAdd;
		}
		public static TmpZeckendorf start(long in)	{
			return new TmpZeckendorf(in,0l);
		}
		public boolean canContinue()	{
			return (remaining>0)||(currentFibo>0);
		}
		public TmpZeckendorf next()	{
			if (remaining==0) return END;
			int position=Arrays.binarySearch(FIBONACCIS,remaining);
			if (position>=0) return new TmpZeckendorf(0,remaining);
			long limitFibo=FIBONACCIS[-position-2];
			return new TmpZeckendorf(remaining-limitFibo,limitFibo);
		}
		public long getFibo()	{
			return currentFibo;
		}
	}
	
	private static class Simulator	{
		private final LongObjMap<LongObjMap<Boolean>> alwaysLosesTable;
		private final LongObjMap<LongObjMap<Boolean>> canWinTable;
		public Simulator()	{
			alwaysLosesTable=HashLongObjMaps.newMutableMap();
			canWinTable=HashLongObjMaps.newMutableMap();
		}
		// Methods alwaysLoses and canWin gracefully lifted from 692's initial experiments.
		private boolean alwaysLoses(long in,long take)	{
			if (take>=in) return false;
			LongObjMap<Boolean> subMap=alwaysLosesTable.computeIfAbsent(in,(long unused)->HashLongObjMaps.newMutableMap());
			return subMap.computeIfAbsent(take,(long t)->	{
				long t2=2*t;
				long i2=in-t;
				for (long i=1;i<=t2;++i) if (canWin(i2,i)) return true;
				return false;
			});
		}
		private boolean canWin(long in,long take)	{
			if (take>=in) return true;
			LongObjMap<Boolean> subMap=canWinTable.computeIfAbsent(in,(long unused)->HashLongObjMaps.newMutableMap());
			return subMap.computeIfAbsent(take,(long t)->	{
				long t2=2*t;
				long i2=in-t;
				for (long i=1;i<=t2;++i) if (!alwaysLoses(i2,i)) return false;
				return true;
			});
		}
		public long simulate(long in)	{
			long[] zeck=getZeckendorfRepresentation(in);
			if (zeck.length==1) return 0l;
			long[] possibilities=new long[zeck.length-1];
			possibilities[0]=in-zeck[0];
			for (int i=1;i<possibilities.length;++i) possibilities[i]=possibilities[i-1]-zeck[i];
			for (long j:possibilities) if (canWin(in,j)) return j;
			return 0;
		}
	}
	
	private static long[] getZeckendorfRepresentation(long in)	{
		return Stream.iterate(TmpZeckendorf.start(in),TmpZeckendorf::canContinue,TmpZeckendorf::next).skip(1).mapToLong(TmpZeckendorf::getFibo).toArray();
	}

	public static void main(String[] args)	{
		// H(1)=1, H(4)=1, H(17)=1, H(8)=8 and H(18)=5
		/*
		long[] toTry=new long[] {1,4,17,8,18};
		for (long i:toTry) System.out.println("H("+i+")="+simulate(i)+".");
		*/
		long sum=0l;
		Simulator sim=new Simulator();
		for (long i=1;i<=1597;++i)	{
			long h=sim.simulate(i);
			System.out.println("H("+i+")="+h+"; "+i+"="+Arrays.toString(getZeckendorfRepresentation(i))+".");
			sum+=h;
		}
		System.out.println(sum);
	}
}
