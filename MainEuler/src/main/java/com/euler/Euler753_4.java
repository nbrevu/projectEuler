package com.euler;

import com.euler.common.Primes;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongObjCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler753_4 {
	private static class ResidueSummary	{
		public final int amountOfResidues;
		public final boolean is2Residue;
		public final int amountOfSums;
		public final int appearancesOfEachResidue;
		public ResidueSummary(int amountOfResidues,boolean is2Residue,int amountOfSums,int appearancesOfEachResidue)	{
			this.amountOfResidues=amountOfResidues;
			this.is2Residue=is2Residue;
			this.amountOfSums=amountOfSums;
			this.appearancesOfEachResidue=appearancesOfEachResidue;
		}
	}
	
	private static class ResidueFinder	{
		private final long[] cubes;
		public ResidueFinder(int limit)	{
			cubes=new long[limit];
			for (int i=1;i<limit;++i) cubes[i]=i*(long)i*i;
		}
		private static int summariseAppearances(LongObjMap<LongSet> appearances)	{
			if (appearances.isEmpty()) return 0;
			LongObjCursor<LongSet> cursor=appearances.cursor();
			cursor.moveNext();
			int result=cursor.value().size();
			for (;cursor.moveNext();) if (result!=cursor.value().size()) throw new RuntimeException("I wasn't ready for this.");
			return result;
		}
		public ResidueSummary studyCase(long p)	{
			LongIntMap residues=HashLongIntMaps.newMutableMap();
			for (int i=1;i<p;++i) residues.addValue(cubes[i]%p,1);
			for (LongIntCursor cursor=residues.cursor();cursor.moveNext();) if (cursor.value()!=3) throw new RuntimeException("I didn't expect the Spanish Inquisition.");
			int sums=0;
			LongObjMap<LongSet> matches=HashLongObjMaps.newMutableMap();
			for (int i=1;i<p;++i) for (int j=1;j<p;++j) if (residues.containsKey((cubes[i]+cubes[j])%p))	{
				sums+=3;
				matches.computeIfAbsent(cubes[i]%p,(long unused)->HashLongSets.newMutableSet()).add(cubes[j]%p);
				matches.computeIfAbsent(cubes[j]%p,(long unused)->HashLongSets.newMutableSet()).add(cubes[i]%p);
			}
			return new ResidueSummary(residues.size(),residues.containsKey(2l),sums,summariseAppearances(matches));
		}
	}
	
	public static void main(String[] args)	{
		/*
		 * Having confirmed some hypotheses, I can do this in QUADRATIC time. Which is slowwwwww, but doable in a matter of hours.
		 * The amount of residues has a very obvious formula, (p-1)/3. Each residue appears exactly 3 times.
		 * For the appearances of each residue, we can use this trick: it's the same for every number, which means that in particular is true for
		 * k=1, which is a residue that obviously will always appear. So I can just store the bits in a bitset and then count the amount of cases
		 * where both bits X and X+1 are present. ENDUT, HOCH HECH!
		 */
		int limit=1000;
		ResidueFinder finder=new ResidueFinder(limit);
		for (long p:Primes.listLongPrimesAsArray(limit)) if ((p%3)==1)	{
			ResidueSummary summary=finder.studyCase(p);
			long expected=27*(summary.amountOfResidues*summary.appearancesOfEachResidue);
			System.out.println(String.format("p=%d.",p));
			System.out.println(String.format("\tThere are %d different residues (expected: %d).",summary.amountOfResidues,(p-1)/3));
			System.out.println(String.format("\t2 is%sa residue.",summary.is2Residue?" ":" not "));
			System.out.println(String.format("\tEach residue appears %d times (expected %s, is %s).",summary.appearancesOfEachResidue,summary.is2Residue?"odd":"even",((summary.appearancesOfEachResidue%2)==1)?"odd":"even"));
			System.out.println(String.format("\tTotal sums found: %d (expected=%d).",summary.amountOfSums,expected));
		}
	}
}
