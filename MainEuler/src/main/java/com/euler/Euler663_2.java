package com.euler;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class Euler663_2 {
	private final static int N=107;
	private final static int L=1000;
	
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
		public LongArray(int length)	{
			data=new long[length];
			runs=TreeRangeMap.create();
			LinkedRun initialRun=new LinkedRun();
			initialRun.start=0;
			initialRun.end=length;
			initialRun.sum=0;
			initialRun.localMax=0;
			initialRun.globalMax=0;
			initialRun.previous=null;
			initialRun.next=null;
			runs.put(Range.closedOpen(0,length),initialRun);
		}
		public void update(int value,int pos)	{
			long previous=data[pos];
			data[pos]+=2*value+1-data.length;
			long current=data[pos];
			if (previous==current)	{
				System.out.println("Happy case! No update needed.");
				return;	// Very happy case! Nothing to update.
			}
			boolean prevCategory=getCategory(previous);
			boolean curCategory=getCategory(current);
			LinkedRun currentRun=runs.get(pos);
			if (prevCategory==curCategory)	{
				System.out.println("Update in the middle of a run.");
				// Unchanged category. We just update the run.
				currentRun.sum+=current-previous;
				updateKadaneData(currentRun,currentRun);
			}	else if (pos==0)	{
				// Changed category at the left border.
				if (currentRun.end==1)	{
					System.out.println("Accumulate singleton run at the start into the next one.");
					// Accumulate this run into the next one.
					LinkedRun nextRun=currentRun.next;
					nextRun.start=0;
					nextRun.sum+=current;
					nextRun.previous=null;
					runs.put(Range.closedOpen(0,nextRun.end),nextRun);
					updateKadaneData(nextRun,nextRun);
				}	else	{
					System.out.println("New singleton run at the start.");
					// New LinkedRun at the start.
					LinkedRun newRun=new LinkedRun();
					newRun.start=0;
					newRun.end=1;
					newRun.sum=current;
					newRun.previous=null;
					newRun.next=currentRun;
					newRun.localMax=Long.MIN_VALUE;
					newRun.globalMax=Long.MIN_VALUE;
					currentRun.previous=newRun;
					currentRun.start=1;
					runs.put(Range.closedOpen(0,1),newRun);
					runs.put(Range.closedOpen(1,currentRun.end),currentRun);
					updateKadaneData(newRun,currentRun);
				}
			}	else if (pos==data.length-1)	{
				// Changed category at the right border.
				if (currentRun.start==data.length-1)	{
					System.out.println("Accumulate singleton run at the end into the previous one.");
					// Accumulate this run into the next one.
					LinkedRun prevRun=currentRun.previous;
					prevRun.end=data.length;
					prevRun.sum+=current;
					prevRun.next=null;
					runs.put(Range.closedOpen(prevRun.start,data.length),prevRun);
					updateKadaneData(prevRun,prevRun);
				}	else	{
					System.out.println("New singleton run at the start.");
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
					runs.put(Range.closedOpen(currentRun.start,data.length-1),currentRun);
					runs.put(Range.closedOpen(data.length-1,data.length),newRun);
					updateKadaneData(currentRun,newRun);
				}
			}	else if ((currentRun.start==pos)&&(currentRun.end==pos+1))	{
				System.out.println("Removing a singleton and coalescing its neighbours.");
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
				runs.put(Range.closedOpen(coalescedRun.start,coalescedRun.end),coalescedRun);
				updateKadaneData(coalescedRun,coalescedRun);
			}	else if (currentRun.start==pos)	{
				System.out.println("Switching current element into the previous run.");
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
				runs.put(Range.closedOpen(prevRun.start,pos+1),prevRun);
				runs.put(Range.closedOpen(pos+1,currentRun.end),currentRun);
				updateKadaneData(prevRun,currentRun);
			}	else if (currentRun.end==pos+1)	{
				System.out.println("Switching current element into the next run.");
				// Like the previous case, but in reverse.
				LinkedRun nextRun=currentRun.next;
				currentRun.end=pos;
				currentRun.sum-=previous;
				nextRun.start=pos;
				nextRun.sum+=current;
				runs.put(Range.closedOpen(currentRun.start,pos),currentRun);
				runs.put(Range.closedOpen(pos,nextRun.end),nextRun);
				updateKadaneData(currentRun,nextRun);
			}	else	{
				System.out.println("Splitting a run in smaller parts.");
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
				runs.put(Range.closedOpen(leftRun.start,leftRun.end),leftRun);
				runs.put(Range.closedOpen(middleRun.start,middleRun.end),middleRun);
				runs.put(Range.closedOpen(rightRun.start,rightRun.end),rightRun);
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
			/*-
			for (LinkedRun run=startingFrom;run!=null;run=run.next)	{
				localMax=Math.max(run.sum,localMax+run.sum);
				globalMax=Math.max(globalMax,localMax);
				if ((localMax==run.localMax)&&(globalMax==run.globalMax)) break;
				else	{
					run.localMax=localMax;
					run.globalMax=globalMax;
				}
			}
			*/
		}
		public long getBestSum()	{
			return runs.get(data.length-1).globalMax;
		}
		private String getCoherenceMessage()	{
			LinkedRun previous=null;
			LinkedRun current=runs.get(0);
			do	{
				int lastEnd;
				if (previous==null) lastEnd=0;
				else lastEnd=previous.end;
				if (current.start!=lastEnd) return "Inexact mapping.";
				if (current.previous!=previous) return "Bad linking.";
				for (int i=current.start;i<current.end;++i) if (runs.get(i)!=current) return "Incoherent map.";
				if (current.sum!=manualSum(current.start,current.end)) return "Bad sum.";
				boolean category=getCategory(data[current.start]);
				for (int i=current.start;i<current.end;++i) if (category!=getCategory(data[i])) return "Bad categorisation.";
				previous=current;
				current=current.next;
			}	while (current!=null);
			if (previous.end!=data.length) return "Premature end.";
			return null;
		}
		public void checkCoherence()	{
			String msg=getCoherenceMessage();
			if (msg!=null) throw new RuntimeException(msg);
		}
	}
	
	public static void main(String[] args)	{
		LongArray array=new LongArray(N);
		TribonacciGenerator gen=new TribonacciGenerator(N);
		long result=0;
		for (int i=1;i<=L;++i)	{
			int goalPos=gen.getNext();
			int augendPos=gen.getNext();
			array.update(augendPos,goalPos);
			array.checkCoherence();
			result+=array.getBestSum();
		}
		System.out.println(result);
	}
}
