package com.euler;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.euler.common.EulerUtils;

public class Euler310_2 {
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
		}
		public Simulator()	{}
		public Winner simulate(long[] array)	{
			long xor=(array[0]^array[1]^array[2])&1;
			return (xor==0)?Winner.NEXT_PLAYER:Winner.CURRENT_PLAYER;
		}
	}
	
	private static int countLosingPositions(long limit,Simulator sim)	{
		Map<Simulator.Winner,Integer> counter=new EnumMap<>(Simulator.Winner.class);
		counter.put(Simulator.Winner.NEXT_PLAYER,0);
		for (long[] pos:new PositionGenerator(limit)) EulerUtils.increaseCounter(counter,sim.simulate(pos));
		return counter.get(Simulator.Winner.NEXT_PLAYER);
	}
	
	public static void main(String[] args)	{
		/*
		Simulator sim=new Simulator();
		for (long l=0;l<=200;++l) System.out.println(""+l+" => "+countLosingPositions(l,sim));
		*/
		for (long l=0;l<=10;++l)	{
			System.out.println("l="+l+":");
			Map<Long,Integer> counters=new HashMap<>();
			for (long[] array:new PositionGenerator(l))	{
				long xor=array[0]^array[1]^array[2];
				EulerUtils.increaseCounter(counters,xor);
			}
			for (Map.Entry<Long,Integer> entry:counters.entrySet()) System.out.println("\t("+l+","+entry.getKey()+") => "+entry.getValue()+".");
		}
	}
}
