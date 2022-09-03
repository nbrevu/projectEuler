package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler325_6 {
	/*
	 * The correct way would be to use mods all over the operations, but you know, I want to see the BigInteger itself, since I expect it to
	 * have exactly 48 digits, and to be very quickly computed.
	 */
	private final static long LIMIT=LongMath.pow(10l,16);
	private final static long MOD=LongMath.pow(7l,10);
	
	private static class CountState	{
		public final BigInteger currentIndex;
		public final BigInteger currentWidth;
		public final BigInteger currentSum;
		public CountState(BigInteger currentIndex,BigInteger currentWidth,BigInteger currentSum)	{
			this.currentIndex=currentIndex;
			this.currentWidth=currentWidth;
			this.currentSum=currentSum;
		}
	}
	
	private static BigInteger sumConsecutive(BigInteger x0,BigInteger xf)	{
		BigInteger twiceSum=x0.add(xf);
		BigInteger amount=xf.add(BigInteger.ONE).subtract(x0);
		return amount.multiply(twiceSum).shiftRight(1);
	}
	
	private static class FibonacciBlock	{
		private final BigInteger length;	// Length covered by this block (typically a Fibonacci number itself).
		private final BigInteger addedCount;	// Increase in width caused by this block.
		private final BigInteger addedTerms;	// Total amount of terms in the "shape" of this block.
		private final BigInteger addedXSum;	// Added value to the X terms of the "shape".
		private final BigInteger addedYSum;	// Added value to the Y terms of the "shape".
		private final FibonacciBlock prev2;
		private final FibonacciBlock prev;
		private FibonacciBlock(long length,long addedCount,long addedTerms,long addedXSum,long addedYSum)	{
			this.length=BigInteger.valueOf(length);
			this.addedCount=BigInteger.valueOf(addedCount);
			this.addedTerms=BigInteger.valueOf(addedTerms);
			this.addedXSum=BigInteger.valueOf(addedXSum);
			this.addedYSum=BigInteger.valueOf(addedYSum);
			this.prev2=null;
			this.prev=null;
		}
		public BigInteger getLength()	{
			return length;
		}
		public FibonacciBlock(FibonacciBlock prev2,FibonacciBlock prev)	{
			this.length=prev2.length.add(prev.length);
			this.addedCount=prev2.addedCount.add(prev.addedCount);
			/*
			 * The following terms are all calculated using the same structure:
			 * (A block) + (B block) + (inner rectangular block).
			 * A block is stored directly; B block is modified by some offset; and the inner rectangular block is added from A and B's data.
			 */
			this.addedTerms=prev2.addedTerms.add(prev.addedTerms).add(prev2.addedCount.multiply(prev.length));
			this.addedXSum=prev2.addedXSum.add(prev.addedXSum.add(prev2.length.multiply(prev.addedTerms))).add(prev2.addedCount.multiply(sumConsecutive(prev2.length,this.length.subtract(BigInteger.ONE))));
			this.addedYSum=prev2.addedYSum.add(prev.addedYSum.add(prev.addedTerms.multiply(prev2.length.add(prev2.addedCount)))).add(prev2.length.add(this.length).add(prev2.addedCount).multiply(prev2.addedCount).multiply(prev.length).shiftRight(1));
			this.prev2=prev2;
			this.prev=prev;
		}
		public final static FibonacciBlock BLOCK_ZERO=new FibonacciBlock(1,0,0,0,0);
		public final static FibonacciBlock BLOCK_ONE=new FibonacciBlock(1,1,1,0,1);
		public CountState advanceTotal(CountState state)	{
			BigInteger index=state.currentIndex.add(length);
			BigInteger width=state.currentWidth.add(addedCount);
			BigInteger sum=state.currentSum.add(sumBlockX(state)).add(sumShapeX(state)).add(sumBlockY(state)).add(sumShapeY(state));
			return new CountState(index,width,sum);
		}
		public CountState advancePartial(CountState state,BigInteger amount)	{
			int comparison=amount.compareTo(prev2.length);
			if (comparison<0) return prev2.advancePartial(state,amount);
			else if (comparison==0) return prev2.advanceTotal(state);
			else	{
				CountState intermediate=prev2.advanceTotal(state);
				return prev.advancePartial(intermediate,amount.subtract(prev2.length));
			}
		}
		private BigInteger sumBlockX(CountState state)	{
			return state.currentWidth.multiply(sumConsecutive(state.currentIndex,state.currentIndex.add(length).subtract(BigInteger.ONE)));
		}
		private BigInteger sumShapeX(CountState state)	{
			return addedTerms.multiply(state.currentIndex).add(addedXSum);
		}
		private BigInteger sumBlockY(CountState state)	{
			/*
			 * The amount to sum here is: (average of indices+average of counters)*(amount of indices)*(amount of counters).
			 * "Indices" goes from index to index+length-1. "Counters" goes from 1 to width.
			 * But, it happens that average of indices+average of counters can be calculated as average of (indices+counters).
			 * So we can do: firstIndex+lastIndex+1+lastWidth (and then divide by two).
			 * BUT! Since lastIndex=firstIndex+length-1, we can calculate the double of the sum by using this:
			 */
			BigInteger twiceSum=state.currentIndex.add(state.currentIndex).add(length).add(state.currentWidth);
			BigInteger amount=length.multiply(state.currentWidth);
			/*
			 * In order for twiceSum to be odd, ONE (but not both) of {length,currentWidth} must be odd. This means that the other is even.
			 * And so, length*currentWidth. And the product of twiceSum times amount is always an even number.
			 */
			return twiceSum.multiply(amount).shiftRight(1);
		}
		private BigInteger sumShapeY(CountState state)	{
			return state.currentIndex.add(state.currentWidth).multiply(addedTerms).add(addedYSum);
		}
	}
	
	private static BigInteger getFullWythoffSum(BigInteger limit)	{
		FibonacciBlock prev=FibonacciBlock.BLOCK_ZERO;	// For 1.
		FibonacciBlock curr=FibonacciBlock.BLOCK_ONE;	// For 2.
		CountState state=new CountState(BigInteger.TWO,BigInteger.ZERO,BigInteger.ZERO);
		for (;;)	{
			BigInteger reached=state.currentIndex.add(curr.getLength());
			int comparison=reached.compareTo(limit);
			if (comparison<0)	{
				state=curr.advanceTotal(state);
				FibonacciBlock next=new FibonacciBlock(prev,curr);
				prev=curr;
				curr=next;
			}	else if (comparison==0)	{
				state=curr.advanceTotal(state);
				break;
			}	else	{
				state=curr.advancePartial(state,limit.subtract(state.currentIndex));
				break;
			}
		}
		return state.currentSum;
	}
	
	// I could only see this thanks to geometry. It's the kind of damn beautiful thing that average humans can't appreciate.
	private static BigInteger getTruncatedWythoffSum(BigInteger xLimit,BigInteger limit)	{
		return limit.subtract(xLimit).multiply(sumConsecutive(xLimit,limit));
	}
	
	private static BigInteger getWythoffLimit(long in)	{
		// Calculation of in/phi, with enough precision.
		BigDecimal sq5_1=BigDecimal.valueOf(5).sqrt(new MathContext(20)).subtract(BigDecimal.ONE);
		BigDecimal result=sq5_1.multiply(BigDecimal.valueOf(in/2));
		return result.setScale(0,RoundingMode.UP).toBigInteger();
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger wythoffLimit=getWythoffLimit(LIMIT);
		BigInteger s1=getFullWythoffSum(wythoffLimit);
		BigInteger s2=getTruncatedWythoffSum(wythoffLimit,BigInteger.valueOf(LIMIT));
		BigInteger bigResult=s1.add(s2);
		long result=bigResult.mod(BigInteger.valueOf(MOD)).longValue();
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(bigResult);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
