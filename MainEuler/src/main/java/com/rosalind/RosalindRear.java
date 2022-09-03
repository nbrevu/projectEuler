package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils.Pair;
import com.rosalind.utils.IoUtils;
import com.rosalind.utils.PermutationGraph;

public class RosalindRear {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_rear.txt";
	private final static String TMP_FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\TmpFiles\\rosalind_rear.txt";
	
	public static void main(String[] args) throws IOException	{
		List<Pair<PermutationGraph.Permutation,PermutationGraph.Permutation>> permPairs=new ArrayList<>();
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			for (;;)	{
				String line1=reader.readLine();
				if (line1==null) break;
				String line2=reader.readLine();
				PermutationGraph.Permutation p1=new PermutationGraph.Permutation(IoUtils.parseStringAsArrayOfInts(line1,PermutationGraph.PERM_SIZE));
				PermutationGraph.Permutation p2=new PermutationGraph.Permutation(IoUtils.parseStringAsArrayOfInts(line2,PermutationGraph.PERM_SIZE));
				permPairs.add(new Pair<>(p1,p2));
				if (reader.readLine()==null) break;	// Skip blank line.
			}
		}
		PermutationGraph graph=new PermutationGraph(PermutationGraph.PERM_SIZE);
		graph.fillGraph(Paths.get(TMP_FILE));
		boolean first=true;
		for (Pair<PermutationGraph.Permutation,PermutationGraph.Permutation> pair:permPairs)	{
			if (first) first=false;
			else System.out.print(" ");
			System.out.print(graph.getDistance(pair.first,pair.second));
		}
		System.out.println();
	}
}
