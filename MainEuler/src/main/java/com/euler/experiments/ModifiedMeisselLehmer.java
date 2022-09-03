package com.euler.experiments;

import java.util.BitSet;

import com.euler.common.BitSetCursor;
import com.euler.common.EulerUtils;
import com.koloboke.collect.IntCursor;

// I'm not sure if I will finish this. But at the very least I'm going to start it. Let's see how far I get before finding a roadblock.
public class ModifiedMeisselLehmer {
	private final int K;
	private final BitSet residueClasses;
	/*
	 * This array is such that if element [a][b] is c, then multiplying the residue class a times b gets the residue class c.
	 * Values non coprime with K will be left to zero. This array is used to model transitions between values after multiplication by some prime.
	 */
	private final int[][] residueTransitions;
	
	/*
	 * Next stop: the precalculated values. How many? Up to how much? Only pi, or also phi? Does Meissel-Lehmer do *exactly* the same?
	 */
	
	public ModifiedMeisselLehmer(int K)	{
		this.K=K;
		residueClasses=new BitSet(K);
		residueTransitions=new int[K][];
		for (int i=1;i<K;++i) if (EulerUtils.areCoprime(i,K))	{
			residueClasses.set(i);
			residueTransitions[i]=new int[K];
		}
		for (IntCursor c1=new BitSetCursor(residueClasses,true);c1.moveNext();) for (IntCursor c2=new BitSetCursor(residueClasses,true);c2.moveNext();) residueTransitions[c1.elem()][c2.elem()]=(c1.elem()*c2.elem())%K;
	}
}
