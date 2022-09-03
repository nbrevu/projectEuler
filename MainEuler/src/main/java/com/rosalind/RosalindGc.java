package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindGc {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_gc.txt";
	
	private static NavigableMap<Double,FastaEntry> sortByGc(Collection<FastaEntry> entries)	{
		NavigableMap<Double,FastaEntry> result=new TreeMap<>();
		for (FastaEntry entry:entries)	{
			double gcContent=calculateGc(entry.getContent());
			if (result.containsKey(gcContent)) throw new IllegalStateException();	// Should never happens, I guess.
			result.put(gcContent,entry);
		}
		return result;
	}
	
	private static double calculateGc(String bases)	{
		int at=0;
		int gc=0;
		for (char ch:bases.toCharArray()) switch (ch)	{
			case 'a':case 'A':case 't':case 'T':++at;break;
			case 'c':case 'C':case 'g':case 'G':++gc;break;
		}
		return (double)(100*gc)/(double)(at+gc);
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		NavigableMap<Double,FastaEntry> sorted=sortByGc(entries);
		Map.Entry<Double,FastaEntry> highestGc=sorted.lastEntry();
		System.out.println(highestGc.getValue().getId());
		System.out.println(highestGc.getKey());
	}
}
