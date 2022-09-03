package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Euler793_6 {
	private final static int N=1000003;
	
	private static class RowInterval	{
		public final int lower;
		public final int upper;
		public final int size;
		public final long lowest;
		public final long highest;
		public long currentLower;
		public long currentUpper;
		public RowInterval(int lower,int upper,long lowest,long highest)	{
			this.lower=lower;
			this.upper=upper;
			size=upper+1-lower;
			this.lowest=lowest;
			this.highest=highest;
			currentLower=lower;
			currentUpper=upper;
		}
	}
	
	private static List<RowInterval> calculateInitialBounds(long n,long[] numbers)	{
		long pairs=(n*(n-1))/2;
		long p=(pairs-1)/2;
		List<RowInterval> result=new ArrayList<>();
		for (long x=0;x<n;++x)	{
			long b=1+2*x;
			long c=2*p+2-n*n+n+2*x*n;
			long delta=b*b-4*c;
			long firstY=(delta<0)?(x+1):Math.max(x+1,(long)Math.ceil((b+Math.sqrt(delta))*0.5));
			double num=2*p+2+x*x+x;
			double den=2*x+2;
			long lastY=Math.min(n-1,(long)Math.floor(num/den));
			if (firstY<=lastY)	{
				int intFirstY=(int)firstY;
				int intLastY=(int)lastY;
				long rowValue=numbers[(int)x];
				long lowest=numbers[intFirstY]*rowValue;
				long highest=numbers[intLastY]*rowValue;
				result.add(new RowInterval(intFirstY,intLastY,lowest,highest));
			}
			else break;
		}
		return result;
	}
	
	private static class Finder	{
		private final long[] numbers;
		private final int validRows;
		private final List<RowInterval> rowBounds;
		private final long goal;
		public Finder(int n)	{
			numbers=new long[N];
			numbers[0]=290797;
			for (int i=1;i<N;++i) numbers[i]=(numbers[i-1]*numbers[i-1])%50515093l;
			Arrays.sort(numbers);
			rowBounds=calculateInitialBounds(N,numbers);
			validRows=rowBounds.size();
			long totalSize=0;
			for (int i=0;i<validRows;++i) totalSize+=rowBounds.get(i).size;
			goal=(totalSize-1)/2;
		}
		private long evaluate(int x,int y)	{
			// Calculate the amount of numbers that fall BELOW this value.
			long value=numbers[x]*numbers[y];
			long result=0;
			//*
			for (int i=0;i<validRows;++i)	{
				RowInterval rowData=rowBounds.get(i);
				if (i==x) result+=y-rowData.lower;
				else if (value<rowData.lowest) continue;
				else if (value>=rowData.highest) result+=rowData.size;
				else	{
					int position=Arrays.binarySearch(numbers,rowData.lower,1+rowData.upper,value/numbers[i]);
					if (position<0) position=-2-position;
					result+=position+1-rowData.lower;
				}
			}
			/*/
			for (int i=0;i<validRows;++i)	{
				RowInterval rowData=rowBounds.get(i);
				long n=numbers[i];
				int k=0;
				for (int j=rowData.lower;j<=rowData.upper;++j)	{
					long n2=n*numbers[j];
					if (n2<value)	{
						++result;
						++k;
					}	else break;
				}
				System.out.println("\tFor row "+i+" I believe that I increase in "+k+".");
			}
			//*/
			// System.out.println(String.format("Evaluating (%d,%d): %d.",x,y,result));
			return result;
		}
		public long search()	{
			// System.out.println(goal);
			Random r=new Random();
			int x=validRows/2;
			RowInterval row=rowBounds.get(x);
			int y=(row.lower+row.upper)/2;
			for (;;)	{
				long count=evaluate(x,y);
				if (count==goal) return numbers[x]*numbers[y];
				else if (count<goal)	{
					for (int i=0;i<=x;++i)	{
						row=rowBounds.get(i);
						row.currentLower=Math.max(row.currentLower,y+1);
					}
					boolean canMoveRight=y<rowBounds.get(x).currentUpper;
					boolean canMoveDown=(x<validRows)&&(y>=rowBounds.get(x+1).currentLower)&&(y<=rowBounds.get(x+1).currentUpper);
					if (!canMoveRight&&!canMoveDown) throw new IllegalStateException();
					else if (!canMoveRight) ++x;
					else if (!canMoveDown) ++y;
					else if (r.nextBoolean()) ++x;
					else ++y;
				}	else	{
					for (int i=x;i<validRows;++i)	{
						row=rowBounds.get(i);
						row.currentUpper=Math.min(row.currentUpper,y-1);
					}
					boolean canMoveLeft=y>rowBounds.get(x).currentLower;
					boolean canMoveUp=(x>0)&&(y>=rowBounds.get(x-1).currentLower)&&(y<=rowBounds.get(x-1).currentUpper);
					if (!canMoveLeft&&!canMoveUp) throw new IllegalStateException();
					else if (!canMoveLeft) --x;
					else if (!canMoveUp) --y;
					else if (r.nextBoolean()) --x;
					else --y;
				}
			}
		}
	}
	
	/*
	 * This is on the right track but it's also far too slow. Next idea: save the "limits" at each iteration and then use them aggressively
	 * to prune the ranges at each row. I will need to store the "remaining" part as well, updating it when I remove "lower" elements.
	 * 
	 * Anyway...
	 * 475808650131120
	 * Elapsed 5053.2884356 seconds. JAJA SI.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Finder f=new Finder(N);
		System.out.println(f.search());
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
