package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;
import com.rosalind.utils.ProteinUtils;

public class RosalindRevp {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_revp.txt";
	private final static int MIN_LENGTH=4;
	private final static int MAX_LENGTH=12;
	
	private final static class ReversePalindromePosition	{
		public final int position;
		public final int length;
		public ReversePalindromePosition(int position,int length)	{
			this.position=position;
			this.length=length;
		}
		@Override
		public String toString()	{
			return position+" "+length;
		}
	}
	
	private static List<ReversePalindromePosition> findReversePalindromePositions(String in,int minLength,int maxLength)	{
		int N=in.length();
		List<ReversePalindromePosition> result=new ArrayList<>();
		for (int i=minLength;i<=maxLength;i+=2)	{
			int startPos=0;
			int endPos=i;
			for (;endPos<=N;++startPos,++endPos)	{
				String s1=in.substring(startPos,endPos);
				if (s1.equals(ProteinUtils.getComplement(s1))) result.add(new ReversePalindromePosition(1+startPos,i));
			}
		}
		result.sort(new Comparator<ReversePalindromePosition>()	{
			@Override
			public int compare(ReversePalindromePosition p1, ReversePalindromePosition p2) {
				int d1=p1.position-p2.position;
				return (d1==0)?(p1.length-p2.length):d1;
			}
		});
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=1) throw new IllegalArgumentException();
		for (ReversePalindromePosition palindrome:findReversePalindromePositions(entries.get(0).getContent(),MIN_LENGTH,MAX_LENGTH)) System.out.println(palindrome);
	}
}
