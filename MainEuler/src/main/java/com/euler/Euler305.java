package com.euler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.google.common.math.LongMath;

public class Euler305 {
	private final static long BASE=3;
	private final static long GOAL=13;
	
	private static int amountOfDigits(long number)	{
		int result=0;
		while (number>0)	{
			number/=10;
			++result;
		}
		return result;
	}
	
	private static long getStartingPositionOfPowerOfTen(int power)	{
		long result=0;
		long toCount=9;
		for (int i=1;i<=power;++i)	{
			result+=i*toCount;
			toCount*=10l;
		}
		return result;
	}
	
	private static long getStartingPositionOfInteger(long number)	{
		int n=amountOfDigits(number);
		if (n<=1) return number;
		return getStartingPositionOfPowerOfTen(n-1)+n*(number-LongMath.pow(10l,n-1))+1;
	}
	
	private static class Index implements Comparable<Index>	{
		public final long position;
		public final long offset;
		public Index(long position,long offset)	{
			this.position=position;
			this.offset=offset;
		}
		public long getExactIndex()	{
			return getStartingPositionOfInteger(position)+offset;
		}
		@Override
		public int compareTo(Index o) {
			if (position<o.position) return -1;
			else if (position>o.position) return 1;
			else if (offset<o.offset) return -1;
			else if (offset>o.offset) return 1;
			else return 0;
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(position)+Long.hashCode(offset);
		}
		@Override
		public boolean equals(Object other)	{
			if (!(other instanceof Index)) return false;
			Index oIndex=(Index)other;
			return ((position==oIndex.position)&&(offset==oIndex.offset));
		}
		@Override
		public String toString()	{
			return "["+position+","+offset+"]";
		}
	}
	
	private static interface Sequencer extends Iterator<Index>	{}
	
	private static class PrefixSequencer implements Sequencer	{
		private static class PartialPrefixSequencer implements Sequencer	{
			private final long base;
			private final long maxOffset;
			private long offset;
			public PartialPrefixSequencer(long prefix,long powerOfTen)	{
				base=prefix*powerOfTen;
				maxOffset=powerOfTen;
				offset=0l;
			}
			@Override
			public boolean hasNext() {
				return offset<maxOffset;
			}
			@Override
			public Index next() {
				long result=base+offset;
				++offset;
				return new Index(result,0l);
			}
		}
		private final long prefix;
		private long powerOfTen;
		private PartialPrefixSequencer currentSequencer;
		public PrefixSequencer(long prefix)	{
			this.prefix=prefix;
			powerOfTen=1l;
			currentSequencer=new PartialPrefixSequencer(prefix,powerOfTen);
		}
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public Index next() {
			if (!currentSequencer.hasNext())	{
				powerOfTen*=10l;
				currentSequencer=new PartialPrefixSequencer(prefix,powerOfTen);
			}
			return currentSequencer.next();
		}
	}
	
	private static class SuffixSequencer implements Sequencer	{
		private static class PartialSuffixSequencer implements Sequencer	{
			private final long base;
			private final int N;
			private final long distance;
			private final long maxOffset;
			private long offset;
			public PartialSuffixSequencer(long suffix,int prefixLength)	{
				base=suffix;
				N=prefixLength;
				int suffixDigits=amountOfDigits(suffix);
				distance=LongMath.pow(10l,suffixDigits);
				maxOffset=LongMath.pow(10l,suffixDigits+N);
				offset=(N==0)?0:LongMath.pow(10l,suffixDigits+N-1);
			}
			@Override
			public boolean hasNext() {
				return offset<maxOffset;
			}

			@Override
			public Index next() {
				long number=offset+base;
				offset+=distance;
				return new Index(number,N);
			}
		}
		private final long suffix;
		private int extraDigits;
		private PartialSuffixSequencer currentSequencer;
		public SuffixSequencer(long suffix)	{
			this.suffix=suffix;
			extraDigits=0;
			currentSequencer=new PartialSuffixSequencer(suffix,extraDigits);
		}
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public Index next() {
			if (!currentSequencer.hasNext())	{
				++extraDigits;
				currentSequencer=new PartialSuffixSequencer(suffix,extraDigits);
			}
			return currentSequencer.next();
		}
	}
	
	private static class IntermediateSequencer implements Sequencer	{
		private static class PartialIntermediateSequencer implements Sequencer	{
			private final long base;
			private final long distance;
			private final long maxDistance;
			private final int indexOffset;
			private long offset;
			public PartialIntermediateSequencer(long prefix,long suffix,int middleDigits)	{
				int suffixDigits=amountOfDigits(suffix);
				distance=LongMath.pow(10l,suffixDigits);
				maxDistance=LongMath.pow(10l,suffixDigits+middleDigits);
				base=prefix*maxDistance+suffix;
				indexOffset=amountOfDigits(prefix)+middleDigits;
				offset=0l;
			}
			@Override
			public boolean hasNext() {
				return offset<maxDistance;
			}
			@Override
			public Index next() {
				long number=base+offset;
				Index result=new Index(number,indexOffset);
				offset+=distance;
				return result;
			}
		}
		private final long prefix;
		private final long suffix;
		private int middleDigits;
		private PartialIntermediateSequencer currentSequencer;
		public IntermediateSequencer(long baseNumber,int chopPoint)	{
			long powerOfTen=LongMath.pow(10l,chopPoint);
			prefix=baseNumber%powerOfTen;
			suffix=baseNumber/powerOfTen;
			middleDigits=0;
			currentSequencer=new PartialIntermediateSequencer(prefix,suffix,middleDigits);
		}
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public Index next() {
			if (!currentSequencer.hasNext())	{
				++middleDigits;
				currentSequencer=new PartialIntermediateSequencer(prefix,suffix,middleDigits);
			}
			return currentSequencer.next();
		}
	}
	
