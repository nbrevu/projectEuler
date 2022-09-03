package com.euler;

import java.util.Arrays;
import java.util.Locale;

import com.euler.common.DoubleMatrix;

// Hell and fire was spawned to be released.
public class Euler666 {
	private final static int K=500;
	private final static int M=10;
	
	private final static double TOL=1e-9;
	
	private static class PseudoRandomSequence	{
		private int r;
		public PseudoRandomSequence()	{
			r=306;
		}
		public int next()	{
			int result=r;
			r=(r*r)%10007;
			return result;
		}
	}
	
	private static enum BehaviourType	{
		DIE {
			@Override
			public Behaviour getBehaviour(int i,int k) {
				return Die.INSTANCE;
			}
		},	CLONE {
			@Override
			public Behaviour getBehaviour(int i,int k) {
				return new Clone(i);
			}
		},	MUTATE {
			@Override
			public Behaviour getBehaviour(int i,int k) {
				int otherIndex=(i+i)%k;
				return new Mutate(otherIndex);
			}
		},	SPLIT {
			@Override
			public Behaviour getBehaviour(int i,int k) {
				int otherIndex=(i*i+1)%k;
				return new Split(otherIndex);
			}
		},	SPAWN {
			@Override
			public Behaviour getBehaviour(int i,int k) {
				int otherIndex=(i+1)%k;
				return new Spawn(i,otherIndex);
			}
		};
		private static final BehaviourType[] VALUES=values();
		public static BehaviourType getFromIndex(int i)	{
			return VALUES[i];
		}
		public abstract Behaviour getBehaviour(int i,int k);
	}
	
	private static interface Behaviour	{
		public double getDyingProb(double[] xs);
		public void getDyingProbDerivative(double[] xs,double[] row);
	}
	
	private static enum Die implements Behaviour	{
		INSTANCE;
		@Override
		public double getDyingProb(double[] xs)	{
			return 1d;
		}
		@Override
		public void getDyingProbDerivative(double[] xs,double[] row)	{}
	}
	
	private static class Clone implements Behaviour	{
		private final int index;
		public Clone(int index)	{
			this.index=index;
		}
		@Override
		public double getDyingProb(double[] xs)	{
			double p=xs[index];
			return p*p;
		}
		@Override
		public void getDyingProbDerivative(double[] xs,double[] row)	{
			row[index]+=2*xs[index];
		}
	}
	
	private static class Mutate implements Behaviour	{
		private final int otherIndex;
		public Mutate(int otherIndex)	{
			this.otherIndex=otherIndex;
		}
		@Override
		public double getDyingProb(double[] xs)	{
			return xs[otherIndex];
		}
		@Override
		public void getDyingProbDerivative(double[] xs,double[] row)	{
			row[otherIndex]+=1d;
		}
	}
	
	private static class Split implements Behaviour	{
		private final int otherIndex;
		public Split(int otherIndex)	{
			this.otherIndex=otherIndex;
		}
		@Override
		public double getDyingProb(double[] xs)	{
			double p=xs[otherIndex];
			return p*p*p;
		}
		@Override
		public void getDyingProbDerivative(double[] xs,double[] row)	{
			double p=xs[otherIndex];
			row[otherIndex]+=3*p*p;
		}
	}
	
	private static class Spawn implements Behaviour	{
		private final int myIndex;
		private final int otherIndex;
		public Spawn(int myIndex,int otherIndex)	{
			this.myIndex=myIndex;
			this.otherIndex=otherIndex;
		}
		@Override
		public double getDyingProb(double[] xs)	{
			return xs[myIndex]*xs[otherIndex];
		}
		@Override
		public void getDyingProbDerivative(double[] xs,double[] row)	{
			row[myIndex]+=xs[otherIndex];
			row[otherIndex]+=xs[myIndex];
		}
	}
	
	private static class Evaluator	{
		private final Behaviour[][] behaviours;
		private final double[] ps;
		private final double[] f;
		private final double[] decr;
		private final DoubleMatrix matrix;
		private final DoubleMatrix inverse;
		private final double minusM;
		public Evaluator(int k,int m)	{
			behaviours=new Behaviour[k][m];
			PseudoRandomSequence seq=new PseudoRandomSequence();
			for (int i=0;i<k;++i) for (int j=0;j<m;++j)	{
				BehaviourType type=BehaviourType.getFromIndex(seq.next()%5);
				behaviours[i][j]=type.getBehaviour(i,k);
			}
			ps=new double[k];
			f=new double[k];
			decr=new double[k];
			matrix=new DoubleMatrix(k);
			inverse=new DoubleMatrix(k);
			this.minusM=-m;
		}
		public double iterate()	{
			for (int i=0;i<behaviours.length;++i)	{
				double val=minusM*ps[i];
				double[] row=matrix.row(i);
				Arrays.fill(row,0d);
				row[i]=minusM;
				for (Behaviour b:behaviours[i])	{
					val+=b.getDyingProb(ps);
					b.getDyingProbDerivative(ps,row);
				}
				f[i]=val;
			}
			DoubleMatrix.destructiveInverseWithStorage(matrix,inverse);
			Arrays.fill(decr,0d);
			inverse.multiplyWithStorage(f,decr);
			double diff=0d;
			for (int i=0;i<ps.length;++i)	{
				double d=decr[i];
				diff+=d*d;
				ps[i]-=d;
			}
			return diff;
		}
		public double getResult()	{
			return ps[0];
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Evaluator ev=new Evaluator(K,M);
		while (ev.iterate()>=TOL);
		double result=ev.getResult();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.8f",result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
