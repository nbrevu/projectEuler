package com.euler;

import java.math.RoundingMode;
import java.util.NavigableSet;

import com.euler.common.PythagoreanTriples;
import com.google.common.collect.TreeMultimap;
import com.google.common.math.LongMath;

public class Euler583 {
	private final static long LIMIT=100000000;
	
	public static void main(String[] args)	{
		TreeMultimap<Long,Long> triples=PythagoreanTriples.getExtendedSetOfTriples(LIMIT/2);
		long sum=0;
		int count=0;
		long maxPerimeter=0;
		for (long ae_2:triples.keySet())	{
			if (ae_2>LIMIT/4) break;
			long ae=2*ae_2;
			NavigableSet<Long> collection=triples.get(ae_2);
			for (long flapHeight:collection)	{
				NavigableSet<Long> candidates=collection.tailSet(2*flapHeight,false);
				for (long fullHeight:candidates)	{
					long ab=fullHeight-flapHeight;
					if (PythagoreanTriples.isPythagoreanTriple(ae,ab))	{
						long bc=LongMath.sqrt((ae_2*ae_2)+(flapHeight*flapHeight),RoundingMode.UNNECESSARY);
						long perimeter=2*(bc+ab)+ae;
						if (perimeter<LIMIT)	{
							if (perimeter>=maxPerimeter)	{
								System.out.println("AB = DE = "+ab+", AE = "+ae+ ", BC = CD = "+bc+", flap height = "+flapHeight+", perimeter = "+perimeter);
								maxPerimeter=perimeter;
							}
							++count;
							sum+=perimeter;
						}
					}
				}
			}
		}
		System.out.println(sum);
		System.out.println("Found "+count+" possible envelopes.");
	}
}
