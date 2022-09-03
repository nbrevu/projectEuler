package com.euler;

public class Euler461 {
	// No es lo suficientemente bueno...
	private static double DEN=10000.0;
	
	private final static int V1=300;
	private final static int V2=3750;
	private final static int V3=4450;
	private final static int V4=11300;
	
	private final static int LIMIT=14300;
	
	private static double[] FUNCTION_CACHE=calculateFunctionValues();
	
	private static double[] calculateFunctionValues()	{
		double[] res=new double[1+LIMIT];
		for (int i=0;i<=LIMIT;++i) res[i]=Math.exp(i/DEN)-1.0;
		return res;
	}
	
	private static double getDistance(int[] solution)	{
		return Math.abs(FUNCTION_CACHE[solution[0]]+FUNCTION_CACHE[solution[1]]+FUNCTION_CACHE[solution[2]]+FUNCTION_CACHE[solution[3]]-Math.PI);
	}
	
	private static int[] iterate(int[] solution,int range)	{
		int[] current=solution;
		double curDist=getDistance(solution);
		int[] minima=new int[4];
		int[] maxima=new int[4];
		for (int i=0;i<4;++i)	{
			minima[i]=Math.max(0,solution[i]-range);
			maxima[i]=Math.min(LIMIT,solution[i]+range);
		}
		int[] iterator=new int[4];
		for (iterator[0]=minima[0];iterator[0]<=maxima[0];++iterator[0]) for (iterator[1]=minima[1];iterator[1]<=maxima[1];++iterator[1]) for (iterator[2]=minima[2];iterator[2]<=maxima[2];++iterator[2]) for (iterator[3]=minima[3];iterator[3]<=maxima[3];++iterator[3])	{
			double distance=getDistance(iterator);
			if (distance<curDist)	{
				current=iterator.clone();
				curDist=distance;
			}
		}
		return current;
	}
	
	public static void main(String[] args)	{
		int[] solution=new int[]{V1,V2,V3,V4};
		for (int i=1;;++i)	{
			System.out.println(""+i+"...");
			int[] newSolution=iterate(solution,100);
			if (newSolution.equals(solution))	{
				solution=newSolution;
				break;
			}
			solution=newSolution;
		}
		long res=0;
		for (int p:solution) res+=(long)(p*p);
		System.out.println(res);
	}
}
