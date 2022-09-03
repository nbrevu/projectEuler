package com.euler;

import java.util.Arrays;

public class Euler460_3 {
	private final static int GOAL=10;
	
	private final static int MAX_HEIGHT=GOAL;
	
	private static class TimesStorage	{
		private final double[][] times;
		public TimesStorage(int maxHeight)	{
			/*
			 * To save space, we use DISPLACED indices.
			 * times[a][b] represents the time needed to go from y=a-1 to y=(a-1)+b.
			 */
			times=new double[maxHeight][];
			for (int y0=1;y0<=maxHeight;++y0)	{
				int i=y0-1;
				times[i]=new double[maxHeight+1-y0];
				double l0=Math.log(y0);
				// Special case: y1=y0 -> time=1/y0.
				times[i][0]=1d/y0;
				for (int y1=y0+1;y1<=maxHeight;++y1)	{
					int j=y1-y0;
					double diff=j;
					double dist=Math.sqrt(1+diff*diff);
					double logDiff=Math.log(y1)-l0;
					times[i][j]=dist*logDiff/diff;
				}
			}
		}
		public double getTime(int y0,int y1)	{
			if (y1<y0) return getTime(y1,y0);
			return times[y0-1][y1-y0];
		}
	}
	
	private static class Steps implements Comparable<Steps>	{
		private final int[] steps;
		private final double time;
		private Steps(int[] steps,double time)	{
			this.steps=steps;
			this.time=time;
		}
		public static Steps getInitial()	{
			return new Steps(new int[] {1},0d);
		}
		public Steps advance(int nextStep,double addedTime)	{
			int[] newSteps=Arrays.copyOf(steps,steps.length+1);
			newSteps[steps.length]=nextStep;
			return new Steps(newSteps,time+addedTime);
		}
		public int getLastPosition()	{
			return steps[steps.length-1];
		}
		@Override
		public int compareTo(Steps other)	{
			return Double.compare(time,other.time);
		}
	}
	
	private static Steps solveUsingBruteForce(int maxHeight,int goal)	{
		TimesStorage storage=new TimesStorage(goal);
		Steps steps=Steps.getInitial();
		return search(steps,storage,maxHeight,goal);
	}
	
	private static Steps search(Steps current,TimesStorage storage,int maxHeight,int remainingSteps)	{
		int lastPos=current.getLastPosition();
		if (remainingSteps==1) return current.advance(1,storage.getTime(lastPos,1));
		Steps solution=null;
		for (int y1=1;y1<=maxHeight;++y1)	{
			Steps next=current.advance(y1,storage.getTime(lastPos,y1));
			Steps bestInBranch=search(next,storage,maxHeight,remainingSteps-1);
			if (solution==null) solution=bestInBranch;
			else solution=(solution.compareTo(bestInBranch)<=0)?solution:bestInBranch;
		}
		return solution;
	}

	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Steps bestSolution=solveUsingBruteForce(MAX_HEIGHT,GOAL);
		double shortestTime=bestSolution.time;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(Arrays.toString(bestSolution.steps));
		System.out.println(shortestTime);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
