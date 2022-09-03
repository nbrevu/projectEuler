package com.euler;

public class Euler710_2 {
	private final static long MOD=1_000_000l;
	
	private final static int LIMIT=100_000_000;

	public static void main(String[] args)	{
		long f[]=new long[LIMIT];
		long g[]=new long[LIMIT];
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
		for (int i=5;i<=LIMIT;++i)	{
			f[i]=(((i%2)==0)?2:1)+f[i-2];
			for (int j=i-6;j>0;j-=2) f[i]+=f[j];
			g[i]=f[i-4];
			for (int j=i-2;j>0;j-=2) g[i]+=g[j];
			f[i]%=MOD;
			g[i]%=MOD;
			if (g[i]==0)	{
				System.out.println(i);
				return;
			}
		}
		System.out.println("Pues no :'(.");
	}
}
