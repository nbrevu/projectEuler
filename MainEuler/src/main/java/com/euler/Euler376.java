package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.euler.common.CombinationIterator;

public class Euler376 {
	private static boolean isAcceptable(int[] a1,int[] a2)	{
		int result=0;
		for (int i=0;i<6;++i)	{
			int val=a2[i];
			for (int j=0;j<6;++j) if (a1[j]<val) ++result;
			else break;
		}
		return result>18;
	}
	
	public static void main(String[] args)	{
		List<int[]> combis=new ArrayList<>();
		CombinationIterator iter=new CombinationIterator(6,7,true,true);
		iter.forEach(combis::add);
		int result=0;
		for (int i=0;i<combis.size();++i)	{
			int[] a1=combis.get(i);
			for (int j=i+1;j<combis.size();++j)	{
				int[] a2=combis.get(j);
				if (isAcceptable(a1,a2)) for (int k=j+1;k<combis.size();++k)	{
					int[] a3=combis.get(k);
					if (isAcceptable(a2,a3)&&isAcceptable(a3,a1))	{
						++result;
						System.out.println(String.format("Found case: %s, %s, %s.",Arrays.toString(a1),Arrays.toString(a2),Arrays.toString(a3)));
					}
				}	else if (isAcceptable(a2,a1)) for (int k=j+1;k<combis.size();++k)	{
					int[] a3=combis.get(k);
					if (isAcceptable(a1,a3)&&isAcceptable(a3,a2))	{
						++result;
						System.out.println(String.format("Found case: %s, %s, %s.",Arrays.toString(a1),Arrays.toString(a2),Arrays.toString(a3)));
					}
				}
			}
		}
		System.out.println(result);
	}
}
