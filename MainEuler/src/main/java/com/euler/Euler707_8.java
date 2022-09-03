package com.euler;

import java.math.RoundingMode;
import java.util.BitSet;

import com.google.common.math.IntMath;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler707_8 {
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
							/*
							 * Sanity checks!!! Ensure that the elements at the end are actually zero.
							 */
							for (int nextRow=currentRow;nextRow<sizeB;++nextRow) for (int q=0;q<blocks[currentBlock].length;++q) if (!blocks[currentBlock][q].getRow(nextRow).isEmpty()) throw new IllegalStateException("Rango mal :'(.");
							for (int nextBlock=1+currentBlock;nextBlock<sizeA;++nextBlock) for (int nextRow=0;nextRow<sizeB;++nextRow) for (int q=0;q<blocks[nextBlock].length;++q) if (!blocks[nextBlock][q].getRow(nextRow).isEmpty()) throw new IllegalStateException("Rango mal :'(.");
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
	
	private static int calculateBlockTridiagonal(int m,int n)	{
		TridiagZ2Matrix matrix=new TridiagZ2Matrix(m,n);
		return matrix.rankDestructive();
	}
	
	public static void main(String[] args)	{
		int maxSize=5000;
		IntObjMap<IntSet> wrongResults=HashIntObjMaps.newMutableMap();
		for (int m=2;m<=IntMath.sqrt(maxSize,RoundingMode.DOWN);++m) for (int n=m+1;(n*m)<=maxSize;++n)	{
			// Direct calculation.
			Z2Matrix matrix=new Z2Matrix(m*n);
			for (int i=0;i<m;++i) for (int j=0;j<n;++j)	{
				int me=n*i+j;
				matrix.set(me,me);
				if (i>0) matrix.set(me,me-n);
				if (i<m-1) matrix.set(me,me+n);
				if (j>0) matrix.set(me,me-1);
				if (j<n-1) matrix.set(me,me+1);
			}
			int directVal=matrix.rankDestructive();
			int fineVal=calculateBlockTridiagonal(n,m);
			String areEqual=(directVal==fineVal)?"Correct!":"WRONG!!!!!";
			String summary=String.format("Calculating F(%d,%d). The brute force calculation says %d. The fine one says %d. The result is... %s",n,m,directVal,fineVal,areEqual);
			if (directVal!=fineVal)	{
				IntSet toAdd=wrongResults.computeIfAbsent(n,(int unused)->HashIntSets.newMutableSet());
				toAdd.add(m);
			}
			System.out.println(summary);
		}
		System.out.println("Set of wrong values found: "+wrongResults+".");
	}
}
