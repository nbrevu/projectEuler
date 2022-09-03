package com.euler;

import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler782_3 {
	// I'm close, but it fails for i=20.
	// Are there terms so that c(20,x)=4? I might want to investigate.
	public static void main(String[] args)	{
		int n=20;
		int n2=n*n;
		IntSet terms2=HashIntSets.newMutableSet();
		for (int j=1;j<n;++j)	{
			int j2=j*j;
			int k=n-j;
			int k2=k*k;
			terms2.add(j2);
			terms2.add(n2-j*j);
			terms2.add(j2+k2);
			terms2.add(n2-(j2+k2));
		}
		boolean[] seen=new boolean[1+n2];
		// Known cases for n=2...
		for (int x:terms2) seen[x]=true;
		// Blocks...
		for (int i=0;i<=n;++i) for (int j=0;j<=n;++j)	{
			int ij=i*j;
			seen[ij]=true;
			seen[n2-ij]=true;
		}
		// Gnomon numbers...
		for (int i=2;i<n;++i) for (int j=1;j<i;++j)	{
			int ij=i*i-j*j;
			seen[ij]=true;
			seen[n2-ij]=true;
		}
		// Known cases for n=2 for smaller sizes...
		for (int m=0;m<n;++m) for (int j=1;j<m;++j)	{
			int m2=m*m;
			int j2=j*j;
			int k=m-j;
			int k2=k*k;
			seen[m2-j2]=true;
			seen[n2-(m2-j2)]=true;
			seen[m2-(j2+k2)]=true;
			seen[n2-m2+j2+k2]=true;
		}
		for (int i=0;i<seen.length;++i) if (!seen[i]) System.out.println("Caso a estudiar: i="+i+".");
	}
}
