package com.euler.common;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.math.IntMath;

public class Convergents {
	private static class ConvergentIterator implements Iterator<Convergent>	{
		private final PeriodicContinuedFraction contFraction;
		private BigInteger prevP;
		private BigInteger prevQ;
		private BigInteger p;
		private BigInteger q;
		private int index;
		public ConvergentIterator(PeriodicContinuedFraction contFraction)	{
			this.contFraction=contFraction;
			prevP=BigInteger.ONE.negate();
			prevQ=BigInteger.ONE.negate();
			p=BigInteger.ONE;
			q=BigInteger.ZERO;
			index=0;
		}
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public Convergent next() {
			BigInteger newP,newQ;
			if (index==0)	{
				newP=BigInteger.valueOf(contFraction.getRoot());
				newQ=BigInteger.ONE;
			}	else	{
				BigInteger a=BigInteger.valueOf(contFraction.getTerm(index));
				newP=a.multiply(p).add(prevP);
				newQ=a.multiply(q).add(prevQ);
			}
			++index;
			prevP=p;
			prevQ=q;
			p=newP;
			q=newQ;
			return new Convergent(p,q);
		}
	}

	public static class PeriodicContinuedFraction implements Iterable<Convergent>	{
		private final int root;
		private final int[] periodicConvergents;
		private PeriodicContinuedFraction(int root,int[] periodicConvergents)	{
			this.root=root;
			this.periodicConvergents=periodicConvergents;
		}
		public static PeriodicContinuedFraction getForSquareRootOf(int in)	{
			int root=IntMath.sqrt(in,RoundingMode.DOWN);
			if (root*root==in) throw new IllegalArgumentException("No perfect squares allowed!");
			double s=Math.sqrt(in);
			List<Integer> convergents=new ArrayList<>();
			BigInteger A=BigInteger.ZERO;
			BigInteger B=BigInteger.ONE;
			BigInteger C=BigInteger.ONE;
			BigInteger D=BigInteger.valueOf(-root);
			BigInteger bigIn=BigInteger.valueOf(in);
			for (;;)	{
				// Rationalization: (A*s(x)+B) / (C*s(x)+D) = [(BC-AD)*s(x)+(ACx-BD)] / [C^2x-D^2]
				BigInteger sqFactorInNum=B.multiply(C).subtract(A.multiply(D));
				BigInteger freeFactorInNum=A.multiply(C).multiply(bigIn).subtract(B.multiply(D));
				BigInteger denom=C.multiply(C).multiply(bigIn).subtract(D.multiply(D));
				double num=sqFactorInNum.doubleValue()*s+freeFactorInNum.doubleValue();
				double den=denom.doubleValue();
				int convergent=(int)(num/den);
				convergents.add(convergent);
				if (convergent==2*root) break;	// Yep. See http://mathworld.wolfram.com/PeriodicContinuedFraction.html.
				BigInteger bigConv=BigInteger.valueOf(convergent);
				BigInteger AA=C;
				BigInteger BB=D;
				BigInteger CC=A.subtract(bigConv.multiply(C));
				BigInteger DD=B.subtract(bigConv.multiply(D));
				A=AA;
				B=BB;
				C=CC;
				D=DD;
			}
			int[] arrayConvergents=convergents.stream().mapToInt(Integer::intValue).toArray();
			return new PeriodicContinuedFraction(root,arrayConvergents);
		}
		public int getRoot()	{
			return root;
		}
		public int getTerm(int n)	{
			--n;
			return periodicConvergents[n%periodicConvergents.length];
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
		@Override
		public Iterator<Convergent> iterator() {
			return new ConvergentIterator(this);
		}
		public int getPeriodLength()	{
			return periodicConvergents.length;
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
