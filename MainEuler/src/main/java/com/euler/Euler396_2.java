package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils;

public class Euler396_2 {
	private static long pow(long base,long exp)	{
		long res=1;
		for (long l=1;l<=exp;++l) res*=base;
		return res;
	}
	
	private static long getGoodsteinLength(long base,long[] digits)	{
		switch (digits.length)	{
			case 1:return digits[0];
			case 2:	{
				if (digits[0]>0)	{
					long shift=digits[0];
					long[] newDigits=new long[]{0,digits[1]};
					return shift+getGoodsteinLength(base+shift,newDigits);
				}	else return (base+1)*(pow(2,digits[1])-1);
			}	case 3:	{
				if (digits[0]>0)	{
					long shift=digits[0];
					long[] newDigits=new long[]{0,digits[1],digits[2]};
					return shift+getGoodsteinLength(base+shift,newDigits);
				}	else if (digits[1]>0)	{
					long shift=(base+1)*(pow(2,digits[1])-1);
					long[] newDigits=new long[]{0,0,digits[2]};
					return shift+getGoodsteinLength(base+shift,newDigits);
				}	else if (digits[2]==1) return (base+1)*(pow(2,base+1)-1);
				else throw new UnsupportedOperationException("No.");
			}	default: throw new UnsupportedOperationException("Que no.");
		}
	}
	
	private static long[] getBase2Rep(long in)	{
		List<Long> list=new ArrayList<>();
		while (in>0)	{
			list.add(in%2);
			in/=2;
		}
		long[] res=new long[list.size()];
		for (int i=0;i<list.size();++i) res[i]=list.get(i);
		return res;
	}
	
	public static void main(String[] args)	{
		for (long n=1;n<=7;++n)	{
			long[] digits=getBase2Rep(n);
			long res=getGoodsteinLength(2,digits);
			System.out.println("G("+n+")="+res+".");
		}
		
		for (long n=1;n<=10;++n)	{
			long exp=1000000000+n;
			long result=EulerUtils.expMod(2,exp,1000000000);
			System.out.println("2^"+exp+"%1000000000="+result+".");
		}
	}
}
