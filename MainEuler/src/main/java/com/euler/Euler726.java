package com.euler;

import java.util.Arrays;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.google.common.math.LongMath;

public class Euler726 {
	private final static long MOD=LongMath.pow(10l,9)+33;
	
	// I don't think that what I want to do fits in memory for N=10000...
	private static class Array	{
		public final int[] data;
		public final int hashCode;
		public Array(int[] data)	{
			this.data=data;
			hashCode=Arrays.hashCode(data);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Array aOther=(Array)other;
			return Arrays.equals(data,aOther.data);
		}
	}
	private static class BottleSurfaceCalculator	{
		private final long mod;
		private final long[] powersOf2;
		public BottleSurfaceCalculator(int N,long mod)	{
			this.mod=mod;
			powersOf2=new long[N+2];
			powersOf2[0]=1l;
			for (int i=1;i<powersOf2.length;++i) powersOf2[i]=(powersOf2[i-1]+powersOf2[i-1])%MOD;
		}
		public BottleSurface initialPeak(int N)	{
			NavigableMap<Integer,Array> surface=new TreeMap<>();
			surface.put(N,new Array(new int[] {0}));
			return new BottleSurface(surface);
		}
		private class BottleSurface	{
			private final NavigableMap<Integer,Array> surface;
			private final int hashCode;
			private BottleSurface(NavigableMap<Integer,Array> surface)	{
				this.surface=surface;
				this.hashCode=surface.hashCode();
			}
			@Override
			public int hashCode()	{
				return hashCode;
			}
			@Override
			public boolean equals(Object other)	{
				BottleSurface bsOther=(BottleSurface)other;
				return surface.equals(bsOther.surface);
			}
		}
	}
	
	private static long fact(long in)	{
		return (in<=1)?in:(in*fact(in-1));
	}
	
	public static void main(String[] args)	{
		long result4=15l*14l*(7*(6*8*fact(5)+6*6*fact(4)+3*fact(6))+3*2*7*6*fact(4));
		System.out.println(result4);
	}
}
