package com.euler;

import java.util.BitSet;

import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashObjLongMaps;

public class Euler806_2 {
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
	
	private static interface Solution	{}
	
	private static class Solution2 implements Solution	{
		private final int n;
		public Solution2(int n)	{
			this.n=n;
		}
		@Override
		public int hashCode()	{
			return n;
		}
		@Override
		public boolean equals(Object other)	{
			if (other instanceof Solution2) return n==((Solution2)other).n;
			else return false;
		}
		@Override
		public String toString()	{
			return String.format("{%d,%d}",n,n);
		}
	}
	
	private static class Solution3 implements Solution	{
		private final int a;
		private final int b;
		private final int c;
		public Solution3(int a,int b,int c)	{
			this.a=a;
			this.b=b;
			this.c=c;
		}
		@Override
		public int hashCode()	{
			return 961*a+31*b+c;
		}
		@Override
		public boolean equals(Object other)	{
			if (!(other instanceof Solution3)) return false;
			Solution3 s3Other=(Solution3)other;
			return (a==s3Other.a)&&(b==s3Other.b)&&(c==s3Other.c);
		}
		@Override
		public String toString()	{
			return String.format("{%d,%d,%d}",a,b,c);
		}
	}
	
	private static Solution getSolution(int a,int b,int c)	{
		int swap=0;
		if (a<b)	{
			swap=a;
			a=b;
			b=swap;
		}
		if (b<c)	{
			swap=b;
			b=c;
			c=swap;
		}
		if (a<b)	{
			swap=a;
			a=b;
			b=swap;
		}
		if (c==0) return new Solution2(a);
		else return new Solution3(a,b,c);
	}
	
	private static class SolutionSummary	{
		private final long count;
		private final long sum;
		private final ObjLongMap<Solution> cases;
		public SolutionSummary(long count,long sum,ObjLongMap<Solution> cases)	{
			this.count=count;
			this.sum=sum;
			this.cases=cases;
		}
	}
	
	private static class HanoiNimSimulator	{
		private Peg[] pegCache;
		public HanoiNimSimulator(int n)	{
			int distinctPegs=1<<n;
			pegCache=new Peg[distinctPegs];
			for (int i=0;i<distinctPegs;++i) pegCache[i]=new Peg(i);
		}
		public SolutionSummary simulate()	{
			Peg[] pegs=new Peg[3];
			int lastIndex=pegCache.length-1;
			pegs[0]=pegCache[lastIndex];
			pegs[1]=pegCache[0];
			pegs[2]=pegCache[0];
			int lastMoved=-1;
			int index=0;
			long count=0;
			long sum=0;
			ObjLongMap<Solution> cases=HashObjLongMaps.newMutableMap();
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
					// System.out.println(String.format("Found losing state: %s (index=%d).",Arrays.toString(pegs),index));
					++count;
					sum+=index;
					cases.addValue(getSolution(pegs[0].size(),pegs[1].size(),pegs[2].size()),1l);
				}
				if ((pegs[0].getId()==0)&&(((pegs[1].getId()==0)&&(pegs[2].getId()==lastIndex))||((pegs[2].getId()==0)&&(pegs[1].getId()==lastIndex)))) break;
				lastMoved=destination;
			}
			return new SolutionSummary(count,sum,cases);
		}
	}
	
	// I'm aware that there is no way in hell I solve this, but let's try and see how far I get.
	public static void main(String[] args)	{
		for (int i=2;i<=30;i+=2)	{
			HanoiNimSimulator sim=new HanoiNimSimulator(i);
			SolutionSummary result=sim.simulate();
			System.out.println(String.format("For N=%d there are %d solutions adding up to %d.",i,result.count,result.sum));
			System.out.println(String.format("\tThe distribution of subcases is: %s.",result.cases.toString()));
		}
	}
}
