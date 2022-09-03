package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindLong {
	// La variante simple no funciona. Hay que liarla algo parda. ¡Ooooooh!
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_long.txt";
	
	private static class StringHolder	{
		private final Set<String> strings=new HashSet<>();
		public StringHolder()	{}
		public void addString(String in)	{
			String result=addAtStart(in);
			addAtEnd(result);
		}
		private String addAtStart(String in)	{
			for (String str:strings)	{
				String result=tryAndCombine(in,str);
				if (result!=null)	{
					strings.remove(str);
					return result;
				}
			}
			return in;
		}
		private void addAtEnd(String in)	{
			for (String str:strings)	{
				String result=tryAndCombine(str,in);
				if (result!=null)	{
					strings.remove(str);
					strings.add(result);
					return;
				}
			}
			strings.add(in);
		}
		private static String tryAndCombine(String a,String b)	{
			int N=a.length();
			int Nb=b.length();
			int minLength=Math.min(N,Nb)/2;
			for (int i=minLength;i<N;++i)	{
				String s=a.substring(N-i,N);
				if (b.startsWith(s)) return a+b.substring(i,b.length());
			}
			return null;
		}
		public String getSingletonString()	{
			if (strings.size()!=1) throw new IllegalStateException();
			return strings.iterator().next();
		}
		@Override
		public String toString()	{
			return strings.toString();
		}
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		StringHolder holder=new StringHolder();
		for (FastaEntry entry:entries) holder.addString(entry.getContent());
		try	{
			System.out.println(holder.getSingletonString());
		}	catch (Exception ex)	{
			System.out.println(holder.strings.size());
		}
	}
}
