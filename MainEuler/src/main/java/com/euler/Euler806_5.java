package com.euler;

public class Euler806_5 {
	/*
	 * 5000150001 total cases; 729 valid cases.
	 * ARE YOU FUCKING KIDDING ME.
	 */
	public static void main(String[] args)	{
		int N=100000;
		long counter=0;
		long otherCounter=0;
		for (int a=0;a<=N;++a)	{
			int maxB=N-a;
			for (int b=0;b<=maxB;++b)	{
				int c=maxB-b;
				++counter;
				if ((a^b^c)==0)	{
					System.out.println(String.format("Found a valid case: (%d,%d,%d).",a,b,c));
					++otherCounter;
				}
			}
		}
		System.out.println(String.format("%d total cases; %d valid cases.",counter,otherCounter));
	}
}
