package com.euler;

import java.util.List;

import com.google.common.math.LongMath;

public class Euler385_2 {
	private final static long LIMIT=LongMath.pow(10l,9);
	
	private static class LongPair	{
		public final long x;
		public final long y;
		public LongPair(long x,long y)	{
			this.x=x;
			this.y=y;
		}
	}
	private static abstract class IterationScheme	{
		private final LongPair start;
		private final long multiplier;
		protected final long limit;
		public IterationScheme(LongPair start,long multiplier,long limit)	{
			this.start=start;
			this.multiplier=multiplier;
			this.limit=limit;
		}
		protected abstract long area(LongPair pair);
		protected abstract boolean canFinish(LongPair pair);
		private static LongPair evolve(LongPair base)	{
			return new LongPair(2*base.x+base.y,3*base.x+2*base.y);
		}
		public long sumAllAreas()	{
			long result=0;
			for (LongPair p=start;!canFinish(p);p=evolve(p)) result+=area(p);
			return multiplier*result;
		}
	}
	private static class NonSymmetricIterationScheme extends IterationScheme	{
		/*
		 * In this case the triangle is (5x,3y)-(2x,-4y)-(-7x,y).
		 * The area is 3/2*abs(-20xy-6xy)=39*x*y.
		 * The limit is surpassed when 7x>limit or 4y>limit.
		 */
		public NonSymmetricIterationScheme(LongPair start,long limit)	{
			super(start,4,limit);
		}
		@Override
		protected long area(LongPair pair)	{
			return 39*pair.x*pair.y;
		}
		@Override
		protected boolean canFinish(LongPair pair)	{
			return (7*pair.x>limit)||(4*pair.y>limit);
		}
	}
	private static class SymmetricIterationScheme extends IterationScheme	{
		/*
		 * In this case the triangle is (x,y)-(x,-y),(-2x,0).
		 * The area is 3/2*abs(-xy-xy)=3*x*y.
		 * The limit is surpassed when 2x>limit or y>limit.
		 */
		public SymmetricIterationScheme(LongPair start,long limit)	{
			super(start,2,limit);
		}
		@Override
		protected long area(LongPair pair)	{
			return 3*pair.x*pair.y;
		}
		@Override
		protected boolean canFinish(LongPair pair)	{
			return (2*pair.x>limit)||(pair.y>limit);
		}
	}
	
	public static void main(String[] args)	{
		// Wow, this was easy :). 70%? No way.
		long tic=System.nanoTime();
		List<IterationScheme> schemes=List.of(new NonSymmetricIterationScheme(new LongPair(2,3),LIMIT),new SymmetricIterationScheme(new LongPair(4,3),LIMIT),new SymmetricIterationScheme(new LongPair(5,6),LIMIT));
		long result=0;
		for (IterationScheme s:schemes) result+=s.sumAllAreas();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
