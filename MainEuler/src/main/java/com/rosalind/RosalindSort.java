package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.rosalind.utils.IoUtils;
import com.rosalind.utils.PermutationGraph;

public class RosalindSort {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_sort.txt";
	// YES, the temporary file is the one from "rear".
	private final static String TMP_FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\TmpFiles\\rosalind_rear.txt";
	
	public static void main(String[] args) throws IOException	{
		PermutationGraph.Permutation p1;
		PermutationGraph.Permutation p2;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			p1=new PermutationGraph.Permutation(IoUtils.parseStringAsArrayOfInts(reader.readLine(),PermutationGraph.PERM_SIZE));
			p2=new PermutationGraph.Permutation(IoUtils.parseStringAsArrayOfInts(reader.readLine(),PermutationGraph.PERM_SIZE));
		}
		PermutationGraph graph=new PermutationGraph(PermutationGraph.PERM_SIZE);
		graph.fillGraph(Paths.get(TMP_FILE));
		List<PermutationGraph.PermutationInfo> reversals=graph.getDistanceExtended(p1,p2);
		System.out.println(reversals.size());
		for (PermutationGraph.PermutationInfo pInfo:reversals) System.out.println(pInfo);
	}
}
