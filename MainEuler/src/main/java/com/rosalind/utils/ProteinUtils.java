package com.rosalind.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import com.rosalind.aminoacids.TranslatedCodon;
import com.rosalind.utils.FastaReader.FastaEntry;

public class ProteinUtils {
	private final static Predicate<TranslatedCodon> IS_START=new Predicate<TranslatedCodon>()	{
		@Override
		public boolean test(TranslatedCodon codon) {
			return codon.isStart();
		}
	};
	private final static Predicate<TranslatedCodon> IS_STOP=new Predicate<TranslatedCodon>()	{
		@Override
		public boolean test(TranslatedCodon codon) {
			return codon.isStop();
		}
	};
	
	public static List<String> getProteinCandidatesAsStrings(List<TranslatedCodon> codons)	{
		List<String> result=new ArrayList<>();
		for (List<TranslatedCodon> candidate:getProteinCandidatesAsCodons(codons)) result.add(TranslatedCodon.toString(candidate,true));
		return result;
	}
	
	public static List<List<TranslatedCodon>> getProteinCandidatesAsCodons(List<TranslatedCodon> codons)	{
		List<Integer> startPositions=lookFor(codons,IS_START);
		List<Integer> stopPositions=lookFor(codons,IS_STOP);
		if (startPositions.isEmpty()||stopPositions.isEmpty()) return Collections.emptyList();
		int lastStop=stopPositions.get(stopPositions.size()-1);
		List<List<TranslatedCodon>> result=new ArrayList<>();
		for (int start:startPositions)	{
			if (start>lastStop) break;
			for (int stop:stopPositions) if (stop>start)	{
				result.add(codons.subList(start,stop+1));
				break;
			}
		}
		return result;
	}
	
	private static List<Integer> lookFor(List<TranslatedCodon> codons,Predicate<TranslatedCodon> predicate)	{
		List<Integer> result=new ArrayList<>();
		for (int i=0;i<codons.size();++i) if (predicate.test(codons.get(i))) result.add(i);
		return result;
	}

	public static String getComplement(String in)	{
		StringBuilder result=new StringBuilder();
		for (char ch:in.toCharArray()) switch (ch)	{
			case 'a':case 'A':result.append('T');break;
			case 'c':case 'C':result.append('G');break;
			case 'g':case 'G':result.append('C');break;
			case 't':case 'T':result.append('A');break;
			default:throw new IllegalStateException();
		}
		return result.reverse().toString();
	}
	
	public static FastaEntry removeIntrons(FastaEntry origin,Collection<FastaEntry> introns)	{
		String str=origin.getContent();
		for (FastaEntry entry:introns) str=str.replaceFirst(entry.getContent(),"");
		return new FastaEntry(origin.getId(),str);
	}

	public static int computeHammingDistance(String s1,String s2)	{
		int N=s1.length();
		if (s2.length()!=N) throw new IllegalArgumentException();
		int dist=0;
		for (int i=0;i<N;++i) if (s1.charAt(i)!=s2.charAt(i)) ++dist;
		return dist;
	}
	
	public static int[][] computeLevenshteinArray(String s1,String s2)	{
		int N=s1.length();
		int M=s2.length();
		int[][] d=new int[N+1][M+1];
		for (int i=1;i<=N;++i) d[i][0]=i;
		for (int j=1;j<=M;++j) d[0][j]=j;
		for (int i=1;i<=N;++i) for (int j=1;j<=M;++j)	{
			int substitutionCost=(s1.charAt(i-1)==s2.charAt(j-1))?0:1;
			d[i][j]=Math.min(1+Math.min(d[i-1][j],d[i][j-1]),substitutionCost+d[i-1][j-1]);
		}
		return d;
	}
	
	public static int computeLevenshteinDistance(String s1,String s2)	{
		return computeLevenshteinArray(s1,s2)[s1.length()][s2.length()];
	}

	public static double getProbability(String baseString,double gcContent)	{
		double result=1.0;
		double probGc=gcContent/2.0;
		double probAt=0.5-probGc;
		for (char symbol:baseString.toCharArray()) switch (symbol)	{
			case 'A':case 'T':case 'U':result*=probAt;break;
			case 'C':case 'G':result*=probGc;break;
			default:throw new IllegalArgumentException();
		}
		return result;
	}
}
