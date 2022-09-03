package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.rosalind.utils.Generation;
import com.rosalind.utils.IoUtils;

public class RosalindWfmd {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_wfmd.txt";

	public static void main(String[] args) throws IOException	{
		int N,m,g,k;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			int[] data=IoUtils.parseStringAsArrayOfInts(reader.readLine(),4);
			N=data[0];
			m=data[1];
			g=data[2];
			k=data[3];
		}
		System.out.println(Generation.getFixedGeneration(N,m).nextGenerations(g).getProbOfAtLeastKRecessive(k));
	}
}
