package com.rosalind;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindLong_2 {
	// La variante simple no funciona. Hay que liarla algo parda. ¡Ooooooh!
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_long.txt";
	
	private static class StringCombiner	{
		private static class StringInfo	{
			public final String string;
			public final List<Integer> prefixes;
			public final List<Integer> suffixes;
			public StringInfo(String string)	{
				this.string=string;
				prefixes=new ArrayList<>();
				suffixes=new ArrayList<>();
			}
		}
		private StringInfo[] strings;
		public StringCombiner(List<FastaEntry> entries)	{
			int N=entries.size();
			strings=new StringInfo[N];
			for (int i=0;i<N;++i) strings[i]=new StringInfo(entries.get(i).getContent());
		}
		public void fillPrefixesAndSuffixes()	{
			int N=strings.length;
			int N_=N-1;
			for (int i=0;i<N_;++i)	{
				String s1=strings[i].string;
				for (int j=i+1;j<N;++j)	{
					String s2=strings[j].string;
					if (canCombine(s1,s2))	{
						strings[i].suffixes.add(j);
						strings[j].prefixes.add(i);
					}
					if (canCombine(s2,s1))	{
						strings[i].prefixes.add(j);
						strings[j].suffixes.add(i);
					}
				}
			}
		}
		private Integer lookForStart()	{
			int result=-1;
			for (int i=0;i<strings.length;++i) if (strings[i].prefixes.isEmpty())	{
				result=i;
				break;
			}
			if (result==-1) return null;
			for (int i=result+1;i<strings.length;++i) if (strings[i].prefixes.isEmpty()) throw new IllegalStateException();
			return result;
		}
		// ACHTUNG! Danger, potential combinatory explosion.
		private List<Integer> findChain(List<Integer> current)	{
			if (current.size()==strings.length) return current;
			int last=current.get(current.size()-1);
			for (Integer s:strings[last].suffixes) if (!current.contains(s))	{
				List<Integer> newList=new ArrayList<>(current);
				newList.add(s);
				List<Integer> result=findChain(newList);
				if (result!=null) return result;
			}
			return null;
		}
		private List<Integer> findChain(Integer start)	{
			return findChain(Arrays.asList(start));
		}
		private List<Integer> findChain()	{
			Integer startPos=lookForStart();
			if (startPos!=null) return findChain(startPos);
			else for (int i=0;i<strings.length;++i)	{
				List<Integer> result=findChain(i);
				if (result!=null) return result;
			}
			return null;
		}
		public String findAndCombine()	{
			List<Integer> chain=findChain();
			String result=strings[chain.get(0)].string;
			for (int i=1;i<strings.length;++i) result=doCombine(result,strings[chain.get(i)].string);
			return result;
		}
		private static boolean canCombine(String s1,String s2)	{
			int N=s1.length();
			int Nb=s2.length();
			int minLength=Math.min(N,Nb)/2;
			for (int i=minLength;i<N;++i)	{
				String s=s1.substring(N-i,N);
				if (s2.startsWith(s)) return true;
			}
			return false;
		}
		private static String doCombine(String s1,String s2)	{
			int N=s1.length();
			int Nb=s2.length();
			int minLength=Math.min(N,Nb)/2;
			for (int i=minLength;i<N;++i)	{
				String s=s1.substring(N-i,N);
				if (s2.startsWith(s)) return s1+s2.substring(i,s2.length());
			}
			throw new IllegalArgumentException();
		}
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		StringCombiner combiner=new StringCombiner(entries);
		combiner.fillPrefixesAndSuffixes();
		String result=combiner.findAndCombine();
		try (PrintStream out=new PrintStream("F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\OutFiles\\rosalind_long.txt"))	{
			out.println(result);
		}
	}
}
