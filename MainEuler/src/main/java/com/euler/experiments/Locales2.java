package com.euler.experiments;

import java.io.IOException;

public class Locales2 {
	public static void main(String[] args) throws IOException	{
		int parseable=0;
		for (int i=0;i<65536;++i)	{
			char c=(char)i;
			String s=""+c;
			try	{
				int x=Integer.parseInt(s);
				System.out.println(String.format("Character %d (%s) is parseable as %d.",i,s,x));
				++parseable;
			}	catch (NumberFormatException nfe)	{}
		}
		System.out.println("Found "+parseable+" parseable characters.");
	}
}
