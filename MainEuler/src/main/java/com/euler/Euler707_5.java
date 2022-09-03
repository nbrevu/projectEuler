package com.euler;

import java.util.BitSet;

import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler707_5 {
	private static boolean DOPRINT=false;
	
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
			/*
			 * sizeA is the amount of blocks; sizeB is the size of each block.
			 * Finding a pivot is O(sizeB).
			 * XORing is O(sizeB).
			 * Clearing a column implies O(sizeB) XORings, therefore it's O(sizeB^2).
			 * Processing a row implies at most one of each of these operations, therefore it's O(sizeB^2).
			 * Processing a block is O(sizeB^3).
			 * In the end, finding a rank is O(sizeB^3*sizeA), therefore it's more advantageous to use sizeA for the biggest dimension and
			 * sizeB for the smallest.
			 * 
			 * In terms of space, the requirements are O(sizeB^2*sizeA).
			 */
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
		private void addRow(Z2Matrix block,int row,StringBuilder sb)	{
			BitSet line=block.getRow(row);
			for (int i=0;i<sizeB;++i) sb.append(line.get(i)?'1':'0');
		}
		public void print()	{
			if (!DOPRINT) return;
			String empty="0".repeat(sizeB);
			// First row of blocks.
			for (int j=0;j<sizeB;++j)	{
				StringBuilder sb=new StringBuilder();
				addRow(blocks[0][0],j,sb);
				addRow(blocks[0][1],j,sb);
				addRow(blocks[0][2],j,sb);
				for (int k=3;k<sizeA;++k) sb.append(empty);
				System.out.println(sb.toString());
			}
			// Middle rows.
			for (int i=1;i<sizeA-2;++i) for (int j=0;j<sizeB;++j)	{
				StringBuilder sb=new StringBuilder();
				for (int k=0;k<i-1;++k) sb.append(empty);
				addRow(blocks[i][0],j,sb);
				addRow(blocks[i][1],j,sb);
				addRow(blocks[i][2],j,sb);
				addRow(blocks[i][3],j,sb);
				for (int k=i+3;k<sizeA;++k) sb.append(empty);
				System.out.println(sb.toString());
			}
			// Second to last row.
			for (int j=0;j<sizeB;++j)	{
				StringBuilder sb=new StringBuilder();
				for (int k=0;k<sizeA-2;++k) sb.append(empty);
				addRow(blocks[sizeA-2][0],j,sb);
				addRow(blocks[sizeA-2][1],j,sb);
				addRow(blocks[sizeA-2][2],j,sb);
				System.out.println(sb.toString());
			}
			// End row.
			for (int j=0;j<sizeB;++j)	{
				StringBuilder sb=new StringBuilder();
				for (int k=0;k<sizeA-2;++k) sb.append(empty);
				addRow(blocks[sizeA-1][0],j,sb);
				addRow(blocks[sizeA-1][1],j,sb);
				System.out.println(sb.toString());
			}
			System.out.println();
		}
		public int rankDestructive()	{
			// ZUTUN! Creo que debería separar el caso sizeA-1 del resto. El 0 lo puedo dejar, con la ñapilla del mainPos.
			/*
			 * Y después, montarse alguna ñapa para que salga claramente el estado de la matriz tras cada iteración y depurar algún caso
			 * que sepa que sale mal, como el 3*5 (sale 13 pero es 12).
			 */
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
				print();
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
					print();
				}
			}
			return sizeA*sizeB;	// Gaussian elimination finished successfully: matrix is regular.
		}
	}
	
	private static int calculate(int m,int n)	{
		TridiagZ2Matrix matrix=new TridiagZ2Matrix(m,n);
		return matrix.rankDestructive();
	}
	
	/*
	 * It seems to be correct now...
	 */
	public static void main(String[] args)	{
		/*-
		for (int m=2;m<=5;++m) for (int n=m;(n*m)<=30;++n)	{
			int log=calculate(m,n);
			long f=1l<<log;
			String line=String.format("F(%d,%d)=%d; log2(%d)=%d.",m,n,f,f,log);
			System.out.println(line);
		}
		int log=calculate(11,7);
		System.out.println("¿Es "+log+" igual a 70?");
		*/
		/*
		 * OK! Here goes my first hypothesis about the FINAL problem:
		 * Given a prime p, and two numbers a and b such that b-a multiplies p^2-1, then F(p,a)-F(p,a-1) = F(p,b)-F(p,b-1).
		 * In other words, the differences repeat in cycles of length p^2-1.
		 * F(199*199,199) can be calculated in reasonable time and the value is 7880599.
		 * Sometimes the cycle is smaller :O.
		 * For 13, the cycle has length 18 (!!).
		 * 
		 * For 199, the cycle looks 60, BUT there are irregularities in the values of the form 20n+19,20n+20.
F(199,79)=15689. Diff=167.
F(199,80)=15918. Diff=229.
F(199,99)=19685. Diff=185.
F(199,100)=19900. Diff=215.
F(199,119)=23642. Diff=160.
F(199,120)=23880. Diff=238.
F(199,139)=27645. Diff=183.
F(199,140)=27858. Diff=213.
F(199,159)=31609. Diff=169.
F(199,160)=31840. Diff=231.
F(199,179)=35598. Diff=176.
F(199,180)=35820. Diff=222.
F(199,199)=39569. Diff=167.
F(199,200)=39798. Diff=229.
		 * Since Diff(199)=Diff(79) and Diff(200)=Diff(80)... maybe the real cycle has length 120?
		 * NOT A SINGLE "Failed for..." message in the first 1000 :).
		 * I can accept that the cycle is 120 units length.
		 * This is enough to calculate the results, but some finesse is required. I believe that I can do this in not so many lines of code.
		 * 
		 * F(199,1024)=203752. Diff=175. Status for 1024: WRONG!! BALLA BALLA :).
		 * Efectivamente la diferencia debería ser 195 y no 175 (similarmente, la del 65 sería 199 y no 219).
		 * 
		 * It seems like problems appear in numbers of the form 1025*n-1, i.e. 1024, 2049, 3074 and so on.
		 * The "difference" pattern is not clear at all :(. This affects a single one of the 199 numbers (the 197th, in fact).
		 */
		int prime=199;
		int maxSize=39600;
		int[] allVals=new int[1+maxSize];
		int[] diffs=new int[1+maxSize];
		allVals[1]=Z2Matrix.jay(prime).rankDestructive();
		diffs[1]=allVals[1];
		IntSet wrongValues=HashIntSets.newMutableSet();
		for (int m=2;m<=maxSize;++m)	{
			allVals[m]=calculate(m,prime);
			diffs[m]=allVals[m]-allVals[m-1];
			System.out.println(String.format("F(%d,%d)=%d. Diff=%d.",prime,m,allVals[m],diffs[m]));
			if (m>120)	{
				int q=m/120;
				int r=m%120;
				int expected=q*allVals[120]+allVals[r];
				boolean isCorrect=(expected==allVals[m]);
				System.out.println("\tStatus for "+m+": "+(isCorrect?"correct.":"WRONG!!"));
				if (!isCorrect) wrongValues.add(m);
			}
		}
		System.out.println("Wrong values found: "+wrongValues+".");
	}
}
