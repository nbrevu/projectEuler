package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Euler65 {
	private static class Aprox	{
		private BigInteger num,den;
		public Aprox(List<Integer> in)	{
			num=BigInteger.valueOf(in.get(in.size()-1));
			den=BigInteger.valueOf(1);
			for (int i=in.size()-2;i>=0;--i)	{
				BigInteger a=BigInteger.valueOf(in.get(i));
				BigInteger newNum=den.add(num.multiply(a));
				den=num;
				num=newNum;
			}
		}
		public String toString()	{
			return num.toString()+'/'+den.toString();
		}
		public int getSumOfNumeratorDigits()	{
			int res=0;
			for (char c:num.toString().toCharArray()) res+=c-'0';
			return res;
		}
	}
	private static List<Integer> generateList(int size)	{
		if (size<2) throw new IllegalArgumentException();
		List<Integer> res=new ArrayList<>();
		res.add(2);
		for (int curVal=2;;curVal+=2)	{
			res.add(1);
			if (res.size()==size) break;
			res.add(curVal);
			if (res.size()==size) break;
			res.add(1);
			if (res.size()==size) break;
		}
		return res;
	}
	private static int LIMIT=100;
	public static void main(String[] args)	{
		List<Integer> sequence=generateList(LIMIT);
		Aprox fraction=new Aprox(sequence);
		System.out.println(fraction.getSumOfNumeratorDigits());
	}
}
