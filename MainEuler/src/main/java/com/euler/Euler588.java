package com.euler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Euler588 {
	private static class Polynomial	{
		private final List<Integer> coefficients;	// Position indicates degree of x.
		public Polynomial(List<Integer> coefficients)	{
			this.coefficients=coefficients;
		}
		public static Polynomial product(Polynomial a,Polynomial b)	{
			int N=a.coefficients.size()+b.coefficients.size()-1;
			List<Integer> coefficients=new ArrayList<>(N);
			for (int i=0;i<N;++i) coefficients.add(0);
			for (int i=0;i<a.coefficients.size();++i) for (int j=0;j<b.coefficients.size();++j)	{
				int pos=i+j;
				int sum=a.coefficients.get(i)*b.coefficients.get(j)+coefficients.get(pos);
				coefficients.set(pos,sum%2);
			}
			return new Polynomial(coefficients);
		}
		public int countOddCoeffs()	{
			int result=0;
			for (int val:coefficients) if (val==1) ++result;
			else if (val!=0) throw new RuntimeException("MAL.");
			return result;
		}
		@Override
		public String toString()	{
			return coefficients.toString();
		}
	}
	
	public static void main(String[] args)	{
		int howMany=1000;
		Polynomial base=new Polynomial(Arrays.asList(1,1,1,1,1));
		List<Polynomial> allPolys=new ArrayList<>(1+howMany);
		allPolys.add(new Polynomial(Arrays.asList(1)));
		allPolys.add(base);
		for (int i=2;i<=howMany;++i) allPolys.add(Polynomial.product(allPolys.get(i-1),base));
		try (PrintStream ps=new PrintStream(new File("C:\\out588.txt")))	{
			for (int i=0;i<=howMany;++i)	{
				ps.println("Degree "+i+":");
				Polynomial poly=allPolys.get(i);
				ps.println("\tPolynomial: "+poly.toString()+".");
				ps.println("\tOdd coefficients: "+poly.countOddCoeffs()+".");
				ps.println();
			}
		}	catch (IOException exc)	{
			System.out.println("No :(.");
		}
	}
}
