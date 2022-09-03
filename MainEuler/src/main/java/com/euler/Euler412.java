package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.google.common.collect.ImmutableMap;

public class Euler412 {
	// Mirarse https://github.com/Meng-Gen/ProjectEuler/blob/master/412.py
	
	// This is most probably (still) a combinatorial explosion, but still worth a try.
	private static class Gnomon	{
		private final int bigSize;
		private final int firstLowCol;
		public Gnomon(int bigSize,int smallSize)	{
			this.bigSize=bigSize;
			firstLowCol=bigSize-smallSize;
			if (firstLowCol<=0) throw new RuntimeException("No.");
		}
		private int maxHeight(int column)	{
			return (column<firstLowCol)?bigSize:firstLowCol;
		}
		public boolean isThereRoom(int column,int height)	{
			return height<maxHeight(column);
		}
		public int maxColumn()	{
			return bigSize;
		}
	}
	
	private static class State	{
		private final int[] heights;
		public State()	{
			heights=new int[0];
		}
		private State(int[] heights)	{
			this.heights=heights;
		}
		@Override
		public boolean equals(Object other)	{
			State sOther=(State)other;
			return Arrays.equals(heights,sOther.heights);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(heights);
		}
		@Override
		public String toString()	{
			return Arrays.toString(heights);
		}
		public List<State> getChildren(Gnomon gnomon)	{
			if (heights.length==0)	{
				int[] array=new int[]{1};
				return Arrays.asList(new State(array));
			}
			List<State> result=new ArrayList<>();
			for (int i=0;i<heights.length;++i) if (gnomon.isThereRoom(i,heights[i])) if ((i==0)||(heights[i]<heights[i-1])) result.add(increase(i));
			if (heights.length<gnomon.maxColumn()) result.add(append());
			return result;
		}
		private State increase(int index)	{
			int[] newHeights=cloneHeights(false);
			++newHeights[index];
			return new State(newHeights);
		}
		private State append()	{
			int[] newHeights=cloneHeights(true);
			newHeights[heights.length]=1;
			return new State(newHeights);
		}
		private int[] cloneHeights(boolean add1)	{
			int n=add1?(heights.length+1):heights.length;
			int[] newHeights=new int[n];
			System.arraycopy(heights,0,newHeights,0,heights.length);
			return newHeights;
		}
	}
	
	private static Map<State,Long> iterate(Map<State,Long> previous,Gnomon gnomon,long mod)	{
		Map<State,Long> result=new HashMap<>();
		for (Map.Entry<State,Long> entry:previous.entrySet())	{
			State state=entry.getKey();
			long counter=entry.getValue();
			for (State child:state.getChildren(gnomon)) EulerUtils.increaseCounter(result,child,counter,mod);
		}
		return result;
	}
	
	private static long gnomonCalculator(int bigSize,int smallSize,long mod)	{
		Gnomon gnomon=new Gnomon(bigSize,smallSize);
		int iterations=bigSize*bigSize-smallSize*smallSize;
		Map<State,Long> states=ImmutableMap.of(new State(),1l);
		for (int i=0;i<iterations;++i)	{
			System.out.println(""+i+" iteraciones: tengo "+states.size()+" casos.");
			states=iterate(states,gnomon,mod);
		}
		if (states.size()!=1) throw new RuntimeException("Algo ha salido mal.");
		return states.entrySet().iterator().next().getValue();
	}
	
	private final static int BIG_SIZE=10000;
	private final static int SMALL_SIZE=5000;
	private final static long MOD=76543217l;

	public static void main(String[] args)	{
		long result=gnomonCalculator(BIG_SIZE,SMALL_SIZE,MOD);
		System.out.println(result);
	}
}
