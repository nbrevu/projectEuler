package com.euler;

public class Euler325_5 {
	private final static long LIMIT=10000;
	
	private final static double PHI=(1.0+Math.sqrt(5.0))/2.0;
	
	/*
	 * Wythoff(x)=LIMIT -> floor(phi*x)=LIMIT -> phi*x>=LIMIT -> x=ceil(LIMIT/phi).	
	 */
	private static long getWythoffLimit(long in)	{
		return (long)Math.ceil(in/PHI);
	}
	
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
		public long getLength()	{
			return length;
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
			if (amount<prev2.length) return prev2.advancePartial(state,amount);
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
	
	private static long getFullWythoffSum(long limit)	{
		FibonacciBlock prev=FibonacciBlock.BLOCK_ZERO;	// For 1.
		FibonacciBlock curr=FibonacciBlock.BLOCK_ONE;	// For 2.
		CountState state=new CountState(2,0,0);
		for (;;)	{
			long reached=state.currentIndex+curr.getLength();
			if (reached<limit)	{
				state=curr.advanceTotal(state);
				FibonacciBlock next=new FibonacciBlock(prev,curr);
				prev=curr;
				curr=next;
			}	else if (reached==limit)	{
				state=curr.advanceTotal(state);
				break;
			}	else	{
				state=curr.advancePartial(state,limit-state.currentIndex);
				break;
			}
		}
		return state.currentSum;
	}
	
	// I could only see this thanks to geometry. It's the kind of damn beautiful thing that average humans can't appreciate.
	private static long getTruncatedWythoffSum(long xLimit,long limit)	{
		return (limit-xLimit)*sumConsecutive(xLimit,limit);
	}
	
	public static void main(String[] args)	{
		long wythoffLimit=getWythoffLimit(LIMIT);
		long s1=getFullWythoffSum(wythoffLimit);
		long s2=getTruncatedWythoffSum(wythoffLimit,LIMIT);
		long result=s1+s2;
		System.out.println(result);
	}
}
