package com.euler;

public class Euler237 {
	// First terms of the "base" array: 1, 0, 3, 4.
	// Therefore: first terms of the "sum" array: 1, 1, 4, 8.
	private static long[] createBaseArray(int length,long mod)	{
		/*
		long[] res=new long[length];
		res[0]=1l;
		res[1]=0l;
		for (int i=2;i<length;++i)	{
			res[i]=res[i-2];
			for (int j=2;j<=i;++j) res[i]+=2*(j-1)*res[i-j];
			res[i]%=mod;
		}
		return res;
		*/
		long[] res=new long[length];
		res[0]=1l;
		res[1]=0l;
		res[2]=3l;
		res[3]=4l;
		for (int i=4;i<length;++i) res[i]=(2*res[i-1]+2*res[i-2]-2*res[i-3]+res[i-4])%mod;
		return res;
	}
	
	private static long getResult(int length,long mod)	{
		long[] baseArray=createBaseArray(length,mod);
		long res=0l;
		for (int i=0;i<baseArray.length;++i) res=(res+baseArray[i])%mod;
		return res;
	}
	
	public static void main(String[] args)	{
		System.out.println(getResult(11,100000000l));
	}
}
