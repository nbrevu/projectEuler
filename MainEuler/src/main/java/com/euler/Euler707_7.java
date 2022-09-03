package com.euler;

import java.util.BitSet;

import com.euler.common.EulerUtils;

public class Euler707_7 {
	private static class Z2Matrix	{
		private final BitSet[] data;
		public Z2Matrix(int size)	{
			data=new BitSet[size];
			for (int i=0;i<size;++i) data[i]=new BitSet(size);
		}
		public static Z2Matrix eye(int size)	{
			Z2Matrix result=new Z2Matrix(size);
			for (int i=0;i<size;++i) result.set(i,i);
			return result;
		}
		public static Z2Matrix jay(int size)	{
			Z2Matrix result=new Z2Matrix(size);
			for (int i=0;i<size;++i) result.set(i,i);
			for (int i=1;i<size;++i)	{
				result.set(i-1,i);
				result.set(i,i-1);
			}
			return result;
		}
		public boolean get(int i,int j)	{
			return data[i].get(j);
		}
		public void set(int i,int j)	{
			data[i].set(j);
		}
		public void xorRow(int source,int target)	{
			data[target].xor(data[source]);
		}
		public BitSet getRow(int row)	{
			return data[row];
		}
		public void xorRow(BitSet source,int target)	{
			data[target].xor(source);
		}
		public int rankDestructive()	{
			int n=data.length;
			for (int i=0;i<n;++i)	{
				if (!get(i,i))	{
					int rowToPivot=-1;
					for (int j=i;(j<n)&&(rowToPivot<0);++j) for (int k=i;k<n;++k) if (get(k,j))	{
						rowToPivot=k;
						break;
					}
					if (rowToPivot<0) return i;
					if (rowToPivot!=i) xorRow(rowToPivot,i);
					// Sanity check!
					if (!get(i,i)) throw new IllegalStateException("Mal.");
				}
				for (int j=i+1;j<n;++j) if (data[j].get(i)) xorRow(i,j);
			}
			return n;
		}
	}
	private static class TridiagZ2Matrix	{
		private final int sizeA;
		private final int sizeB;
		private final Z2Matrix[][] blocks;
		public TridiagZ2Matrix(int sizeA,int sizeB)	{
			this.sizeA=sizeA;
			this.sizeB=sizeB;
			if ((sizeA<2)||(sizeB<2)) throw new RuntimeException("No.");
			blocks=new Z2Matrix[sizeA][];
			blocks[0]=new Z2Matrix[3];
			blocks[0][0]=Z2Matrix.jay(sizeB);
			blocks[0][1]=Z2Matrix.eye(sizeB);
			blocks[0][2]=new Z2Matrix(sizeB);
			for (int i=1;i<sizeA-1;++i)	{
				blocks[i]=new Z2Matrix[4];
				blocks[i][0]=Z2Matrix.eye(sizeB);
				blocks[i][1]=Z2Matrix.jay(sizeB);
				blocks[i][2]=Z2Matrix.eye(sizeB);
				blocks[i][3]=new Z2Matrix(sizeB);
			}
			blocks[sizeA-1]=new Z2Matrix[3];
			blocks[sizeA-1][0]=Z2Matrix.eye(sizeB);
			blocks[sizeA-1][1]=Z2Matrix.jay(sizeB);
			blocks[sizeA-1][2]=new Z2Matrix(sizeB);
		}
		public int rankDestructive()	{
			for (int currentBlock=0;currentBlock<sizeA;++currentBlock)	{
				int mainPos=(currentBlock==0)?0:1;
				Z2Matrix main=blocks[currentBlock][mainPos];
				boolean somethingBelow=(currentBlock!=sizeA-1);
				Z2Matrix right,right2,below,diag,diag2;
				if (somethingBelow)	{
					right=blocks[currentBlock][1+mainPos];
					right2=blocks[currentBlock][2+mainPos];
					below=blocks[1+currentBlock][0];
					diag=blocks[1+currentBlock][1];
					diag2=blocks[1+currentBlock][2];
				}	else	{
					right=null;
					right2=null;
					below=null;
					diag=null;
					diag2=null;
				}
				for (int currentRow=0;currentRow<sizeB;++currentRow)	{
					if (!main.get(currentRow,currentRow))	{
						// Pivot.
						boolean pivotAtHome=false;
						int pivotRow=-1;
						for (int j=currentRow+1;j<sizeB;++j) if (main.get(j,currentRow))	{
							pivotAtHome=true;
							pivotRow=j;
							break;
						}
						if (somethingBelow&&(pivotRow<0)) for (int j=0;j<sizeB;++j) if (below.get(j,currentRow))	{
							pivotAtHome=false;
							pivotRow=j;
							break;
						}
						if (pivotRow<0)	{
							// Can't pivot. Elimination finished before reaching the end of the matrix. The rank equals the current row.
							return currentBlock*sizeB+currentRow;
						}
						// Pivot successfully found!
						BitSet rowFirstPart,rowSecondPart,rowThirdPart;
						if (pivotAtHome)	{
							rowFirstPart=main.getRow(pivotRow);
							rowSecondPart=somethingBelow?right.getRow(pivotRow):null;
							rowThirdPart=somethingBelow?right2.getRow(pivotRow):null;
						}	else	{
							rowFirstPart=below.getRow(pivotRow);
							rowSecondPart=diag.getRow(pivotRow);
							rowThirdPart=diag2.getRow(pivotRow);
						}
						main.xorRow(rowFirstPart,currentRow);
						if (somethingBelow)	{
							right.xorRow(rowSecondPart,currentRow);
							right2.xorRow(rowThirdPart,currentRow);
						}
					}
					// Pivoting finished, let's clear the current column.
					for (int j=currentRow+1;j<sizeB;++j) if (main.get(j,currentRow))	{
						main.xorRow(currentRow,j);
						if (somethingBelow)	{
							right.xorRow(currentRow,j);
							right2.xorRow(currentRow,j);
						}
					}
					if (somethingBelow)	{
						BitSet rowFirstPart=main.getRow(currentRow);
						BitSet rowSecondPart=right.getRow(currentRow);
						BitSet rowThirdPart=right2.getRow(currentRow);
						for (int j=0;j<sizeB;++j) if (below.get(j,currentRow))	{
							below.xorRow(rowFirstPart,j);
							diag.xorRow(rowSecondPart,j);
							diag2.xorRow(rowThirdPart,j);
						}
					}
				}
			}
			return sizeA*sizeB;	// Gaussian elimination finished successfully: matrix is regular.
		}
	}
	
