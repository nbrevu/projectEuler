package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.PythagoreanTriples;
import com.euler.common.PythagoreanTriples.SimplePythagoreanTriple;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler662 {
	private final static int W=10000;
	private final static int H=10000;
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private static class Movement	{
		public final int incrX;
		public final int incrY;
		public Movement(int incrX,int incrY)	{
			this.incrX=incrX;
			this.incrY=incrY;
		}
	}
	
	private static class FibonacciPytTripleGenerator	{
		private static int[] generateFibonacciValues(int upTo)	{
			SortedSet<Integer> values=new TreeSet<>();
			int prev=1;
			int curr=2;
			values.add(prev);
			while (curr<=upTo)	{
				values.add(curr);
				int newVal=prev+curr;
				prev=curr;
				curr=newVal;
			}
			return values.stream().mapToInt(Integer::intValue).toArray();
		}
		private final int[] fibonacciValues;
		private final int width;
		private final int height;
		public FibonacciPytTripleGenerator(int width,int height)	{
			this.width=width;
			this.height=height;
			fibonacciValues=generateFibonacciValues(IntMath.sqrt(width*width+height*height,RoundingMode.UP));
		}
		public List<Movement> generateHorizontalMovements()	{
			List<Movement> result=new ArrayList<>();
			for (int val:fibonacciValues) if (val>width) break;
			else result.add(new Movement(val,0));
			return result;
		}
		public List<Movement> generateVerticalMovements()	{
			List<Movement> result=new ArrayList<>();
			for (int val:fibonacciValues) if (val>height) break;
			else result.add(new Movement(0,val));
			return result;
		}
		public List<Movement> generateDiagonalMovements()	{
			List<SimplePythagoreanTriple> baseTriples=PythagoreanTriples.getSimpleTriplesUpTo(Math.max(width,height));
			List<Movement> result=new ArrayList<>();
			for (SimplePythagoreanTriple triple:baseTriples)	{
				int a=(int)triple.a;
				int b=(int)triple.b;
				int c=(int)LongMath.sqrt(triple.a*triple.a+triple.b*triple.b,RoundingMode.UNNECESSARY);
				for (int val:fibonacciValues) if ((val%c)==0)	{
					int q=val/c;
					int qa=q*a;
					int qb=q*b;
					if (((qa)<=width)&&((qb)<=height)) result.add(new Movement(qa,qb));
					if (((qb)<=width)&&((qa)<=height)) result.add(new Movement(qb,qa));
				}
			}
			return result;
		}
		public List<Movement> getAllAvailableMovements()	{
			List<Movement> result=generateDiagonalMovements();
			result.addAll(generateHorizontalMovements());
			result.addAll(generateVerticalMovements());
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		FibonacciPytTripleGenerator generator=new FibonacciPytTripleGenerator(W,H);
		List<Movement> movements=generator.getAllAvailableMovements();
		long[][] cases=new long[1+W][1+H];
		cases[0][0]=1;
		for (int i=0;i<=W;++i) for (int j=0;j<=H;++j) if ((i>0)||(j>0)) for (Movement m:movements) if ((i>=m.incrX)&&(j>=m.incrY))	{
			cases[i][j]+=cases[i-m.incrX][j-m.incrY];
			cases[i][j]%=MOD;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(cases[W][H]);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
