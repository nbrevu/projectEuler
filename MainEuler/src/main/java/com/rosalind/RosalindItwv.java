package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RosalindItwv {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_itwv.txt";
	
	// Pues está mal :O.
	private static boolean isInterwovenStringRecursive(String in,String a,String b)	{
		if (a.length()==0) return in.equals(b);
		else if (b.length()==0) return in.equals(a);
		char c1=a.charAt(0);
		char c2=b.charAt(0);
		char c0=in.charAt(0);
		String sub=in.substring(1);
		if ((c0==c1)&&(c0==c2)) return isInterwovenStringRecursive(sub,a.substring(1),b)||isInterwovenStringRecursive(sub,a,b.substring(1));
		else if (c0==c1) return isInterwovenStringRecursive(sub,a.substring(1),b);
		else if (c0==c2) return isInterwovenStringRecursive(sub,a,b.substring(1));
		else return false;
	}
	
	private static boolean isInterwovenString(String in,String a,String b)	{
		return isInterwovenStringRecursive(in,a,b);
	}
	
	private static boolean isInterwovenAtAnyPoint(String in,String a,String b)	{
		int begin=0;
		int end=a.length()+b.length();
		for (;end<=in.length();++begin,++end) if (isInterwovenString(in.substring(begin,end),a,b)) return true;
		return false;
	}
	
	private static String boolStr(boolean in)	{
		return in?"1":"0";
	}

	public static void main(String[] args) throws IOException	{
		String base;
		List<String> substrings;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			base=reader.readLine();
			substrings=new ArrayList<>();
			for (;;)	{
				String line=reader.readLine();
				if (line==null) break;
				else substrings.add(line);
			}
		}	catch (IOException exc)	{
			base="GACCACGGTT";
			substrings=Arrays.asList("ACAG","GT","CCG");
		}
		int N=substrings.size();
		boolean[][] matrix=new boolean[N][N];
		for (int i=0;i<N;++i)	{
			String thisString=substrings.get(i);
			matrix[i][i]=isInterwovenAtAnyPoint(base,thisString,thisString);
			for (int j=i+1;j<N;++j)	{
				boolean result=isInterwovenAtAnyPoint(base,thisString,substrings.get(j));
				matrix[i][j]=result;
				matrix[j][i]=result;
			}
		}
		for (int i=0;i<N;++i)	{
			System.out.print(boolStr(matrix[i][0]));
			for (int j=1;j<N;++j) System.out.print(" "+boolStr(matrix[i][j]));
			System.out.println();
		}
	}
}
