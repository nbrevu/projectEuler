package com.euler;

public class Euler325_4 {
	private static class CountState	{
		public final long currentIndex;
		public final long currentWidth;
		public final long currentSum;
		public CountState(long currentIndex,long currentWidth,long currentSum)	{
			this.currentIndex=currentIndex;
			this.currentWidth=currentWidth;
			this.currentSum=currentSum;
		}
	}
	
	private static long sumConsecutive(long x0,long xf)	{
		long twiceSum=x0+xf;
		long amount=xf+1-x0;
		return (amount*twiceSum)/2;
	}
	
	private static class FibonacciBlock	{
		private final long length;	// Length covered by this block (typically a Fibonacci number itself).
		private final long addedCount;	// Increase in width caused by this block.
		private final long addedTerms;	// Total amount of terms in the "shape" of this block.
		private final long addedXSum;	// Added value to the X terms of the "shape".
		private final long addedYSum;	// Added value to the Y terms of the "shape".
		private final FibonacciBlock prev2;
		private final FibonacciBlock prev;
		private FibonacciBlock(long length,long addedCount,long addedTerms,long addedXSum,long addedYSum)	{
			this.length=length;
			this.addedCount=addedCount;
			this.addedTerms=addedTerms;
			this.addedXSum=addedXSum;
			this.addedYSum=addedYSum;
			this.prev2=null;
			this.prev=null;
		}
		@Override
		public String toString()	{
			return String.format("L=%d, C=%d, T=%d, X=%d, Y=%d.",length,addedCount,addedTerms,addedXSum,addedYSum);
		}
	
		public FibonacciBlock(FibonacciBlock prev2,FibonacciBlock prev)	{
			this.length=prev2.length+prev.length;
			this.addedCount=prev2.addedCount+prev.addedCount;
			/*
			 * The following terms are all calculated using the same structure:
			 * (A block) + (B block) + (inner rectangular block).
			 * A block is stored directly; B block is modified by some offset; and the inner rectangular block is added from A and B's data.
			 * The parentheses are omitted when not necesary any more.
			 */
			this.addedTerms=prev2.addedTerms+prev.addedTerms+prev2.addedCount*prev.length;
			this.addedXSum=prev2.addedXSum+(prev.addedXSum+prev2.length*prev.addedTerms)+(prev2.addedCount*sumConsecutive(prev2.length,this.length-1));
			this.addedYSum=prev2.addedYSum+(prev.addedYSum+prev.addedTerms*(prev2.length+prev2.addedCount))+(((prev2.length+this.length+prev2.addedCount)*prev2.addedCount*prev.length)/2);
			this.prev2=prev2;
			this.prev=prev;
		}
		public final static FibonacciBlock BLOCK_ZERO=new FibonacciBlock(1,0,0,0,0);
		public final static FibonacciBlock BLOCK_ONE=new FibonacciBlock(1,1,1,0,1);
		public CountState advanceTotal(CountState state)	{
			long index=state.currentIndex+length;
			long width=state.currentWidth+addedCount;
			long sum=state.currentSum+sumBlockX(state)+sumShapeX(state)+sumBlockY(state)+sumShapeY(state);
			return new CountState(index,width,sum);
		}
		public CountState advancePartial(CountState state,long amount)	{
			if (amount<=prev2.length) return prev2.advancePartial(state,amount);
			else if (amount==prev2.length) return prev2.advanceTotal(state);
			else	{
				CountState intermediate=prev2.advanceTotal(state);
				return prev.advancePartial(intermediate,amount-prev2.length);
			}
		}
		private long sumBlockX(CountState state)	{
			return state.currentWidth*sumConsecutive(state.currentIndex,state.currentIndex+length-1);
		}
		private long sumShapeX(CountState state)	{
			return addedTerms*state.currentIndex+addedXSum;
		}
		private long sumBlockY(CountState state)	{
			/*
			 * The amount to sum here is: (average of indices+average of counters)*(amount of indices)*(amount of counters).
			 * "Indices" goes from index to index+length-1. "Counters" goes from 1 to width.
			 * But, it happens that average of indices+average of counters can be calculated as average of (indices+counters).
			 * So we can do: firstIndex+lastIndex+1+lastWidth (and then divide by two).
			 * BUT! Since lastIndex=firstIndex+length-1, we can calculate the double of the sum by using this:
			 */
			long twiceSum=2*state.currentIndex+length+state.currentWidth;
			long amount=length*state.currentWidth;
			/*
			 * In order for twiceSum to be odd, ONE (but not both) of {length,currentWidth} must be odd. This means that the other is even.
			 * And so, length*currentWidth. And the product of twiceSum times amount is always an even number.
			 */
			return (twiceSum*amount)/2;
		}
		private long sumShapeY(CountState state)	{
			return (state.currentIndex+state.currentWidth)*addedTerms+addedYSum;
		}
	}
	
	public static void main(String[] args)	{
		// I almost have it...
		FibonacciBlock b0=FibonacciBlock.BLOCK_ZERO;	// For 1.
		FibonacciBlock b1=FibonacciBlock.BLOCK_ONE;	// For 2.
		FibonacciBlock b01=new FibonacciBlock(b0,b1);	// For 3-4.
		FibonacciBlock b101=new FibonacciBlock(b1,b01);	// For 5-7.
		FibonacciBlock b01101=new FibonacciBlock(b01,b101);	// For 8-12.
		System.out.println("0: "+b0+".");
		System.out.println("1: "+b1+".");
		System.out.println("01: "+b01+".");
		System.out.println("101: "+b101+".");
		System.out.println("01101: "+b01101+".");
	}
}
