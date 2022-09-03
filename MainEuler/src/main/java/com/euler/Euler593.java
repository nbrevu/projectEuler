package com.euler;

import static com.euler.common.EulerUtils.expMod;
import static com.google.common.math.IntMath.divide;
import static java.math.RoundingMode.FLOOR;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.euler.common.Primes;

public class Euler593 {
	private final static int AMOUNT_OF_PRIMES=10000000;
	private final static long PRIME_BOUND=20l*AMOUNT_OF_PRIMES;
	private final static int MOD=10007;
	private final static int Q=10000;
	
	private static long[] getPrimes()	{
		List<Long> primes=Primes.listLongPrimes(PRIME_BOUND);
		if (primes.size()<AMOUNT_OF_PRIMES) throw new NoSuchElementException("Not enough primes!");
		return makeArray(primes,AMOUNT_OF_PRIMES);
	}
	
	private static long[] makeArray(List<Long> primes,int howMany)	{
		long[] result=new long[howMany];
		int i=0;
		for (long p:primes)	{
			result[i]=p;
			++i;
			if (i>=AMOUNT_OF_PRIMES) break;
		}
		return result;
	}
	
	private static int sum(int[] array)	{
		int result=0;
		for (int c:array) result+=c;
		return result;
	}
	
	private static int[] getS()	{
		long[] primes=getPrimes();
		int[] result=new int[AMOUNT_OF_PRIMES];
		for (int i=0;i<AMOUNT_OF_PRIMES;++i) result[i]=(int)expMod(primes[i],1+i,MOD);
		return result;
	}
	
	private static int[] getS2()	{
		int[] S=getS();
		int[] result=new int[AMOUNT_OF_PRIMES];
		for (int i=0;i<AMOUNT_OF_PRIMES;++i)	{
			int i2=divide(i+1,Q,FLOOR);
			result[i]=S[i]+S[i2];
		}
		return result;
	}
	
	private static class Median	{
		public final long intPart;
		public final boolean hasPointFive;
		public Median(long intPart,boolean hasPointFive)	{
			this.intPart=intPart;
			this.hasPointFive=hasPointFive;
		}
		public Median add(Median other)	{
			int pointFives=(hasPointFive?1:0)+(other.hasPointFive?1:0);
			long baseSum=intPart+other.intPart;
			if (pointFives==2) ++baseSum;
			return new Median(baseSum,pointFives==1);
		}
		@Override
		public String toString()	{
			return ""+intPart+'.'+(hasPointFive?'5':'0');
		}
	}
	
