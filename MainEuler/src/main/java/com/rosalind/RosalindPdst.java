package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.euler.common.DoubleMatrix;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;
import com.rosalind.utils.ProteinUtils;

public class RosalindPdst {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_pdst.txt";
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		int N=entries.size();
		DoubleMatrix result=new DoubleMatrix(N);
		int len=entries.get(0).getContent().length();
		for (int i=1;i<N;++i) for (int j=0;j<i;++j)	{
			int dist=ProteinUtils.computeHammingDistance(entries.get(i).getContent(),entries.get(j).getContent());
			double ratio=(double)dist/(double)len;
			result.assign(i,j,ratio);
			result.assign(j,i,ratio);
		}
		System.out.println(result.toString());
	}
}
