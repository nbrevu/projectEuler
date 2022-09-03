package com.euler;

public class Euler710 {
	private final static int N=20;
	
	public static void main(String[] args)	{
		long f[]=new long[1+N];
		long g[]=new long[1+N];
		f[0]=1;	// The empty set is a set.
		f[1]=1;	// {1}.
		f[2]=1;	// {1,1}.
		f[3]=2;	// {1,1,1}, {3}.
		f[4]=2; // {1,1,1,1}, {4}.
		g[0]=0;
		g[1]=0;
		g[2]=1;	// {2}.
		g[3]=0;
		g[4]=2;	// {1,2,1}, {2,2}.
		for (int i=5;i<=N;++i)	{
			f[i]=(((i%2)==0)?2:1)+f[i-2];
			for (int j=i-6;j>0;j-=2) f[i]+=f[j];
			g[i]=f[i-4];
			for (int j=i-2;j>0;j-=2) g[i]+=g[j];
		}
		System.out.println(g[N]);
	}
}