	private static int calculateSingleRow(int m)	{
		Z2Matrix matrix=Z2Matrix.jay(m);
		return matrix.rankDestructive();
	}
	
	private static int calculateBlockTridiagonal(int m,int n)	{
		TridiagZ2Matrix matrix=new TridiagZ2Matrix(m,n);
		return matrix.rankDestructive();
	}
	
	private final static int BASE=5;
	private final static int MAX_FIBO=7;
	private final static long MOD=1_000_000_007l;
	
	public static void main(String[] args)	{
		/*
		 * The results are correct for the base cases. So there isn't any error regarding fibonacci management. Either the cycle management is
		 * wrong, or my assumptions about the cycle are :(. Or I botched again the rank calculations.
		 * 
		 * The cycle is correct at least until 1000. The cycle management seems to be trivial, but maybe I have overlooked something.
		 */
		long tic=System.nanoTime();
		int[] fibos=new int[MAX_FIBO];
		fibos[0]=1;
		fibos[1]=1;
		for (int i=2;i<fibos.length;++i) fibos[i]=fibos[i-1]+fibos[i-2];
		long result=0l;
		for (int n:fibos)	{
			int fullRank=(n==1)?calculateSingleRow(BASE):calculateBlockTridiagonal(BASE,n);
			result+=EulerUtils.expMod(2l,fullRank,MOD);
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
