package com.euler;

public class Euler663_6 {
	private final static int N=10_000_003;
	private final static int INITIAL_SEGMENT=10_000_000;
	private final static int END_SEGMENT=10_200_000;

	private final static long MIN=Long.MIN_VALUE/2;	// So that even after "adding" a negative number we get a negative one!
	
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
	
	private static class KadaneData	{
		public long localMax;
		public long globalMax;
	}
	
	private static class LongArray	{
		private final KadaneData[] algorithmData;
		private final long[] values;
		public LongArray(long[] values)	{
			this.values=values;
			algorithmData=new KadaneData[values.length];
			initKadane();
		}
		private void initKadane()	{
			long localMax=0;
			long globalMax=MIN;
			for (int i=0;i<values.length;++i)	{
				localMax=Math.max(values[i],localMax+values[i]);
				globalMax=Math.max(globalMax,localMax);
				algorithmData[i]=new KadaneData();
				algorithmData[i].localMax=localMax;
				algorithmData[i].globalMax=globalMax;
			}
		}
		public void update(int value,int pos)	{
			values[pos]+=2*value+1-values.length;
			long localMax;
			long globalMax;
			if (pos==0)	{
				localMax=0;
				globalMax=MIN;
			}	else	{
				localMax=algorithmData[pos-1].localMax;
				globalMax=algorithmData[pos-1].globalMax;
			}
			for (int i=pos;i<algorithmData.length;++i)	{
				localMax=Math.max(values[i],localMax+values[i]);
				globalMax=Math.max(globalMax,localMax);
				if ((localMax==algorithmData[i].localMax)&&(globalMax==algorithmData[i].globalMax)) break;
				algorithmData[i].localMax=localMax;
				algorithmData[i].globalMax=globalMax;
			}
		}
		public long getBestSum()	{
			return algorithmData[algorithmData.length-1].globalMax;
		}
	}
	
	/*
	 * "Elapsed 512.9071727 seconds". Too much for such a relatively simple problem.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		TribonacciGenerator gen=new TribonacciGenerator(N);
		long[] data=new long[N];
		long result=0;
		for (int i=1;i<=INITIAL_SEGMENT;++i)	{
			int pos=gen.getNext();
			int augend=gen.getNext();
			data[pos]+=2*augend+1-data.length;
		}
		LongArray array=new LongArray(data);
		for (int i=1+INITIAL_SEGMENT;i<=END_SEGMENT;++i)	{
			int pos=gen.getNext();
			int augend=gen.getNext();
			array.update(augend,pos);
			result+=array.getBestSum();
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
