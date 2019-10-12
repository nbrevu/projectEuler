package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.euler.common.Timing;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler122 {
	private final static int N=200;
	
	private static long solve()	{
		List<int[]> list1=new ArrayList<>();
		List<int[]> list2=new ArrayList<>();
		int[] initial=new int[] {1};
		list1.add(initial);
		boolean[] found=new boolean[1+N];
		int pending=N-1;
		long result=0;
		int depth=0;
		while (pending>0)	{
			++depth;
			IntSet recentlyAdded=HashIntSets.newMutableSet();
			for (int[] studySet:list1)	{
				int l=studySet.length;
				for (int i=0;i<l;++i) for (int j=i;j<l;++j)	{
					int newVal=studySet[i]+studySet[j];
					if ((newVal>N)||(found[newVal]&&!recentlyAdded.contains(newVal))) continue;
					if (!found[newVal])	{
						result+=depth;
						found[newVal]=true;
						--pending;
						recentlyAdded.add(newVal);
					}
					int[] newSet=Arrays.copyOf(studySet,l+1);
					newSet[l]=newVal;
					list2.add(newSet);
				}
			}
			list1=list2;
			list2=new ArrayList<>();
		}
		return result;
		/*
		while (!searchedFor.empty())	{
			++depth;
			typename list<set<T> >::const_iterator le=l1->end();
			for (typename list<set<T> >::const_iterator it=l1->begin();it!=le;++it)	{
				const set<T> &current=*it;
				typename set<T>::const_iterator e=current.end();
				T lastVal=*current.rbegin();
				for (typename set<T>::const_iterator it2=current.begin();it2!=e;++it2)	{
					T newVal=lastVal+(*it2);
					if (res.count(newVal)==1)	{
						if (res[newVal]<depth) continue;
					}	else if (newVal>in) continue;
					set<T> newSet=current;
					newSet.insert(newVal);
					l2->push_back(newSet);
					res[newVal]=depth;
					searchedFor.erase(newVal);
				}
			}
			swap(l1,l2);
			l2->clear();
		}
		 */
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler122::solve);
	}
}
