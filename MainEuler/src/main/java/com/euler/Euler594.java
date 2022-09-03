package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

import com.euler.common.BitSetCursor;
import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.euler.common.LongMatrix;

// Relevant OEIS: https://oeis.org/A093937. In any case, I have the appropriate paper.
public class Euler594 {
	private final static int A_C=4;
	private final static int B_D=2;
	
	private static interface BidimensionalFamily	{
		public int getValue(int k,int l);
	}
	
	private static class XFamily implements BidimensionalFamily	{
		private final int[][] data;
		private final int a;
		public XFamily(int[][] data,int a)	{
			this.data=data;
			this.a=a;
		}
		@Override
		public int getValue(int k,int l)	{
			if ((k<=0)||(l<=0)) return 0;
			else if ((k>data.length)||(l>data[0].length)) return a;
			else return data[k-1][l-1];
		}
	}
	
	private static class YFamily implements BidimensionalFamily	{
		private final int[][] data;
		private final int c;
		public YFamily(int[][] data,int c)	{
			this.data=data;
			this.c=c;
		}
		@Override
		public int getValue(int k,int l)	{
			if ((l<=0)||(k>data.length)) return 0;
			else if ((k<=0)||(l>data[0].length)) return c;
			else return data[k-1][l-1];
		}
	}
	
	private abstract static class BidimensionalFamilyCreator<T extends BidimensionalFamily>	{
		private static int[][] copy(int[][] array)	{
			int[][] result=new int[array.length][];
			for (int i=0;i<result.length;++i) result[i]=Arrays.copyOf(array[i],array[i].length);
			return result;
		}
		private final int maxValue;
		private final int maxK;
		private final int maxL;
		private final List<int[][]> distributions;
		public BidimensionalFamilyCreator(int maxValue,int maxK,int maxL)	{
			this.maxValue=maxValue;
			this.maxK=maxK;
			this.maxL=maxL;
			distributions=new ArrayList<>();
		}
		private BitSet getBaseBitSet(int minValue)	{
			BitSet result=new BitSet(maxValue);
			result.set(minValue,1+maxValue);
			return result;
		}
		protected abstract void truncate(BitSet validValues,int upperValue);
		protected abstract T constructor(int[][] data);
		public void generateAll()	{
			int[][] holder=new int[maxK][maxL];
			continueGeneration(0,0,holder);
		}
		public List<T> getAllDistributions()	{
			return distributions.stream().map(this::constructor).collect(Collectors.toList());
		}
		private void continueGeneration(int row,int col,int[][] holder)	{
			int leftValue=(col>0)?holder[row][col-1]:0;
			BitSet validValues=getBaseBitSet(leftValue);
			if (row>0) truncate(validValues,holder[row-1][col]);
			for (BitSetCursor cursor=new BitSetCursor(validValues,true);cursor.moveNext();)	{
				holder[row][col]=cursor.elem();
				if (col<holder[row].length-1) continueGeneration(row,col+1,holder);
				else if (row<holder.length-1) continueGeneration(row+1,0,holder);
				else distributions.add(copy(holder));
			}
		}
	}
	private static class XFamilyCreator extends BidimensionalFamilyCreator<XFamily>	{
		private final int a;
		public XFamilyCreator(int a,int b,int d)	{
			super(a,b,d);
			this.a=a;
		}
		@Override
		protected void truncate(BitSet validValues,int upperValue)	{
			validValues.clear(0,upperValue);
		}
		@Override
		protected XFamily constructor(int[][] data)	{
			return new XFamily(data,a);
		}
	}
	private static class YFamilyCreator extends BidimensionalFamilyCreator<YFamily>	{
		private final int c;
		public YFamilyCreator(int c,int b,int d)	{
			super(c,b,d);
			this.c=c;
		}
		@Override
		protected void truncate(BitSet validValues,int upperValue)	{
			validValues.clear(upperValue+1,validValues.length());
		}
		@Override
		protected YFamily constructor(int[][] data)	{
			return new YFamily(data,c);
		}
	}
	private static class OctagonCalculator	{
		private final int b;
		private final int d;
		private final CombinatorialNumberCache combis;
		private final List<XFamily> xFamilies;
		private final List<YFamily> yFamilies;
		private final LongMatrix mHolder;
		private final LongMatrix pHolder;
		public OctagonCalculator(int a,int b,int c,int d)	{
			this.b=b;
			this.d=d;
			combis=new CombinatorialNumberCache(2*Math.max(a,c));
			XFamilyCreator xCreator=new XFamilyCreator(a,b,d);
			xCreator.generateAll();
			xFamilies=xCreator.getAllDistributions();
			YFamilyCreator yCreator=new YFamilyCreator(c,b,d);
			yCreator.generateAll();
			yFamilies=yCreator.getAllDistributions();
			mHolder=new LongMatrix(b);
			pHolder=new LongMatrix(d);
		}
		private class MatrixGenerator	{
			private final XFamily xs;
			private final YFamily ys;
			public MatrixGenerator(XFamily xs,YFamily ys)	{
				this.xs=xs;
				this.ys=ys;
			}
			private long getMij(int i,int j,int u)	{
				int upper=xs.getValue(j,u)-xs.getValue(i,u-1)+ys.getValue(j,u)-ys.getValue(i,u-1);
				int lower=xs.getValue(j,u)-xs.getValue(i,u-1)+j-i;
				return combis.get(upper,lower);
			}
			private long getPij(int i,int j,int v)	{
				int upper=xs.getValue(v,j)-xs.getValue(v-1,i)+ys.getValue(v-1,i)-ys.getValue(v,j);
				int lower=xs.getValue(v,j)-xs.getValue(v-1,i)+j-i;
				return combis.get(upper,lower);
			}
			private long getMDeterminant(int u)	{
				for (int i=1;i<=b;++i) for (int j=1;j<=b;++j) mHolder.assign(i-1,j-1,getMij(i,j,u));
				return EulerUtils.bareissAlgorithm(mHolder);
			}
			private long getPDeterminant(int v)	{
				for (int i=1;i<=d;++i) for (int j=1;j<=d;++j) pHolder.assign(i-1,j-1,getPij(i,j,v));
				return EulerUtils.bareissAlgorithm(pHolder);
			}
		}
		public long getT()	{
			long result=0;
			for (XFamily xs:xFamilies) for (YFamily ys:yFamilies)	{
				MatrixGenerator gen=new MatrixGenerator(xs,ys);
				long product=1l;
				for (int u=1;u<=d+1;++u) product*=gen.getMDeterminant(u);
				for (int v=1;v<=b+1;++v) product*=gen.getPDeterminant(v);
				result+=product;
			}
			return result;
		}
	}	
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		OctagonCalculator calculator=new OctagonCalculator(A_C,B_D,A_C,B_D);
		long result=calculator.getT();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
