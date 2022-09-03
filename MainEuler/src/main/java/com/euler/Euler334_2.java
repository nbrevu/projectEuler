package com.euler;

public class Euler334_2 {
	private static class BeanSimulation	{
		private int currentValue;
		private int nextValue;
		private int leftSeqLength;	// All ones except maybe one zero.
		private int zeroPos;	// -1 if the sequence is all ones. Otherwise 1 or greater, counting from the "currentValue" position.
		private int totalSteps;
		public BeanSimulation(int value1,int value2)	{
			// This includes the first step. Doing so avoids an additional branch in step().
			currentValue=value1-2;
			nextValue=value2+1;
			leftSeqLength=1;
			zeroPos=-1;
			totalSteps=1;
		}
		public boolean step()	{
			if (zeroPos>1)	{
				totalSteps+=zeroPos;
				--zeroPos;
				--currentValue;
				++nextValue;
			}	else if (zeroPos==1)	{
				zeroPos=-1;
				currentValue-=2;
				++nextValue;
				++totalSteps;
			}	else	{
				zeroPos=leftSeqLength;
				++leftSeqLength;
				--currentValue;
				++nextValue;
				totalSteps+=leftSeqLength;
			}
			if ((currentValue<=1)&&(nextValue<=1)) return false;
			if (currentValue==1)	{
				++leftSeqLength;
				if (zeroPos>0) ++zeroPos;
				currentValue=nextValue;
				nextValue=0;
			}	else if (currentValue==0)	{
				++leftSeqLength;
				if (zeroPos>0) throw new IllegalStateException();	// This shouldn't happen, but just in case...
				zeroPos=1;
				currentValue=nextValue;
				nextValue=0;
			}
			return true;
		}
		public int simulate()	{
			while (step());
			return totalSteps;
		}
	}
	
	public static void main(String[] args)	{
		BeanSimulation simulation=new BeanSimulation(289,145);
		System.out.println(simulation.simulate());
	}
}
