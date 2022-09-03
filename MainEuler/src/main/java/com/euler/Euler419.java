package com.euler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.euler.common.LongMatrix;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

public class Euler419 {
	private final static long N=LongMath.pow(10l,12);
	private final static long MOD=LongMath.pow(2l,30);
	
	private final static Pattern PATTERN=Pattern.compile("^([123]+)\\s+([\\(\\)\\d]+)$");
	private final static Pattern SUB_PATTERN=Pattern.compile("^\\((\\d+)\\)([\\(\\)\\d]*)$");
	// Data extracted from http://www.njohnston.ca/2010/10/a-derivation-of-conways-degree-71-look-and-say-polynomial/ (with some processing).
	private final static String[] PATTERNS=new String[] {"1112 	(63)",
			"1112133 	(64)(62)",
			"111213322112 	(65)",
			"111213322113 	(66)",
			"1113 	(68)",
			"11131 	(69)",
			"111311222112 	(84)(55)",
			"111312 	(70)",
			"11131221 	(71)",
			"1113122112 	(76)",
			"1113122113 	(77)",
			"11131221131112 	(82)",
			"111312211312 	(78)",
			"11131221131211 	(79)",
			"111312211312113211 	(80)",
			"111312211312113221133211322112211213322112 	(81)(29)(91)",
			"111312211312113221133211322112211213322113 	(81)(29)(90)",
			"11131221131211322113322112 	(81)(30)",
			"11131221133112 	(75)(29)(92)",
			"1113122113322113111221131221 	(75)(32)",
			"11131221222112 	(72)",
			"111312212221121123222112 	(73)",
			"111312212221121123222113 	(74)",
			"11132 	(83)",
			"1113222 	(86)",
			"1113222112 	(87)",
			"1113222113 	(88)",
			"11133112 	(89)(92)",
			"12 	(1)",
			"123222112 	(3)",
			"123222113 	(4)",
			"12322211331222113112211 	(2)(61)(29)(85)",
			"13 	(5)",
			"131112 	(28)",
			"13112221133211322112211213322112 	(24)(33)(61)(29)(91)",
			"13112221133211322112211213322113 	(24)(33)(61)(29)(90)",
			"13122112 	(7)",
			"132 	(8)",
			"13211 	(9)",
			"132112 	(10)",
			"1321122112 	(21)",
			"132112211213322112 	(22)",
			"132112211213322113 	(23)",
			"132113 	(11)",
			"1321131112 	(19)",
			"13211312 	(12)",
			"1321132 	(13)",
			"13211321 	(14)",
			"132113212221 	(15)",
			"13211321222113222112 	(18)",
			"1321132122211322212221121123222112 	(16)",
			"1321132122211322212221121123222113 	(17)",
			"13211322211312113211 	(20)",
			"1321133112 	(6)(61)(29)(92)",
			"1322112 	(26)",
			"1322113 	(27)",
			"13221133112 	(25)(29)(92)",
			"1322113312211 	(25)(29)(67)",
			"132211331222113112211 	(25)(29)(85)",
			"13221133122211332 	(25)(29)(68)(61)(29)(89)",
			"22 	(61)",
			"3 	(33)",
			"3112 	(40)",
			"3112112 	(41)",
			"31121123222112 	(42)",
			"31121123222113 	(43)",
			"3112221 	(38)(39)",
			"3113 	(44)",
			"311311 	(48)",
			"31131112 	(54)",
			"3113112211 	(49)",
			"3113112211322112 	(50)",
			"3113112211322112211213322112 	(51)",
			"3113112211322112211213322113 	(52)",
			"311311222 	(47)(38)",
			"311311222112 	(47)(55)",
			"311311222113 	(47)(56)",
			"3113112221131112 	(47)(57)",
			"311311222113111221 	(47)(58)",
			"311311222113111221131221 	(47)(59)",
			"31131122211311122113222 	(47)(60)",
			"3113112221133112 	(47)(33)(61)(29)(92)",
			"311312 	(45)",
			"31132 	(46)",
			"311322113212221 	(53)",
			"311332 	(38)(29)(89)",
			"3113322112 	(38)(30)",
			"3113322113 	(38)(31)",
			"312 	(34)",
			"312211322212221121123222113 	(36)",
			"312211322212221121123222112 	(35)",
			"32112 	(37)"
	};
	
	private static class Subsequence	{
		public final int num1s;
		public final int num2s;
		public final int num3s;
		public final IntIntMap next;
		private Subsequence(int num1s,int num2s,int num3s,IntIntMap next)	{
			this.num1s=num1s;
			this.num2s=num2s;
			this.num3s=num3s;
			this.next=next;
		}
		public static Subsequence fromString(String in)	{
			// The result is 998567458,1046245404,43363922 and I really appreciate that one of the numbers actually exceeds 10^9!
			Matcher matcher=PATTERN.matcher(in);
			if (!matcher.matches()) throw new IllegalArgumentException();
			String content=matcher.group(1);
			int num1s=0;
			int num2s=0;
			int num3s=0;
			for (char c:content.toCharArray()) if (c=='1') ++num1s;
			else if (c=='2') ++num2s;
			else if (c=='3') ++num3s;
			else throw new IllegalArgumentException();
			IntIntMap next=HashIntIntMaps.newMutableMap();
			String nextStr=matcher.group(2);
			while (!nextStr.isEmpty())	{
				Matcher matcher2=SUB_PATTERN.matcher(nextStr);
				if (!matcher2.matches()) throw new IllegalArgumentException();
				next.addValue(Integer.parseInt(matcher2.group(1))-1,1);	// YES! Minus one. The original data is 1-based, our arrays are 0-based.
				nextStr=matcher2.group(2);
			}
			return new Subsequence(num1s,num2s,num3s,next);
		}
	}
	
	private static LongMatrix createInitialMatrix(Subsequence[] seqs)	{
		LongMatrix result=new LongMatrix(seqs.length);
		for (int i=0;i<seqs.length;++i) for (IntIntCursor cursor=seqs[i].next.cursor();cursor.moveNext();) result.assign(cursor.key(),i,cursor.value());
		return result;
	}
	
	private static String getFinalResult(long[] vector,Subsequence[] seqs,long mod)	{
		long num1s=0l;
		long num2s=0l;
		long num3s=0l;
		for (int i=0;i<seqs.length;++i)	{
			num1s+=vector[i]*seqs[i].num1s;
			num2s+=vector[i]*seqs[i].num2s;
			num3s+=vector[i]*seqs[i].num3s;
		}
		return String.format("%s,%s,%s",num1s%mod,num2s%mod,num3s%mod);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Subsequence[] subseqs=new Subsequence[PATTERNS.length];
		for (int i=0;i<PATTERNS.length;++i) subseqs[i]=Subsequence.fromString(PATTERNS[i]);
		LongMatrix mat=createInitialMatrix(subseqs);
		LongMatrix finalMatrix=mat.pow(N-8,MOD);
		long[] initialVector=new long[PATTERNS.length];
		initialVector[23]=1l;
		initialVector[38]=1l;
		long[] finalVector=finalMatrix.multiply(initialVector,MOD);
		String result=getFinalResult(finalVector,subseqs,MOD);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
