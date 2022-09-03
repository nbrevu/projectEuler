package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils.Pair;
import com.rosalind.utils.IoUtils;

public class RosalindSubs {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_tree.txt";
	
	public static void main(String[] args) throws IOException	{
		int N;
		List<Pair<Integer,Integer>> edges=new ArrayList<>();
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			N=Integer.parseInt(reader.readLine());
			for (;;)	{
				String line=reader.readLine();
				if (line==null) break;
				int[] edge=IoUtils.parseStringAsArrayOfInts(line,2);
				edges.add(new Pair<>(edge[0],edge[1]));
			}
		}
		// I feel almost dirty doing this.
		int result=N-1-edges.size();
		System.out.println(result);
	}
}
