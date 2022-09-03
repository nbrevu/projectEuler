package com.euler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Predicates;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler360 {
	private static int differentiationMultiplier(long[] array)	{
		LongSet longs=HashLongSets.newImmutableSet(array);
		switch (longs.size())	{
			case 1:return 1;
			case 2:return 3;
			case 3:return 6;
			default:throw new IllegalArgumentException();
		}
	}
	
	private static int getMultiplier(long[] array)	{
		int nonZeroMultiplier=1;
		for (int i=0;i<3;++i) if (array[i]!=0) nonZeroMultiplier*=2;
		return nonZeroMultiplier*differentiationMultiplier(array);
	}
	
	public static void main(String[] args) throws IOException	{
		// D'OH! this is for 10^5, not 10^10. Is mathematica up to the task? I don't think so :(. OVERFLOW! :(.
		// According to mathematica there are 58593750 cases, which are not SO many.
		int counter=0;
		long sum=0l;
		// Is it cheating if I got the results from mathematica? It took 1074.659689 seconds to generate these numbers.
		List<String> lines=Files.lines(Paths.get("C:\\in360.txt")).filter(Predicates.not(String::isEmpty)).collect(Collectors.toUnmodifiableList());
		for (String line:lines)	{
			String[] numberStrs=line.split(",");
			if (numberStrs.length!=3) throw new RuntimeException();
			long[] numbers=Arrays.stream(numberStrs).mapToLong(Long::parseLong).toArray();
			int multiplier=getMultiplier(numbers);
			counter+=multiplier;
			sum+=multiplier*Arrays.stream(numbers).sum();
		}
		System.out.println(counter);
		System.out.println(sum);
	}
}
