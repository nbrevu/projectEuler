package com.other.layton.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class BreadthSearch<T,U extends ProblemStatus<T,U>> {
	private Predicate<SearchPath<T,U>> isFinal=new Predicate<SearchPath<T,U>>()	{
		@Override
		public boolean apply(SearchPath<T,U> path)	{
			U status=path.getCurrentStatus();
			return status.isFinal();
		}
	};
	
	private Set<U> alreadyVisited;
	private List<SearchPath<T,U>> currentPaths;
	
	public BreadthSearch(U initial)	{
		alreadyVisited=new HashSet<>();
		alreadyVisited.add(initial);
		currentPaths=Arrays.asList(new SearchPath<>(initial));
	}
	
	public BreadthSearch(Collection<U> initials)	{
		alreadyVisited=new HashSet<>(initials);
		currentPaths=new ArrayList<>();
		for (U initial:initials) currentPaths.add(new SearchPath<>(initial));
	}
	
	public Collection<SearchPath<T,U>> solve()	{
		for (;;)	{
			if (currentPaths.isEmpty()) return Collections.emptyList();	// No solution!
			else	{
				Collection<SearchPath<T,U>> result=Collections2.filter(currentPaths,isFinal);
				if (!result.isEmpty()) return result;
				else currentPaths=nextGeneration();
			}
		}
	}
	
	private List<SearchPath<T,U>> nextGeneration()	{
		List<SearchPath<T,U>> result=new ArrayList<>();
		for (SearchPath<T,U> path:currentPaths)	{
			U currentStatus=path.getCurrentStatus();
			for (T move:currentStatus.availableMoves())	{
				SearchPath<T,U> newPath=path.move(move);
				U newStatus=newPath.getCurrentStatus();
				if (alreadyVisited.add(newStatus)) result.add(newPath);
			}
		}
		return result;
	}
}
