package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Euler324 {
	/*
	 * Primera aproximación...
	 * 1) Hay 2^9=512 configuraciones en cuanto a "huecos" disponibles.
	 * 2) Creamos un tipo de "configuración" de fila, definida por:
	 * 	2.a) La lista de "huecos" disponibles, que no han sido rellenados por la fila anterior.
	 *  2.b) La lista de "salientes" que le quitan un hueco a la fila siguiente.
	 * 3) De este modo tenemos una matriz de 512x512. Almacenamos en la matriz la CANTIDAD de configuraciones.
	 * 	Esto es, si M[324][127]=99, es porque hay 99 configuraciones que parten de la "configuración" 324 y
	 *  le dejan la "127" a la siguiente. Por supuesto me acabo de inventar el número.
	 *  3.a) Partimos de una configuración "base" sin salientes y se le añaden los que toquen.
	 * 4) La matriz muy probablemente sea muy dispersa. Hay que pensar cómo hacer esto eficientemente.
	 * 5) Podemos generar recursivamente un esquema que, a partir de estos bloques de una fila, genere los
	 *  bloques de 10 filas; con éstos, los de 10^2 filas, y así sucesivamente.
	 * 6) De este modo, con 10000 iteraciones del módulo principal, solucionamos el problema.
	 * 7) ¡Es fácil decirlo! Pero tiene pinta de ser bastante chunguete.
	 * 
	 * "Result found in 4889.478104828 seconds.". Pero funciona :O.
	 */
	private final static int BASE=10;
	private final static int EXPONENT=10000;
	private final static long MOD=100000007l;
	
	private static boolean[][] copy3x3Array(boolean[][] in)	{
		boolean[][] result=new boolean[3][3];
		for (int i=0;i<3;++i) for (int j=0;j<3;++j) result[i][j]=in[i][j];
		return result;
	}
	
	private static class BooleanMatrix	{
		/*
		 * Represents a matrix of 9 boolean values.
		 * When translated into an integer index, the whole values are:
		 * 0 1 2
		 * 3 4 5
		 * 6 7 8
		 * 
		 * Therefore, an arrangement like
		 * 0 1 0
		 * 1 1 0
		 * 0 1 0
		 * gets translated as 2 + 8 + 16 + 128 = 154.
		 */
		private final boolean[][] array;
		public final int id;
		
		public BooleanMatrix(boolean[][] array)	{
			this.array=copy3x3Array(array);
			id=calculateId(array);
		}
		
		public BooleanMatrix(int id)	{
			this.id=id;
			array=new boolean[3][3];
			fillArray(array,id);
		}
		
		private int calculateId(boolean[][] array)	{
			int result=0;
			int shifted=1;
			for (int i=0;i<3;++i) for (int j=0;j<3;++j)	{
				if (array[i][j]) result+=shifted;
				shifted+=shifted;
			}
			return result;
		}
		
		public int getBitCount()	{
			int result=0;
			for (int i=0;i<3;++i) for (int j=0;j<3;++j) if (array[i][j]) ++result;
			return result;
		}
		
		private void fillArray(boolean[][] array,int id)	{
			int shifted=1;
			for (int i=0;i<3;++i) for (int j=0;j<3;++j)	{
				array[i][j]=((shifted&id)!=0);
				shifted+=shifted;
			}
		}
		
		public Optional<BooleanMatrix> putHorizontal(int i,int j)	{
			if (j==2) return Optional.empty();
			else if (array[i][j]||array[i][j+1]) return Optional.empty();
			boolean[][] newArray=copy3x3Array(array);
			newArray[i][j]=true;
			newArray[i][j+1]=true;
			return Optional.of(new BooleanMatrix(newArray));
		}
		
		public Optional<BooleanMatrix> putVertical(int i,int j)	{
			if (i==2) return Optional.empty();
			else if (array[i][j]||array[i+1][j]) return Optional.empty();
			boolean[][] newArray=copy3x3Array(array);
			newArray[i][j]=true;
			newArray[i+1][j]=true;
			return Optional.of(new BooleanMatrix(newArray));
		}
		
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			for (int i=0;i<3;++i)	{
				for (int j=0;j<3;++j) sb.append(array[i][j]?"1 ":"0 ");
				sb.append("\n");
			}
			return sb.toString();
		}
	}
	
	private static class SingleRowCounter	{
		private long[] counter;
		public SingleRowCounter()	{
			counter=new long[512];
			Arrays.fill(counter,-1);
			for (int i=0;i<512;++i) getValue(i);
		}
		public long getValue(int index)	{
			if (counter[index]==-1) counter[index]=calculateValue(index);
			return counter[index];
		}
		private int calculateValue(int index)	{
			BooleanMatrix mat=new BooleanMatrix(index);
			int bitCount=mat.getBitCount();
			if (bitCount==9) return 1;
			else if ((bitCount%2)!=1) return 0;
			int result=0;
			// Überdirty, but works and it's fast enough.
			for (int i=0;i<3;++i) for (int j=0;j<3;++j)	{
				Optional<BooleanMatrix> mat1=mat.putHorizontal(i,j);
				Optional<BooleanMatrix> mat2=mat.putVertical(i,j);
				if (mat1.isPresent()) result+=getValue(mat1.get().id);
				if (mat2.isPresent()) result+=getValue(mat2.get().id);
			}
			if (bitCount==1) return result/4;
			else if (bitCount==3) return result/3;
			else if (bitCount==5) return result/2;
			else return result;
		}
	}
	
	private static abstract class BlockHolder	{
		protected final long[][] possibilities;
		private final List<List<Integer>> nonZeroIndices;	// This should be an array[512] of Lists, but Java is stupid.
		public BlockHolder(long[][] possibilities)	{
			this.possibilities=possibilities;
			nonZeroIndices=searchForNonZeroIndices(possibilities);
		}

		public final List<Integer> getNonZeroIndicesForCase(int theCase)	{
			return nonZeroIndices.get(theCase);
		}
		
		public final long getPossibilities(int below,int above)	{
			return possibilities[below][above];
		}

		private static List<List<Integer>> searchForNonZeroIndices(long[][] array)	{
			List<List<Integer>> result=new ArrayList<>(512);
			for (int i=0;i<512;++i)	{
				List<Integer> nonZero=new ArrayList<>();
				for (int j=0;j<512;++j) if (array[i][j]!=0) nonZero.add(j);
				result.add(nonZero);
			}
			return result;
		}
	}
	
	private static class RowHolder extends BlockHolder	{
		public RowHolder()	{
			super(generatePossibilities());
		}
		
		private static long[][] generatePossibilities()	{
			long[][] result=new long[512][512];
			SingleRowCounter rowProbabilities=new SingleRowCounter();
			for (int i=0;i<512;++i)	{
				long base=rowProbabilities.getValue(i);
				if (base==0) continue;
				int[][] connections=getAllConnections(i);
				int N=connections.length;
				for (int j=0;j<N;++j) result[connections[j][0]][connections[j][1]]+=base;
			}
			return result;
		}
		
		private static int[][] getAllConnections(int id)	{
			int bits=id;
			List<Integer> bitMasks=new ArrayList<>();
			for (int shifted=1;bits>0;shifted+=shifted) if ((bits&shifted)!=0)	{
				bitMasks.add(shifted);
				bits-=shifted;
			}
			int N=1<<bitMasks.size();
			int[][] result=new int[N][2];
			for (int i=0;i<N;++i)	{
				int previous=sumBitMasks(bitMasks,i);
				int next=sumBitMasks(bitMasks,N-1-i);
				// This specific bit might be very wrong. Maybe some inversions are needed.
				result[i][0]=previous;
				result[i][1]=next;
			}
			return result;
		}
		
		private static int sumBitMasks(List<Integer> bitMasks,int mask)	{
			int result=0;
			for (int augend:bitMasks)	{
				if ((mask&1)!=0) result+=augend;
				mask/=2;
				if (mask==0) break;
			}
			return result;
		}
	}
	
	private static class IterativeBlockHolder extends BlockHolder	{
		public IterativeBlockHolder(BlockHolder base,int compositionSize,long mod)	{
			super(calculatePossibilities(base,compositionSize,mod));
		}
		
		private static long[][] calculatePossibilities(BlockHolder base,int compositionSize,long mod)	{
			long[][] lastIteration=base.possibilities;
			for (int m=1;m<compositionSize;++m)	{
				long[][] nextIteration=new long[512][512];
				for (int i=0;i<512;++i) for (int j=0;j<512;++j) if (lastIteration[i][j]!=0) for (int k:base.getNonZeroIndicesForCase(j))	{
					nextIteration[i][k]+=lastIteration[i][j]*base.getPossibilities(j,k);
					nextIteration[i][k]%=mod;
				}
				lastIteration=nextIteration;
			}
			return lastIteration;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BlockHolder currentIteration=new RowHolder();
		for (int i=0;i<EXPONENT;++i) currentIteration=new IterativeBlockHolder(currentIteration,BASE,MOD);
		long tac=System.nanoTime();
		double seconds=(tac-tic)/1e9;
		System.out.println(currentIteration.getPossibilities(0,0));
		System.out.println("Result found in "+seconds+" seconds.");
	}
}
