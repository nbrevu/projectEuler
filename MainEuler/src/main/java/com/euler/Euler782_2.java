package com.euler;

import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler782_2 {
	// I'm close, but it fails for i=20.
	// Are there terms so that c(20,x)=4? I might want to investigate.
	public static void main(String[] args)	{
		for (int i=2;i<=20;++i)	{
			int i2=i*i;
			IntSet specialTerms=HashIntSets.newMutableSet();
			for (int j=1;j<i;++j)	{
				int j2=j*j;
				int k=i-j;
				int k2=k*k;
				specialTerms.add(j2);
				specialTerms.add(i2-j*j);
				specialTerms.add(j2+k2);
				specialTerms.add(i2-(j2+k2));
			}
			int c=3*(i2+1)-4-specialTerms.size();
			System.out.println("C("+i+")="+c+".");
		}
	}
}
