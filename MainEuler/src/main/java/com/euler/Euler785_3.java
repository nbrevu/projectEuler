package com.euler;

public class Euler785_3 {
	public static void main(String[] args)	{
		int max=2040;
		int counter=0;
		for (int x=0;x<max;++x) for (int y=0;y<max;++y) for (int z=0;z<max;++z)	{
			if (((x+y+z)%8)!=0) continue;
			if (((x*y+x*z+y*z)%15)!=0) continue;
			if (((x*x+y*y+z*z)%34)!=0) continue;
			int x3=x%3;
			int y3=y%3;
			int z3=z%3;
			if (((x3==1)&&(y3==1)&&(z3==1))||((x3==0)&&(y3==0)&&(z3==2))||((x3==0)&&(y3==2)&&(z3==0))||((x3==2)&&(y3==0)&&(z3==0))) ++counter;
		}
		System.out.println(counter);
	}
}
