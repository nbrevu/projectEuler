package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.euler.common.EulerUtils;
import com.google.common.math.IntMath;

public class Euler806_16 {
	private final static int N=1100;
	private final static long MOD=1_000_000_007l;
	
	private static class Repo	{
		private final long[][] data;
		public Repo(int n)	{
			data=new long[1+n][];
			for (int i=0;i<=n;++i) data[i]=new long[n+1-i];
		}
		public void zeroIn()	{
			for (long[] array:data) Arrays.fill(array,0l);
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
	
	public static void main(String[] args)	{
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
			for (int i=0;i<=s;++i)	{
				int maxJ=s-i;
				for (int j=0;j<=maxJ;++j)	{
					long result=0;
					if (i>=2) result+=minus2.data[i-2][j];
					if (j>=2) result+=minus2.data[i][j-2];
					result+=minus2.data[i][j];
					if ((i>=1)&&(j>=1)) result-=2*minus3.data[i-1][j-1];
					current.data[i][j]=result%MOD;
				}
			}
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
