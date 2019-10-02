package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils.Pair;
import com.euler.common.Timing;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntDoubleCursor;
import com.koloboke.collect.map.IntDoubleMap;
import com.koloboke.collect.map.hash.HashIntDoubleMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler84 {
	private final static IntSet CC=HashIntSets.newImmutableSetOf(2,17,33);
	private final static IntSet CH=HashIntSets.newImmutableSetOf(7,22,36);
	
	private static double[][] getBaseMatrix(IntDoubleMap diceAdvances)	{
		double[][] result=new double[40][40];
		IntDoubleCursor cursor=diceAdvances.cursor();
		while (cursor.moveNext()) for (int i=0;i<40;++i)	{
			int nextSquare=(i+cursor.key())%40;
			result[i][nextSquare]=cursor.value();
		}
		return result;
	}
	
	private static int nextR(int current)	{
		int mod=current%10;
		int toAdvance=((mod<5)?5:15)-mod;
		return (current+toAdvance)%40;
	}
	
	private static int nextU(int current)	{
		return ((current>12)&&(current<28))?28:12;
	}
	
	private static void applySpecialRules(double[][] matrix)	{
		// G2J
		for (int i=0;i<40;++i) if (matrix[i][30]!=0)	{
			matrix[i][10]+=matrix[i][30];
			matrix[i][30]=0;
		}
		// CH
		IntCursor chCursor=CH.cursor();
		while (chCursor.moveNext()) for (int i=0;i<40;++i) if (matrix[i][chCursor.elem()]!=0)	{
			double sixteenth=matrix[i][chCursor.elem()]/16;
			matrix[i][0]+=sixteenth;
			matrix[i][10]+=sixteenth;
			matrix[i][11]+=sixteenth;
			matrix[i][24]+=sixteenth;
			matrix[i][39]+=sixteenth;
			matrix[i][5]+=sixteenth;
			matrix[i][nextR(chCursor.elem())]+=2*sixteenth;
			matrix[i][nextU(chCursor.elem())]+=sixteenth;
			matrix[i][(chCursor.elem()+37)%40]+=sixteenth;
			matrix[i][chCursor.elem()]-=10*sixteenth;
		}
		// CC
		IntCursor ccCursor=CC.cursor();
		while (ccCursor.moveNext()) for (int i=0;i<40;++i) if (matrix[i][ccCursor.elem()]!=0)	{
			double sixteenth=matrix[i][ccCursor.elem()]/16;
			matrix[i][0]+=sixteenth;
			matrix[i][10]+=sixteenth;
			matrix[i][ccCursor.elem()]-=2*sixteenth;
		}
	}
	
	private static Pair<double[][],double[]> getLinearProblem(double[][] baseTransitionMatrix)	{
		/*
		 * Assumes P(39)=1 and creates a deterministic problem (the original matrix, after subtracting 1 to the main diagonal,
		 * has infinite solutions including all zeros).
		 */
		double[][] matrix=new double[39][39];
		double[] vector=new double[39];
		for (int i=0;i<39;++i)	{
			for (int j=0;j<39;++j) matrix[i][j]=baseTransitionMatrix[j][i];
			matrix[i][i]-=1.0;
			vector[i]=-baseTransitionMatrix[39][i];
		}
		return new Pair<>(matrix,vector);
	}
	
	private static void gaussianElimination(double[][] matrix,double[] vector)	{
		int N=matrix.length;
		for (int i=0;i<N;++i)	{
			double pivot=matrix[i][i];
			if (pivot==0) throw new RuntimeException("I should exchange rows here. But let's assume this doesn't happen.");
			for (int j=i+1;j<N;++j) matrix[i][j]/=pivot;
			vector[i]/=pivot;
			matrix[i][i]=1;
			for (int k=i+1;k<N;++k)	{
				pivot=matrix[k][i];
				if (pivot==0) continue;
				for (int j=i+1;j<N;++j) matrix[k][j]-=pivot*matrix[i][j];
				matrix[k][i]=0;
				vector[k]-=pivot*vector[i];
			}
		}
		// At this point we won't update the matrix any more. We only need the vector.
		for (int i=N-1;i>=1;--i) for (int j=i-1;j>=0;--j) vector[j]-=matrix[j][i]*vector[i];
	}
	
	private static double[] getTrueSolution(double[] in)	{
		double sum=1;	// The 40th element, assumed to be 1.
		for (int i=0;i<in.length;++i) sum+=in[i];
		double[] result=new double[1+in.length];
		for (int i=0;i<in.length;++i) result[i]=in[i]/sum;
		result[in.length]=1/sum;
		return result;
	}
	
	private static IntDoubleMap getDieConfiguration(int howManySides)	{
		IntDoubleMap result=HashIntDoubleMaps.newMutableMap();
		double baseProb=1d/(double)howManySides;
		for (int i=1;i<=howManySides;++i) result.put(i,baseProb);
		return result;
	}
	
	private static IntDoubleMap addDie(IntDoubleMap currentConfiguration,IntDoubleMap die)	{
		IntDoubleMap result=HashIntDoubleMaps.newMutableMap();
		IntDoubleCursor cursor1=currentConfiguration.cursor();
		while (cursor1.moveNext())	{
			IntDoubleCursor cursor2=die.cursor();
			while (cursor2.moveNext())	{
				int position=cursor1.key()+cursor2.key();
				double prob=cursor1.value()*cursor2.value();
				result.compute(position,(int currentKey,double currentValue)->currentValue+prob);
			}
		}
		return result;
	}
	
	private static IntDoubleMap getDiceConfiguration(int howManyDice,int howManySides)	{
		IntDoubleMap result=HashIntDoubleMaps.newImmutableMapOf(0,1d);
		IntDoubleMap singleDie=getDieConfiguration(howManySides);
		for (int i=0;i<howManyDice;++i) result=addDie(result,singleDie);
		return result;
	}
	
	private static class Square implements Comparable<Square>	{
		public final int square;
		public final double probability;
		public Square(int square,double probability)	{
			this.square=square;
			this.probability=probability;
		}
		@Override
		public int compareTo(Square other)	{
			int result=Double.compare(other.probability,probability);
			if (result!=0) return result;
			else return square-other.square;
		}
	}
	
	private static List<Square> getSortedSolutions(double[] vector)	{
		List<Square> result=new ArrayList<>();
		for (int i=0;i<vector.length;++i) result.add(new Square(i,vector[i]));
		result.sort(null);
		return result;
	}
	
	private static String solve()	{
		IntDoubleMap diceConfiguration=getDiceConfiguration(2,4);
		double[][] baseMatrix=getBaseMatrix(diceConfiguration);
		applySpecialRules(baseMatrix);
		Pair<double[][],double[]> linearProblem=getLinearProblem(baseMatrix);
		double[][] matrix=linearProblem.first;
		double[] vector=linearProblem.second;
		gaussianElimination(matrix,vector);
		double[] solution=getTrueSolution(vector);
		List<Square> sorted=getSortedSolutions(solution);
		StringBuilder sb=new StringBuilder();
		for (int i=0;i<3;++i) sb.append(String.format("%02d",sorted.get(i).square));
		return sb.toString();
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler84::solve);
	}
}
