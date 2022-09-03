package com.rosalind.aminoacids;

import java.util.HashMap;
import java.util.Map;

public enum Aminoacid implements TranslatedCodon {
	PHENYLALANINE("Phenylalanine","Phe",'F'),
	LEUCINE("Leucine","Leu",'L'),
	ISOLEUCINE("Isoleucine","Ile",'I'),
	METHIONINE("Methionine","Met",'M'),
	VALINE("Valine","Val",'V'),
	SERINE("Serine","Ser",'S'),
	PROLINE("Proline","Pro",'P'),
	THREONINE("Threonine","Thr",'T'),
	ALANINE("Alanine","Ala",'A'),
	TYROSINE("Tyrosine","Tyr",'Y'),
	HISTIDINE("Histidine","His",'H'),
	GLUTAMINE("Glutamine","Gln",'Q'),
	ASPARAGINE("Asparagine","Asn",'N'),
	LYSINE("Lysine","Lys",'K'),
	ASPARTIC_ACID("Aspartic acid","Asp",'D'),
	GLUTAMIC_ACID("Glutamic acid","Glu",'E'),
	CYSTEINE("Cysteine","Cys",'C'),
	TRYPTOPHAN("Tryptophan","Trp",'W'),
	ARGININE("Arginine","Arg",'R'),
	GLYCINE("Glycine","Gly",'G');
	
	public final String name;
	public final String shortName;
	public final char symbol;
	
	private Aminoacid(String name,String shortName,char symbol)	{
		this.name=name;
		this.shortName=shortName;
		this.symbol=symbol;
	}
	
	private final static Map<Character,Aminoacid> SYMBOL_MAP=new HashMap<>();
	static	{
		for (Aminoacid acid:values()) SYMBOL_MAP.put(acid.symbol,acid);
	}

	@Override
	public boolean isStop() {
		return false;
	}
	
	@Override
	public boolean isStart()	{
		/*
		 * NOTE: in our genome code, there is only one codon for methionine, which happens to be the start codon. If there were more than one
		 * methionine codon, but only one of them were the start one, this would get more complicated because this enum would need to differentiate
		 * between the two cases.
		 */
		return this==METHIONINE;
	}

	@Override
	public char getSymbol() {
		return symbol;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public static Aminoacid fromSymbol(char symbol)	{
		return SYMBOL_MAP.get(symbol);
	}
}
