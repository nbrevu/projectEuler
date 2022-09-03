package com.rosalind.aminoacids;

import java.util.List;

public interface TranslatedCodon {
	public boolean isStop();
	public boolean isStart();
	public char getSymbol();
	public String getName();
	
	public static String toString(List<TranslatedCodon> codons,boolean stopAtStops)	{
		StringBuilder result=new StringBuilder();
		for (TranslatedCodon aminoacid:codons)	{
			if (stopAtStops&&aminoacid.isStop()) break;
			else result.append(aminoacid.getSymbol());
		}
		return result.toString();
	}
}
