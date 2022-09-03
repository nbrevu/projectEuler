package com.rosalind;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindKmer {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_kmer.txt";
	private final static int K=4;
	
	private final static String SYMBOLS="ACGT";
	private final static String OUT_FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\OutFiles\\rosalind_kmer.txt";

	private static String getForIndices(int[] in)	{
		StringBuilder result=new StringBuilder();
		for (int i:in) result.append(SYMBOLS.charAt(i));
		return result.toString();
	}
	
	private static boolean nextIndex(int[] indices,int maxIndex)	{
		for (int i=indices.length-1;i>=0;--i)	{
			++indices[i];
			if (indices[i]==maxIndex) indices[i]=0;
			else return true;
		}
		return false;
	}
	
	private static void fillWithZeros(Map<String,Integer> map,int k)	{
		int[] indices=new int[k];
		int maxIndex=SYMBOLS.length();
		do map.put(getForIndices(indices),0); while (nextIndex(indices,maxIndex));
	}
	
	private static SortedMap<String,Integer> getKMerComposition(String in,int k)	{
		SortedMap<String,Integer> result=new TreeMap<>();
		fillWithZeros(result,k);
		int begin=0;
		int end=k;
		int N=in.length();
		for (;end<=N;++begin,++end) EulerUtils.increaseCounter(result,in.substring(begin,end));
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=1) throw new IllegalArgumentException();
		SortedMap<String,Integer> kMerComposition=getKMerComposition(entries.get(0).getContent(),K);
		boolean first=true;
		try (PrintStream out=new PrintStream(OUT_FILE))	{
			for (int value:kMerComposition.values())	{
				if (first) first=false;
				else out.print(" ");
				out.print(value);
			}
			out.println();
		}
	}
}
