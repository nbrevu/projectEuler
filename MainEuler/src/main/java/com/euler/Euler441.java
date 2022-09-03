package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler441 {
	private static class Term	{
		private final static Table<Long,Long,Term> KNOWN_TERMS=HashBasedTable.create();
		public final long p;
		public final long q;
		public final double value;
		private Term(long p,long q)	{
			this.p=p;
			this.q=q;
			value=1d/(p*q);
		}
		public static Term getTerm(long p,long q)	{
			return KNOWN_TERMS.row(p).computeIfAbsent(q,(Long unused)->new Term(p,q));
		}
		@Override
		public String toString()	{
			return String.format("1/(%d·%d)",p,q);
		}
	}
	
	private static List<Term> getTermsInR(int m)	{
		List<Term> result=new ArrayList<>();
		for (int p=1;p<m;++p) for (int q=Math.max(p+1,m-p);q<=m;++q) if (EulerUtils.areCoprime(p,q)) result.add(Term.getTerm(p,q));
		return result;
	}
	
	private static List<Term> subtract(List<Term> min,List<Term> sub)	{
		List<Term> result=new ArrayList<>(min);
		result.removeAll(sub);
		return result;
	}
	
	public static void main(String[] args)	{
		double result=0;
		Joiner joiner=Joiner.on('+');
		IntObjMap<List<Term>> allTerms=HashIntObjMaps.newMutableMap();
		int M=100;
		for (int m=2;m<=M;++m)	{
			List<Term> terms=getTermsInR(m);
			allTerms.put(m,terms);
			for (Term t:terms) result+=t.value;
		}
		for (int m=2;m<M;++m)	{
			List<Term> nextList=allTerms.get(m+1);
			List<Term> thisList=allTerms.get(m);
			List<Term> addedTerms=subtract(nextList,thisList);
			List<Term> removedTerms=subtract(thisList,nextList);
			System.out.println(String.format("Moving from m=%d to m=%d:",m,m+1));
			System.out.println(String.format("\tAdded terms: %s.",addedTerms.isEmpty()?"<none>":joiner.join(addedTerms)));
			System.out.println(String.format("\tRemoved terms: %s.",removedTerms.isEmpty()?"<none>":joiner.join(removedTerms)));
		}
		System.out.println(result);
	}
}
