package com.euler;

public class Euler663 {
	private final static int N=107;
	private final static int L=1000;
	
	private static class TribonacciGenerator	{
		private final int mod;
		private int prev;
		private int cur;
		private int next;
		public TribonacciGenerator(int mod)	{
			this.mod=mod;
			prev=1;
			cur=0;
			next=0;
		}
		public int getNext()	{
			int nextN=(prev+cur+next)%mod;
			prev=cur;
			cur=next;
			next=nextN;
			return prev;
		}
	}
	
	private static class LongArray	{
		private final long[] data;
		public LongArray(int length)	{
			data=new long[length];
		}
		public void update(int value,int goalPos)	{
			data[goalPos]+=2*value+1-data.length;
		}
		public long kadaneAlgorithm()	{
			long localMax=0;
			long result=Long.MIN_VALUE;
			for (long val:data)	{
				localMax=Math.max(val,localMax+val);
				result=Math.max(result,localMax);
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		LongArray array=new LongArray(N);
		TribonacciGenerator gen=new TribonacciGenerator(N);
		long result=0;
		for (int i=1;i<=L;++i)	{
			int goalPos=gen.getNext();
			int augendPos=gen.getNext();
			array.update(augendPos,goalPos);
			result+=array.kadaneAlgorithm();
		}
		System.out.println(result);
	}
}
