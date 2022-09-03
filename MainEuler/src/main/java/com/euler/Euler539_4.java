package com.euler;

public class Euler539_4 {
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
		private long getLastBlockSum()	{
			if (previousCase==null) return 1;
			else return previousCase.totalSum+previousCase.getLastBlockSum();
		}
		private long getTransitionPartialSum(long size)	{
			// I think this is wrong, but I don't care, this isn't actually called :|.
			/*-
			BigInteger[] division=size.divideAndRemainder(groupSize);
			BigInteger resultBase=groupSum.multiply(division[0]).add(getGroupPartialSum(division[1]));
			return power2.multiply(size.add(BigInteger.ONE)).add(resultBase);
			 */
			long baseGroup=size/groupSize;
			long posInBaseGroup=size%groupSize;
			return groupSum*baseGroup+getGroupPartialSum(posInBaseGroup)+power2*(size+1);
		}
		private long getGroupPartialSum(long size)	{
			if ((size%previousCase.groupSize)==(previousCase.groupSize-1)) return previousCase.groupSum*((size+1)/previousCase.groupSize);
			long baseGroup=size/previousCase.groupSize;	// Always between 0 and 3, but the operation returns a long...
			long posInBaseGroup=size%previousCase.groupSize;
			if (baseGroup==0) return previousCase.getGroupPartialSum(posInBaseGroup)+2*previousCase.power2*(1+posInBaseGroup);
			else if (baseGroup==1) return previousCase.groupSum+2*previousCase.power2*previousCase.groupSize+previousCase.getGroupPartialSum(posInBaseGroup)+2*previousCase.power2*(1+posInBaseGroup);
			else if (baseGroup==2) return 2*previousCase.groupSum+4*previousCase.power2*previousCase.groupSize+previousCase.getGroupPartialSum(posInBaseGroup)+3*previousCase.power2*(1+posInBaseGroup);
			else if (baseGroup==3) return 3*previousCase.groupSum+7*previousCase.power2*previousCase.groupSize+previousCase.getGroupPartialSum(posInBaseGroup)+3*previousCase.power2*(1+posInBaseGroup);
			else throw new RuntimeException("Algo ha ido MUY mal.");
		}
		public long getSum(long maxIndex)	{
			if (maxIndex>=transitionEnd) return new Block(this).getSum(maxIndex);
			else if (maxIndex>=normalEnd)	{
				long transitionSize=maxIndex-normalEnd;
				return getTransitionPartialSum(transitionSize)+4*groupSum+getLastBlockSum();
			}	else if (maxIndex>=start)	{
				long diff=maxIndex-start;
				long completeGroups=diff/groupSize;	// Always between 0 and 3, but the operation returns a long...
				long sizeInGroup=diff%groupSize;
				return getGroupPartialSum(sizeInGroup)+completeGroups*groupSum+getLastBlockSum();
			}	else return previousCase.getSum(maxIndex);
		}
	}
	
	public static void main(String[] args)	{
		/*-
		long result=new Block().getSum(1000l);
		System.out.println(result);
		*/
		Block b5=new Block(new Block(new Block(new Block(new Block(new Block())))));
		for (long i=8;i<=4096;++i) System.out.println("s("+i+")="+b5.getSum(i)+".");
	}
}
