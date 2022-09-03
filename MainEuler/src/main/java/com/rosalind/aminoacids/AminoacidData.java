package com.rosalind.aminoacids;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class AminoacidData {
	// Ideally, this will grow with time.
	public final double monoisotopicMass;
	
	private AminoacidData(double monoisotopicMass)	{
		this.monoisotopicMass=monoisotopicMass;
	}
	
	private final static int[][] BLOSUM_BASE={
		{4,0,-2,-1,-2,0,-2,-1,-1,-1,-1,-2,-1,-1,-1,1,0,0,-3,-2},
		{0,9,-3,-4,-2,-3,-3,-1,-3,-1,-1,-3,-3,-3,-3,-1,-1,-1,-2,-2},
		{-2,-3,6,2,-3,-1,-1,-3,-1,-4,-3,1,-1,0,-2,0,-1,-3,-4,-3},
		{-1,-4,2,5,-3,-2,0,-3,1,-3,-2,0,-1,2,0,0,-1,-2,-3,-2},
		{-2,-2,-3,-3,6,-3,-1,0,-3,0,0,-3,-4,-3,-3,-2,-2,-1,1,3},
		{0,-3,-1,-2,-3,6,-2,-4,-2,-4,-3,0,-2,-2,-2,0,-2,-3,-2,-3},
		{-2,-3,-1,0,-1,-2,8,-3,-1,-3,-2,1,-2,0,0,-1,-2,-3,-2,2},
		{-1,-1,-3,-3,0,-4,-3,4,-3,2,1,-3,-3,-3,-3,-2,-1,3,-3,-1},
		{-1,-3,-1,1,-3,-2,-1,-3,5,-2,-1,0,-1,1,2,0,-1,-2,-3,-2},
		{-1,-1,-4,-3,0,-4,-3,2,-2,4,2,-3,-3,-2,-2,-2,-1,1,-2,-1},
		{-1,-1,-3,-2,0,-3,-2,1,-1,2,5,-2,-2,0,-1,-1,-1,1,-1,-1},
		{-2,-3,1,0,-3,0,1,-3,0,-3,-2,6,-2,0,0,1,0,-3,-4,-2},
		{-1,-3,-1,-1,-4,-2,-2,-3,-1,-3,-2,-2,7,-1,-2,-1,-1,-2,-4,-3},
		{-1,-3,0,2,-3,-2,0,-3,1,-2,0,0,-1,5,1,0,-1,-2,-2,-1},
		{-1,-3,-2,0,-3,-2,0,-3,2,-2,-1,0,-2,1,5,-1,-1,-3,-3,-2},
		{1,-1,0,0,-2,0,-1,-2,0,-2,-1,1,-1,0,-1,4,1,-2,-3,-2},
		{0,-1,-1,-1,-2,-2,-2,-1,-1,-1,-1,0,-1,-1,-1,1,5,0,-2,-2},
		{0,-1,-3,-2,-1,-3,-3,3,-2,1,1,-3,-2,-2,-3,-2,0,4,-3,-1},
		{-3,-2,-4,-3,1,-2,-2,-3,-3,-2,-1,-4,-4,-2,-3,-3,-2,-3,11,2},
		{-2,-2,-3,-2,3,-3,2,-1,-2,-1,-1,-2,-3,-1,-2,-2,-2,-1,2,7}
	};
	
	private final static Map<Character,AminoacidData> TABLE=new HashMap<>();
	private final static Table<Character,Character,Integer> BLOSUM62=HashBasedTable.create();
	
	static	{
		TABLE.put('A',new AminoacidData(71.03711));
		TABLE.put('C',new AminoacidData(103.00919));
		TABLE.put('D',new AminoacidData(115.02694));
		TABLE.put('E',new AminoacidData(129.04259));
		TABLE.put('F',new AminoacidData(147.06841));
		TABLE.put('G',new AminoacidData(57.02146));
		TABLE.put('H',new AminoacidData(137.05891));
		TABLE.put('I',new AminoacidData(113.08406));
		TABLE.put('K',new AminoacidData(128.09496));
		TABLE.put('L',new AminoacidData(113.08406));
		TABLE.put('M',new AminoacidData(131.04049));
		TABLE.put('N',new AminoacidData(114.04293));
		TABLE.put('P',new AminoacidData(97.05276));
		TABLE.put('Q',new AminoacidData(128.05858));
		TABLE.put('R',new AminoacidData(156.10111));
		TABLE.put('S',new AminoacidData(87.03203));
		TABLE.put('T',new AminoacidData(101.04768));
		TABLE.put('V',new AminoacidData(99.06841));
		TABLE.put('W',new AminoacidData(186.07931));
		TABLE.put('Y',new AminoacidData(163.06333));
		String characters="ACDEFGHIKLMNPQRSTVWY";
		for (int i=0;i<20;++i) for (int j=0;j<20;++j) BLOSUM62.put(characters.charAt(i),characters.charAt(j),BLOSUM_BASE[i][j]);
	}
	
	public static AminoacidData getForCharacter(char symbol)	{
		return TABLE.get(symbol);
	}
	
	public static AminoacidData getForAminoacid(Aminoacid acid)	{
		return TABLE.get(acid.getSymbol());
	}
	
	public static double getMass(Collection<? extends TranslatedCodon> aminoacids)	{
		// Non-aminoacids will be ignored.
		double result=0;
		for (TranslatedCodon codon:aminoacids)	{
			char c=codon.getSymbol();
			AminoacidData data=TABLE.get(c);
			if (data!=null) result+=data.monoisotopicMass;
		}
		return result;
	}
	
	public static double getMass(String chars)	{
		double result=0;
		for (char ch:chars.toCharArray())	{
			AminoacidData data=TABLE.get(ch);
			if (data!=null) result+=data.monoisotopicMass;
		}
		return result;
	}
	
	public static Aminoacid getNearestAminoacid(double mass)	{
		NavigableMap<Double,Aminoacid> nearest=new TreeMap<>();
		for (Map.Entry<Character,AminoacidData> entry:TABLE.entrySet())	{
			double diff=Math.abs(mass-entry.getValue().monoisotopicMass);
			nearest.put(diff,Aminoacid.fromSymbol(entry.getKey()));
		}
		return nearest.firstEntry().getValue();
	}
	
	// This version returns null if the difference is high enough.
	public static Aminoacid getNearestAminoacid(double mass,double maxDiff)	{
		NavigableMap<Double,Aminoacid> nearest=new TreeMap<>();
		for (Map.Entry<Character,AminoacidData> entry:TABLE.entrySet())	{
			double diff=Math.abs(mass-entry.getValue().monoisotopicMass);
			nearest.put(diff,Aminoacid.fromSymbol(entry.getKey()));
		}
		Aminoacid result=nearest.firstEntry().getValue();
		double diff=Math.abs(mass-TABLE.get(result.symbol).monoisotopicMass);
		return (diff<=maxDiff)?result:null; 
	}
	
	public static Integer getBlosumScore(Character c1,Character c2)	{
		return BLOSUM62.get(c1,c2);
	}
}