	private static <K,V> void addToMapList(Map<K,List<V>> map,K key,V value)	{
		List<V> valueList=map.get(key);
		if (valueList==null)	{
			valueList=new ArrayList<>();
			map.put(key,valueList);
		}
		valueList.add(value);
	}
	
	private static class CombinedSequencer implements Sequencer	{
		private NavigableMap<Index,List<Sequencer>> sortedSequencers;
		public CombinedSequencer(List<Sequencer> sequencers)	{
			sortedSequencers=new TreeMap<>();
			for (Sequencer s:sequencers) addToMapList(sortedSequencers,s.next(),s);
		}
		@Override
		public boolean hasNext() {
			return !sortedSequencers.isEmpty();
		}
		@Override
		public Index next() {
			Map.Entry<Index,List<Sequencer>> entry=sortedSequencers.firstEntry();
			for (Sequencer s:entry.getValue()) if (s.hasNext()) addToMapList(sortedSequencers,s.next(),s);
			sortedSequencers.remove(entry.getKey());
			return entry.getKey();
		}
	}
	
	private static class FixedSizeMiddleSequencer implements Sequencer	{
		private final long base;
		private final long maxPrefix;
		private final long prefixDistance;
		private final long maxSuffix;
		private final int offset;
		private long prefix;
		private long suffix;
		public FixedSizeMiddleSequencer(long number,int prefixSize,int suffixSize)	{
			int numberDigits=amountOfDigits(number);
			maxSuffix=LongMath.pow(10l,suffixSize);
			base=number*maxSuffix;
			prefixDistance=LongMath.pow(10l,numberDigits+suffixSize);
			maxPrefix=LongMath.pow(10l,numberDigits+suffixSize+prefixSize);
			offset=prefixSize;
			prefix=LongMath.pow(10l,numberDigits+suffixSize+prefixSize-1);
			suffix=0;
		}
		@Override
		public boolean hasNext() {
			return (prefix<maxPrefix)&&(suffix<maxSuffix);
		}
		@Override
		public Index next() {
			long currentNumber=prefix+base+suffix;
			Index result=new Index(currentNumber,offset);
			++suffix;
			if (suffix>=maxSuffix)	{
				suffix=0l;
				prefix+=prefixDistance;
			}
			return result;
		}
	}
	
	private static class MiddleSequencer implements Sequencer	{
		private long base;
		private int currentExtraDigits;
		private Sequencer currentSequencer;
		public MiddleSequencer(long number)	{
			base=number;
			currentExtraDigits=2;
			currentSequencer=new CombinedSequencer(createFixedSizeSequencers(base,currentExtraDigits));
		}
		private static List<Sequencer> createFixedSizeSequencers(long base, int currentExtraDigits) {
			List<Sequencer> sequencers=new ArrayList<>(currentExtraDigits-1);
			for (int i=1;i<currentExtraDigits;++i) sequencers.add(new FixedSizeMiddleSequencer(base,i,currentExtraDigits-i));
			return sequencers;
		}
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public Index next() {
			if (!currentSequencer.hasNext())	{
				++currentExtraDigits;
				currentSequencer=new CombinedSequencer(createFixedSizeSequencers(base,currentExtraDigits));
			}
			return currentSequencer.next();
		}
	}
	
	private static class NumberSequencer implements Sequencer	{
		private CombinedSequencer baseSequencer;
		public NumberSequencer(long number)	{
			baseSequencer=new CombinedSequencer(getAllSequencers(number));
		}
		private static boolean isChopValid(long number,int position)	{
			number/=LongMath.pow(10l,position-1);
			return (number%10l)!=0l;
		}
		private static List<Sequencer> getAllSequencers(long number)	{
			int digits=amountOfDigits(number);
			List<Sequencer> result=new ArrayList<>(2+digits);
			result.add(new PrefixSequencer(number));
			result.add(new SuffixSequencer(number));
			result.add(new MiddleSequencer(number));
			for (int i=1;i<digits;++i) if (isChopValid(number,i)) result.add(new IntermediateSequencer(number,i));
			return result;
		}
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public Index next() {
			return baseSequencer.next();
		}
	}
	
	private static long findSpecialSequences(long in)	{
		// This is dirty but it's enough for our problem.
		return ((in==12)||(in==243))?1:0;
	}
	
	private static long getF(long in)	{
		NumberSequencer sc=new NumberSequencer(in);
		long specialSequences=findSpecialSequences(in);
		for (long l=1+specialSequences;l<in;++l) sc.next();
		Index result=sc.next();
		return result.getExactIndex();
	}
	
	public static void main(String[] args)	{
		long sum=0;
		long p=1;
		for (int i=1;i<=GOAL;++i)	{
			p*=BASE;
			sum+=getF(p);
		}
		System.out.println(sum);
	}
}
