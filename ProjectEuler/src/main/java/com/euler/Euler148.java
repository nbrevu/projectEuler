package com.euler;

import com.euler.common.Timing;
import com.google.common.math.LongMath;

public class Euler148 {
	private final static long LIMIT=LongMath.pow(10l,9);
	
	private static long[] getTriangulars(int max)	{
		long[] result=new long[1+max];
		for (int i=1;i<=max;++i) result[i]=i+result[i-1];
		return result;
	}
	
	private static long[] getPows28(int max)	{
		long[] result=new long[max];
		result[0]=1l;
		for (int i=1;i<max;++i) result[i]=28*result[i-1];
		return result;
	}
	
	private static int[] getBase7Representation(long in)	{
		int maxSize=1+(int)(Math.floor(Math.log(in)/Math.log(7d)));
		int[] result=new int[maxSize];
		for (int i=0;i<maxSize;++i)	{
			result[i]=(int)(in%7);
			in/=7;
		}
		return result;
	}
			
	private static long solve()	{
		int[] rep=getBase7Representation(LIMIT);
		long[] tris=getTriangulars(6);
		long[] pows28=getPows28(rep.length);
		long prod=1;
		long result=0;
		for (int i=rep.length-1;i>=0;--i)	{
			result+=tris[rep[i]]*pows28[i]*prod;
			prod*=1+rep[i];
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler148::solve);
	}
}
