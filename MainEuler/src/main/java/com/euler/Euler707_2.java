package com.euler;

import java.util.HashSet;
import java.util.Set;

public class Euler707_2 {
	private static class Simulator	{
		private final int height;
		private final int width;
		private final boolean[] simulation;
		public Simulator(int height,int width)	{
			if (height*width>30) throw new RuntimeException("No te flipes.");
			this.height=height;
			this.width=width;
			simulation=new boolean[1<<(height*width)];
		}
		public int simulate()	{
			simulateFromStartingState(0);
			return countTrueValues();
		}
		private void simulateFromStartingState(int startingValue)	{
			Set<Integer> pending=new HashSet<>();
			pending.add(startingValue);
			while (!pending.isEmpty())	{
				Integer setHead=pending.iterator().next();
				pending.remove(setHead);
				int value=setHead.intValue();
				if (simulation[value]) continue;
				simulation[value]=true;
				for (int i=0;i<height;++i) for (int j=0;j<width;++j)	{
					int newState=toggle(value,i,j);
					if (!simulation[newState]) pending.add(newState);
				}
			}
		}
		private int toggle(int currentState,int i,int j)	{
			int bitMask=0;
			if (i>0) bitMask+=getBit(i-1,j);
			if (j>0) bitMask+=getBit(i,j-1);
			bitMask+=getBit(i,j);
			if (i<height-1) bitMask+=getBit(i+1,j);
			if (j<width-1) bitMask+=getBit(i,j+1);
			return currentState^bitMask;
		}
		private int getBit(int i,int j)	{
			int bit=i*width+j;
			return 1<<bit;
		}
		private int countTrueValues()	{
			int result=0;
			for (int i=0;i<simulation.length;++i) if (simulation[i]) ++result;
			return result;
		}
	}
	
	private static int simulate(int height,int width)	{
		Simulator s=new Simulator(height,width);
		return s.simulate();
	}
	
	public static void main(String[] args)	{
		for (int i=1;i<=5;++i) for (int j=i;j<=10;++j) if ((i*j)<30) System.out.println("F("+i+","+j+")="+simulate(i,j)+".");
	}
}
