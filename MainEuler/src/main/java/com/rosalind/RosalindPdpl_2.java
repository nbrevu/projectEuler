package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.rosalind.utils.IoUtils;

public class RosalindPdpl_2 {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_pdpl.txt";
	
	private static <T extends Collection<Integer>> T addAll(int[] diffs,T coll)	{
		for (int d:diffs) coll.add(d);
		return coll;
	}
	
	private static Multiset<Integer> getDifferences(Set<Integer> baseSet,int newElement)	{
		Multiset<Integer> result=TreeMultiset.create();
		for (int n:baseSet) result.add(Math.abs(n-newElement));
		return result;
	}
	
	private static SortedSet<Integer> getInitialSetRecursive(SortedSet<Integer> currentResult,Multiset<Integer> remainingDiffs,NavigableSet<Integer> candidates)	{
		if (remainingDiffs.isEmpty()) return currentResult;
		for (Integer cand:candidates)	{
			Multiset<Integer> differences=getDifferences(currentResult,cand);
			if (remainingDiffs.containsAll(differences))	{
				SortedSet<Integer> newResult=new TreeSet<>(currentResult);
				newResult.add(cand);
				Multiset<Integer> newDiffMultiset=TreeMultiset.create(remainingDiffs);
				EulerUtils.removeCarefully(newDiffMultiset,differences);
				for (Integer d:differences) newDiffMultiset.remove(d);
				NavigableSet<Integer> newCandidates=candidates.tailSet(cand,true);
				SortedSet<Integer> result=getInitialSetRecursive(newResult,newDiffMultiset,newCandidates);
				if (result!=null) return result;
			}
		}
		return null;
	}
	
	private static SortedSet<Integer> getInitialSet(int[] diffs)	{
		NavigableSet<Integer> candidates=addAll(diffs,new TreeSet<>());
		Multiset<Integer> diffMultiset=addAll(diffs,TreeMultiset.create());
		int lastValue=candidates.last();
		diffMultiset.remove(lastValue);
		SortedSet<Integer> initialSet=ImmutableSortedSet.of(0,lastValue);
		return getInitialSetRecursive(initialSet,diffMultiset,candidates);
	}
	
	private static int[] toArray(SortedSet<Integer> set)	{
		int[] result=new int[set.size()];
		int index=0;
		for (int n:set)	{
			result[index]=n;
			++index;
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		int[] diffs;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			diffs=IoUtils.parseStringAsArrayOfInts(reader.readLine(),-1);
		}
		SortedSet<Integer> result=getInitialSet(diffs);
		int[] resArray=toArray(result);
		System.out.println(IoUtils.toStringWithSpaces(resArray));
	}
}
