package com.euler;

public class Euler334_4 {
	private final static int N=1500;
	
	private static int[] getSequence(int length)	{
		int[] result=new int[length];
		int t=123456;
		for (int i=0;i<length;++i)	{
			boolean doXor=((t&1)!=0);
			t>>=1;
			if (doXor) t^=926252;
			result[i]=(t%2048)+1;
		}
		return result;
	}
	
	private static class NextValuesSource	{
		private final int[] values;
		private int index;
		public NextValuesSource(int[] values)	{
			this.values=values;
			index=0;
		}
		public int next()	{
			if (index>=values.length) return 0;
			int result=values[index];
			++index;
			return result;
		}
	}
	
	private static class BeanSimulation	{
		private final NextValuesSource values;
		private int currentValue;
		private int nextValue;
		private int leftSeqLength;	// All ones except maybe one zero.
		private int zeroPos;	// -1 if the sequence is all ones. Otherwise 1 or greater, counting from the "currentValue" position.
		private long totalSteps;
		public BeanSimulation(int n)	{
			this(new NextValuesSource(getSequence(n)));
		}
		public BeanSimulation(NextValuesSource values)	{
			this.values=values;
			int value1=values.next();
			int value2=values.next();
			// This includes the first step. Doing so avoids an additional branch in step().
			currentValue=value1-2;
			nextValue=value2+1;
			leftSeqLength=1;
			zeroPos=-1;
			totalSteps=1;
		}
		private void move()	{
			// First, try to move the zero (if present).
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
					totalSteps+=((x+1)*(long)x)/2;
				}
			}
			// "Main" loop. Actualy we don't expect that many iterations, except at the beginning.
			while (currentValue>=leftSeqLength+2)	{
				int x=leftSeqLength;
				++leftSeqLength;
				currentValue-=x+2;	// This might result in currentValue==0. This is fine.
				nextValue+=x+1;
				totalSteps+=((x+1)*(long)(x+2))/2;
			}
			// End segment.
			if (currentValue>1)	{
				int x=leftSeqLength;
				int s=currentValue-1;
				++leftSeqLength;
				zeroPos=x-s+1;
				currentValue=1;
				nextValue+=s;
				totalSteps+=((2*x-s+3)*(long)s)/2;
			}
		}
		private boolean step()	{
			move();
			if (nextValue<=1) return false;
			++leftSeqLength;
			if (currentValue==0) zeroPos=1;
			else if (zeroPos>0) ++zeroPos;
			currentValue=nextValue;
			nextValue=values.next();
			return true;
		}
		public long simulate()	{
			while (step());
			return totalSteps;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BeanSimulation simulation=new BeanSimulation(N);
		long result=simulation.simulate();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
