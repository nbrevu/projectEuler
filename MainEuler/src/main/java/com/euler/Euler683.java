package com.euler;

import com.euler.common.DoubleMatrix;

public class Euler683 {
	private final static int N=500;
	
	private final static double _1_9=1d/9d;
	private final static double _2_9=2d/9d;
	private final static double _3_9=3d/9d;
	
	private static enum Advance	{
		// All the real logic related to the Q matrix is here.
		MINUS_2(_1_9) {
			@Override
			public int getNextIndex(int n,int index,boolean isOdd) {
				if (index>=2) return index-2;
				else return 1;	// index=1.
			}
		},	MINUS_1(_2_9)	{
			@Override
			public int getNextIndex(int n,int index,boolean isOdd) {
				return index-1;
			}
		},	STAY(_3_9)	{
			@Override
			public int getNextIndex(int n,int index,boolean isOdd) {
				return index;
			}
		},	PLUS_1(_2_9)	{
			@Override
			public int getNextIndex(int n,int index,boolean isOdd) {
				if (n==1) return isOdd?1:0;	// It can only be index==1.
				else if (index==n) return isOdd?n:n-1;
				else return 1+index;
			}
		},	PLUS_2(_1_9)	{
			@Override
			public int getNextIndex(int n,int index,boolean isOdd) {
				if (n==1) return isOdd?0:1;	// It can only be index==1.
				else if (index==n-1) return isOdd?n:n-1;
				else if (index==n) return isOdd?n-1:n-2;
				else return index+2;
			}
		};
		
		public final double value;
		private Advance(double value)	{
			this.value=value;
		}
		public abstract int getNextIndex(int n,int index,boolean isOdd);
	}
	
	// Cached for efficiency.
	private final static Advance[] ADVANCES=Advance.values();
	
	private static DoubleMatrix getQ(int players)	{
		int n=players/2;
		boolean oddPlayers=((players%2)==1);
		DoubleMatrix result=new DoubleMatrix(n);
		for (int i=1;i<=n;++i) for (Advance advance:ADVANCES)	{
			int destination=advance.getNextIndex(n,i,oddPlayers);
			if (destination!=0) result.add(i-1,destination-1,advance.value);
		}
		return result;
	}
	
	private static double getSSquared(DoubleMatrix q,int players)	{
		int size=q.size();
		DoubleMatrix eye=DoubleMatrix.eye(size);
		DoubleMatrix toInvert=eye.subtract(q);
		DoubleMatrix nMatrix=DoubleMatrix.destructiveInverse(toInvert);
		double[] t=new double[size];
		for (int i=0;i<size;++i) for (int j=0;j<size;++j) t[i]+=nMatrix.get(i,j);
		DoubleMatrix n2=nMatrix.add(nMatrix).subtract(eye);
		double[] t2=n2.multiply(t);
		double f=1d/players;
		double f2=2*f;
		double result=0;
		for (int i=0;i<size-1;++i) result+=f2*t2[i];
		result+=(((players%2)==1)?f2:f)*t2[size-1];
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		double result=0d;
		for (int i=2;i<=N;++i) result+=getSSquared(getQ(i),i);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(String.format("%.8e",result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
