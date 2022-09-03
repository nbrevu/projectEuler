package com.euler;

import java.util.Arrays;

public class Euler335 {
	private static class BeanSimulation	{
		private final int[] beans;
		private int lastPosition;
		public BeanSimulation(int n)	{
			beans=new int[n];
			Arrays.fill(beans,1);
			lastPosition=0;
		}
		public boolean nextStep()	{
			int howMany=beans[lastPosition];
			beans[lastPosition]=0;
			for (int i=0;i<howMany;++i)	{
				lastPosition=(lastPosition+1)%beans.length;
				++beans[lastPosition];
			}
			for (int x:beans) if (x!=1) return false;
			return true;
		}
		public int countSteps()	{
			int result=1;
			while (!nextStep()) ++result;
			return result;
		}
	}
	
	public static void main(String[] args)	{
		// OEIS doesn't tell me anything for the sequence itself, but the differences for M(2^k+1) are A214091.
		int lastValue=2;
		for (int i=2;i<1000;++i)	{
			BeanSimulation simulation=new BeanSimulation(i);
			int value=simulation.countSteps();
			if ((value>=6)&&(Integer.bitCount(i-2)==1)) System.out.println(String.format("f(%d)=%d = 2*f(%d)+%d.",i,value,i-1,value-2*lastValue));
			else System.out.println(String.format("f(%d)=%d=f(%d)+%d.",i,value,i-1,value-lastValue));
			lastValue=value;
		}
	}
}
