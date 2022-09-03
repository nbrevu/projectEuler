package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.padic.RationalPAdicArithmetic;
import com.euler.common.padic.RationalPAdicArithmetic.PAdicRational;
import com.euler.common.padic.RationalPAdicArithmetic.PAdicRationalSquareMatrix;

public class Euler541_2 {
	private final static int PRIME=137;
	
	private final static int N=10;
	private final static int PRECISION=5*N;
	
	private static class GmElement	{
		public final long n;
		public final PAdicRational h;
		public GmElement(long n,PAdicRational h)	{
			this.n=n;
			this.h=h;
		}
		@Override
		public String toString()	{
			return Long.toString(n);
		}
	}
	
	// First version. Brute force of sorts.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		// Initialise the p-adic calculator and related variables.
		RationalPAdicArithmetic calculator=new RationalPAdicArithmetic(PRIME,PRECISION);
		int maxInitialH=PRIME*N;
		// Calculate the first p-adic harmonic numbers normally.
		PAdicRational[] hs=new PAdicRational[maxInitialH+1];
		hs[0]=calculator.getInteger(0l);
		for (int i=1;i<=maxInitialH;++i) hs[i]=calculator.add(hs[i-1],calculator.inverse(i));
		// Now we need the independent terms of the equation in c_kp^{2k}, calculated from the initial H set.
		PAdicRational[] bs=new PAdicRational[N];
		for (int i=0;i<N;++i) bs[i]=calculator.subtract(hs[PRIME*(i+1)],calculator.multiplyTimesPrimePower(hs[i+1],-1));
		// And then the main matrix of the system.
		PAdicRationalSquareMatrix vandermonde=calculator.createMatrix(N);
		for (int i=0;i<N;++i)	{
			long n=i+1;
			PAdicRational nn=calculator.getInteger(n*n);
			vandermonde.set(i,0,nn);
			for (int j=1;j<N;++j) vandermonde.set(i,j,calculator.multiply(nn,vandermonde.get(i,j-1)));
		}
		// From the matrix and the independent terms we calculate c_kp^{2k}, and with them, c_k.
		// I'm getting slightly different values for c_k than the paper says :(. They are, still, mostly right...
		PAdicRational[] shiftedCs=calculator.solve(vandermonde,bs);
		// The cs values are not strictly necessary. We can use the shifted Cs.
		/*
		PAdicRational[] cs=new PAdicRational[N];
		for (int i=0;i<N;++i) cs[i]=calculator.multiplyTimesPrimePower(shiftedCs[i],-2*(i+1));
		*/
		// Now we initialise G1.
		List<GmElement> currentG=new ArrayList<>();
		for (int i=1;i<PRIME;++i) if (hs[i].v>0) currentG.add(new GmElement(i,hs[i]));
		// And now the main iterations.
		long result;
		for (;;)	{
			// System.out.println(currentG+"...");
			List<GmElement> nextG=new ArrayList<>();
			for (GmElement elem:currentG)	{
				// Calculate H_{pn}=1/p*H_n + sum_{k=1}^N (c_kp^{2k})*(n^2)^k.
				PAdicRational hPN=calculator.multiplyTimesPrimePower(elem.h,-1);
				PAdicRational n=calculator.getInteger(elem.n);
				PAdicRational nn=calculator.multiply(n,n);
				PAdicRational powerN=nn;
				for (int i=0;i<N;++i)	{
					hPN=calculator.add(hPN,calculator.multiply(shiftedCs[i],powerN));
					powerN=calculator.multiply(powerN,nn);
				}
				long pn=PRIME*elem.n;
				PAdicRational h=hPN;
				for (int i=1;i<PRIME;++i)	{
					long x=pn+i;
					h=calculator.add(h,calculator.inverse(x));
					if (h.v>0) nextG.add(new GmElement(x,h));
				}
			}
			if (nextG.isEmpty())	{
				long lastJ=currentG.get(currentG.size()-1).n;
				result=PRIME*(lastJ+1)-1;
				break;
			}	else currentG=nextG;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		// It only works for some primes :O. The result is wrong for P=7.
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
