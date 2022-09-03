package com.euler;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class Euler663_5 {
	private final static int N=10_000_003;
	private final static int INITIAL_SEGMENT=10_000_000;
	private final static int END_SEGMENT=10_200_000;

	private final static long MIN=Long.MIN_VALUE/2;	// So that even after "adding" a negative number we get a negative one!
	
	private static class TribonacciGenerator	{
		private final int mod;
		private int prev;
		private int cur;
		private int next;
		public TribonacciGenerator(int mod)	{
			this.mod=mod;
			prev=1;
			cur=0;
			next=0;
		}
		public int getNext()	{
			int nextN=(prev+cur+next)%mod;
			prev=cur;
			cur=next;
			next=nextN;
			return prev;
		}
	}
	
	private static class LinkedRun	{
		public int start;
		public int end;
		public long sum;
		public long localMax;
		public long globalMax;
		public LinkedRun previous;
		public LinkedRun next;
	}
	
	private static class LongArray	{
		private static boolean getCategory(long number)	{
			// This could be an enum, but a boolean does the job.
			return number>=0;
		}
		private final long[] data;
		private final RangeMap<Integer,LinkedRun> runs;
		public LongArray(long[] data)	{
			this.data=data;
			runs=TreeRangeMap.create();
			initKadane();
		}
		private void putRun(LinkedRun run)	{
			runs.put(Range.closedOpen(run.start,run.end),run);
		}
		private void initKadane()	{
			LinkedRun prevRun=null;
			boolean currentCategory=getCategory(data[0]);
			int currentStart=0;
			long currentSum=data[0];
			long localMax=0;
			long globalMax=MIN;
			for (int i=1;i<data.length;++i)	{
				long value=data[i];
				boolean category=getCategory(value);
				if (currentCategory==category) currentSum+=value;
				else	{
					LinkedRun newRun=new LinkedRun();
					newRun.start=currentStart;
					newRun.end=i;
					newRun.sum=currentSum;
					localMax=Math.max(currentSum,localMax+currentSum);
					globalMax=Math.max(globalMax,localMax);
					newRun.localMax=localMax;
					newRun.globalMax=globalMax;
					newRun.previous=prevRun;
					if (prevRun!=null) prevRun.next=newRun;
					putRun(newRun);
					prevRun=newRun;
					currentStart=i;
					currentSum=value;
				}
			}
			LinkedRun lastRun=new LinkedRun();
			lastRun.start=currentStart;
			lastRun.end=data.length;
			lastRun.sum=currentSum;
			localMax=Math.max(currentSum,localMax+currentSum);
			globalMax=Math.max(globalMax,localMax);
			lastRun.localMax=localMax;
			lastRun.globalMax=globalMax;
			lastRun.previous=prevRun;
			if (prevRun!=null) prevRun.next=lastRun;
			putRun(lastRun);
		}
		public void update(int value,int pos)	{
			long previous=data[pos];
			data[pos]+=2*value+1-data.length;
			long current=data[pos];
			if (previous==current) return;	// Very happy case! Nothing to update.
			boolean prevCategory=getCategory(previous);
			boolean curCategory=getCategory(current);
			LinkedRun currentRun=runs.get(pos);
			if (prevCategory==curCategory)	{
				// Unchanged category. We just update the run.
				currentRun.sum+=current-previous;
				updateKadaneData(currentRun,currentRun);
			}	else if (pos==0)	{
				// Changed category at the left border.
				if (currentRun.end==1)	{
					// Accumulate this run into the next one.
					LinkedRun nextRun=currentRun.next;
					nextRun.start=0;
					nextRun.sum+=current;
					nextRun.previous=null;
					putRun(nextRun);
					updateKadaneData(nextRun,nextRun);
				}	else	{
					// New LinkedRun at the start.
					LinkedRun newRun=new LinkedRun();
					newRun.start=0;
					newRun.end=1;
					newRun.sum=current;
					newRun.previous=null;
					newRun.next=currentRun;
					newRun.localMax=MIN;
					newRun.globalMax=MIN;
					currentRun.previous=newRun;
					currentRun.start=1;
					putRun(newRun);
					putRun(currentRun);
					updateKadaneData(newRun,currentRun);
				}
			}	else if (pos==data.length-1)	{
				// Changed category at the right border.
				if (currentRun.start==data.length-1)	{
					// Accumulate this run into the next one.
					LinkedRun prevRun=currentRun.previous;
					prevRun.end=data.length;
					prevRun.sum+=current;
					prevRun.next=null;
					putRun(prevRun);
					updateKadaneData(prevRun,prevRun);
				}	else	{
					// New LinkedRun.
					LinkedRun newRun=new LinkedRun();
					newRun.start=data.length-1;
					newRun.end=data.length;
					newRun.sum=current;
					newRun.previous=currentRun;
					newRun.next=null;
					newRun.localMax=Long.MIN_VALUE;
					newRun.globalMax=Long.MIN_VALUE;
					currentRun.sum-=previous;
					currentRun.next=newRun;
					currentRun.end=data.length-1;
					putRun(currentRun);
					putRun(newRun);
					updateKadaneData(currentRun,newRun);
				}
			}	else if ((currentRun.start==pos)&&(currentRun.end==pos+1))	{
				// Singleton, to be removed. The immediately previous and following runs are fused.
				LinkedRun prevRun=currentRun.previous;	// Guaranteed not to be null.
				LinkedRun prevPrevRun=prevRun.previous;	// Might be null!
				LinkedRun nextRun=currentRun.next;	// Guaranteed not to be null.
				LinkedRun nextNextRun=nextRun.next;	// Might be null!
				LinkedRun coalescedRun=new LinkedRun();
				coalescedRun.start=prevRun.start;
				coalescedRun.end=nextRun.end;
				coalescedRun.sum=prevRun.sum+current+nextRun.sum;
				coalescedRun.previous=prevPrevRun;
				coalescedRun.next=nextNextRun;
				coalescedRun.localMax=Long.MIN_VALUE;
				coalescedRun.globalMax=Long.MIN_VALUE;
				if (prevPrevRun!=null) prevPrevRun.next=coalescedRun;
				if (nextNextRun!=null) nextNextRun.previous=coalescedRun;
				putRun(coalescedRun);
				updateKadaneData(coalescedRun,coalescedRun);
			}	else if (currentRun.start==pos)	{
				/*
				 * The current element switches to the previous run. We are guaranteed that the current run is not left empty, because
				 * otherwise we would be in the previous case. We are also guaranteed that the previous run exists, otherwise pos==0 and
				 * we would be in another already checked case.
				 */
				LinkedRun prevRun=currentRun.previous;
				prevRun.end=pos+1;
				prevRun.sum+=current;
				currentRun.start=pos+1;
				currentRun.sum-=previous;
				putRun(prevRun);
				putRun(currentRun);
				updateKadaneData(prevRun,currentRun);
			}	else if (currentRun.end==pos+1)	{
				// Like the previous case, but in reverse.
				LinkedRun nextRun=currentRun.next;
				currentRun.end=pos;
				currentRun.sum-=previous;
				nextRun.start=pos;
				nextRun.sum+=current;
				putRun(currentRun);
				putRun(nextRun);
				updateKadaneData(currentRun,nextRun);
			}	else	{
				// Worse case: the current run is broken. We need to split.
				LinkedRun prevRun=currentRun.previous;	// Might be null!
				LinkedRun nextRun=currentRun.next;	// Might be null!
				LinkedRun leftRun=new LinkedRun();
				leftRun.start=currentRun.start;
				leftRun.end=pos;
				int leftLen=leftRun.end-leftRun.start;
				LinkedRun middleRun=new LinkedRun();
				middleRun.start=pos;
				middleRun.end=pos+1;
				LinkedRun rightRun=new LinkedRun();
				rightRun.start=pos+1;
				rightRun.end=currentRun.end;
				int rightLen=rightRun.end-rightRun.start;
				middleRun.sum=current;
				if (leftLen<=rightLen)	{
					leftRun.sum=manualSum(leftRun.start,leftRun.end);
					rightRun.sum=currentRun.sum-(leftRun.sum+previous);
				}	else	{
					rightRun.sum=manualSum(rightRun.start,rightRun.end);
					leftRun.sum=currentRun.sum-(previous+rightRun.sum);
				}
				if (prevRun!=null) prevRun.next=leftRun;
				leftRun.previous=prevRun;
				leftRun.next=middleRun;
				middleRun.previous=leftRun;
				middleRun.next=rightRun;
				rightRun.previous=middleRun;
				rightRun.next=nextRun;
				if (nextRun!=null) nextRun.previous=rightRun;
				putRun(leftRun);
				putRun(middleRun);
				putRun(rightRun);
				updateKadaneData(leftRun,rightRun);
			}
		}
		private long manualSum(int start,int end) {
			long result=0;
			for (int i=start;i<end;++i) result+=data[i];
			return result;
		}
		private void updateKadaneData(LinkedRun startingFrom,LinkedRun minUpperBound)	{
			long localMax;
			long globalMax;
			LinkedRun previousRun=startingFrom.previous;
			if (previousRun==null)	{
				localMax=0;
				globalMax=Long.MIN_VALUE;
			}	else	{
				localMax=previousRun.localMax;
				globalMax=previousRun.globalMax;
			}
			boolean hasPassedBound=false;
			for (LinkedRun run=startingFrom;run!=null;run=run.next)	{
				if (run==minUpperBound) hasPassedBound=true;
				localMax=Math.max(run.sum,localMax+run.sum);
				globalMax=Math.max(globalMax,localMax);
				if ((localMax==run.localMax)&&(globalMax==run.globalMax))	{
					if (hasPassedBound) break;
				}	else	{
					run.localMax=localMax;
					run.globalMax=globalMax;
				}
			}
		}
		public long getBestSum()	{
			return runs.get(data.length-1).globalMax;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		TribonacciGenerator gen=new TribonacciGenerator(N);
		long[] data=new long[N];
		long result=0;
		for (int i=1;i<=INITIAL_SEGMENT;++i)	{
			int pos=gen.getNext();
			int augend=gen.getNext();
			data[pos]+=2*augend+1-data.length;
		}
		LongArray array=new LongArray(data);
		for (int i=1+INITIAL_SEGMENT;i<=END_SEGMENT;++i)	{
			int pos=gen.getNext();
			int augend=gen.getNext();
			array.update(augend,pos);
			result+=array.getBestSum();
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
