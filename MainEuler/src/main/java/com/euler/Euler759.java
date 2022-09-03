package com.euler;

public class Euler759 {
	public static void main(String[] args)	{
		int size=1048576;
		long[] expectedMultipliers=new long[size];
		expectedMultipliers[1]=1;
		for (int i=2;i<=20;++i)	{
			int start=1<<(i-1);
			int len=1<<(i-2);
			for (int j=0;j<len;++j)	{
				expectedMultipliers[j+start]=expectedMultipliers[j+start-len];
				expectedMultipliers[j+start+len]=1+expectedMultipliers[j+start-len];
			}
		}
		long[] series=new long[size];
		series[1]=1;
		for (int i=2;i<size;++i) if ((i%2)==0) series[i]=2*series[i/2];
		else	{
			int n=(i-1)/2;
			series[i]=i+2*series[n]+series[n]/n;
		}
		for (int i=1;i<size;++i)	{
			long f=series[i];
			if (f%i!=0) throw new RuntimeException("JAJA SI.");
			long m=f/i;
			if (m!=expectedMultipliers[i]) throw new RuntimeException("Fallo en i="+i+".");
			System.out.println(String.format("f(%d)=%d=%d*%d",i,f,m,i));
			// OK. El patrón es "sencillo" :). Se puede hacer. BIBA!
		}
	}
}
