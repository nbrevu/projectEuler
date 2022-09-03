package com.euler;

public class Euler622_2 {
	private static class Shuffler	{
		private final int N2;
		private int[] current;
		private int[] shuffler;
		public Shuffler(int N)	{
			if ((N%2)!=0) throw new IllegalArgumentException();
			N2=N/2;
			current=createOrderedArray(N);
			shuffler=new int[N];
		}
		private static int[] createOrderedArray(int N)	{
			int[] result=new int[N];
			for (int i=0;i<N;++i) result[i]=i;
			return result;
		}
		private void interleave(int[] in,int[] out)	{
			for (int i=0;i<N2;++i)	{
				out[2*i]=in[i];
				out[2*i+1]=in[i+N2];
			}
		}
		private void shuffle()	{
			interleave(current,shuffler);
			int[] middle=current;
			current=shuffler;
			shuffler=middle;
		}
		private int countShuffles()	{
			int counter=0;
			do	{
				shuffle();
				++counter;
			}	while (!isOrdered(current));
			return counter;
		}
		private int countShufflesPartial()	{
			int counter=0;
			do	{
				shuffle();
				++counter;
			}	while (current[1]!=1);
			return counter;
		}
		private static boolean isOrdered(int[] in)	{
			int N=in.length;
			for (int i=0;i<N;++i) if (in[i]!=i) return false;
			return true;
		}
		public static int countShuffles(int N,boolean partial)	{
			Shuffler shuffler=new Shuffler(N);
			return partial?shuffler.countShufflesPartial():shuffler.countShuffles();
		}
	}
	
	public static void main(String[] args)	{
		for (int i=2;i<=20000;i+=2)	{
			int s1=Shuffler.countShuffles(i,false);
			int s2=Shuffler.countShuffles(i,true);
			if (s1==60) System.out.println("i="+i+": "+s1+" ("+s2+").");
			if (s1!=s2) throw new RuntimeException("UN MOMENTO. ESE HOMBRE ES UN IMPOSTOL.");
		}
	}
}
