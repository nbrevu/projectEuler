package com.euler;

import java.util.Arrays;

import com.google.common.math.LongMath;

public class Euler602 {
	private static long[][] getCRecursive(int maxN)	{
		assert maxN>1;
		long[][] result=new long[1+maxN][];
		result[0]=new long[]{0l};
		result[1]=new long[]{0l,1l};
		for (int n=2;n<=maxN;++n)	{
			result[n]=new long[1+n];
			for (int k=1;k<=n;++k) result[n][k]=((k<=n-1)?(k*result[n-1][k]):0)+(n+1-k)*result[n-1][k-1];
		}
		return result;
	}
	
	private static long[][] getCBinomial(int maxN)	{
		long[] factorials=new long[2+maxN];
		factorials[0]=1l;
		for (int i=1;i<=1+maxN;++i) factorials[i]=i*factorials[i-1];
		long[][] result=new long[1+maxN][];
		result[0]=new long[]{0l};
		for (int n=1;n<=maxN;++n)	{
			result[n]=new long[1+n];
			for (int k=1;k<=n;++k)	{
				long value=0;
				for (int i=0;i<=k;++i)	{
					long binomial=factorials[n+1]/(factorials[i]*factorials[n+1-i]);
					long augend=binomial*LongMath.pow(k-i,n);
					if ((i%2)==0) value+=augend;
					else value-=augend;
				}
				result[n][k]=value;
			}
		}
		return result;
	}
	
	private static void display(long[][] array)	{
		for (int i=0;i<array.length;++i) System.out.println(Arrays.toString(array[i]));
	}
	
	private static void compareArrays(long[][] c1,long[][] c2)	{
		if (c1.length!=c2.length) throw new RuntimeException();
		for (int i=0;i<c1.length;++i)	{
			if (c1[i].length!=c2[i].length) throw new RuntimeException();
			for (int j=0;j<c1[i].length;++j) if (c1[i][j]!=c2[i][j]) throw new RuntimeException();
		}
	}
	
	public static void main(String[] args)	{
		long[][] c1=getCRecursive(10);
		display(c1);
		System.out.println("Y ahora el coro de los viejos locos.");
		long[][] c2=getCBinomial(10);
		display(c2);
		compareArrays(c1,c2);
	}
}
