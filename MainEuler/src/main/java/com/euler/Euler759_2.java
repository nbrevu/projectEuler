package com.euler;

import java.math.BigInteger;
import java.util.stream.Stream;

public class Euler759_2 {
	private static class LesserBlock	{
		private final BigInteger n;
		private final BigInteger sum;
		public final BigInteger squareSum;
		public LesserBlock(BigInteger n,BigInteger sum,BigInteger squareSum)	{
			this.n=n;
			this.sum=sum;
			this.squareSum=squareSum;
		}
		public LesserBlock shift(BigInteger distance)	{
			BigInteger sum2=sum.add(distance.multiply(n));
			BigInteger squareSum2=squareSum.add(BigInteger.TWO.multiply(distance).multiply(sum)).add(distance.multiply(distance).multiply(n));
			return new LesserBlock(n,sum2,squareSum2);
		}
		public LesserBlock add(LesserBlock another)	{
			return new LesserBlock(n.add(another.n),sum.add(another.sum),squareSum.add(another.squareSum));
		}
		public final static LesserBlock BLOCK_ZERO=new LesserBlock(BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO);
	}
	
	private static class GreaterBlock	{
		public final LesserBlock[] blocks;
		private GreaterBlock(LesserBlock[] blocks)	{
			this.blocks=blocks;
		}
		public static GreaterBlock initial()	{
			LesserBlock[] result=new LesserBlock[1];
			result[0]=new LesserBlock(BigInteger.ONE,BigInteger.ONE,BigInteger.ONE);
			return new GreaterBlock(result);
		}
		public GreaterBlock next(BigInteger start)	{
			BigInteger start2=start.add(start);
			LesserBlock[] result=new LesserBlock[1+blocks.length];
			result[0]=blocks[0].shift(start);
			for (int i=1;i<blocks.length;++i) result[i]=blocks[i].shift(start).add(blocks[i-1].shift(start2));
			result[blocks.length]=blocks[blocks.length-1].shift(start2);
			return new GreaterBlock(result);
		}
		public GreaterBlock shift(BigInteger distance)	{
			LesserBlock[] result=new LesserBlock[blocks.length];
			for (int i=0;i<result.length;++i) result[i]=blocks[i].shift(distance);
			return new GreaterBlock(result);
		}
		public GreaterBlock shiftAndMove(BigInteger distance)	{
			LesserBlock[] result=new LesserBlock[1+blocks.length];
			result[0]=LesserBlock.BLOCK_ZERO;
			for (int i=1;i<result.length;++i) result[i]=blocks[i-1].shift(distance);
			return new GreaterBlock(result);
		}
		public GreaterBlock add(GreaterBlock another)	{
			int thisL=blocks.length;
			int otherL=another.blocks.length;
			LesserBlock[] result=new LesserBlock[Math.max(thisL,otherL)];
			if (thisL==otherL) for (int i=0;i<thisL;++i) result[i]=blocks[i].add(another.blocks[i]);
			else if (thisL<otherL)	{
				for (int i=0;i<thisL;++i) result[i]=blocks[i].add(another.blocks[i]);
				for (int i=thisL;i<otherL;++i) result[i]=another.blocks[i];
			}	else	{
				for (int i=0;i<otherL;++i) result[i]=blocks[i].add(another.blocks[i]);
				for (int i=otherL;i<thisL;++i) result[i]=blocks[i];
			}
			return new GreaterBlock(result);
		}
		public BigInteger getSquareSum()	{
			BigInteger result=BigInteger.ZERO;
			for (int i=0;i<blocks.length;++i)	{
				int multiplier2=(i+1)*(i+1);
				result=result.add(BigInteger.valueOf(multiplier2).multiply(blocks[i].squareSum));
			}
			return result;
		}
	}
	
	public static class FullBlockChain	{
		private static class MarkedBlock	{
			public final BigInteger start;	// It's also the length.
			public final GreaterBlock block;
			public MarkedBlock(BigInteger start,GreaterBlock block)	{
				this.start=start;
				this.block=block;
			}
		}
		private final MarkedBlock blocks[];
		public FullBlockChain(BigInteger limit)	{
			Stream.Builder<MarkedBlock> contents=Stream.builder();
			GreaterBlock currentBlock=GreaterBlock.initial();
			BigInteger position=BigInteger.ONE;
			for (;;)	{
				contents.add(new MarkedBlock(position,currentBlock));
				BigInteger nextPosition=position.add(position);
				if (nextPosition.compareTo(limit)>0) break;
				currentBlock=currentBlock.next(position);
				position=nextPosition;
			}
			blocks=contents.build().toArray(MarkedBlock[]::new);
		}
		private GreaterBlock getFullSum(int upTo)	{
			GreaterBlock result=blocks[0].block;
			for (int i=1;i<=upTo;++i) result=result.add(blocks[i].block);
			return result;
		}
		private GreaterBlock getPartialSum(int block,BigInteger limit)	{
			MarkedBlock markedBlock=blocks[block];
			BigInteger size=markedBlock.start;
			GreaterBlock blockData=markedBlock.block;
			BigInteger firstStart=size.add(size);
			BigInteger firstEnd=firstStart.add(size).subtract(BigInteger.ONE);
			int compare=limit.compareTo(firstEnd);
			if (compare==0) return blockData.shift(size);
			else if (compare<0)	{
				GreaterBlock previous=getPartialSum(block-1,limit.subtract(size));
				return previous.shift(size);
			}	else	{
				GreaterBlock firstHalf=blockData.shift(size);
				GreaterBlock secondHalf=getPartialSum(block-1,limit.subtract(firstStart)).shiftAndMove(firstStart);
				return firstHalf.add(secondHalf);
			}
		}
		public BigInteger getSquareSum(BigInteger limit)	{
			if (blocks[blocks.length-1].start.multiply(BigInteger.valueOf(4l)).compareTo(limit)<=0) throw new RuntimeException("Too low.");
			int lastBlock=blocks.length-1;
			for (;;)	{
				BigInteger end=blocks[lastBlock].start.multiply(BigInteger.TWO).subtract(BigInteger.ONE);
				if (end.equals(limit)) return getFullSum(lastBlock).getSquareSum();
				else if (end.compareTo(limit)>0) --lastBlock;
				else break;
			}
			return getFullSum(lastBlock).add(getPartialSum(lastBlock,limit)).getSquareSum();
		}
	}
	
	private final static BigInteger LIMIT=BigInteger.valueOf(10l).pow(16);
	private final static BigInteger MOD=BigInteger.valueOf(1_000_000_007l);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		FullBlockChain chain=new FullBlockChain(LIMIT);
		BigInteger fullResult=chain.getSquareSum(LIMIT);
		BigInteger result=fullResult.mod(MOD);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(fullResult);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
