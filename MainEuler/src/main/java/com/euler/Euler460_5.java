package com.euler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.ToDoubleFunction;

import com.euler.common.EulerUtils;
import com.koloboke.collect.map.ObjDoubleMap;
import com.koloboke.collect.map.hash.HashObjDoubleMaps;

public class Euler460_5 {
	private final static int GOAL=100;
	
	private final static int MAX_HEIGHT=GOAL;

	/*
	 * There should be a gcd somewhere, but it's not needed because in practice this is used only for coprime pairs.
	 */
	private static class DiagonalStep implements Comparable<DiagonalStep>	{
		public final int dx;
		public final int dy;
		public DiagonalStep(int num,int den)	{
			this.dx=num;
			this.dy=den;
		}
		@Override
		public int compareTo(DiagonalStep o) {
			return dx*o.dy-dy*o.dx;
		}
		@Override
		public int hashCode()	{
			return dx+dy;
		}
		@Override
		public boolean equals(Object other)	{
			if (this==other) return true;
			DiagonalStep dsOther=(DiagonalStep)other;
			return (dx==dsOther.dx)&&(dy==dsOther.dy);
		}
		@Override
		public String toString()	{
			return "("+dx+","+dy+")";
		}
		public double getDistance()	{
			return Math.sqrt(dx*dx+dy*dy);
		}
	}
	
	private static class Magnitudes	{
		private final ObjDoubleMap<DiagonalStep> distances;
		private final double[][] speeds;
		public Magnitudes(int maxHeight)	{
			distances=HashObjDoubleMaps.newMutableMap();
			speeds=new double[maxHeight][];
			for (int y0=1;y0<=maxHeight;++y0)	{
				int i=y0-1;
				speeds[i]=new double[maxHeight+1-y0];
				double l0=Math.log(y0);
				// Special case: y1=y0 -> time=1/y0.
				speeds[i][0]=y0;
				for (int y1=y0+1;y1<=maxHeight;++y1)	{
					int j=y1-y0;
					double diff=j;
					double logDiff=Math.log(y1)-l0;
					speeds[i][j]=diff/logDiff;
				}
			}
		}
		public double getSpeed(int y0,int y1)	{
			if (y1<y0) return getSpeed(y1,y0);
			return speeds[y0-1][y1-y0];
		}
		public double getDistance(DiagonalStep fraction)	{
			return distances.computeIfAbsent(fraction,(ToDoubleFunction<DiagonalStep>)DiagonalStep::getDistance);
		}
	}
	
	private static class Steps implements Comparable<Steps>	{
		private final DiagonalStep[] steps;
		private final double time;
		private Steps(DiagonalStep[] steps,double time)	{
			this.steps=steps;
			this.time=time;
		}
		public static Steps getInitial()	{
			return new Steps(new DiagonalStep[0],0d);
		}
		public Steps advance(DiagonalStep nextStep,double addedTime)	{
			DiagonalStep[] newSteps=Arrays.copyOf(steps,steps.length+1);
			newSteps[steps.length]=nextStep;
			return new Steps(newSteps,time+addedTime);
		}
		@Override
		public int compareTo(Steps other)	{
			return Double.compare(time,other.time);
		}
	}
	
	private static class Solver	{
		private final Set<DiagonalStep> possibleSteps;
		private final Magnitudes magnitudes;
		private final Steps[][] bestSolutions;
		public Solver(int maxSteps,int maxHeight)	{
			possibleSteps=new HashSet<>();
			possibleSteps.add(new DiagonalStep(1,0));
			for (int i=1;i<=maxSteps;++i) for (int j=1;j<=maxHeight;++j) if (EulerUtils.areCoprime(i,j))	{
				possibleSteps.add(new DiagonalStep(i,j));
				possibleSteps.add(new DiagonalStep(i,-j));
			}
			magnitudes=new Magnitudes(maxHeight);
			bestSolutions=new Steps[maxSteps][maxHeight];
		}
		public void calculate()	{
			Steps initialState=Steps.getInitial();
			advance(-1,1,initialState);
			for (int x=0;x<bestSolutions.length;++x)	{
				System.out.println("x="+x+"...");
				for (int y=0;y<bestSolutions[x].length;++y) advance(x,y+1,bestSolutions[x][y]);
			}
		}
		public Steps getBestSolution()	{
			return bestSolutions[bestSolutions.length-1][0];
		}
		private void put(Steps s,int step,int height)	{
			Steps existing=bestSolutions[step][height-1];
			if ((existing==null)||(s.compareTo(existing)<0)) bestSolutions[step][height-1]=s;
		}
		private void advance(int currentX,int currentHeight,Steps currentState)	{
			for (DiagonalStep step:possibleSteps)	{
				int nextX=currentX+step.dx;
				if (nextX>=bestSolutions.length) continue;
				int nextHeight=currentHeight+step.dy;
				if ((nextHeight<1)||(nextHeight>bestSolutions[nextX].length)) continue;
				double distance=magnitudes.getDistance(step);
				double speed=magnitudes.getSpeed(currentHeight,nextHeight);
				double time=distance/speed;
				Steps newState=currentState.advance(step,time);
				put(newState,nextX,nextHeight);
			}
		}
	}
	
	public static void main(String[] args)	{
		/*
		 * The solution for N=100 is NOT OPTIMAL :O. For 10 it is. I don't know what else to do right now :(. Am I missing some step type?
		 * VERTICAL steps??
		 */
		long tic=System.nanoTime();
		Solver s=new Solver(GOAL,MAX_HEIGHT);
		s.calculate();
		Steps solution=s.getBestSolution();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(solution.time);
		System.out.println(Arrays.toString(solution.steps));
		int x=0;
		int y=1;
		System.out.println("\t("+x+","+y+")...");
		for (DiagonalStep step:solution.steps)	{
			x+=step.dx;
			y+=step.dy;
			System.out.println("\t("+x+","+y+")...");
		}
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
