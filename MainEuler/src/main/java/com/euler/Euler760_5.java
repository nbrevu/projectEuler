package com.euler;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler760_5 {
	private final static BigInteger LIMIT=BigInteger.TEN.pow(18);
	private final static BigInteger MOD=BigInteger.valueOf(1_000_000_007l);
	
	private final static BigInteger THREE=BigInteger.valueOf(3);
	
	private static class ElementMultiplier	{
		public final BigInteger f2s;
		public final BigInteger normalPrefix;
		public final BigInteger normalSuffix;
		public final BigInteger specialPrefix;
		public final BigInteger specialSuffix;
		public ElementMultiplier(int s)	{
			BigInteger power2=BigInteger.TWO.pow(s);
			BigInteger power2Plus=power2.add(power2);
			f2s=power2.multiply(power2Plus.add(BigInteger.valueOf(2-s)));
			normalPrefix=power2Plus.multiply(power2.add(BigInteger.ONE));
			normalSuffix=f2s;
			specialPrefix=power2Plus.multiply(power2.subtract(BigInteger.ONE));
			specialSuffix=f2s.add(power2Plus.multiply(power2.subtract(THREE)));
		}
	}
	
	private static class BlockData	{
		public final BigInteger start;
		public final BigInteger indexCount;
		public final BigInteger indexSum;
		public final BigInteger functionSum;
		public final BigInteger previousLastValue;
		public final BigInteger currentLastValue;
		public BlockData(BigInteger start,BigInteger indexCount,BigInteger indexSum,BigInteger functionSum,BigInteger previousLastValue,BigInteger currentLastValue)	{
			this.start=start;
			this.indexCount=indexCount;
			this.indexSum=indexSum;
			this.functionSum=functionSum;
			this.previousLastValue=previousLastValue;
			this.currentLastValue=currentLastValue;
		}
	}
	
	private static class SumCalculator	{
		private final ElementMultiplier[] multipliers;
		private final BlockData[] data;
		private final Map<BigInteger,BigInteger> singleValueCache;
		private final IntObjMap<Map<BigInteger,BlockData>> partialBlockCache;
		private static BigInteger triangularMinus(BigInteger in)	{
			return in.multiply(in.subtract(BigInteger.ONE)).divide(BigInteger.TWO);
		}
		private static BigInteger shiftBlock(ElementMultiplier multiplier,BlockData block)	{
			BigInteger result=BigInteger.ZERO;
			result=result.add(multiplier.normalPrefix.multiply(block.functionSum));
			result=result.add(multiplier.normalSuffix.multiply(block.indexSum.add(block.indexCount)));
			result=result.add(multiplier.specialPrefix.multiply(block.functionSum.add(block.previousLastValue).subtract(block.currentLastValue)));
			result=result.add(multiplier.specialSuffix.multiply(block.indexSum));
			return result;
		}
		public SumCalculator(int maxSize)	{
			multipliers=new ElementMultiplier[maxSize];
			data=new BlockData[maxSize];
			singleValueCache=new HashMap<>();
			partialBlockCache=HashIntObjMaps.newMutableMap();
			for (int i=0;i<maxSize;++i) multipliers[i]=new ElementMultiplier(i);
			BigInteger four=BigInteger.valueOf(4);
			data[0]=new BlockData(BigInteger.ONE,BigInteger.ONE,BigInteger.ONE,four,BigInteger.ZERO,four);
			for (int i=1;i<maxSize;++i)	{
				BigInteger thisFunctionSum=multipliers[i].f2s;
				for (int j=1;j<=i;++j)	{
					ElementMultiplier multiplier=multipliers[j-1];
					BlockData prevBlock=data[i-j];
					thisFunctionSum=thisFunctionSum.add(shiftBlock(multiplier,prevBlock));
				}
				BlockData lastBlock=data[i-1];
				BigInteger newStart=lastBlock.start.add(lastBlock.start);
				BigInteger prevCount=lastBlock.indexCount;
				BigInteger thisIndexCount=prevCount.add(prevCount);
				BigInteger firstVal=BigInteger.TWO.pow(i);
				BigInteger lastVal=firstVal.add(firstVal).subtract(BigInteger.ONE);
				BigInteger indexSum=firstVal.add(lastVal).multiply(prevCount);
				BigInteger prevLastValue=lastBlock.currentLastValue;
				BigInteger currLastValue=lastBlock.currentLastValue.multiply(four).add(BigInteger.TWO.pow(i+2));
				data[i]=new BlockData(newStart,thisIndexCount,indexSum,thisFunctionSum,prevLastValue,currLastValue);
			}
		}
		private BigInteger doCalculateSingleValue(BigInteger x)	{
			if (x.signum()==0) return BigInteger.ZERO;
			int shift=x.getLowestSetBit();
			x=x.shiftRight(shift);
			if (x.equals(BigInteger.ONE)) return multipliers[shift].f2s;
			x=x.shiftRight(1);
			BigInteger suffixId=BigInteger.ONE.shiftLeft(shift);
			BigInteger prefixShift=suffixId.shiftLeft(1);
			BigInteger normalPrefix=calculateSingleValue(x).multiply(prefixShift).multiply(suffixId.add(BigInteger.ONE));
			BigInteger normalSuffix=x.add(BigInteger.ONE).multiply(multipliers[shift].f2s);
			BigInteger specialPrefix=calculateSingleValue(x.subtract(BigInteger.ONE)).multiply(prefixShift).multiply(suffixId.subtract(BigInteger.ONE));
			BigInteger specialSuffix=x.multiply(multipliers[shift].f2s.add(BigInteger.TWO.multiply(suffixId).multiply(suffixId.subtract(THREE))));
			return normalPrefix.add(normalSuffix).add(specialPrefix).add(specialSuffix);
		}
		private BigInteger calculateSingleValue(BigInteger n)	{
			// computeIfAbsent triggers a ConcurrentModificationException, so this has to be done manually.
			BigInteger result=singleValueCache.get(n);
			if (result==null)	{
				result=doCalculateSingleValue(n);
				singleValueCache.put(n,result);
			}
			return result;
		}
		private BlockData doGetPartialBlock(int index,BigInteger amount)	{
			// Assumes amount>=1.
			BigInteger start=data[index].start;
			BigInteger indexCount=amount;
			BigInteger indexSum=start.multiply(indexCount).add(triangularMinus(indexCount));
			BigInteger functionSum=multipliers[index].f2s;
			BigInteger previousLastValue=data[index].previousLastValue;
			amount=amount.subtract(BigInteger.ONE);
			BigInteger currentLastValue=calculateSingleValue(start.add(amount));
			if (amount.compareTo(BigInteger.ZERO)>0) for (int i=0;amount.signum()!=0;++i)	{
				BigInteger[] division=amount.divideAndRemainder(BigInteger.TWO);
				BigInteger partialAmount=division[0].add(division[1]);
				BlockData previousBlock=getPartialBlock(index-i-1,partialAmount);
				ElementMultiplier multiplier=multipliers[i];
				functionSum=functionSum.add(shiftBlock(multiplier,previousBlock));
				amount=division[0];
			}
			return new BlockData(start,indexCount,indexSum,functionSum,previousLastValue,currentLastValue);
		}
		private BlockData getPartialBlock(int index,BigInteger amount)	{
			Map<BigInteger,BlockData> partialMap=partialBlockCache.computeIfAbsent(index,(int unused)->new HashMap<>());
			return partialMap.computeIfAbsent(amount,(BigInteger x)->doGetPartialBlock(index,x));
		}
		public BigInteger getSum(BigInteger limit)	{
			BigInteger result=BigInteger.ZERO;
			for (int i=0;i<data.length;++i)	{
				BigInteger difference=limit.subtract(data[i].start.subtract(BigInteger.ONE));
				if (difference.signum()==0) return result;
				int compare=difference.compareTo(data[i].indexCount);
				if (compare>0) result=result.add(data[i].functionSum);
				else if (compare==0)	{
					result=result.add(data[i].functionSum);
					return result;
				}	else	{
					BlockData partialBlock=getPartialBlock(i,difference);
					result=result.add(partialBlock.functionSum);
					return result;
				}
			}
			throw new RuntimeException("Too big.");
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int size=LIMIT.bitLength();
		SumCalculator calculator=new SumCalculator(size);
		BigInteger fullResult=calculator.getSum(LIMIT);
		BigInteger result=fullResult.mod(MOD);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(fullResult);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
