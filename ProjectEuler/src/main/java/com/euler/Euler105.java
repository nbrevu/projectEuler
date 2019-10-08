package com.euler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.euler.common.Timing;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler105 {
	private static boolean isSpecialSumSet(int[] numbers)	{
		IntObjMap<NavigableSet<Integer>> found=HashIntObjMaps.newMutableMap();
		int maxSet=1<<numbers.length;
		for (int i=1;i<maxSet;++i)	{
			BitSet indexMatrix=BitSet.valueOf(new long[] {i});
			int sum=0;
			for (int bit=indexMatrix.nextSetBit(0);bit!=-1;bit=indexMatrix.nextSetBit(1+bit)) sum+=numbers[bit];
			int size=indexMatrix.cardinality();
			NavigableSet<Integer> thisSize=found.computeIfAbsent(size,(int unused)->new TreeSet<>());
			if (thisSize.contains(sum)) return false;
			NavigableSet<Integer> prevSize=found.get(size-1);
			if ((prevSize!=null)&&!prevSize.isEmpty()&&prevSize.last().intValue()>=sum) return false;
			NavigableSet<Integer> nextSize=found.get(size+1);
			if ((nextSize!=null)&&!nextSize.isEmpty()&&nextSize.first().intValue()<=sum) return false;
			thisSize.add(sum);
		}
		return true;
	}
	
	private static int[] parse(String in)	{
		String[] split=in.split(",");
		int[] result=new int[split.length];
		for (int i=0;i<split.length;++i) result[i]=Integer.parseInt(split[i]);
		return result;
	}
	
	private static int sumNumbers(int[] array)	{
		int result=0;
		for (int n:array) result+=n;
		return result;
	}
	
	private static long solve()	{
		try	{
			URL resource=Euler105.class.getResource("in105.txt");
			List<String> lines=Files.lines(Paths.get(resource.toURI())).collect(Collectors.toList());
			int sum=0;
			for (String line:lines)	{
				int[] numbers=parse(line);
				if (isSpecialSumSet(numbers)) sum+=sumNumbers(numbers);
			}
			return sum;
		}	catch (IOException|URISyntaxException exc)	{
			throw new RuntimeException(exc);
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler105::solve);
	}
}
