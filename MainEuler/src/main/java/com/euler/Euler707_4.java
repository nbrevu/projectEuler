package com.euler;

import java.util.BitSet;

public class Euler707_4 {
	// This is still not good enough, because 199*199 is a matrix of size almost 40000, unworkable.
	// Of course, the fact that it's a simple block-diagonal matrix SHOULD be used somewhere...
	private static class Z2Matrix	{
		private final BitSet[] data;
		public Z2Matrix(int size)	{
			data=new BitSet[size];
			for (int i=0;i<size;++i) data[i]=new BitSet(size);
		}
		public boolean get(int i,int j)	{
			return data[i].get(j);
		}
		public void set(int i,int j)	{
			data[i].set(j);
		}
		private void xorRow(int source,int target)	{
			data[target].xor(data[source]);
		}
		private void xorColumn(int source,int target)	{
			if (target>=0) throw new RuntimeException("Wow.");	// Vale. Nunca ocurre :D.
			// Unfortunately this is not as easy as the row version...
			int n=data.length;
			for (int i=0;i<n;++i) if (data[i].get(source)) data[i].flip(target);
		}
		public int rankDestructive()	{
			int n=data.length;
			for (int i=0;i<n;++i)	{
				if (!get(i,i))	{
					int rowToPivot=-1;
					int colToPivot=-1;
					for (int j=i;(j<n)&&(rowToPivot<0);++j) for (int k=i;k<n;++k) if (get(k,j))	{
						rowToPivot=k;
						colToPivot=j;
						break;
					}
					if (rowToPivot<0) return i;
					if (colToPivot!=i) xorColumn(colToPivot,i);
					if (rowToPivot!=i) xorRow(rowToPivot,i);
					// Sanity check!
					if (!get(i,i)) throw new IllegalStateException("Mal.");
				}
				for (int j=i+1;j<n;++j) if (data[j].get(i)) xorRow(i,j);
			}
			return n;
		}
	}
	
	private static int calculate(int m,int n)	{
		Z2Matrix matrix=new Z2Matrix(m*n);
		for (int i=0;i<m;++i) for (int j=0;j<n;++j)	{
			int me=n*i+j;
			matrix.set(me,me);
			if (i>0) matrix.set(me,me-n);
			if (i<m-1) matrix.set(me,me+n);
			if (j>0) matrix.set(me,me-1);
			if (j<n-1) matrix.set(me,me+1);
		}
		return matrix.rankDestructive();
	}
	
	public static void main(String[] args)	{
		/*
		 * VALE. Hasta aquí funciona.
		 * Next stop:
		 * 1) hacer la simulación tridiagonal por bloques. Debería ser razonablemente sencilla. Lo prometo.
		 * 2) A partir de la simulación tridiagonal, hacer pruebas hasta (w,h)<=200 o así.
		 * 3) Buscar patrones en el rango de la matriz.
		 */
		for (int m=1;m<=5;++m) for (int n=m;(n*m)<=30;++n)	{
			int log=calculate(m,n);
			long f=1l<<log;
			String line=String.format("F(%d,%d)=%d; log2(%d)=%d.",m,n,f,f,log);
			System.out.println(line);
		}
		int log=calculate(7,11);
		System.out.println("¿Es "+log+" igual a 70?");
	}
}
