package com.euler;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Euler484_3 {
	public static void main(String[] args)	{
		class Cosica	{
			public final long n;
			public Cosica(long n)	{
				this.n=n;
			}
		}
		Cosica[] values=new Cosica[150000000];
		Random r=new Random();
		for (int i=0;i<values.length;++i) values[i]=new Cosica(r.nextLong());
		long tic=System.nanoTime();
		Arrays.sort(values,Comparator.comparingLong((Cosica x)->x.n));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
