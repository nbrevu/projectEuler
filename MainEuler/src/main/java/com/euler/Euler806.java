package com.euler;

import java.util.Arrays;
import java.util.BitSet;

public class Euler806 {
	private final static int N=10;
	
	private static class Peg	{
		private final int id;
		private final int size;
		private final int discOnTop;
		private static int getLowestBit(int x)	{
			// This is slightly different than Integer.lowestOneBit.
			if (x==0) return Integer.MAX_VALUE;
			int result=0;
			for (;;)	{
				if ((x&1)==1) return result;
				x>>=1;
				++result;
			}
		}
		public Peg(int id)	{
			this.id=id;
			size=Integer.bitCount(id);
			discOnTop=getLowestBit(id);
		}
		public int size()	{
			return size;
		}
		public int getId()	{
			return id;
		}
		public int getDiscOnTop()	{
			return discOnTop;
		}
		public int getNextIdAfterAdding(int disc)	{
			int bit=1<<disc;
			if ((id&bit)!=0) throw new IllegalArgumentException();
			return id+bit;
		}
		public int getNextIdAfterRemoving(int disc)	{
			int bit=1<<disc;
			if ((id&bit)!=bit) throw new IllegalArgumentException();
			return id-bit;
		}
		public boolean canMoveDisc(Peg other)	{
			return discOnTop<other.discOnTop;
		}
		@Override
		public String toString()	{
			return BitSet.valueOf(new long[] {id}).toString();
		}
	}
	
	private static class HanoiNimSimulator	{
		private Peg[] pegCache;
		public HanoiNimSimulator(int n)	{
			int distinctPegs=1<<n;
			pegCache=new Peg[distinctPegs];
			for (int i=0;i<distinctPegs;++i) pegCache[i]=new Peg(i);
		}
		public long simulate()	{
			Peg[] pegs=new Peg[3];
			int lastIndex=pegCache.length-1;
			pegs[0]=pegCache[lastIndex];
			pegs[1]=pegCache[0];
			pegs[2]=pegCache[0];
			int lastMoved=-1;
			int index=0;
			long result=0;
			for (;;)	{
				int origin=(lastMoved+1)%3;
				Peg thisPeg=pegs[origin];
				int destination=(origin+1)%3;
				Peg nextPeg=pegs[destination];
				if (!thisPeg.canMoveDisc(nextPeg))	{
					destination=(destination+1)%3;
					nextPeg=pegs[destination];
					if (!thisPeg.canMoveDisc(nextPeg))	{
						origin=(origin+1)%3;
						thisPeg=pegs[origin];
						if (!thisPeg.canMoveDisc(nextPeg))	{
							destination=(destination+1)%3;
							nextPeg=pegs[destination];
							if (!thisPeg.canMoveDisc(nextPeg)) throw new IllegalStateException();
						}
					}
				}
				int movedDisc=thisPeg.getDiscOnTop();
				pegs[origin]=pegCache[thisPeg.getNextIdAfterRemoving(movedDisc)];
				pegs[destination]=pegCache[nextPeg.getNextIdAfterAdding(movedDisc)];
				++index;
				int xorValue=pegs[0].size()^pegs[1].size()^pegs[2].size();
				if (xorValue==0)	{
					System.out.println(String.format("Found losing state: %s (index=%d).",Arrays.toString(pegs),index));
					result+=index;
				}	else System.out.println(String.format("Found normal state: %s (index=%d).",Arrays.toString(pegs),index));
				if ((pegs[0].getId()==0)&&(((pegs[1].getId()==0)&&(pegs[2].getId()==lastIndex))||((pegs[2].getId()==0)&&(pegs[1].getId()==lastIndex)))) break;
				lastMoved=destination;
			}
			return result;
		}
	}
	
	// I'm aware that there is no way in hell I solve this, but let's try and see how far I get.
	public static void main(String[] args)	{
		HanoiNimSimulator sim=new HanoiNimSimulator(N);
		System.out.println(sim.simulate());
	}
}
