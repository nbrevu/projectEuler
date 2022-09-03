package com.euler;

public class Euler622 {
	private static int[] interleave(int[] in)	{
		int N=in.length;
		if ((N%2)!=0) throw new IllegalArgumentException();
		int N2=N/2;
		int[] result=new int[N];
		for (int i=0;i<N2;++i)	{
			result[2*i]=in[i];
			result[2*i+1]=in[i+N2];
		}
		return result;
	}
	
	private static boolean isOrdered(int[] in)	{
		int N=in.length;
		for (int i=0;i<N;++i) if (in[i]!=i) return false;
		return true;
	}
	
	private static int[] createOrderedArray(int N)	{
		int[] result=new int[N];
		for (int i=0;i<N;++i) result[i]=i;
		return result;
	}
	
	public static void main(String[] args)	{
		int[] array=createOrderedArray(52);
		int counter=0;
		do	{
			++counter;
			array=interleave(array);
		}	while (!isOrdered(array));
		System.out.println("He dado "+counter+" vueltas.");
	}
}
