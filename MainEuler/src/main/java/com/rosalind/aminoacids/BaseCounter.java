package com.rosalind.aminoacids;

public class BaseCounter	{
	private int a;
	private int c;
	private int g;
	private int tOrU;
	public BaseCounter()	{
		a=c=g=tOrU=0;
	}
	public void readString(String in,boolean useU)	{
		for (char ch:in.toCharArray()) switch (ch)	{
			case 'a':case 'A':++a;break;
			case 'c':case 'C':++c;break;
			case 'g':case 'G':++g;break;
			case 't':case 'T':if (!useU) ++tOrU;else throw new IllegalStateException();break;
			case 'u':case 'U':if (useU) ++tOrU;else throw new IllegalStateException();break;
			default:throw new IllegalStateException();
		}
	}
	@Override
	public String toString()	{
		return String.format("%d %d %d %d",a,c,g,tOrU);
	}
	public int getA()	{
		return a;
	}
	public int getC()	{
		return c;
	}
	public int getG()	{
		return g;
	}
	public int getTorU()	{
		return tOrU;
	}
}