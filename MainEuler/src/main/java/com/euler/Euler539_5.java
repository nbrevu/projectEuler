package com.euler;

import java.math.BigInteger;

public class Euler539_5 {
	private final static BigInteger N=BigInteger.TEN.pow(18);
	private final static BigInteger MOD=BigInteger.valueOf(987654321l);
	
	private final static BigInteger THREE=BigInteger.valueOf(3l);
	private final static BigInteger FOUR=BigInteger.valueOf(4l);
	
	private static class Block	{
		public final Block previousCase;
		public final BigInteger power2;
		public final BigInteger start;
		public final BigInteger normalEnd;	// Non-inclusive!
		public final BigInteger transitionEnd;	// Non-inclusive!
		public final BigInteger groupSize;
		public final BigInteger groupSum;
		public final BigInteger totalSum;
		public Block()	{
			// Constructor for the case n=0.
			previousCase=null;
			power2=BigInteger.TWO;
			start=BigInteger.TWO;
			normalEnd=BigInteger.valueOf(6);
			transitionEnd=BigInteger.valueOf(8);
			groupSize=BigInteger.ONE;
			groupSum=BigInteger.TWO;
			totalSum=BigInteger.valueOf(16);
		}
		public Block(Block base)	{
			previousCase=base;
			power2=base.power2.multiply(FOUR);
			start=base.start.multiply(FOUR);
			normalEnd=start.multiply(THREE);
			transitionEnd=start.multiply(FOUR);
			groupSize=base.groupSize.multiply(FOUR);
			groupSum=base.groupSum.multiply(FOUR).add(base.groupSize.multiply(base.power2.multiply(BigInteger.TEN)));
			BigInteger transitionSum=groupSum.multiply(BigInteger.TWO).add(groupSize.multiply(power2).multiply(BigInteger.TWO));
			totalSum=groupSum.multiply(FOUR).add(transitionSum);
		}
		private BigInteger getTransitionPartialSum(BigInteger size)	{
			BigInteger powerOf2TotalShift=power2.multiply(size.add(BigInteger.ONE));
			return powerOf2TotalShift.add(getPartialSumInsideFourGroups(size));
		}
		public BigInteger getPartialSumInsideGroup(BigInteger size)	{
			BigInteger toAdd2,toAdd3;
			BigInteger twoGroups=BigInteger.TWO.multiply(previousCase.groupSize);
			if (size.compareTo(twoGroups)<0)	{
				toAdd2=BigInteger.ONE.add(size);
				toAdd3=BigInteger.ZERO;
			}	else	{
				toAdd2=twoGroups;
				toAdd3=BigInteger.ONE.add(size).subtract(toAdd2);
			}
			BigInteger powerOf2TotalShift=BigInteger.TWO.multiply(toAdd2).add(THREE.multiply(toAdd3)).multiply(previousCase.power2);
			BigInteger[] division=size.divideAndRemainder(previousCase.groupSize);
			BigInteger baseGroup=division[0];
			BigInteger posInBaseGroup=division[1];
			if (posInBaseGroup.add(BigInteger.ONE).equals(previousCase.groupSize)) return (baseGroup.add(BigInteger.ONE)).multiply(previousCase.groupSum).add(powerOf2TotalShift);	// "Base group" complete!
			else return baseGroup.multiply(previousCase.groupSum).add(previousCase.getPartialSumInsideGroup(posInBaseGroup)).add(powerOf2TotalShift);
		}
		public BigInteger getPartialSumInsideFourGroups(BigInteger sizeInBlock)	{
			BigInteger[] division=sizeInBlock.divideAndRemainder(groupSize);
			BigInteger completeGroups=division[0];
			BigInteger sizeInGroup=division[1];
			if (sizeInGroup.add(BigInteger.ONE).equals(groupSize)) return completeGroups.add(BigInteger.ONE).multiply(groupSum);
			else return completeGroups.multiply(groupSum).add(getPartialSumInsideGroup(sizeInGroup));
		}
		public BigInteger getFullPreviousSum()	{
			BigInteger result=BigInteger.ONE;
			for (Block p=previousCase;p!=null;p=p.previousCase) result=result.add(p.totalSum);
			return result;
		}
		public BigInteger getTotalSum(BigInteger maxIndex)	{
			if (maxIndex.compareTo(transitionEnd)>=0) throw new RuntimeException("Ich brauche eine GROßE Zahl!!!!!");
			else if (maxIndex.compareTo(normalEnd)>=0)	{
				BigInteger transitionSize=maxIndex.subtract(normalEnd);
				return getTransitionPartialSum(transitionSize).add(FOUR.multiply(groupSum)).add(getFullPreviousSum());
			}	else if (maxIndex.compareTo(start)>=0)	{
				BigInteger diff=maxIndex.subtract(start);
				return getPartialSumInsideFourGroups(diff).add(getFullPreviousSum());
			}	else return previousCase.getTotalSum(maxIndex);	// D'oh! Too big :).
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Block largeBlock=new Block();
		while (largeBlock.transitionEnd.compareTo(N)<0) largeBlock=new Block(largeBlock);
		BigInteger fullResult=largeBlock.getTotalSum(N);
		BigInteger result=fullResult.mod(MOD);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(fullResult);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
