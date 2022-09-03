package com.euler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.ToDoubleFunction;

import com.euler.common.EulerUtils;
import com.koloboke.collect.map.ObjDoubleMap;
import com.koloboke.collect.map.hash.HashObjDoubleMaps;

public class Euler460_7 {
	private final static int GOAL=1000;
	
	private final static int MAX_HEIGHT=GOAL/2+5;

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
		public double getAngle()	{
			return Math.atan2(dy,dx);
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
		public double[] getAngles()	{
			double[] result=new double[steps.length];
			for (int i=0;i<result.length;++i) result[i]=steps[i].getAngle();
			return result;
		}
		public double[] getAngleDiffs()	{
			double[] angles=getAngles();
			double[] result=new double[steps.length-1];
			for (int i=0;i<result.length;++i) result[i]=angles[i]-angles[i+1];
			return result;
		}
	}
	
	private static class Solver	{
		private final Set<DiagonalStep> possibleSteps;
		private final Magnitudes magnitudes;
		private final Steps[][] bestSolutions;
		public Solver(int maxSteps,int maxHeight)	{
			possibleSteps=new HashSet<>();
			possibleSteps.add(new DiagonalStep(1,0));
			possibleSteps.add(new DiagonalStep(0,1));
			for (int i=1;i<=maxSteps;++i) for (int j=1;j<=maxHeight;++j) if (EulerUtils.areCoprime(i,j)) possibleSteps.add(new DiagonalStep(i,j));
			magnitudes=new Magnitudes(maxHeight);
			bestSolutions=new Steps[1+maxSteps][maxHeight];
		}
		public void calculate()	{
			bestSolutions[0][0]=Steps.getInitial();
			for (int x=0;x<bestSolutions.length;++x)	{
				System.out.println("x="+x+"...");
				for (int y=0;y<bestSolutions[x].length;++y) advance(x,y+1,bestSolutions[x][y]);
			}
		}
		public Steps getBestSolution()	{
			Steps bestSolution=bestSolutions[bestSolutions.length-1][0];
			for (int i=1;i<bestSolutions[bestSolutions.length-1].length;++i)	{
				Steps otherSolution=bestSolutions[bestSolutions.length-1][i];
				if (otherSolution.compareTo(bestSolution)<0) bestSolution=otherSolution;
			}
			return bestSolution;
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
		 * Ok, I'm finally getting the optimal solution for N=100. It would be horribly slow for N=10000, but now I can start pruning.
		 * My first attempt is going to be a prune for angles +- certain angle.
		 * 
		 * For N=100:
		 * Max difference in radians: 0.1418970546041639.
		 * Max difference in degrees: 8.130102354155978.
		 * 
		 * For N=200:
		 * Max difference in radians: 0.09966865249116204.
		 * Max difference in degrees: 5.710593137499643.
		 * 
		 * For N=300:
		 * Max difference in radians: 0.08314123188844122.
		 * Max difference in degrees: 4.763641690726177
		 */
		long tic=System.nanoTime();
		Solver s=new Solver(GOAL/2,MAX_HEIGHT);
		s.calculate();
		Steps solution=s.getBestSolution();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(2*solution.time);
		System.out.println(Arrays.toString(solution.steps));
		int x=0;
		int y=1;
		System.out.println("\t("+x+","+y+")...");
		for (DiagonalStep step:solution.steps)	{
			x+=step.dx;
			y+=step.dy;
			System.out.println("\t("+x+","+y+")...");
		}
		double[] angleDiffs=solution.getAngleDiffs();
		System.out.println("Angle differences: "+Arrays.toString(angleDiffs)+".");
		double maxDiff=0;
		for (double d:angleDiffs) maxDiff=Math.max(maxDiff,d);
		System.out.println("Max difference in radians: "+maxDiff+".");
		System.out.println("Max difference in degrees: "+(180*maxDiff/Math.PI)+".");
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
