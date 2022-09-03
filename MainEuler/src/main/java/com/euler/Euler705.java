package com.euler;

import java.util.ArrayList;
import java.util.List;

public class Euler705 {
	// Ok. This works. But 3312=23*144. Where does that 23 come from? Is it a sum, or a count? Or I'm thinking this wrong? (Yes. Probably).
	private static int countInversions(int[] digits)	{
		int result=0;
		int size=digits.length;
		for (int i=0;i<size-1;++i) for (int j=i+1;j<size;++j) if (digits[i]>digits[j]) ++result;
		return result;
	}
	
	private static int countDividedInversions(int[] digits)	{
		int size=digits.length;
		List<int[]> divisors=new ArrayList<>(size);
		for (int i=0;i<size;++i)	{
			int n=digits[i];
			List<Integer> thisDivisors=new ArrayList<>();
			for (int j=1;j<=n;++j) if ((n%j)==0) thisDivisors.add(j);
			divisors.add(thisDivisors.stream().mapToInt(Integer::intValue).toArray());
		}
		int result=0;
		int[] indices=new int[size];
		int[] placeHolder=new int[size];
		for (;;)	{
			for (int i=0;i<size;++i) placeHolder[i]=divisors.get(i)[indices[i]];
			result+=countInversions(placeHolder);
			int index=size-1;
			while (index>=0)	{
				++indices[index];
				if (indices[index]<divisors.get(index).length) break;
				--index;
			}
			if (index<0) break;
			for (++index;index<size;++index) indices[index]=0;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		int[] seq=new int[] {2,3,5};
		int counter=countDividedInversions(seq);
		System.out.println(counter+".");
	}
}
