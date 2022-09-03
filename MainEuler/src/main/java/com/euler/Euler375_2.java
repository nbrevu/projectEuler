package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;

public class Euler375_2 {
	private final static long N=2*LongMath.pow(10l,9);
	
	/*
	 * S_1=S_6308949=629527.
	 * El ciclo es puro (sin prefijos) y contiene 6308948 elementos.
	 * 
	 * Se me ocurre una especie de estructura de árbol en la que los mínimos estén señalados.
	 * A lo mejor se puede almacenar, para cada número X, la posición del siguiente valor menor que X. Esto puede servir para calcular
	 * la cantidad de A(i,j) que son iguales a cada valor. Por lo pronto, A(i,i)=S_i así que cada valor se cuenta al menos una vez.
	 * 
	 * Por otro lado, 2e9=6308948*317 + 63484 -> 317 y pico ciclos.
	 * 
	 * "Minimum value: 3, found at 2633997", so the vast majority of the A(i,j) are equal to 3.
	 * In particular, if j>=i+6308948, A(i,j)=3, no question.
	 */
	private static class PseudoRandomGenerator	{
		private final static long MOD=50515093l;
		private final static long INITIAL=290797l;
		private long s;
		public PseudoRandomGenerator()	{
			s=INITIAL;
		}
		public long next()	{
			s=(s*s)%MOD;
			return s;
		}
	}
	
	private static class NumberAndReference	{
		public final long number;
		public int nextMinimum;
		public NumberAndReference(long number)	{
			this.number=number;
			nextMinimum=-1;
		}
	}
	
	private static class SequenceSummary	{
		public final List<NumberAndReference> numbers;
		public final int size;
		public final long minimum;
		public SequenceSummary(List<NumberAndReference> numbers,long minimum)	{
			this.numbers=numbers;
			size=numbers.size()-1;
			this.minimum=minimum;
		}
	}
	
	private static SequenceSummary getSequenceAnalysis()	{
		LongIntMap appearances=HashLongIntMaps.newMutableMap();
		List<NumberAndReference> numbers=new ArrayList<>();
		NavigableMap<Long,Integer> pendingNumbers=new TreeMap<>();
		numbers.add(null);	// Position 0.
		PseudoRandomGenerator generator=new PseudoRandomGenerator();
		for (int i=1;;++i)	{
			long s=generator.next();
			int pos=appearances.getOrDefault(s,-1);
			if (pos==-1)	{
				numbers.add(new NumberAndReference(s));
				appearances.put(s,i);
				Map<Long,Integer> toUpdate=pendingNumbers.tailMap(s);
				for (int oldPos:toUpdate.values()) numbers.get(oldPos).nextMinimum=i-oldPos;
				toUpdate.clear();
				pendingNumbers.put(s,i);
			}	else break;
		}
		int cycleLength=numbers.size()-1;
		for (int i=1;;++i)	{
			long s=numbers.get(i).number;
			Map<Long,Integer> toUpdate=pendingNumbers.tailMap(s,false);
			if (!toUpdate.isEmpty())	{
				int pos=i+cycleLength;
				for (int oldPos:toUpdate.values()) numbers.get(oldPos).nextMinimum=pos-oldPos;
				toUpdate.clear();
			}
			if (pendingNumbers.size()<=1) break;
		}
		Map.Entry<Long,Integer> minimumEntry=pendingNumbers.firstEntry();	// First and only!
		return new SequenceSummary(numbers,minimumEntry.getKey());
	}
	
	private static class CyclePrefixInfo	{
		public final List<NumberAndReference> cycles;
		public final long totalSum;
		public final int totalCount;
		public CyclePrefixInfo(List<NumberAndReference> cycles)	{
			this.cycles=cycles;
			long sum=0;
			int count=0;
			for (NumberAndReference portion:cycles)	{
				sum+=portion.number*portion.nextMinimum;
				count+=portion.nextMinimum;
			}
			totalSum=sum;
			totalCount=count;
		}
		public long getPartialCount(int size)	{
			int remaining=size;
			long result=0;
			for (NumberAndReference portion:cycles)	{
				int toCount=Math.min(remaining,portion.nextMinimum);
				result+=toCount*portion.number;
				remaining-=toCount;
				if (remaining<=0) break;
			}
			return result;
		}
	}
	
	private static CyclePrefixInfo extractCyclePrefix(SequenceSummary sequence,int index)	{
		int currentIndex=index;
		List<NumberAndReference> result=new ArrayList<>();
		for (;;)	{
			NumberAndReference currentValue=sequence.numbers.get(currentIndex);
			if (currentValue.number==sequence.minimum) return new CyclePrefixInfo(result);
			result.add(currentValue);
			currentIndex+=currentValue.nextMinimum;
			if (currentIndex>sequence.size) currentIndex-=sequence.size;
		}
	}
	
	private static long sumMinimaStartingWith(SequenceSummary sequence,int index,long maxIndex)	{
		CyclePrefixInfo info=extractCyclePrefix(sequence,index);
		long size=maxIndex-index+1;
		long result=0;
		/*
		 * There is a better way of doing this, but hey, this worked. My old approach didn't properly calculate the amount of times the
		 * minimum must be added, I think.
		 */
		while (size>0)	{
			if (size>=info.totalCount) result+=info.totalSum+(size-info.totalCount)*sequence.minimum;
			else result+=info.getPartialCount((int)size);
			size-=sequence.size;
		}
		return result;
		/*-
		CyclePrefixInfo info=extractCyclePrefix(sequence,index);
		long sizeToCalculate=maxIndex-index+1;
		long fullCycles=sizeToCalculate/sequence.size;
		long lastCyclePrefix=sizeToCalculate%sequence.size;
		if (lastCyclePrefix>=info.totalCount)	{
			++fullCycles;
			long totalSum=info.totalSum*fullCycles;
			long totalCount=info.totalCount*fullCycles;
			long minima=sizeToCalculate-totalCount;
			return minima*sequence.minimum+totalSum;
		}	else	{
			long totalSum=info.totalSum*fullCycles;
			long totalCount=info.totalCount*fullCycles;
			totalSum+=info.getPartialCount((int)lastCyclePrefix);
			totalCount+=lastCyclePrefix;
			long minima=sizeToCalculate-totalCount;
			return minima*sequence.minimum+totalSum;
		}
		*/
	}
	
	/*
	 * What a curious comment I found on the problem thread...
	 * 
	 * "This answer shares a common 4 last digits (6168) with the unmoded answer to Problem 325 and the 5th digis is only one off. I looked just at those digits and tried to figure out how I had run the wrong code."
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SequenceSummary sequence=getSequenceAnalysis();
		long result=0;
		int size=sequence.numbers.size()-1;
		for (int i=1;i<=size;++i) result+=sumMinimaStartingWith(sequence,i,N);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
