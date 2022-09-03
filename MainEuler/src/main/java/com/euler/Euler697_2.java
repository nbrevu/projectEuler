package com.euler;

import java.util.function.DoubleUnaryOperator;

public class Euler697_2 {
	/*
	 * Not good; bad precision. Mathematica saves my ass again.
	 * 
	 * res = Solve[CDF[ErlangDistribution[10000000, 1], x] == 0.75, x];
	 * res[[All, 1, 2]]/Log[10]
	 */
	private final static int LIMIT=100000;
	
	private static double[] getFactorialLogs(int max)	{
		double[] result=new double[1+max];
		result[0]=0.0;
		for (int i=1;i<=max;++i) result[i]=result[i-1]+Math.log(i);
		return result;
	}
	
	private static class CumDistributionCalculator	{
		private final int k;
		private final double[] factorialLogs;
		public CumDistributionCalculator(int k)	{
			this.k=k;
			factorialLogs=getFactorialLogs(k-1);
		}
		public double getF(double x)	{
			double logX=Math.log(x);
			double sum=0.0;
			for (int i=k-1;i>=0;--i) sum+=Math.exp(i*logX-factorialLogs[i]);
			return Math.exp(-x)*sum;
		}
	}
	
	private static double bipartition(DoubleUnaryOperator decreasingFunction,double min,double max,double goal)	{
		double middle=(max+min)/2;
		if ((middle==min)||(middle==max)) return middle;
		double value=decreasingFunction.applyAsDouble(middle);
		if (value==goal) return middle;
		else if (value>goal) return bipartition(decreasingFunction,middle,max,goal);
		else return bipartition(decreasingFunction,min,middle,goal);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		CumDistributionCalculator calc=new CumDistributionCalculator(LIMIT);
		double result=bipartition(calc::getF,1,2*LIMIT,0.25);
		double result10=result/Math.log(10);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result10);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
