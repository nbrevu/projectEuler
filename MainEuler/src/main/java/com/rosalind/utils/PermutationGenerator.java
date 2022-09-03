package com.rosalind.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.euler.common.EulerUtils;

public class PermutationGenerator	{
	private final int[] currentPerm;
	private String currentString;
	public PermutationGenerator(int in)	{
		currentPerm=getFirstPermutation(in);
		currentString=getString();
	}
	private String getString()	{
		return getString(currentPerm);
	}
	public String getNext()	{
		String result=currentString;
		if (EulerUtils.nextPermutation(currentPerm)) currentString=getString();
		else currentString=null;
		return result;
	}
	private static int[] getFirstPermutation(int in)	{
		int[] result=new int[in];
		for (int i=0;i<in;++i) result[i]=i+1;
		return result;
	}
	public static List<int[]> generateAllPermutations(int size)	{
		List<int[]> result=new ArrayList<>();
		int[] currentPerm=getFirstPermutation(size);
		do result.add(Arrays.copyOf(currentPerm,size)); while (EulerUtils.nextPermutation(currentPerm));
		return result;
	}
	public static String getString(int[] permutation)	{
		StringBuilder sb=new StringBuilder();
		sb.append(permutation[0]);
		for (int i=1;i<permutation.length;++i) sb.append(' ').append(permutation[i]);
		return sb.toString();
	}
}