package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.rosalind.utils.IoUtils;

public class RosalindIndc {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_indc.txt";

	private static double getCombinatorialNumber(double[] factorials,int n,int k)	{
		return factorials[n]/(factorials[k]*factorials[n-k]);
	}
	
	private static double[] getFactorials(int N)	{
		double[] result=new double[1+N];
		result[0]=1.0;
		result[1]=1.0;
		for (int i=2;i<=N;++i) result[i]=i*result[i-1];
		return result;
	}
	
	private static double[] getAllCombinatorials(int N)	{
		double[] factorials=getFactorials(N);
		double base=Math.pow(0.5,N);
		double[] result=new double[1+N];
		for (int i=0;i<=N;++i) result[i]=base*getCombinatorialNumber(factorials,N,i);
		return result;
	}
	
	private static double[] getCumulative(double[] in)	{
		int N=in.length;
		double[] result=new double[N];
		result[N-1]=in[N-1];
		for (int i=N-2;i>=0;--i) result[i]=result[i+1]+in[i];
		return result;
	}
	
	private static double[] getLogs(double[] in)	{
		int N=in.length;
		double[] result=new double[N];
		for (int i=0;i<N;++i) result[i]=Math.log10(in[i]);
		return result;
	}
	
	private static double[] removeFirst(double[] in)	{
		int N=in.length;
		double[] result=new double[N-1];
		System.arraycopy(in,1,result,0,N-1);
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		int N;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			N=Integer.parseInt(reader.readLine());
		}
		System.out.println(IoUtils.toStringWithSpaces(removeFirst(getLogs(getCumulative(getAllCombinatorials(2*N))))));
	}
}