	private static class MedianCalculator	{
		private static class Position	{
			public int lowerHalf;
			public int higherHalf;
			public int leftFromLower;
			// public int rightFromHigher;
			public int lowerHalfCount;
			// public int higherHalfCount;
			public Median getMedian()	{
				int sum=lowerHalf+higherHalf;
				boolean hasPointFive=(sum%2)==1;
				int intPart=divide(sum,2,FLOOR);
				return new Median(intPart,hasPointFive);
			}
			public void getFromSequence(int[] counters)	{
				int size=sum(counters);
				if ((size%2)==1) throw new IllegalArgumentException("This shouldn't happen in this implementation.");
				int halfSize=size/2;
				int i=0;
				int currentCount=0;
				for (;;++i)	{
					currentCount+=counters[i];
					if (currentCount>halfSize)	{
						lowerHalf=i;
						higherHalf=i;
						leftFromLower=currentCount-counters[i];
						// rightFromHigher=halfSize-currentCount;
						lowerHalfCount=counters[i];
						// higherHalfCount=counters[i];
						return;
					}	else if (currentCount==halfSize)	{
						lowerHalf=i;
						leftFromLower=currentCount-counters[i];
						lowerHalfCount=counters[i];
						do ++i; while (counters[i]==0);
						higherHalf=i;
						// higherHalfCount=counters[i];
						// rightFromHigher=size-(leftFromLower+lowerHalfCount+higherHalfCount);
						return;
					}
				}
			}
		}
		private Position currentPosition;
		private int[] currentSequence;
		private final int halfSize;
		public MedianCalculator(int[] startingSequence)	{
			int N=startingSequence.length;
			currentSequence=new int[N];
			System.arraycopy(startingSequence,0,currentSequence,0,N);
			currentPosition=new Position();
			currentPosition.getFromSequence(currentSequence);
			halfSize=sum(startingSequence)/2;
		}
		public Median getMedian()	{
			return currentPosition.getMedian();
		}
		public void adjust(int remove,int add)	{
			if (remove==add) return;	// Woohoo! Too bad this doesn't really happen often.
			--currentSequence[remove];
			++currentSequence[add];
			if ((remove<currentPosition.lowerHalf)&&(add<currentPosition.lowerHalf)) return;
			if ((remove>currentPosition.higherHalf)&&(add>currentPosition.higherHalf)) return;
			// At least one of [remove,add] is inside [lowerHalf,higherHalf]. Ugly logic ensues.
			if (remove<currentPosition.lowerHalf) --currentPosition.leftFromLower;
			else if (remove==currentPosition.lowerHalf) --currentPosition.lowerHalfCount;
			else if (remove<currentPosition.higherHalf) throw new IllegalStateException("You have fucked something up, mate.");
			// else if (remove==currentPosition.higherHalf) --currentPosition.higherHalfCount;
			// else --currentPosition.rightFromHigher;
			if (add<currentPosition.lowerHalf) ++currentPosition.leftFromLower;
			else if (add==currentPosition.lowerHalf) ++currentPosition.lowerHalfCount;
			// else if (add<currentPosition.higherHalf);
			// else if (add==currentPosition.higherHalf) ++currentPosition.higherHalfCount;
			// else ++currentPosition.higherHalfCount;
			// And now the logic itself.
			if (currentPosition.leftFromLower>=halfSize)	{
				int i=currentPosition.lowerHalf-1;
				for (;;--i)	{
					currentPosition.leftFromLower-=currentSequence[i];
					if (currentPosition.leftFromLower<halfSize) break;
				}
				currentPosition.lowerHalf=i;
				currentPosition.lowerHalfCount=currentSequence[i];
			}	else if (currentPosition.leftFromLower+currentPosition.lowerHalfCount<halfSize)	{
				int i=currentPosition.lowerHalf;
				for (;;++i)	{
					currentPosition.leftFromLower+=currentSequence[i];
					if (currentPosition.leftFromLower>=halfSize) break;
				}
				currentPosition.leftFromLower-=currentSequence[i];
				currentPosition.lowerHalf=i;
				currentPosition.lowerHalfCount=currentSequence[i];
			}
			// Now that the left half has been established, update from there.
			if (currentPosition.leftFromLower+currentPosition.lowerHalfCount>halfSize)	{
				currentPosition.higherHalf=currentPosition.lowerHalf;
				// currentPosition.higherHalfCount=currentPosition.lowerHalfCount;
				// currentPosition.rightFromHigher=2*halfSize-(currentPosition.leftFromLower+currentPosition.lowerHalfCount);
				return;
			}
			for (int i=1+currentPosition.lowerHalf;;++i) if (currentSequence[i]>0)	{
				currentPosition.higherHalf=i;
				// currentPosition.higherHalfCount=currentSequence[i];
				// currentPosition.rightFromHigher=2*halfSize-(currentPosition.leftFromLower+currentPosition.lowerHalfCount+currentPosition.higherHalfCount);
				return;
			}
		}
	}
	
	private static class MedianIterator implements Iterator<Median>	{
		private final int[] baseSequence;
		
		private final MedianCalculator calculator;
		private int removeIndex;
		private int addIndex;
		public MedianIterator(int[] baseSequence,int size)	{
			this.baseSequence=baseSequence;
			int[] startingSequence=new int[1+2*MOD];
			for (int i=0;i<size;++i) ++startingSequence[baseSequence[i]];
			calculator=new MedianCalculator(startingSequence);
			removeIndex=0;
			addIndex=size;
		}
		@Override
		public boolean hasNext() {
			return addIndex<=baseSequence.length;
		}
		@Override
		public Median next() {
			Median result=calculator.getMedian();
			if (addIndex<baseSequence.length) calculator.adjust(baseSequence[removeIndex],baseSequence[addIndex]);
			++removeIndex;
			++addIndex;
			return result;
		}
	}
	
	public static String getResult(int[] arrayIn,int size)	{
		Median sum=new Median(0,false);
		MedianIterator iter=new MedianIterator(arrayIn,size);
		while (iter.hasNext()) sum=sum.add(iter.next());
		return sum.toString();
	}
	
	public static void main(String[] args)	{
		long tic0=System.nanoTime();
		int[] S2=getS2();
		long tic=System.nanoTime();
		System.out.println(getResult(S2,100000));
		long tac=System.nanoTime();
		double pSeconds=((double)(tic-tic0))/1e9;
		double seconds=((double)(tac-tic))/1e9;
		System.out.println("Primes calculated in "+pSeconds+" seconds.");
		System.out.println("Medians calculated in "+seconds+" seconds.");
	}
}
