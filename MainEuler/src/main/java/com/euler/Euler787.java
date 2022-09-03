package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.LongPair;

public class Euler787 {
	private static List<LongPair> getValidTurns(LongPair state)	{
		List<LongPair> result=new ArrayList<>();
		for (int i=0;i<=state.x;++i) for (int j=((i==0)?1:0);j<=state.y;++j)	{
			long diff=state.x*j-state.y*i;
			if (Math.abs(diff)==1) result.add(new LongPair(i,j));
		}
		return result;
	}
	
	public static void main(String[] args)	{
		for (int i=1;i<=100;++i) for (int j=1;j<=100;++j) if (EulerUtils.areCoprime(i,j))	{
			List<LongPair> cases=getValidTurns(new LongPair(i,j));
			if (cases.isEmpty()) throw new RuntimeException("Qué bizarro.");
			System.out.println(String.format("If a=%d and b=%d we can do:",i,j));
			for (LongPair pair:cases) System.out.println(String.format("\tc=%d, d=%d (f=%d)%s.",pair.x,pair.y,i*pair.y-j*pair.x,((pair.x==i)||(pair.y==j))?" (WIN)":""));
		}
	}
}
