package com.euler;

import java.math.BigInteger;

public class Euler775_2 {
	private final static BigInteger N=BigInteger.TEN.pow(16);
	private final static BigInteger MOD=BigInteger.valueOf(1_000_000_007l);
	
	private final static BigInteger THREE=BigInteger.valueOf(3);
	private final static BigInteger FOUR=BigInteger.valueOf(4);
	private final static BigInteger SIX=BigInteger.valueOf(6);
	
	private static class Block	{
		public final BigInteger g;
		public final BigInteger sumG;
		public final BigInteger lastValue;
		public final BigInteger length;
		/*
		 * "s" is the cumulative sum of differences in this block. In practice, this is 2*f(x)-2*f(y), where:
		 * - f(n) is the sequence http://oeis.org/A007818.
		 * - x is the latest value from the block (or, if referring to inside the block, the value of the sequence).
		 * - y is the latest value from the previous block.
		 */
		public final BigInteger s;
		// "t" is the cumulative sum of s all over the block.
		public final BigInteger t;
		private Block(long g,long sumG,long lastValue,long length,long s,long t)	{
			this.g=BigInteger.valueOf(g);
			this.sumG=BigInteger.valueOf(sumG);
			this.lastValue=BigInteger.valueOf(lastValue);
			this.length=BigInteger.valueOf(length);
			this.s=BigInteger.valueOf(s);
			this.t=BigInteger.valueOf(t);
		}
		private Block(BigInteger g,BigInteger sumG,BigInteger lastValue,BigInteger length,BigInteger s,BigInteger t)	{
			this.g=g;
			this.sumG=sumG;
			this.lastValue=lastValue;
			this.length=length;
			this.s=s;
			this.t=t;
		}
		// Latest (third) block from the "1^3->2^3" transition, with values 2446.
		public final static Block INITIAL_BLOCK=new Block(24,80,8,4,16,34);
		public Block repeatLast()	{
			BigInteger nextValue=lastValue.add(length);
			BigInteger nextG=g.add(s);
			BigInteger nextSumG=sumG.add(length.multiply(g)).add(t);
			return new Block(nextG,nextSumG,nextValue,length,s,t);
		}
		public Block move(long addedLength)	{
			BigInteger l=BigInteger.valueOf(addedLength);
			BigInteger nextS=s.add(SIX.multiply(l).subtract(BigInteger.TWO));
			BigInteger triangular=l.multiply(l.add(BigInteger.ONE)).divide(BigInteger.TWO);
			BigInteger nextT=t.add(l.multiply(s.subtract(BigInteger.TWO))).add(triangular.multiply(SIX));
			BigInteger nextLength=length.add(l);
			BigInteger nextValue=lastValue.add(nextLength);
			BigInteger nextG=g.add(nextS);
			BigInteger nextSumG=sumG.add(nextLength.multiply(g)).add(nextT);
			return new Block(nextG,nextSumG,nextValue,nextLength,nextS,nextT);
		}
	}
	
	private static enum Action	{
		REPEAT_LAST	{
			@Override
			public Block getNext(Block lastBlock,long n)	{
				return lastBlock.repeatLast();
			}
		},	ADD_MIDDLE	{
			@Override
			public Block getNext(Block lastBlock,long n)	{
				return lastBlock.move(n-1);
			}
		},	ADD_END	{
			@Override
			public Block getNext(Block lastBlock,long n)	{
				return lastBlock.move(n);
			}
		};
		public abstract Block getNext(Block lastBlock,long n);
	}
	
	private static BigInteger getPrefixT(BigInteger x)	{
		BigInteger result=BigInteger.TWO.multiply(x);
		x=x.subtract(BigInteger.ONE);
		BigInteger n=BigInteger.ONE;
		boolean advanceNext=true;
		while (x.signum()>0)	{
			if (n.compareTo(x)>0) n=x;
			result=result.add(FOUR.multiply(x)).add(THREE.multiply(n.subtract(BigInteger.ONE)).multiply(BigInteger.TWO.multiply(x).subtract(n)));
			x=x.subtract(n);
			if (advanceNext)	{
				n=n.add(BigInteger.ONE);
				advanceNext=false;
			}	else advanceNext=true;
		}
		return result;
	}
	
	private static BigInteger calculate(BigInteger x)	{
		Block b=Block.INITIAL_BLOCK;
		Action[] actions=Action.values();
		for (long n=3;;++n) for (Action a:actions)	{
			Block prev=b;
			b=a.getNext(b,n);
			int comp=x.compareTo(b.lastValue);
			if (comp==0) return b.sumG;
			else if (comp<0)	{
				BigInteger diff=x.subtract(prev.lastValue);
				BigInteger t=getPrefixT(diff);
				return prev.sumG.add(diff.multiply(prev.g)).add(t);
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger n=calculate(N);
		BigInteger result=n.mod(MOD);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println(n);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
