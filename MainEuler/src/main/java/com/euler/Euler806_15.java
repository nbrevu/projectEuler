package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.euler.common.EulerUtils;
import com.google.common.math.IntMath;

public class Euler806_15 {
	private final static int N=1000;
	private final static long MOD=1_000_000_007l;
	
	private final static int NUM_THREADS=16;
	
	private static class Repo	{
		private final int[][] data;
		public Repo(int n)	{
			data=new int[1+n][];
			for (int i=0;i<=n;++i) data[i]=new int[n+1-i];
		}
		public void zeroIn()	{
			for (int[] array:data) Arrays.fill(array,0);
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
		private final int numThreads;
		public ThreadedOperation(Repo minus3,Repo minus2,Repo current,int s,int numThreads)	{
			this.minus3=minus3;
			this.minus2=minus2;
			this.current=current;
			this.s=s;
			this.numThreads=numThreads;
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
				int secondaryIncr=2*numThreads-primaryIncr;
				boolean usePrimary=false;
				for (int i=initialValue;i<=s;i+=(usePrimary?primaryIncr:secondaryIncr),usePrimary=!usePrimary)	{
					int maxJ=s-i;
					for (int j=0;j<=maxJ;++j)	{
						long result=0;
						if (i>=2) result+=minus2.data[i-2][j];
						if (j>=2) result+=minus2.data[i][j-2];
						result+=minus2.data[i][j];
						if ((i>=1)&&(j>=1)) result-=2*minus3.data[i-1][j-1];
						current.data[i][j]=(int)(result%MOD);
					}
				}
			}
		}
		public void calculate() throws InterruptedException	{
			Thread[] threads=new Thread[numThreads];
			InternalThread[] data=new InternalThread[numThreads];
			for (int i=0;i<numThreads;++i)	{
				data[i]=new InternalThread(i);
				threads[i]=new Thread(data[i]);
				threads[i].start();
			}
			for (int i=0;i<numThreads;++i) threads[i].join();
		}
	}
	
	// Doesn't fit in memory D:. Almost!
	public static void main(String[] args) throws InterruptedException	{
		System.out.println("Cojo un repo...");
		Repo minus3=new Repo(N);
		System.out.println("Cojo un repo...");
		Repo minus2=new Repo(N);
		System.out.println("Cojo un repo...");
		Repo minus1=new Repo(N);
		System.out.println("Cojo un repo...");
		Repo current=new Repo(N);
		System.out.println("Endut.");
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
		// ZUTUN! Maybe avoid threads when N<1000 or so. The overhead is too big.
		for (int s=4;s<=N;++s)	{
			System.out.println(s+"...");
			ThreadedOperation operation=new ThreadedOperation(minus3,minus2,current,s,NUM_THREADS);
			operation.calculate();
			Repo swap=minus3;
			minus3=minus2;
			minus2=minus1;
			minus1=current;
			current=swap;
			current.zeroIn();
		}
		long result=0l;
		for (int[] triple:getLosingTriples(N))	{
			if ((triple[0]==0)||(triple[1]==0)||(triple[2]==0)) continue;
			result+=minus1.data[triple[0]][triple[1]];
		}
		result+=2*combinatorialMod(N/4,MOD);
		result%=MOD;
		long modInverse=(MOD+1)/2;
		result=(result*modInverse)%MOD;
		long pow2=EulerUtils.expMod(2l,N,MOD)-1;
		result=(result*pow2)%MOD;
		System.out.println(result);
	}
}
