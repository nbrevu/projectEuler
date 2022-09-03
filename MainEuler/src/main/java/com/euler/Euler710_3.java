package com.euler;

public class Euler710_3 {
	private final static long MOD=1_000_000l;

	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long f[]=new long[6];
		long g[]=new long[6];
		long F[]=new long[6];
		long G[]=new long[6];
		f[0]=0;	// We don't count the empty set.
		f[1]=1;	// {1}.
		f[2]=1;	// {1,1}.
		f[3]=2;	// {1,1,1}, {3}.
		f[4]=2; // {1,1,1,1}, {4}.
		f[5]=3;	// {1,1,1,1,1}, {1,3,1}, {5}.
		g[0]=0;
		g[1]=0;
		g[2]=1;	// {2}.
		g[3]=0;
		g[4]=2;	// {1,2,1}, {2,2}.
		g[5]=1;	// {2,1,2}.
		F[0]=f[0];
		F[1]=f[1];
		G[0]=g[0];
		G[1]=g[1];
		for (int i=2;i<6;++i)	{
			F[i]=F[i-2]+f[i];
			G[i]=G[i-2]+g[i];
		}
		for (int i=6;;++i)	{
			if (i<0)	{
				System.out.println("Overflow :'(.");
				return;
			}
			int iMod=i%6;
			int iMinus2=(i-2)%6;
			int iMinus4=(i-4)%6;
			f[iMod]=((((i%2)==0)?2:1)+f[iMinus2]+F[iMod])%MOD;	// F[i] is actually F[i-6] (we haven't updated F yet).
			F[iMod]=(f[iMod]+F[iMinus2])%MOD;
			g[iMod]=(f[iMinus4]+G[iMinus2])%MOD;
			G[iMod]=(g[iMod]+G[iMinus2])%MOD;
			if (g[iMod]==0)	{
				long tac=System.nanoTime();
				double seconds=(tac-tic)*1e-9;
				System.out.println(i);
				System.out.println("Elapsed "+seconds+" seconds.");
				return;
			}
		}
	}
}
