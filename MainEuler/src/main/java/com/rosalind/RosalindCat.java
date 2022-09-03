package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.rosalind.aminoacids.BaseCounter;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindCat {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_cat.txt";
	private final static long MOD=1000000l;
	
	public static class StringId	{
		public final String id;
		public StringId(String id)	{
			String reverse=new StringBuilder(id).reverse().toString();
			this.id=(id.compareTo(reverse)<0)?id:reverse;
		}
		@Override
		public boolean equals(Object other)	{
			StringId iOther=(StringId)other;
			return id.equals(iOther.id);
		}
		@Override
		public int hashCode()	{
			return id.hashCode();
		}
		@Override
		public String toString()	{
			return id;
		}
	}
	
	private static class NoncrossingFinder	{
		private final static Map<Character,Character> LINK_MAP=ImmutableMap.of('A','U','C','G','G','C','U','A');
		private final Map<StringId,Long> counters;
		private final long mod;
		public NoncrossingFinder(long mod)	{
			counters=new HashMap<>();
			this.mod=mod;
		}
		public long getPathsForString(String in)	{
			StringId id=new StringId(in);
			Long result=counters.get(id);
			if (result==null)	{
				result=calculatePathsForString(in);
				counters.put(id,result);
			}
			return result;
		}
		private long calculatePathsForString(String in)	{
			if (!isBalanced(in)) return 0l;
			int N=in.length();
			if (N==0) return 1l;
			else if (N==2)	{
				char c1=in.charAt(0);
				char c2=in.charAt(1);
				return (LINK_MAP.get(c1).charValue()==c2)?1l:0l;
			}	else	{
				// We need to actually calculate it!
				char c1=in.charAt(0);
				char target=LINK_MAP.get(c1).charValue();
				long result=0l;
				for (int i=1;i<N;i+=2) if (in.charAt(i)==target)	{
					String s1=in.substring(1,i);
					String s2=in.substring(i+1,N);
					long v1=getPathsForString(s1);
					long v2=getPathsForString(s2);
					result=(result+v1*v2)%mod;
				}
				return result;
			}
		}
		private static boolean isBalanced(String in)	{
			BaseCounter counter=new BaseCounter();
			counter.readString(in,true);
			return (counter.getA()==counter.getTorU())&&(counter.getC()==counter.getG());
		}
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=1) throw new IllegalArgumentException();
		NoncrossingFinder finder=new NoncrossingFinder(MOD);
		long result=finder.getPathsForString(entries.get(0).getContent());
		System.out.println(result);
	}
}
