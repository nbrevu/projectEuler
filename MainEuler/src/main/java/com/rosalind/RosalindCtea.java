package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.math.LongMath;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;
import com.rosalind.utils.ProteinUtils;

public class RosalindCtea {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_ctea.txt";
	private final static long MOD=LongMath.pow(2l,27)-1;
	
	/*
def num_optimal_alignments(C, s, t, i, j):
    if i==0 or j == 0:
        return 1
    if (i,j) not in lookup:
        total_ways = 0
        if C[i][j] == C[i-1][j] + 1:
            total_ways += num_optimal_alignments(C, s, t, i-1, j)
        if C[i][j] == C[i][j-1] + 1: 
            total_ways += num_optimal_alignments(C, s, t, i, j-1)
        if C[i][j] == C[i-1][j-1] and s[i-1] == t[j-1]:
            total_ways += num_optimal_alignments(C, s, t, i-1, j-1)
        if C[i][j] == C[i-1][j-1] + 1 and s[i-1] != t[j-1]:
            total_ways += num_optimal_alignments(C, s, t, i-1, j-1)

        assert total_ways > 0
        
        lookup[(i,j)] =  total_ways % (2**27 - 1)

return lookup[(i,j)] 
	 */
	private static long getNumOptimalAlignmentsRecursive(int[][] levenshtein,String s1,String s2,int i,int j,long mod,Table<Integer,Integer,Long> cache)	{
		if ((i==0)||(j==0)) return 1;
		else if (cache.contains(i,j)) return cache.get(i,j);
		else	{
			long result=0;
			if (levenshtein[i][j]==1+levenshtein[i-1][j]) result+=getNumOptimalAlignmentsRecursive(levenshtein,s1,s2,i-1,j,mod,cache);
			if (levenshtein[i][j]==1+levenshtein[i][j-1]) result+=getNumOptimalAlignmentsRecursive(levenshtein,s1,s2,i,j-1,mod,cache);
			if ((levenshtein[i][j]==levenshtein[i-1][j-1])&&(s1.charAt(i-1)==s2.charAt(j-1))) result+=getNumOptimalAlignmentsRecursive(levenshtein,s1,s2,i-1,j-1,mod,cache);
			if ((levenshtein[i][j]==1+levenshtein[i-1][j-1])&&(s1.charAt(i-1)!=s2.charAt(j-1))) result+=getNumOptimalAlignmentsRecursive(levenshtein,s1,s2,i-1,j-1,mod,cache);
			result%=mod;
			cache.put(i,j,result);
			return result;
		}
	}
	
	private static long getNumOptimalAlignments(String s1,String s2,long mod)	{
		int[][] levenshtein=ProteinUtils.computeLevenshteinArray(s1,s2);
		Table<Integer,Integer,Long> cache=HashBasedTable.create();
		return getNumOptimalAlignmentsRecursive(levenshtein,s1,s2,s1.length(),s2.length(),mod,cache);
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		assert entries.size()==2;
		long result=getNumOptimalAlignments(entries.get(0).getContent(),entries.get(1).getContent(),MOD);
		System.out.println(result);
	}
}
