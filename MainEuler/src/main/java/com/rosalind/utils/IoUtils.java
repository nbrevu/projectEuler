package com.rosalind.utils;

public class IoUtils {
	public static int[] parseStringAsArrayOfInts(String in,int size)	{
		String[] split=in.split(" ");
		if (size==-1) size=split.length;
		else if (size!=split.length) throw new IllegalStateException();
		int[] result=new int[size];
		for (int i=0;i<size;++i) result[i]=Integer.parseInt(split[i]);
		return result;
	}
	
	public static long[] parseStringAsArrayOfLongs(String in,int size)	{
		String[] split=in.split(" ");
		if (size==-1) size=split.length;
		else if (size!=split.length) throw new IllegalStateException();
		long[] result=new long[size];
		for (int i=0;i<size;++i) result[i]=Long.parseLong(split[i]);
		return result;
	}
	
	public static double[] parseStringAsArrayOfDoubles(String in,int size)	{
		String[] split=in.split(" ");
		if (size==-1) size=split.length;
		else if (size!=split.length) throw new IllegalStateException();
		double[] result=new double[size];
		for (int i=0;i<size;++i) result[i]=Double.parseDouble(split[i]);
		return result;
	}
	
	public static String toStringWithSpaces(int[] in)	{
		StringBuilder result=new StringBuilder();
		result.append(in[0]);
		for (int i=1;i<in.length;++i) result.append(' ').append(in[i]);
		return result.toString();
	}
	
	public static String toStringWithSpaces(double[] in)	{
		StringBuilder result=new StringBuilder();
		result.append(in[0]);
		for (int i=1;i<in.length;++i) result.append(' ').append(in[i]);
		return result.toString();
	}
}
