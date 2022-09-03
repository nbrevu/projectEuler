package com.euler;

import java.util.Arrays;

public class Euler460_2 {
	private final static int GOAL=1000;
	
	private final static int MAX_HEIGHT=GOAL/2+10;	// Surely not good enough for the GOAL=10000, but good enough for the small tests.
	
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
		@Override
		public int compareTo(Steps other)	{
			return Double.compare(time,other.time);
		}
	}
	
	private static class Solver	{
		private final int maxHeight;
		private final TimesStorage times;
		private final int numSteps;
		public Solver(int maxHeight,int numSteps)	{
			this.maxHeight=maxHeight;
			times=new TimesStorage(maxHeight);
			this.numSteps=numSteps;
		}
		private Steps[] getFirstSteps()	{
			Steps[] result=new Steps[maxHeight];
			Steps base=Steps.getInitial();
			for (int y=1;y<=maxHeight;++y) result[y-1]=base.advance(y,times.getTime(1,y));
			return result;
		}
		private Steps[] getNextSteps(Steps[] prevGen)	{
			Steps[] result=new Steps[prevGen.length];
			// First step: initialise with the base y0=1.
			Steps prevStep=prevGen[0];
			for (int y1=1;y1<=maxHeight;++y1)	{
				double addedTime=times.getTime(1,y1);
				result[y1-1]=prevStep.advance(y1,addedTime);
			}
			// Second step: dynamic programming. We get the minimum at each step.
			for (int i=1;i<prevGen.length;++i)	{
				int y0=i+1;
				prevStep=prevGen[i];
				for (int y1=1;y1<=maxHeight;++y1)	{
					int j=y1-1;
					double addedTime=times.getTime(y0,y1);
					Steps before=result[j];
					Steps now=prevStep.advance(y1,addedTime);
					result[j]=(before.compareTo(now)<=0)?before:now;
				}
			}
			return result;
		}
		public Steps getBestSolution()	{
			Steps[] currentGen=getFirstSteps();
			for (int i=2;i<=numSteps;++i) currentGen=getNextSteps(currentGen);
			return currentGen[0];
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Solver solver=new Solver(MAX_HEIGHT,GOAL);
		Steps bestSolution=solver.getBestSolution();
		double shortestTime=bestSolution.time;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(Arrays.toString(bestSolution.steps));
		System.out.println(shortestTime);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
