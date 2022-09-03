package com.rosalind.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.euler.common.EulerUtils.FactorialCache;
import com.google.common.collect.ImmutableMap;

public class PermutationGraph	{
	public final static int PERM_SIZE=10;
	private final static FactorialCache FACTORIALS=new FactorialCache(PERM_SIZE);
	
	public static class Permutation	{
		private final int[] content;
		public Permutation(int[] content)	{
			this.content=content;
		}
		public long getId()	{
			int N=content.length;
			List<Integer> remaining=getEnumerated(N);
			long result=0;
			for (int i=0;i<N;++i)	{
				long factorial=FACTORIALS.get(N-i-1);
				int index=remaining.indexOf(content[i]);
				remaining.remove(index);	// This calls the "index" version because it's an int, not an integer. Phew!
				if (index==-1) throw new IllegalStateException();
				result+=index*factorial;
			}
			return result;
		}
		public static Permutation fromId(long in,int size)	{
			int[] result=new int[size];
			List<Integer> remaining=getEnumerated(size);
			for (int i=0;i<size;++i)	{
				long factorial=FACTORIALS.get(size-i-1);
				long q=in/factorial;
				in%=factorial;
				result[i]=remaining.get((int)q);
				remaining.remove((int)q);	// Same as above.
			}
			return new Permutation(result);
		}
		public Permutation reversal(int beginIndex,int endIndex)	{
			// Both indices are included.
			int[] newContent=Arrays.copyOf(content,content.length);
			for (int i=beginIndex;i<=endIndex;++i) newContent[i]=content[beginIndex+endIndex-i];
			return new Permutation(newContent);
		}
		private static List<Integer> getEnumerated(int size)	{
			List<Integer> result=new LinkedList<>();
			for (int k=1;k<=size;++k) result.add(k);
			return result;
		}
		@Override
		public boolean equals(Object other)	{
			Permutation pOther=(Permutation)other;
			return Arrays.equals(content,pOther.content);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(content);
		}
	}
	
	private final long[][] adjacents;
	private final int mainSize;
	private final int secSize;
	private final int size;
	public PermutationGraph(int size)	{
		mainSize=(int)FACTORIALS.get(size);
		secSize=(size*(size-1))/2;
		adjacents=new long[mainSize][secSize];
		this.size=size;
	}
	private void calculateGraph()	{
		for (int i=0;i<mainSize;++i)	{
			Permutation perm=Permutation.fromId(i,size);
			int addingIndex=0;
			for (int beginIndex=0;beginIndex<=size-2;++beginIndex) for (int endIndex=1+beginIndex;endIndex<=size-1;++endIndex)	{
				Permutation reversed=perm.reversal(beginIndex,endIndex);
				long revId=reversed.getId();
				adjacents[i][addingIndex]=revId;
				++addingIndex;
			}
		}
	}
	private void readFromFile(Path file) throws IOException	{
		try (BufferedReader reader=Files.newBufferedReader(file))	{
			for (int i=0;i<mainSize;++i)	{
				long[] read=IoUtils.parseStringAsArrayOfLongs(reader.readLine(),secSize);
				System.arraycopy(read,0,adjacents[i],0,secSize);
			}
		}
	}
	private void writeToFile(Path file) throws IOException	{
		try (BufferedWriter writer=Files.newBufferedWriter(file,StandardOpenOption.CREATE))	{
			for (int i=0;i<mainSize;++i)	{
				writer.write(Long.toString(adjacents[i][0]));
				for (int j=1;j<secSize;++j) writer.write(" "+Long.toString(adjacents[i][j]));
				writer.newLine();
			}
		}
	}
	public void fillGraph(Path file) throws IOException	{
		try	{
			readFromFile(file);
		}	catch (IOException exc)	{
			calculateGraph();
			writeToFile(file);
		}
	}
	public int getDistance(Permutation p1,Permutation p2)	{
		long id1=p1.getId();
		long id2=p2.getId();
		if (id1==id2) return 0;
		int result=1;
		Set<Long> visited=new HashSet<>();
		visited.add(id1);
		Set<Long> toVisit=Collections.singleton(id1);
		for (;;)	{
			if (toVisit.isEmpty()) throw new IllegalStateException();
			Set<Long> newToVisit=new HashSet<>();
			for (long visiting:toVisit) for (long next:adjacents[(int)visiting]) if (!visited.contains(next))	{
				if (next==id2) return result;
				visited.add(next);
				newToVisit.add(next);
			}
			toVisit=newToVisit;
			++result;
		}
	}
	public List<PermutationInfo> getDistanceExtended(Permutation p1,Permutation p2)	{
		long id1=p1.getId();
		long id2=p2.getId();
		if (id1==id2) return Collections.emptyList();
		Map<Long,List<PermutationInfo>> visited=new HashMap<>();
		visited.put(id1,Collections.emptyList());
		Map<Long,List<PermutationInfo>> toVisit=ImmutableMap.of(id1,Collections.emptyList());
		for (;;)	{
			if (toVisit.isEmpty()) throw new IllegalStateException();
			Map<Long,List<PermutationInfo>> newToVisit=new HashMap<>();
			for (Map.Entry<Long,List<PermutationInfo>> entry:toVisit.entrySet())	{
				long visiting=entry.getKey();
				List<PermutationInfo> history=entry.getValue();
				int iVisiting=(int)visiting;
				int N=adjacents[iVisiting].length;
				for (int i=0;i<N;++i)	{
					long next=adjacents[iVisiting][i];
					if (!visited.containsKey(next))	{
						List<PermutationInfo> newList=new ArrayList<>(history);
						newList.add(PermutationInfo.getFromIndex(i));
						if (next==id2) return newList;
						visited.put(next,newList);
						newToVisit.put(next,newList);
					}
				}
			}
			toVisit=newToVisit;
		}
	}
	public static class PermutationInfo	{
		private final int begin;
		private final int end;
		private PermutationInfo(int begin,int end)	{
			this.begin=begin;
			this.end=end;
		}
		public static PermutationInfo getFromIndex(int index)	{
			int currentMax=0;
			int currentBegin=1;
			int currentSize=PERM_SIZE-1;
			for (;;)	{
				int newMax=currentMax+currentSize;
				if (index<newMax)	{
					int endIdx=index-currentMax;
					return new PermutationInfo(currentBegin,1+currentBegin+endIdx);
				}
				currentMax=newMax;
				++currentBegin;
				--currentSize;
				if (currentSize==0) throw new IllegalArgumentException();
			}
		}
		@Override
		public String toString()	{
			return begin+" "+end;
		}
	}
}