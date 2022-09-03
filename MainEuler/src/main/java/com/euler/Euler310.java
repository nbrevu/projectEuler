package com.euler;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.euler.common.EulerUtils;

public class Euler310 {
	private static class PositionGenerator implements Iterable<long[]>,Iterator<long[]>	{
		private long N;
		private long[] current;
		private boolean isNextPositionCalculated;
		private boolean finished;
		public PositionGenerator(long N)	{
			this.N=N;
			current=new long[]{0,0,0};
			isNextPositionCalculated=true;
		}
		@Override
		public boolean hasNext() {
			return !finished;
		}
		@Override
		public long[] next() {
			calculateNext();
			isNextPositionCalculated=false;
			finished=(current[0]==N);
			return current;
		}
		@Override
		public Iterator<long[]> iterator() {
			return this;
		}
		private void calculateNext()	{
			if (isNextPositionCalculated) return;
			if (current[0]<current[1]) ++current[0];
			else if (current[1]<current[2])	{
				++current[1];
				current[0]=0;
			}	else if (current[2]<N)	{
				++current[2];
				current[1]=0;
				current[0]=0;
			}
			isNextPositionCalculated=true;
		}
	}
	
	private static class Simulator	{
		public enum Winner	{
			CURRENT_PLAYER,NEXT_PLAYER;
			public Winner reverse()	{
				return (this==CURRENT_PLAYER)?NEXT_PLAYER:CURRENT_PLAYER;
			}
			public Winner combine(Winner other)	{
				return ((this==CURRENT_PLAYER)||(other==CURRENT_PLAYER))?CURRENT_PLAYER:NEXT_PLAYER;
			}
		}
		private static class ArrayWrapper	{
			public long[] array;
			public ArrayWrapper(long[] array)	{
				this.array=copyAndNormalize(array);
			}
			private static long[] copyAndNormalize(long[] in)	{
				long[] result=Arrays.copyOf(in,in.length);
				Arrays.sort(result);
				return result;
			}
			@Override
			public int hashCode()	{
				return Arrays.hashCode(array);
			}
			@Override
			public boolean equals(Object other)	{
				return Arrays.equals(array,((ArrayWrapper)other).array);
			}
		}
		private final static long[] ZEROS=new long[]{0,0,0};
		private Map<ArrayWrapper,Winner> knownCases;
		public Simulator()	{
			knownCases=new HashMap<>();
		}
		public Winner simulate(long[] array)	{
			ArrayWrapper wr=new ArrayWrapper(array);
			Winner result=knownCases.get(wr);
			if (result!=null) return result;
			result=actuallySimulate(array);
			knownCases.put(wr,result);
			return result;
		}
		private Winner actuallySimulate(long[] array)	{
			if (Arrays.equals(array,ZEROS)) return Winner.NEXT_PLAYER;
			Winner current=null;
			for (int i=0;i<3;++i) for (long root=1;;++root)	{
				long sq=root*root;
				if (array[i]<sq) break;
				array[i]-=sq;
				Winner newWinner=simulate(array).reverse();
				array[i]+=sq;
				current=newWinner.combine(current);
			}
			return current;
		}
	}
	
	private static int countLosingPositions(long limit,Simulator sim)	{
		Map<Simulator.Winner,Integer> counter=new EnumMap<>(Simulator.Winner.class);
		for (long[] pos:new PositionGenerator(limit)) EulerUtils.increaseCounter(counter,sim.simulate(pos));
		return counter.get(Simulator.Winner.NEXT_PLAYER);
	}
	
	public static void main(String[] args)	{
		Simulator sim=new Simulator();
		for (long l=1;l<=200;++l) System.out.println(""+l+" => "+countLosingPositions(l,sim));
	}
}
