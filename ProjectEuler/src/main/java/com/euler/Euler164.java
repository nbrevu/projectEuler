package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.euler.common.Timing;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Euler164 {
	private final static int S=9;
	private final static int N=20;
	
	private static class ThreeDigits	{
		public final int d1;
		public final int d2;
		public final int d3;
		private final int value;
		public ThreeDigits(int d1,int d2,int d3)	{
			this.d1=d1;
			this.d2=d2;
			this.d3=d3;
			value=100*d1+10*d2+d3;
		}
		@Override
		public int hashCode()	{
			return value;
		}
		@Override
		public boolean equals(Object other)	{
			return value==((ThreeDigits)other).value;
		}
		@Override
		public String toString()	{
			return Integer.toString(value);
		}
		public int maxTransitionAvailable()	{
			return S-(d2+d3);
		}
		public ThreeDigits transition(int next)	{
			return new ThreeDigits(d2,d3,next);
		}
	}
	
	private static class Transition	{
		public final int from;
		public final int to;
		public Transition(int from,int to)	{
			this.from=from;
			this.to=to;
		}
	}
	
	private static ObjIntMap<ThreeDigits> calculatePositions()	{
		ObjIntMap<ThreeDigits> result=HashObjIntMaps.newMutableMap();
		int counter=0;
		int maxI=Math.min(9,S);
		for (int i=0;i<=maxI;++i)	{
			int maxJ=Math.min(9,S-i);
			for (int j=0;j<=maxJ;++j)	{
				int maxK=Math.min(9,maxJ-j);
				for (int k=0;k<=maxK;++k)	{
					result.put(new ThreeDigits(i,j,k),counter);
					++counter;
				}
			}
		}
		return result;
	}
	
	private static List<Transition> getAllTransitions(ObjIntMap<ThreeDigits> positions)	{
		List<Transition> result=new ArrayList<>();
		for (ObjIntCursor<ThreeDigits> cursor=positions.cursor();cursor.moveNext();)	{
			ThreeDigits origin=cursor.key();
			int from=cursor.value();
			for (int i=0;i<=origin.maxTransitionAvailable();++i) result.add(new Transition(from,positions.getInt(origin.transition(i))));
		}
		return result;
	}
	
	private static long[] getInitialDistribution(ObjIntMap<ThreeDigits> positions)	{
		long[] result=new long[positions.size()];
		for (ObjIntCursor<ThreeDigits> cursor=positions.cursor();cursor.moveNext();) if (cursor.key().d1!=0) result[cursor.value()]=1l;
		return result;
	}
	
	private static void iterate(long[] previous,long[] next,List<Transition> transitions)	{
		Arrays.fill(next,0l);
		for (Transition t:transitions) next[t.to]+=previous[t.from];
	}
	
	private static long solve()	{
		ObjIntMap<ThreeDigits> positions=calculatePositions();
		List<Transition> transitions=getAllTransitions(positions);
		long[] previous=getInitialDistribution(positions);
		long[] next=new long[previous.length];
		for (int i=4;i<=N;++i)	{
			iterate(previous,next,transitions);
			long[] swap=next;
			next=previous;
			previous=swap;
		}
		long result=0;
		for (long v:previous) result+=v;
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler164::solve);
	}
}
