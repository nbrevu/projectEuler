package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.TreeSet;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindCons {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_cons.txt";
	
	private static class ConsensusInfo	{
		private final static Character[] ORDERED_SYMBOLS=new Character[]{'A','C','G','T'};
		private final Table<Integer,Character,Long> counters;
		public ConsensusInfo()	{
			counters=HashBasedTable.create();
		}
		public void addString(String in)	{
			for (int i=0;i<in.length();++i) increaseCounter(i,in.charAt(i));
		}
		public void addStrings(Collection<FastaEntry> strings)	{
			for (FastaEntry entry:strings) addString(entry.getContent());
		}
		private void increaseCounter(Integer position,Character symbol)	{
			counters.put(position,symbol,1+getCounter(position,symbol));
		}
		public int maxLength()	{
			// I'm... not very proud of this.
			return new TreeSet<>(counters.rowKeySet()).last();
		}
		public long getCounter(Integer pos,Character symbol)	{
			Long result=counters.get(pos,symbol);
			return (result==null)?0:result.longValue();
		}
		public Character getOptimumForPosition(Integer i)	{
			Character result=ORDERED_SYMBOLS[0];
			long maxValue=getCounter(i,result);
			for (int k=1;k<ORDERED_SYMBOLS.length;++k)	{
				long newValue=getCounter(i,ORDERED_SYMBOLS[k]);
				if (newValue>maxValue)	{
					result=ORDERED_SYMBOLS[k];
					maxValue=newValue;
				}
			}
			return result;
		}
		@Override
		public String toString()	{
			int maxLen=maxLength();
			StringBuilder result=new StringBuilder();
			for (int i=0;i<=maxLen;++i) result.append(getOptimumForPosition(i));
			result.append(System.lineSeparator());
			for (Character symbol:ORDERED_SYMBOLS)	{
				result.append(symbol).append(':');
				for (int i=0;i<=maxLen;++i) result.append(' ').append(getCounter(i,symbol));
				result.append(System.lineSeparator());
			}
			return result.toString();
		}
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		Collection<FastaEntry> entries=reader.getEntries();
		ConsensusInfo info=new ConsensusInfo();
		info.addStrings(entries);
		System.out.println(info.toString());
	}
}
