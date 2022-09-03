package com.euler;

public class Euler539_6 {
	private static class Block	{
		public final Block previousCase;
		public final long power2;
		public final long start;
		public final long normalEnd;	// Non-inclusive!
		public final long transitionEnd;	// Non-inclusive!
		public final long groupSize;
		public final long groupSum;
		public final long totalSum;
		public Block()	{
			// Constructor for the case n=0.
			previousCase=null;
			power2=2;
			start=2;
			normalEnd=6;
			transitionEnd=8;
			groupSize=1;
			groupSum=2;
			totalSum=4*2+2*(2+2);
		}
		public Block(Block base)	{
			previousCase=base;
			power2=base.power2*4;
			start=4*base.start;
			normalEnd=3*start;
			transitionEnd=4*start;
			groupSize=4*base.groupSize;
			groupSum=4*base.groupSum+10*base.groupSize*base.power2;
			long transitionSum=2*groupSum+2*groupSize*power2;
			totalSum=4*groupSum+transitionSum;
		}
		private long getTransitionPartialSum(long size)	{
			long powerOf2TotalShift=power2*(size+1);
			return powerOf2TotalShift+getPartialSumInsideFourGroups(size);
		}
		public long getPartialSumInsideGroup(long size)	{
			long toAdd2,toAdd3;
			if (size<2*previousCase.groupSize)	{
				toAdd2=1+size;
				toAdd3=0;
			}	else	{
				toAdd2=2*previousCase.groupSize;
				toAdd3=1+size-toAdd2;
			}
			long powerOf2TotalShift=(2*toAdd2+3*toAdd3)*previousCase.power2;
			long baseGroup=size/previousCase.groupSize;
			long posInBaseGroup=size%previousCase.groupSize;
			if (posInBaseGroup+1==previousCase.groupSize) return (baseGroup+1)*previousCase.groupSum+powerOf2TotalShift;	// "Base group" complete!
			else return (baseGroup*previousCase.groupSum)+previousCase.getPartialSumInsideGroup(posInBaseGroup)+powerOf2TotalShift;
		}
		public long getPartialSumInsideFourGroups(long sizeInBlock)	{
			long completeGroups=sizeInBlock/groupSize;
			long sizeInGroup=sizeInBlock%groupSize;
			if (sizeInGroup+1==groupSize) return (completeGroups+1)*groupSum;
			else return completeGroups*groupSum+getPartialSumInsideGroup(sizeInGroup);
		}
		public long getFullPreviousSum()	{
			long result=1;
			for (Block p=previousCase;p!=null;p=p.previousCase) result+=p.totalSum;
			return result;
		}
		public long getTotalSum(long maxIndex)	{
			if (maxIndex>=transitionEnd) throw new RuntimeException("Ich brauche eine GROßE Zahl!!!!!");
			else if (maxIndex>=normalEnd)	{
				long transitionSize=maxIndex-normalEnd;
				return getTransitionPartialSum(transitionSize)+4*groupSum+getFullPreviousSum();
			}	else if (maxIndex>=start)	{
				long diff=maxIndex-start;
				return getPartialSumInsideFourGroups(diff)+getFullPreviousSum();
			}	else return previousCase.getTotalSum(maxIndex);	// D'oh! Too big :).
		}
	}
	
	// YA ESTÁ, YA FUNCIONA. Ahora a pasarlo a BigInteger.
	public static void main(String[] args)	{
		Block b5=new Block(new Block(new Block(new Block(new Block(new Block())))));
		for (long i=8;i<=4096;++i) System.out.println("s("+i+")="+b5.getTotalSum(i)+".");
	}
}
