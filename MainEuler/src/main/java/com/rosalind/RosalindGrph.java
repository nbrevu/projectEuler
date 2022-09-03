package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindGrph {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_grph.txt";
	
	private static String getPrefix(FastaEntry entry)	{
		return entry.getContent().substring(0,3);
	}
	
	private static String getSuffix(FastaEntry entry)	{
		String str=entry.getContent();
		int N=str.length();
		return str.substring(N-3,N);
	}
	
	private static Multimap<String,FastaEntry> getPrefixMap(Collection<FastaEntry> entries)	{
		Multimap<String,FastaEntry> result=HashMultimap.create();
		for (FastaEntry entry:entries) result.put(getPrefix(entry),entry);
		return result;
	}
	
	private static class Edge	{
		private final FastaEntry orig;
		private final FastaEntry dest;
		public Edge(FastaEntry orig,FastaEntry dest)	{
			this.orig=orig;
			this.dest=dest;
		}
		@Override
		public String toString()	{
			return orig.getId()+" "+dest.getId();
		}
	}
	
	private static List<Edge> getEdges(Collection<FastaEntry> entries)	{
		Multimap<String,FastaEntry> prefixMap=getPrefixMap(entries);
		List<Edge> result=new ArrayList<>();
		for (FastaEntry entry:entries) for (FastaEntry associated:prefixMap.get(getSuffix(entry))) if (entry!=associated) result.add(new Edge(entry,associated));
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		for (Edge edge:getEdges(entries)) System.out.println(edge);
	}
}
