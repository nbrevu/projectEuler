package com.euler.common;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import com.euler.common.ConvergentInversion.QuadraticRational;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;

public class Convergents {
	private static class ConvergentIterator implements Iterator<Convergent>	{
		private BigInteger prevP;
		private BigInteger prevQ;
		private BigInteger p;
		private BigInteger q;
		private LongCursor cfTerms;
		private boolean isNextCalculated;
		private boolean hasNext;
		public ConvergentIterator(ContinuedFraction contFraction)	{
			prevP=BigInteger.ZERO;
			prevQ=BigInteger.ONE;
			p=BigInteger.ONE;
			q=BigInteger.ZERO;
			cfTerms=contFraction.getTermsAsCursor();
		}
		@Override
		public boolean hasNext() {
			if (!isNextCalculated) calculateNext();
			return hasNext;
		}
		@Override
		public Convergent next() {
			if (!isNextCalculated) calculateNext();
			if (!hasNext) throw new NoSuchElementException();
			isNextCalculated=false;
			return new Convergent(p,q);
		}
		private void calculateNext()	{
			BigInteger newP,newQ;
			hasNext=cfTerms.moveNext();
			if (hasNext)	{
				BigInteger a=BigInteger.valueOf(cfTerms.elem());
				newP=a.multiply(p).add(prevP);
				newQ=a.multiply(q).add(prevQ);
				prevP=p;
				prevQ=q;
				p=newP;
				q=newQ;
			}
			isNextCalculated=true;
		}
	}
	
	public static interface ContinuedFraction extends Iterable<Convergent>	{
		public LongCursor getTermsAsCursor();
		public default Iterator<Convergent> iterator() {
			return new ConvergentIterator(this);
		}
	}
	
	private static class QuadraticRationalContinuedFractionSource implements LongCursor	{
		private BigInteger A;
		private BigInteger B;
		private BigInteger C;
		private BigInteger D;
		private final BigInteger bigIn;
		private final long root;
		private final double s;
		private long lastValue;
		public QuadraticRationalContinuedFractionSource(BigInteger A,BigInteger B,BigInteger C,BigInteger D,BigInteger bigIn,long root,double s)	{
			this.A=A;
			this.B=B;
			this.C=C;
			this.D=D;
			this.bigIn=bigIn;
			this.root=root;
			this.s=s;
			lastValue=-1;
		}
		public long getRoot()	{
			return root;
		}
		@Override
		public boolean moveNext() {
			BigInteger sqFactorInNum=B.multiply(C).subtract(A.multiply(D));
			BigInteger freeFactorInNum=A.multiply(C).multiply(bigIn).subtract(B.multiply(D));
			BigInteger denom=C.multiply(C).multiply(bigIn).subtract(D.multiply(D));
			double num=sqFactorInNum.doubleValue()*s+freeFactorInNum.doubleValue();
			double den=denom.doubleValue();
			lastValue=(long)(num/den);
			BigInteger bigConv=BigInteger.valueOf(lastValue);
			BigInteger AA=C;
			BigInteger BB=D;
			BigInteger CC=A.subtract(bigConv.multiply(C));
			BigInteger DD=B.subtract(bigConv.multiply(D));
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
			BigInteger A=BigInteger.ZERO;
			BigInteger B=BigInteger.ONE;
			BigInteger C=BigInteger.ONE;
			BigInteger D=BigInteger.valueOf(-root);
			BigInteger bigIn=BigInteger.valueOf(in);
			return new QuadraticRationalContinuedFractionSource(A,B,C,D,bigIn,root,s);
		}
		public static QuadraticRationalContinuedFractionSource forQuadraticRational(long a,long b,long c)	{
			long root=(long)Math.floor((a+Math.sqrt(b))/c);
			double s=Math.sqrt(b);
			BigInteger A=BigInteger.ZERO;
			BigInteger B=BigInteger.valueOf(c);
			BigInteger C=BigInteger.ONE;
			BigInteger D=BigInteger.valueOf(a-c*root);
			BigInteger bigIn=BigInteger.valueOf(b);
			return new QuadraticRationalContinuedFractionSource(A,B,C,D,bigIn,root,s);
		}
		public static QuadraticRationalContinuedFractionSource forQuadraticRational(QuadraticRational rat)	{
			BigInteger bigRoot=rat.a.add(rat.b.sqrt()).divide(rat.c);
			long root=bigRoot.longValue();
			double s=Math.sqrt(rat.b.doubleValue());
			BigInteger A=BigInteger.ZERO;
			BigInteger B=rat.c;
			BigInteger C=BigInteger.ONE;
			BigInteger D=rat.a.subtract(rat.c.multiply(bigRoot));
			BigInteger bigIn=rat.b;
			return new QuadraticRationalContinuedFractionSource(A,B,C,D,bigIn,root,s);
		}
	}
	
	public static class PeriodicContinuedFraction implements ContinuedFraction	{
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

	public static class InfiniteContinuedFraction implements ContinuedFraction	{
		private final QuadraticRationalContinuedFractionSource dataSource;
		private InfiniteContinuedFraction(QuadraticRationalContinuedFractionSource dataSource)	{
			this.dataSource=dataSource;
		}
		public static InfiniteContinuedFraction getForQuadraticRational(long a,long b,long c)	{
			return new InfiniteContinuedFraction(QuadraticRationalContinuedFractionSource.forQuadraticRational(a,b,c));
		}
		public static InfiniteContinuedFraction getForQuadraticRational(QuadraticRational rat)	{
			return new InfiniteContinuedFraction(QuadraticRationalContinuedFractionSource.forQuadraticRational(rat));
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

	public static class Convergent	{
		public final BigInteger p;
		public final BigInteger q;
		public Convergent(BigInteger p,BigInteger q)	{
			this.p=p;
			this.q=q;
		}
		@Override
		public String toString()	{
			return p+"/"+q;
		}
	}
}
