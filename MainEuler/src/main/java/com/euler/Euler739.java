package com.euler;

import java.util.TreeMap;

import com.koloboke.collect.map.CharLongCursor;
import com.koloboke.collect.map.CharLongMap;
import com.koloboke.collect.map.hash.HashCharLongMaps;

public class Euler739 {
	// OK. It's OEIS A009776.
	private static CharLongMap add(CharLongMap m1,CharLongMap m2)	{
		CharLongMap result=HashCharLongMaps.newMutableMap(m1);
		for (CharLongCursor cursor=m2.cursor();cursor.moveNext();) result.addValue(cursor.key(),cursor.value(),0l);
		return result;
	}
	
	private static CharLongMap[][] addBruteForce(int size)	{
		CharLongMap[][] result=new CharLongMap[size][];
		// First row.
		result[0]=new CharLongMap[size];
		for (int j=0;j<size;++j) result[0][j]=HashCharLongMaps.newImmutableMap(new char[] {(char)('a'+j)},new long[] {1l});
		// Remaining rows.
		for (int i=1;i<size;++i)	{
			result[i]=new CharLongMap[size-i];
			result[i][0]=result[i-1][1];
			for (int j=1;j<result[i].length;++j) result[i][j]=add(result[i][j-1],result[i-1][j+1]);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		CharLongMap[][] additions=addBruteForce(10);
		System.out.println(new TreeMap<>(additions[additions.length-1][0]));
	}
}
