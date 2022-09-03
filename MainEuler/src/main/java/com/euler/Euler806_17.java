package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.euler.common.EulerUtils;
import com.google.common.math.IntMath;

public class Euler806_17 {
	private final static int N=100000;
	private final static int N2=N/2;
	private final static long MOD=1_000_000_007l;
	
	private final static int NUM_THREADS=20;
	
	private static class Repo	{
		private final int[][] data;
		public Repo(int n)	{
			int h=n/2;
			data=new int[1+h][];
			for (int i=0;i<=h;++i) data[i]=new int[n+1-2*i];
		}
		public void zeroIn(int s)	{
			int maxI=Math.min(s+1,data.length);
			for (int i=0;i<maxI;++i) Arrays.fill(data[i],0,Math.min(s+1,data[i].length),0);
		}
		public int get(int i,int j,int s)	{
			int k=s-i-j;
			int min=Math.min(i,k);
			return (min<0)?0:data[min][j];
		}
	}
	
	private static long combinatorialMod(long n,long mod)	{
		long num=1;
		long denom=1;
		for (long i=1;i<=n;++i)	{
			num*=n+i;
			num%=mod;
			denom*=i;
			denom%=mod;
		}
		return (num*EulerUtils.modulusInverse(denom,mod))%mod;
	}
	
	private static List<int[]> getLosingTriples(int n)	{
		int n2=n>>1;
		if ((n&1)==1) return List.of();
		n=n2;
		int bitCount=Integer.bitCount(n);
		int[] bits=new int[bitCount];
		for (int i=0;i<bitCount;++i)	{
			bits[i]=Integer.lowestOneBit(n);
			n-=bits[i];
		}
		int size=IntMath.pow(3,bitCount);
		List<int[]> result=new ArrayList<>(size);
		for (int i=0;i<size;++i)	{
			int[] thisCombination=new int[] {n2,n2,n2};
			int x=i;
			for (int j=0;j<bitCount;++j)	{
				thisCombination[x%3]-=bits[j];
				x/=3;
			}
			result.add(thisCombination);
		}
		return result;
	}
	
	private static class ThreadedOperation	{
		private final Repo minus3;
		private final Repo minus2;
		private final Repo current;
		private final int s;
		private final int h;
		public ThreadedOperation(Repo minus3,Repo minus2,Repo current,int s)	{
			this.minus3=minus3;
			this.minus2=minus2;
			this.current=current;
			this.s=s;
			h=s/2;
		}
		private class InternalThread implements Runnable	{
			private final int id;
			public InternalThread(int id)	{
				this.id=id;
			}
			@Override
			public void run()	{
				int initialValue=id;
				int primaryIncr=2*id+1;
				int secondaryIncr=2*NUM_THREADS-primaryIncr;
				boolean usePrimary=false;
				for (int i=initialValue;i<=h;i+=(usePrimary?primaryIncr:secondaryIncr),usePrimary=!usePrimary)	{
					int minJ=Math.max(0,s-i-N2);
					int maxJ=Math.min(s-2*i,N2);
					for (int j=minJ;j<=maxJ;++j)	{
						long result=0;
						if (i>=2) result+=minus2.get(i-2,j,s-2);
						if (j>=2) result+=minus2.get(i,j-2,s-2);
						result+=minus2.get(i,j,s-2);
						if ((i>=1)&&(j>=1)) result-=2*minus3.get(i-1,j-1,s-3);
						current.data[i][j]=(int)(result%MOD);
					}
				}
			}
		}
		public void calculate() throws InterruptedException	{
			Thread[] threads=new Thread[NUM_THREADS];
			InternalThread[] data=new InternalThread[NUM_THREADS];
			for (int i=0;i<NUM_THREADS;++i)	{
				data[i]=new InternalThread(i);
				threads[i]=new Thread(data[i]);
				threads[i].start();
			}
			for (int i=0;i<NUM_THREADS;++i) threads[i].join();
		}
	}
	
	/*
	 * 94394343
	 * Elapsed 62401.3671507 seconds.
	 * With 20 threads though. My cooling system is once again earning its pay.
	 * 
	 * From 23:14:32 to 16:34:33 of the next day. Phew.
	 */
	public static void main(String[] args) throws InterruptedException	{
		long tic=System.nanoTime();
		Repo minus3=new Repo(N);
		Repo minus2=new Repo(N);
		Repo minus1=new Repo(N);
		Repo current=new Repo(N);
		minus3.data[1][0]=-1;
		minus3.data[0][0]=-1;
		minus2.data[2][0]=1;
		minus2.data[1][1]=1;
		minus2.data[0][1]=1;
		minus2.data[0][0]=1;
		minus1.data[3][0]=-1;
		minus1.data[2][0]=-1;
		minus1.data[1][2]=-1;
		minus1.data[1][1]=-2;
		minus1.data[1][0]=-1;
		minus1.data[0][2]=-1;
		minus1.data[0][1]=0;
		minus1.data[0][0]=-1;
		for (int s=4;s<=N;++s)	{
			System.out.println(s+"...");
			ThreadedOperation operation=new ThreadedOperation(minus3,minus2,current,s);
			operation.calculate();
			Repo swap=minus3;
			minus3=minus2;
			minus2=minus1;
			minus1=current;
			current=swap;
			current.zeroIn(s);
		}
		long result=0l;
		for (int[] triple:getLosingTriples(N))	{
			if ((triple[0]==0)||(triple[1]==0)||(triple[2]==0)) continue;
			int firstIndex=Math.min(triple[0],triple[2]);
			result+=minus1.data[firstIndex][triple[1]];
		}
		result+=2*combinatorialMod(N/4,MOD);
		result%=MOD;
		long modInverse=(MOD+1)/2;	// 2^(-1) mod MOD.
		result=(result*modInverse)%MOD;
		long pow2=EulerUtils.expMod(2l,N,MOD)-1;
		result=(result*pow2)%MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
