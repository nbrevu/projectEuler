package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;
import com.rosalind.utils.ProteinUtils;

public class RosalindCorr {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_corr.txt";
	
	private static class StringComplemented	{
		private final String base;
		private final String complement;
		private final String original;
		public StringComplemented(String base)	{
			// The "base" stored is always the first one alphabetically
			original=base;
			String s1=base;
			String s2=ProteinUtils.getComplement(base);
			if (s1.compareTo(s2)<0)	{
				this.base=s1;
				complement=s2;
			}	else	{
				this.base=s2;
				complement=s1;
			}
		}
		public String getOriginalString()	{
			return original;
		}
		@Override
		public boolean equals(Object other)	{
			StringComplemented scOther=(StringComplemented)other;
			return scOther.base.equals(base);
		}
		@Override
		public int hashCode()	{
			return base.hashCode();
		}
		public int hammingDistance(String in)	{
			int d1=ProteinUtils.computeHammingDistance(base,in);
			int d2=ProteinUtils.computeHammingDistance(complement,in);
			return Math.min(d1,d2);
		}
		public int hammingDistance(StringComplemented other)	{
			return hammingDistance(other.base);
		}
		public String getClosestRepresentation(String in)	{
			int d1=ProteinUtils.computeHammingDistance(base,in);
			int d2=ProteinUtils.computeHammingDistance(complement,in);
			return (d1<d2)?base:complement;
		}
	}
	
	private static class StringHolder	{
		private final Set<StringComplemented> potentiallyWrong;
		private final Set<StringComplemented> correct;
		public StringHolder()	{
			potentiallyWrong=new HashSet<>();
			correct=new HashSet<>();
		}
		public void addString(FastaEntry entry)	{
			addString(entry.getContent());
		}
		public void addString(String in)	{
			StringComplemented complemented=new StringComplemented(in);
			if (potentiallyWrong.contains(complemented))	{
				potentiallyWrong.remove(complemented);
				correct.add(complemented);
			}	else if (!correct.contains(complemented)) potentiallyWrong.add(complemented);
		}
		public Map<StringComplemented,StringComplemented> getCorrections()	{
			Map<StringComplemented,StringComplemented> result=new HashMap<>();
			for (StringComplemented str:potentiallyWrong)	{
				for (StringComplemented str2:correct) if (str2.hammingDistance(str)==1)	{
					result.put(str,str2);
					break;
				}
				if (!result.containsKey(str)) throw new IllegalStateException();
			}
			return result;
		}
	}

	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		StringHolder holder=new StringHolder();
		for (FastaEntry entry:entries) holder.addString(entry);
		Map<StringComplemented,StringComplemented> result=holder.getCorrections();
		for (Map.Entry<StringComplemented,StringComplemented> entry:result.entrySet())	{
			String s1=entry.getKey().getOriginalString();
			String s2=entry.getValue().getClosestRepresentation(s1);
			System.out.println(s1+"->"+s2);
		}
	}
}
