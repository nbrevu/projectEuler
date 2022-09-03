package com.euler;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class Euler720 {
	private static class UnpredictablePermutationGenerator	{
		private final int size;
		private final BitSet pendingNumbers;
		private final int[] placeHolder;
		private final List<int[]> result;
		public UnpredictablePermutationGenerator(int size)	{
			this.size=size;
			pendingNumbers=new BitSet(1+size);
			pendingNumbers.set(1,1+size);
			placeHolder=new int[size];
			result=new ArrayList<>();
		}
		public void generate()	{
			generateRecursive(0);
		}
		private void generateRecursive(int currentIndex)	{
			for (int i=pendingNumbers.nextSetBit(0);i>=0;i=pendingNumbers.nextSetBit(i+1))	{
				int i2=2*i;
				boolean isSuitable=true;
				for (int j=0;j<currentIndex;++j)	{
					int toLookFor=i2-placeHolder[j];
					if ((toLookFor>0)&&(toLookFor<=size)&&pendingNumbers.get(toLookFor))	{
						isSuitable=false;
						break;
					}
				}
				if (!isSuitable) continue;
				placeHolder[currentIndex]=i;
				if (currentIndex==size-1) result.add(Arrays.copyOf(placeHolder,placeHolder.length));
				else	{
					pendingNumbers.clear(i);
					generateRecursive(1+currentIndex);
					pendingNumbers.set(i);
				}
			}
		}
		public List<int[]> getPermutations()	{
			return result;
		}
	}
	
	private static List<int[]> getUnpredictablePermutations(int size)	{
		UnpredictablePermutationGenerator generator=new UnpredictablePermutationGenerator(size);
		generator.generate();
		return generator.getPermutations();
	}
	
	public static void main(String[] args) throws IOException	{
		Path output=Paths.get("C:\\out720.txt");
		try (PrintStream ps=new PrintStream(Files.newOutputStream(output)))	{
			getUnpredictablePermutations(4).forEach((int[] perm)->ps.println(Arrays.toString(perm)));
			ps.println();
			getUnpredictablePermutations(8).forEach((int[] perm)->ps.println(Arrays.toString(perm)));
			ps.println();
			getUnpredictablePermutations(16).forEach((int[] perm)->ps.println(Arrays.toString(perm)));
		}
	}
}
