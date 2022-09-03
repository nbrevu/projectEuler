package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.euler.common.CombinationIterator;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.google.common.math.IntMath;
import com.rosalind.utils.IoUtils;

public class RosalindPdpl {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_pdpl.txt";
	
	private static List<Integer> getCandidates(int[] diffs)	{
		NavigableSet<Integer> candidates=new TreeSet<>();
		for (int i=0;i<diffs.length;++i) candidates.add(diffs[i]);
		return new ArrayList<>(candidates);
	}
	
	private static int calculateN(int[] diffs)	{
		int N=diffs.length;
		// n*(n-1)/2 = N
		// n=1+floor(sqrt(2*N))
		int n=1+IntMath.sqrt(2*N,RoundingMode.DOWN);
		assert (n*(n-1))/2==N;
		System.out.println(n);
		return n;
	}
	
	private static class CandidateGenerator	{
		private final int N;
		private final List<Integer> candidates;
		private final Multiset<Integer> diffMultiset;
		public CandidateGenerator(int[] diffs)	{
			N=calculateN(diffs);
			candidates=getCandidates(diffs);
			diffMultiset=getMultiset(diffs);
		}
		private static Multiset<Integer> getMultiset(int[] diffs)	{
			Multiset<Integer> result=TreeMultiset.create();
			for (int d:diffs) result.add(d);
			return result;
		}
		public int[] getInitialSet()	{
			int[] result=new int[N];
			int lastValue=candidates.get(candidates.size()-1);
			result[0]=0;
			result[N-1]=lastValue;
			for (int[] partialCandidate:new CombinationIterator(N-2,candidates.size(),true))	{
				for (int i=0;i<N-2;++i) result[i+1]=candidates.get(partialCandidate[i]);
				if (isCorrect(result)) return result;
			}
			throw new RuntimeException();
		}
		private boolean isCorrect(int[] candidate)	{
			Multiset<Integer> result=TreeMultiset.create();
			int N=candidate.length;
			for (int i=0;i<N-1;++i) for (int j=i+1;j<N;++j) result.add(candidate[j]-candidate[i]);
			return result.equals(diffMultiset);
		}
	}
	
	public static void main(String[] args) throws IOException	{
		int[] diffs;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			diffs=IoUtils.parseStringAsArrayOfInts(reader.readLine(),-1);
		}
		CandidateGenerator gen=new CandidateGenerator(diffs);
		int[] result=gen.getInitialSet();
		System.out.println(IoUtils.toStringWithSpaces(result));
	}
}
