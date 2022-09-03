package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindLcsm {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_lcsm.txt";

	private static class CommonStrings	{
		private final NavigableMap<Integer,Set<String>> contents;
		private final static Predicate<Map.Entry<Integer,Set<String>>> EMPTY_ENTRY=new Predicate<Map.Entry<Integer,Set<String>>>()	{
			@Override
			public boolean test(Map.Entry<Integer,Set<String>> entry)	{
				return entry.getValue().isEmpty();
			}
		};
		public CommonStrings(String in)	{
			contents=new TreeMap<>();
			addContents(in,contents);
		}
		private static void addContents(String in,NavigableMap<Integer,Set<String>> contents)	{
			int N=in.length();
			for (int i=1;i<=N;++i)	{
				Set<String> fixedLength=new HashSet<>();
				int beginIndex=0;
				int endIndex=i+beginIndex;
				for (;endIndex<=N;++beginIndex,++endIndex) fixedLength.add(in.substring(beginIndex,endIndex));
				contents.put(i,fixedLength);
			}
		}
		public void trim(String in)	{
			for (Map.Entry<Integer,Set<String>> fixedLength:contents.entrySet()) fixedLength.getValue().removeIf(new Predicate<String>()	{
				@Override
				public boolean test(String str) {
					return !in.contains(str);
				}
			});
			contents.entrySet().removeIf(EMPTY_ENTRY);
		}
		public String maxLengthCommon()	{
			return contents.lastEntry().getValue().iterator().next();
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		CommonStrings common=new CommonStrings(entries.get(0).getContent());
		for (int i=1;i<entries.size();++i) common.trim(entries.get(i).getContent());
		long tac=System.nanoTime();
		System.out.println(common.maxLengthCommon());
		double seconds=(tac-tic)/1e9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
