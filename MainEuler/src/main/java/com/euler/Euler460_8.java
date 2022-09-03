package com.euler;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.ToDoubleFunction;

import com.euler.common.EulerUtils;
import com.google.common.math.IntMath;
import com.koloboke.collect.map.ObjDoubleMap;
import com.koloboke.collect.map.hash.HashObjDoubleMaps;

public class Euler460_8 {
	private final static int GOAL=10000;
	
	private final static int HALF_GOAL=GOAL/2;
	private final static int MAX_LOOKUP=IntMath.sqrt(GOAL,RoundingMode.UP);
	private final static int MAX_HEIGHT=HALF_GOAL+5;
	private final static double MAX_ANGLE_DIFF=0.025;

	/*
	 * There should be a gcd somewhere, but it's not needed because in practice this is used only for coprime pairs.
	 */
	private static class DiagonalStep implements Comparable<DiagonalStep>	{
		public final static DiagonalStep HORIZONTAL_STEP=new DiagonalStep(1,0);
		public final static DiagonalStep VERTICAL_STEP=new DiagonalStep(0,1);
		public final int dx;
		public final int dy;
		public DiagonalStep(int dx,int dy)	{
			this.dx=dx;
			this.dy=dy;
		}
		@Override
		public int compareTo(DiagonalStep o) {
			return dy*o.dx-dx*o.dy;
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
	
	private static class PartialSolution implements Comparable<PartialSolution>	{
		public final DiagonalStep lastStep;
		public final double time;
		private PartialSolution(DiagonalStep lastStep,double time)	{
			this.lastStep=lastStep;
			this.time=time;
		}
		public static PartialSolution getInitial()	{
			return new PartialSolution(DiagonalStep.VERTICAL_STEP,0d);
		}
		public PartialSolution advance(DiagonalStep nextStep,double addedTime)	{
			return new PartialSolution(nextStep,time+addedTime);
		}
		@Override
		public int compareTo(PartialSolution other)	{
			return Double.compare(time,other.time);
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
	
	private static class Solver	{
		private final NavigableSet<DiagonalStep> possibleSteps;
		private final Map<DiagonalStep,NavigableSet<DiagonalStep>> intervals;
		private final Magnitudes magnitudes;
		private final PartialSolution[][] bestSolutions;
		private final double maxAngleDiff;
		public Solver(int maxSteps,int maxHeight,int maxLookup,double maxAngleDiff)	{
			possibleSteps=new TreeSet<>();
			possibleSteps.add(DiagonalStep.HORIZONTAL_STEP);
			possibleSteps.add(DiagonalStep.VERTICAL_STEP);
			for (int i=1;i<=maxLookup;++i) for (int j=1;j<=maxLookup;++j) if (EulerUtils.areCoprime(i,j)) possibleSteps.add(new DiagonalStep(i,j));
			intervals=new HashMap<>();
			magnitudes=new Magnitudes(maxHeight);
			bestSolutions=new PartialSolution[1+maxSteps][maxHeight];
			this.maxAngleDiff=maxAngleDiff;
		}
		private NavigableSet<DiagonalStep> calculateSubset(DiagonalStep step)	{
			double maxAngle=step.getAngle();
			double minAngle=maxAngle-maxAngleDiff;
			DiagonalStep lowerBound;
			if (minAngle<=0) lowerBound=DiagonalStep.HORIZONTAL_STEP;
			else	{
				int dx=1+bestSolutions.length;
				int dy=(int)Math.round(Math.tan(minAngle)*dx);
				lowerBound=new DiagonalStep(dx,dy);
			}
			return possibleSteps.subSet(lowerBound,true,step,true);
		}
		private NavigableSet<DiagonalStep> getNextCandidates(DiagonalStep step)	{
			return intervals.computeIfAbsent(step,this::calculateSubset);
		}
		public void calculate()	{
			bestSolutions[0][0]=PartialSolution.getInitial();
			for (int x=0;x<bestSolutions.length;++x)	{
				System.out.println(x+"...");
				for (int y=0;y<bestSolutions[x].length;++y) advance(x,y+1,bestSolutions[x][y]);
			}
		}
		public double getBestSolution()	{
			PartialSolution bestSolution=null;
			for (int i=0;i<bestSolutions[bestSolutions.length-1].length;++i)	{
				PartialSolution otherSolution=bestSolutions[bestSolutions.length-1][i];
				if ((otherSolution!=null)&&((bestSolution==null)||(otherSolution.compareTo(bestSolution)<0))) bestSolution=otherSolution;
			}
			return 2*bestSolution.time;
		}
		private void put(PartialSolution s,int step,int height)	{
			PartialSolution existing=bestSolutions[step][height-1];
			if ((existing==null)||(s.compareTo(existing)<0)) bestSolutions[step][height-1]=s;
		}
		private void advance(int currentX,int currentHeight,PartialSolution currentState)	{
			if (currentState==null) return;
			for (DiagonalStep step:getNextCandidates(currentState.lastStep))	{
				int nextX=currentX+step.dx;
				if (nextX>=bestSolutions.length) continue;
				int nextHeight=currentHeight+step.dy;
				if ((nextHeight<1)||(nextHeight>bestSolutions[nextX].length)) continue;
				double distance=magnitudes.getDistance(step);
				double speed=magnitudes.getSpeed(currentHeight,nextHeight);
				double time=distance/speed;
				PartialSolution newState=currentState.advance(step,time);
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
		Solver s=new Solver(HALF_GOAL,MAX_HEIGHT,MAX_LOOKUP,MAX_ANGLE_DIFF);
		s.calculate();
		double result=s.getBestSolution();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK, "%.9f",result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
