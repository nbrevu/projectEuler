package com.euler.common;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;

public class LongConvergents {
	private static class ConvergentIterator implements Iterator<LongConvergent>	{
		private long prevP;
		private long prevQ;
		private long p;
		private long q;
		private LongCursor cfTerms;
		private boolean isNextCalculated;
		private boolean hasNext;
		public ConvergentIterator(LongContinuedFraction contFraction)	{
			prevP=0l;
			prevQ=1l;
			p=1l;
			q=0l;
			cfTerms=contFraction.getTermsAsCursor();
		}
		@Override
		public boolean hasNext() {
			if (!isNextCalculated) calculateNext();
			return hasNext;
		}
		@Override
		public LongConvergent next() {
			if (!isNextCalculated) calculateNext();
			if (!hasNext) throw new NoSuchElementException();
			isNextCalculated=false;
			return new LongConvergent(p,q);
		}
		private void calculateNext()	{
			long newP,newQ;
			hasNext=cfTerms.moveNext();
			if (hasNext)	{
				long a=cfTerms.elem();
				newP=a*p+prevP;
				newQ=a*q+prevQ;
				prevP=p;
				prevQ=q;
				p=newP;
				q=newQ;
			}
			isNextCalculated=true;
		}
	}
	
	public static interface LongContinuedFraction extends Iterable<LongConvergent>	{
		public LongCursor getTermsAsCursor();
		public default Iterator<LongConvergent> iterator() {
			return new ConvergentIterator(this);
		}
	}
	
	private static class QuadraticRationalContinuedFractionSource implements LongCursor	{
		private long A;
		private long B;
		private long C;
		private long D;
		private final long in;
		private final long root;
		private final double s;
		private long lastValue;
		public QuadraticRationalContinuedFractionSource(long A,long B,long C,long D,long in,long root,double s)	{
			this.A=A;
			this.B=B;
			this.C=C;
			this.D=D;
			this.in=in;
			this.root=root;
			this.s=s;
			lastValue=-1;
		}
		public long getRoot()	{
			return root;
		}
		@Override
		public boolean moveNext() {
			long sqFactorInNum=B*C-A*D;
			long freeFactorInNum=A*C*in-B*D;
			long den=C*C*in-D*D;
			double num=sqFactorInNum*s+freeFactorInNum;
			lastValue=(long)(num/den);
			long AA=C;
			long BB=D;
			long CC=A-lastValue*C;
			long DD=B-lastValue*D;
			A=AA;
			B=BB;
			C=CC;
			D=DD;
			return true;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		@Override
		public long elem() {
			return lastValue;
		}
		@Override
		public void forEachForward(LongConsumer arg0) {
			throw new UnsupportedOperationException();
		}
		public static QuadraticRationalContinuedFractionSource forSquareRoot(long in)	{
			long root=LongMath.sqrt(in,RoundingMode.DOWN);
			if (root*root==in) throw new IllegalArgumentException("No perfect squares allowed!");
			double s=Math.sqrt(in);
			long A=0l;
			long B=1l;
			long C=1l;
			long D=-root;
			return new QuadraticRationalContinuedFractionSource(A,B,C,D,in,root,s);
		}
		public static QuadraticRationalContinuedFractionSource forQuadraticRational(long a,long b,long c)	{
			long root=(long)Math.floor((a+Math.sqrt(b))/c);
			double s=Math.sqrt(b);
			long A=0l;
			long B=c;
			long C=1l;
			long D=a-c*root;
			return new QuadraticRationalContinuedFractionSource(A,B,C,D,b,root,s);
		}
	}
	
	public static class PeriodicContinuedFraction implements LongContinuedFraction	{
		private final long root;
		private final long[] periodicConvergents;
		private PeriodicContinuedFraction(long root,long[] periodicConvergents)	{
			this.root=root;
			this.periodicConvergents=periodicConvergents;
		}
		public static PeriodicContinuedFraction getForSquareRootOf(long in)	{
			QuadraticRationalContinuedFractionSource continuedFractionSource=QuadraticRationalContinuedFractionSource.forSquareRoot(in);
			List<Long> convergents=new ArrayList<>();
			for (;continuedFractionSource.moveNext();)	{
				long convergent=continuedFractionSource.elem();
				convergents.add(convergent);
				if (convergent==2*continuedFractionSource.getRoot()) break;	// Yep. See http://mathworld.wolfram.com/PeriodicContinuedFraction.html.
			}
			long[] arrayConvergents=convergents.stream().mapToLong(Long::longValue).toArray();
			return new PeriodicContinuedFraction(continuedFractionSource.getRoot(),arrayConvergents);
		}
		@Override
		public LongCursor getTermsAsCursor()	{
			return new LongCursor()	{
				int index=-2;
				@Override
				public boolean moveNext() {
					++index;
					if (index>=periodicConvergents.length) index=0;
					return true;
				}
				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
				@Override
				public long elem() {
					return (index<0)?root:periodicConvergents[index];
				}
				@Override
				public void forEachForward(LongConsumer arg0) {
					throw new UnsupportedOperationException();
				}
			};
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append('[').append(root).append("; ");
			for (int i=0;i<periodicConvergents.length;++i)	{
				if (i!=0) sb.append(", ");
				sb.append(periodicConvergents[i]);
			}
			sb.append(']');
			return sb.toString();
		}
		public int getPeriodLength()	{
			return periodicConvergents.length;
		}
	}

	public static class InfiniteContinuedFraction implements LongContinuedFraction	{
		private final QuadraticRationalContinuedFractionSource dataSource;
		private InfiniteContinuedFraction(QuadraticRationalContinuedFractionSource dataSource)	{
			this.dataSource=dataSource;
		}
		public static InfiniteContinuedFraction getForQuadraticRational(long a,long b,long c)	{
			return new InfiniteContinuedFraction(QuadraticRationalContinuedFractionSource.forQuadraticRational(a,b,c));
		}
		@Override
		public LongCursor getTermsAsCursor() {
			return new LongCursor()	{
				boolean isInitial=true;
				long value;
				@Override
				public boolean moveNext() {
					if (isInitial)	{
						value=dataSource.getRoot();
						isInitial=false;
					}	else	{
						dataSource.moveNext();
						value=dataSource.elem();
					}
					return true;
				}
				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
				@Override
				public long elem() {
					return value;
				}
				@Override
				public void forEachForward(LongConsumer arg0) {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	public static class LongConvergent	{
		public final long p;
		public final long q;
		public LongConvergent(long p,long q)	{
			this.p=p;
			this.q=q;
		}
		@Override
		public String toString()	{
			return p+"/"+q;
		}
	}
}
