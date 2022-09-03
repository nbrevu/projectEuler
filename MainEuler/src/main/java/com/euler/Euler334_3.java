package com.euler;

public class Euler334_3 {
	private static class BeanSimulation	{
		private int currentValue;
		private int nextValue;
		private int leftSeqLength;	// All ones except maybe one zero.
		private int zeroPos;	// -1 if the sequence is all ones. Otherwise 1 or greater, counting from the "currentValue" position.
		private long totalSteps;
		public BeanSimulation(int value1,int value2)	{
			// This includes the first step. Doing so avoids an additional branch in step().
			currentValue=value1-2;
			nextValue=value2+1;
			leftSeqLength=1;
			zeroPos=-1;
			totalSteps=1;
		}
		private void move()	{
			// First, try to move the zero (if present).
			if (currentValue<=1) throw new IllegalStateException("Sanity check failed!");
			if (zeroPos>0)	{
				int x=zeroPos;
				int y=currentValue;
				if (y<=x)	{
					// We can't "finish" the sequence, we can only do (y-1) steps.
					int s=y-1;
					currentValue-=s;
					nextValue+=s;
					zeroPos-=s;
					long p1=2*x-y+2;
					long p2=y-1;
					totalSteps+=(p1*p2)/2;
					return;
				}	else	{
					currentValue-=x+1;
					nextValue+=x;
					zeroPos=-1;
					totalSteps+=((x+1)*x)/2;
				}
			}
			// "Main" loop. Actualy we don't expect that many iterations, except at the beginning.
			if (zeroPos>=0) throw new IllegalStateException("Sanity check failed!");
			while (currentValue>=leftSeqLength+2)	{
				int x=leftSeqLength;
				++leftSeqLength;
				currentValue-=x+2;	// This might result in currentValue==0. This is fine.
				nextValue+=x+1;
				totalSteps+=((x+1)*(x+2))/2;
			}
			// End segment.
			if (currentValue>1)	{
				int x=leftSeqLength;
				int s=currentValue-1;
				++leftSeqLength;
				zeroPos=x-s+1;
				currentValue=1;
				nextValue+=s;
				totalSteps+=((2*x-s+3)*s)/2;
			}
		}
		private boolean step()	{
			move();
			if ((currentValue>=2)||(currentValue<0)) throw new IllegalStateException("Sanity check failed!");
			if (nextValue<=1) return false;
			++leftSeqLength;
			if (currentValue==0)	{
				if (zeroPos>0) throw new IllegalStateException("Sanity check failed!");
				zeroPos=1;
			}	else if (zeroPos>0) ++zeroPos;
			currentValue=nextValue;
			nextValue=0;
			return true;
		}
		public long simulate()	{
			while (step());
			return totalSteps;
		}
	}
	
	public static void main(String[] args)	{
		// I'm very close, but some of the calculations are wrong.
		BeanSimulation simulation=new BeanSimulation(289,145);
		System.out.println(simulation.simulate());
	}
}
