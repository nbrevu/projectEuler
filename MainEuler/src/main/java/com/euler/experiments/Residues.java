package com.euler.experiments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.euler.common.Primes;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Residues {
	private static enum PairStatus	{
		NOT_PRESENT,ONE_PRESENT,BOTH_PRESENT;
	}
	
	private static class ElementPair implements Comparable<ElementPair>	{
		private final static String RESIDUE_STRING=" (residue)";
		private final static String NON_RESIDUE_STRING=" (non residue)";
		private static String getResidueString(boolean isResidue)	{
			return isResidue?RESIDUE_STRING:NON_RESIDUE_STRING;
		}
		private static long getOrder(long e,long p)	{
			long result=1;
			long x=e;
			while (x!=1)	{
				x=(x*e)%p;
				++result;
			}
			return result;
		}
		public final long e1;
		public final long e2;
		private final long order1;
		private final long order2;
		private boolean isResidue1;
		private boolean isResidue2;
		public ElementPair(long e1,long e2,long p)	{
			this.e1=e1;
			this.e2=e2;
			this.order1=getOrder(e1,p);
			this.order2=getOrder(e2,p);
		}
		public void setResidue1()	{
			if (isResidue1) throw new IllegalStateException();
			isResidue1=true;
		}
		public void setResidue2()	{
			if (isResidue2) throw new IllegalStateException();
			isResidue2=true;
		}
		public void setResidue(long e)	{
			if (e==e1) setResidue1();
			else if (e==e2) setResidue2();
			else throw new IllegalArgumentException();
		}
		public PairStatus getPairStatus()	{
			if ((!isResidue1)&&(!isResidue2)) return PairStatus.NOT_PRESENT;
			else if (isResidue1&&isResidue2) return PairStatus.BOTH_PRESENT;
			else return PairStatus.ONE_PRESENT;
		}
		@Override
		public String toString()	{
			StringBuilder result=new StringBuilder();
			result.append('<');
			result.append(e1).append(getResidueString(isResidue1)).append(", order=").append(order1);
			result.append("; ");
			result.append(e2).append(getResidueString(isResidue2)).append(", order=").append(order2);
			result.append('>');
			return result.toString();
		}
		@Override
		public int compareTo(ElementPair o) {
			return Long.compare(e1,o.e1);
		}
	}
	
	public static void main(String[] args)	{
		long[] primes=Primes.listLongPrimesAsArraySkippingFirstOnes(1000,1);
		for (long p:primes)	{
			long maxBase=(p-1)/2;
			LongObjMap<ElementPair> elementToPairMap=HashLongObjMaps.newMutableMap();
			List<ElementPair> pairs=new ArrayList<>((int)maxBase);
			for (long i=1;i<=maxBase;++i)	{
				ElementPair pair=new ElementPair(i,p-i,p);
				elementToPairMap.put(i,pair);
				elementToPairMap.put(p-i,pair);
				pairs.add(pair);
			}
			for (long i=1;i<=maxBase;++i)	{
				long i2=(i*i)%p;
				elementToPairMap.get(i2).setResidue(i2);
			}
			Multimap<PairStatus,ElementPair> pairsByType=MultimapBuilder.enumKeys(PairStatus.class).treeSetValues().build();
			pairsByType.putAll(Multimaps.index(pairs,ElementPair::getPairStatus));
			System.out.println(String.format("p=%d:",p));
			for (Map.Entry<PairStatus,Collection<ElementPair>> entry:pairsByType.asMap().entrySet())	{
				System.out.println(String.format("\t%s:",entry.getKey()));
				for (ElementPair pair:entry.getValue()) System.out.println(String.format("\t\t%s.",pair.toString()));
			}
		}
	}
}
