package com.rosalind.aminoacids;

import java.util.ArrayList;
import java.util.List;

public class RnaTranscriber {
	private final String dnaSequence;
	
	public RnaTranscriber(String dnaSequence)	{
		this.dnaSequence=dnaSequence;
	}
	
	public String getRnaTranscription()	{
		StringBuilder result=new StringBuilder();
		for (char symbol:dnaSequence.toCharArray()) switch (symbol)	{
			case 'A':case 'a':result.append('U');break;
			case 'C':case 'c':result.append('G');break;
			case 'G':case 'g':result.append('C');break;
			case 'T':case 't':result.append('A');break;
			default:throw new IllegalStateException();
		}
		return result.reverse().toString();
	}
	
	public String getReverseRnaTranscription()	{
		StringBuilder result=new StringBuilder();
		for (char symbol:dnaSequence.toCharArray()) switch (symbol)	{
			case 'A':case 'a':result.append('A');break;
			case 'C':case 'c':result.append('C');break;
			case 'G':case 'g':result.append('G');break;
			case 'T':case 't':result.append('U');break;
			default:throw new IllegalStateException();
		}
		return result.toString();
	}
	
	public List<List<TranslatedCodon>> translateAllOpenFrames(boolean includeReverse)	{
		List<List<TranslatedCodon>> result=new ArrayList<>(includeReverse?6:3);
		String rna=getRnaTranscription();
		for (int i=0;i<3;++i) result.add(CodonTranslation.translateAll(rna.substring(i)));
		if (includeReverse)	{
			String reverseRna=getReverseRnaTranscription();
			for (int i=0;i<3;++i) result.add(CodonTranslation.translateAll(reverseRna.substring(i)));
		}
		return result;
	}
}
