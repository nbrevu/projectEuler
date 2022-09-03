package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.rosalind.RosalindCat.StringId;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindMotz {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_motz.txt";
	private final static long MOD=1000000l;
	
	private static class MotzkinFinder	{
		private final static Map<Character,Character> LINK_MAP=ImmutableMap.of('A','U','C','G','G','C','U','A');
		private final Map<StringId,Long> counters;
		private final long mod;
		public MotzkinFinder(long mod)	{
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
			int N=in.length();
			if (N<=1) return 1l;
			else if (N==2)	{
				char c1=in.charAt(0);
				char c2=in.charAt(1);
				return (c1==LINK_MAP.get(c2).charValue())?2:1;
			}
			else	{
				// We need to actually calculate it!
				char c1=in.charAt(0);
				char target=LINK_MAP.get(c1).charValue();
				long result=0l;
				for (int i=1;i<N;++i) if (in.charAt(i)==target)	{
					String s1=in.substring(1,i);
					String s2=in.substring(i+1,N);
					long v1=getPathsForString(s1);
					long v2=getPathsForString(s2);
					result=(result+v1*v2)%mod;
				}
				result=(result+getPathsForString(in.substring(1)))%mod;
				return result;
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=1) throw new IllegalArgumentException();
		MotzkinFinder finder=new MotzkinFinder(MOD);
		long result=finder.getPathsForString(entries.get(0).getContent());
		System.out.println(result);
	}
}
