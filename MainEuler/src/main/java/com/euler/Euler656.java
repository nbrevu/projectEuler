package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler656 {
	private final static int LIMIT=1000;
	private final static int COUNT=100;
	private final static BigInteger MOD=BigInteger.valueOf(LongMath.pow(10l,15));
	
	private static class Convergent	{
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
	
	private static class PeriodicContinuedFraction implements Iterable<Convergent>	{
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
					// System.out.println("ACHTUNG: ¿es muy grande este valor? "+contFraction.getRoot()+".");
					newP=BigInteger.valueOf(contFraction.getRoot());
					newQ=BigInteger.ONE;
				}	else	{
					// System.out.println("ACHTUNG: ¿es muy grande este valor? "+contFraction.getTerm(index)+".");
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
		// http://mathworld.wolfram.com/PeriodicContinuedFraction.html
		private final int root;
		private final int[] periodicConvergents;
		private PeriodicContinuedFraction(int root,int[] periodicConvergents)	{
			this.root=root;
			this.periodicConvergents=periodicConvergents;
		}
		public static PeriodicContinuedFraction getForSquareRootOf(int in)	{
			int root=IntMath.sqrt(in,RoundingMode.DOWN);
			if (root*root==in) throw new IllegalArgumentException("No perfect squares allowed!");
			double x=Math.sqrt(in);
			int lastConvergent=root;
			List<Integer> convergents=new ArrayList<>();
			for (;;)	{
				double y=1/(x-lastConvergent);
				int convergent=(int)Math.floor(y);
				convergents.add(convergent);
				if (convergent==2*root) break;	// Yep. See the link above.
				x=y;
				lastConvergent=convergent;
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
	}
	
	private static class PalindromeIndexIterator implements Iterator<BigInteger>	{
		private static class Group implements Iterator<BigInteger>	{
			private BigInteger currentValue;
			private final BigInteger increment;
			private final BigInteger lastValue;
			public Group(BigInteger initialValue,BigInteger increment,BigInteger lastValue)	{
				// System.out.println("Creando grupo: "+initialValue+", "+increment+", "+lastValue+".");
				this.currentValue=initialValue;	// YES, the first value is not actually initialValue but initialValue+increment.
				this.increment=increment;
				this.lastValue=lastValue;
			}
			@Override
			public boolean hasNext() {
				return currentValue.compareTo(lastValue)<0;
			}
			@Override
			public BigInteger next() {
				BigInteger result=currentValue;
				currentValue=currentValue.add(increment);
				return result;
			}
		}
		private final Iterator<Convergent> convergents;
		private Group currentGroup;
		private BigInteger lastQ;
		public PalindromeIndexIterator(int n)	{
			convergents=PeriodicContinuedFraction.getForSquareRootOf(n).iterator();
			convergents.next();
			BigInteger firstQ=convergents.next().q;
			BigInteger increment=convergents.next().q;
			BigInteger end=convergents.next().q;
			currentGroup=new Group(firstQ,increment,end);
			lastQ=end;
		}
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public BigInteger next() {
			if (!currentGroup.hasNext())	{
				BigInteger increment=convergents.next().q;
				BigInteger newQ=convergents.next().q;
				currentGroup=new Group(lastQ,increment,newQ);
				lastQ=newQ;
			}
			return currentGroup.next();
		}
	}
	
	private static boolean isPerfectSquare(int in)	{
		int sq=IntMath.sqrt(in,RoundingMode.DOWN);
		return (sq*sq)==in;
	}
	
	private static BigInteger getH(int count,int beta)	{
		Iterator<BigInteger> palindromeIndexIterator=new PalindromeIndexIterator(beta);
		BigInteger result=BigInteger.ZERO;
		for (int i=0;i<count;++i) result=result.add(palindromeIndexIterator.next());
		return result;
	}
	
	public static void main(String[] args)	{
		// Why is this wrong? The result for 31 is right and I can't be having precision problems :(.
		BigInteger result=BigInteger.ZERO;
		for (int i=2;i<=LIMIT;++i) if (!isPerfectSquare(i)) result=result.add(getH(COUNT,i));
		System.out.println(result);
		System.out.println(result.mod(MOD));
	}
}
