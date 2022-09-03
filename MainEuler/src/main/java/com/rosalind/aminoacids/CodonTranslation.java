package com.rosalind.aminoacids;

import java.util.ArrayList;
import java.util.List;

public class CodonTranslation {
	private static TranslatedCodon[] TRANSLATION_TABLE=new TranslatedCodon[]{
			Aminoacid.PHENYLALANINE,
			Aminoacid.PHENYLALANINE,
			Aminoacid.LEUCINE,
			Aminoacid.LEUCINE,
			Aminoacid.LEUCINE,
			Aminoacid.LEUCINE,
			Aminoacid.LEUCINE,
			Aminoacid.LEUCINE,
			Aminoacid.ISOLEUCINE,
			Aminoacid.ISOLEUCINE,
			Aminoacid.ISOLEUCINE,
			Aminoacid.METHIONINE,
			Aminoacid.VALINE,
			Aminoacid.VALINE,
			Aminoacid.VALINE,
			Aminoacid.VALINE,
			Aminoacid.SERINE,
			Aminoacid.SERINE,
			Aminoacid.SERINE,
			Aminoacid.SERINE,
			Aminoacid.PROLINE,
			Aminoacid.PROLINE,
			Aminoacid.PROLINE,
			Aminoacid.PROLINE,
			Aminoacid.THREONINE,
			Aminoacid.THREONINE,
			Aminoacid.THREONINE,
			Aminoacid.THREONINE,
			Aminoacid.ALANINE,
			Aminoacid.ALANINE,
			Aminoacid.ALANINE,
			Aminoacid.ALANINE,
			Aminoacid.TYROSINE,
			Aminoacid.TYROSINE,
			StopCodon.OCHRE,
			StopCodon.AMBER,
			Aminoacid.HISTIDINE,
			Aminoacid.HISTIDINE,
			Aminoacid.GLUTAMINE,
			Aminoacid.GLUTAMINE,
			Aminoacid.ASPARAGINE,
			Aminoacid.ASPARAGINE,
			Aminoacid.LYSINE,
			Aminoacid.LYSINE,
			Aminoacid.ASPARTIC_ACID,
			Aminoacid.ASPARTIC_ACID,
			Aminoacid.GLUTAMIC_ACID,
			Aminoacid.GLUTAMIC_ACID,
			Aminoacid.CYSTEINE,
			Aminoacid.CYSTEINE,
			StopCodon.OPAL,
			Aminoacid.TRYPTOPHAN,
			Aminoacid.ARGININE,
			Aminoacid.ARGININE,
			Aminoacid.ARGININE,
			Aminoacid.ARGININE,
			Aminoacid.SERINE,
			Aminoacid.SERINE,
			Aminoacid.ARGININE,
			Aminoacid.ARGININE,
			Aminoacid.GLYCINE,
			Aminoacid.GLYCINE,
			Aminoacid.GLYCINE,
			Aminoacid.GLYCINE
	};
	
	public static TranslatedCodon translate(String codon)	{
		// This is weird because the table is weird. It's obviously done by biologists, not computer science people. SAD! WRONG!
		int firstBase=rnaBase2Index(codon.charAt(0));
		int secondBase=rnaBase2Index(codon.charAt(1));
		int thirdBase=rnaBase2Index(codon.charAt(2));
		int index=thirdBase+4*firstBase+16*secondBase;
		return TRANSLATION_TABLE[index];
	}
	
	public static TranslatedCodon translate(String rna,int startingPosition)	{
		return translate(rna.substring(startingPosition,3+startingPosition));
	}
	
	public static List<TranslatedCodon> translateAll(String rna)	{
		int howMany=rna.length()/3;
		List<TranslatedCodon> result=new ArrayList<>(howMany);
		for (int i=0;i<howMany;++i) result.add(translate(rna,3*i));
		return result;
	}
	
	public static List<TranslatedCodon> translateUntilStop(String rna,boolean includeStop)	{
		int howMany=rna.length()/3;
		List<TranslatedCodon> result=new ArrayList<>(howMany);
		for (int i=0;i<howMany;++i)	{
			TranslatedCodon translated=translate(rna,3*i);
			if (translated.isStop())	{
				if (includeStop) result.add(translated);
				break;
			}	else  result.add(translated);
		}
		return result;
	}
	
	private static int rnaBase2Index(char symbol)	{
		switch (symbol)	{
			case 'U':case 'u':return 0;
			case 'C':case 'c':return 1;
			case 'A':case 'a':return 2;
			case 'G':case 'g':return 3;
			default:throw new IllegalArgumentException();
		}
	}
	
	public static int howManyCodonsForAminoacid(Aminoacid aminoacid)	{
		int result=0;
		for (TranslatedCodon codon:TRANSLATION_TABLE) if (codon.equals(aminoacid)) ++result;
		return result;
	}
	
	public static int howManyCodonsAreStop()	{
		return 3;	// Yeah, no need to calculate it.
	}
}
