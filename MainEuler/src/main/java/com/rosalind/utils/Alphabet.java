package com.rosalind.utils;

public class Alphabet	{
	private char[] symbols;
	private Alphabet(char[] symbols)	{
		this.symbols=symbols;
	}
	public static Alphabet readSymbols(String in)	{
		String[] syms=in.split(" ");
		char[] symbols=new char[syms.length];
		for (int i=0;i<syms.length;++i)	{
			if (syms[i].length()>1) throw new IllegalStateException();
			symbols[i]=syms[i].charAt(0);
		}
		return new Alphabet(symbols);
	}
	public int getLength()	{
		return symbols.length;
	}
	public char getAt(int i)	{
		return symbols[i];
	}
}