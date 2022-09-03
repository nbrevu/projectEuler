package com.euler;

public class Euler466 {
	// This is a brute force simulation...
	private static int count(int rows,int cols)	{
		boolean[] present=new boolean[1+rows*cols];
		for (int i=1;i<=rows;++i) for (int j=1;j<=cols;++j) present[i*j]=true;
		int count=0;
		for (int i=1;i<=rows*cols;++i) if (present[i]) ++count;
		return count;
	}
	
	private static void trace(int rows,int cols)	{
		int result=count(rows,cols);
		System.out.println("f("+rows+","+cols+")="+result);
	}
	
	public static void main(String[] args)	{
		trace(32,32);
		trace(32,64);
		trace(64,64);
		trace(12,69);
		trace(12,345);
		trace(64,1000);
		trace(64,10000);
		trace(64,100000);
		trace(64,1000000);
	}
}
