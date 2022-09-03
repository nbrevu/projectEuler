package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;
import com.rosalind.utils.PermutationGenerator;

public class RosalindSign {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_sign.txt";
	
	private static long getFactorial(long in)	{
		long result=1;
		for (long i=2;i<=in;++i) result*=i;
		return result;
	}
	
	private static int[] changeSign(int[] permutation,int bits)	{
		int[] result=Arrays.copyOf(permutation,permutation.length);
		int index=0;
		while (bits>0)	{
			if ((bits&1)==1) result[index]=-result[index];
			bits/=2;
			++index;
		}
		return result;
	}
	
	private static List<int[]> getAllSignedCases(int[] permutation)	{
		List<int[]> result=new ArrayList<>();
		int N=permutation.length;
		int bits=1<<N;
		result.add(permutation);
		for (int i=1;i<bits;++i) result.add(changeSign(permutation,i));
		return result;
	}
	
	private static List<int[]> getAllSignedPermutations(int in)	{
		List<int[]> base=PermutationGenerator.generateAllPermutations(in);
		List<int[]> result=new ArrayList<>();
		for (int[] permutation:base) result.addAll(getAllSignedCases(permutation));
		result.sort(Ints.lexicographicalComparator());
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		try (PrintStream out=new PrintStream("F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\OutFiles\\rosalind_sign.txt"))	{
			Path srcFile=Paths.get(FILE);
			int number;
			try (BufferedReader reader=Files.newBufferedReader(srcFile))	{
				number=Integer.parseInt(reader.readLine());
			}
			out.println(getFactorial(number)*LongMath.pow(2l,number));
			for (int[] perm:getAllSignedPermutations(number)) out.println(PermutationGenerator.getString(perm));
		}
	}
}
