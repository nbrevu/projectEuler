package com.euler;

import java.math.BigInteger;

import com.euler.common.BigMatrix;
import com.euler.common.EulerUtils;

public class Euler380 {
	/*
	 * I believe that this can be done.
	 * First of all: https://en.wikipedia.org/wiki/Kirchhoff%27s_theorem. Create the Laplacian matrix, remove a row/column, calculate the
	 * determinant. Unfortunately the determinant of a 50000x50000 matrix is not doable in reasonable time.
	 * Second: the matrix is block-tridiagonal, which suggests using Luca Guido's algorithm. But after removing a row, we get a matrix of size
	 * 49999 which can't be easily decomposed.
	 * Adding a new row/col with just a single 1 doesn't work, because the matrix is now block tridiagonal, but some of the Bi and Ci matrices
	 * are now singular because they have either an all zero row or an all zero column.
	 * The solution to this is use the properties of determinants. We add the "zeroed" row to some strategically chosen row so that the Bi/Ci
	 * has the same format as expected, and then we do the same with the column. Note that this means adding 1 to some element in the main
	 * diagonal (assuming that the vertical and horizontal offsets are the same, which seems likely), because we are adding a row and then a
	 * column, so we first get 1000...010...000 and we need to add THAT row/col, increasing some element in 1.
	 * This makes the determinants more workable (they will be 100x100), but they are still huge.
	 * To keep integer values, the Bareiss algorithm is recommended.
	 * 
	 * Scheme of the program:
	 * 1) Find a way to generate the Ai, Bi and Ci. Keep in mind the modification needed in the first or last row/col.
	 * 	1.1) I believe that Bi and Ci are mostly identity matrices. With some additional corners, maybe.
	 * 2) Calculate the transfer matrix, as per Luca Guido's algorithm.
	 * 3) Extract T_11^(0), again as per Luca Guido's algorithm.
	 * 4) Use Bareiss algorithm to calculate the determinant.
	 * 5) PROFIT!
	 * 
	 * Note: for step 1 this is not strictly necessary, but from step 2 onwards it's mandatory to use BigInteger. I guess that double could do
	 * the trick, but the result is expected to be somewhat around 10^100000, which obviously doesn't fit in a double. Logarithms are an
	 * alternative, or maybe BigDecimal.
	 */
	private final static int M=100;
	private final static int N=500;
	/*
	private static List<String> toString(BigMatrix matrix)	{
		int size=matrix.size();
		List<String> result=new ArrayList<>(size);
		for (int i=0;i<size;++i)	{
			StringBuilder sb=new StringBuilder();
			for (int j=0;j<size;++j)	{
				if (j>0) sb.append(' ');
				sb.append(matrix.get(i,j).toString());
			}
			result.add(sb.toString());
		}
		return result;
	}
	*/
	
	public static void main(String[] args)	{
		/*
		 * IT WORKS! But it's kind of slow.
		 * 6.3202e25093
		 * Elapsed 562.408876809 seconds.
		 * 
		 * AND SOMEONE IN THE COMMENTS CONFIRMS THAT THERE IS A CONNECTION WITH 289!!!!! Maybe my crazy approach would actually work??
		 */
		long tic=System.nanoTime();
		BigInteger minusOne=BigInteger.ONE.negate();
		BigInteger minusTwo=BigInteger.TWO.negate();
		BigInteger minusThree=BigInteger.valueOf(-3);
		BigInteger one=BigInteger.ONE;
		BigInteger two=BigInteger.TWO;
		BigInteger three=BigInteger.valueOf(3);
		BigInteger four=BigInteger.valueOf(4);
		BigMatrix tN=new BigMatrix(2*M);
		BigMatrix tN_1=new BigMatrix(2*M);
		BigMatrix tStd=new BigMatrix(2*M);
		BigMatrix t1=new BigMatrix(2*M);
		// Main diagonals.
		tN.assign(0,0,minusTwo);
		tN_1.assign(0,0,three);
		tStd.assign(0,0,three);
		t1.assign(0,0,two);
		for (int i=1;i<M-1;++i)	{
			tN.assign(i,i,minusThree);
			tN_1.assign(i,i,four);
			tStd.assign(i,i,four);
			t1.assign(i,i,three);
		}
		int m_=M-1;
		tN.assign(m_,m_,minusOne);
		tN_1.assign(m_,m_,four);
		tStd.assign(m_,m_,three);
		t1.assign(m_,m_,two);
		// Secondary diagonals.
		for (int i=1;i<M-1;++i)	{
			tN.assign(i-1,i,one);
			tN.assign(i,i-1,one);
		}
		for (int i=1;i<M;++i)	{
			tN_1.assign(i-1,i,minusOne);
			tN_1.assign(i,i-1,minusOne);
			tStd.assign(i-1,i,minusOne);
			tStd.assign(i,i-1,minusOne);
			t1.assign(i-1,i,minusOne);
			t1.assign(i,i-1,minusOne);
		}
		// Secondary blocks.
		for (int i=0;i<M;++i)	{
			tN.assign(i,i+M,one);
			tN.assign(i+M,i,one);
			tN_1.assign(i,i+M,minusOne);
			tN_1.assign(i+M,i,one);
			tStd.assign(i,i+M,minusOne);
			tStd.assign(i+M,i,one);
			t1.assign(i,i+M,minusOne);
			t1.assign(i+M,i,one);
		}
		BigMatrix t0=tN.multiply(tN_1).multiply(tStd.pow(N-3)).multiply(t1);
		BigInteger value=EulerUtils.bareissAlgorithm(t0,M);
		String resultString=value.toString();
		int exp=resultString.length()-1;
		StringBuilder result=new StringBuilder();
		result.append(resultString.charAt(0));
		result.append('.');
		result.append(resultString.substring(1,5));
		result.append('e');
		result.append(exp);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println("Full result: ");
		System.out.println(resultString);
	}
}
