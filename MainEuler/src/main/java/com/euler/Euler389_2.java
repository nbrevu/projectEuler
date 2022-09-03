package com.euler;

public class Euler389_2 {
	// No hay suficiente precisión :(.
	private static double[][] simulateDiceRolls(int rolls,int faces)	{
		double[][] result=new double[1+rolls][];
		result[0]=new double[1];
		result[0][0]=1.0;
		result[1]=new double[1+faces];
		double frac=1.0/(double)faces;
		for (int j=1;j<=faces;++j) result[1][j]=frac;
		for (int i=2;i<=rolls;++i)	{
			result[i]=new double[1+i*faces];
			for (int j=i-1;j<=(i-1)*faces;++j) for (int k=1;k<=faces;++k) result[i][j+k]=result[i-1][j]*result[1][k];
		}
		return result;
	}
	
	public static void main(String[] args)	{
		double[][] result=simulateDiceRolls(2304,20);
		double min=Double.MAX_VALUE;
		double max=Double.MIN_VALUE;
		for (int i=1;i<=2304;++i)	{
			double sum=0.0;
			int N=result[i].length;
			for (int j=0;j<N;++j) sum+=result[i][j];
			min=Math.min(min,sum);
			max=Math.max(max,sum);
		}
		System.out.println(""+min+"<=[prob]<="+max);
	}
}
