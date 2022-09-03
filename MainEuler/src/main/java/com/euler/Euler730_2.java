package com.euler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.LongPair;
import com.euler.common.EulerUtils.Pair;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.euler.threads.InputThreadStopper;
import com.euler.threads.InterruptableProgram;
import com.euler.threads.ProgramState;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler730_2 extends InterruptableProgram<Euler730_2.Euler730State> {
	private final static long N=LongMath.pow(10l,8);
	private final static long M=100l;
	
	private final static Path TMP_FILE=Paths.get("E:\\tmp730.txt");
	
	public static class Euler730State implements ProgramState	{
		private long currentResult;
		private long r;
		@Override
		public void saveToFile(BufferedWriter writer) throws IOException {
			System.out.println("Saving to file: ("+currentResult+","+r+").");
			writer.write(Long.toString(currentResult));
			writer.newLine();
			writer.write(Long.toString(r));
		}
		@Override
		public void readStateFromFile(BufferedReader reader) throws IOException {
			currentResult=Long.parseLong(reader.readLine());
			r=Long.parseLong(reader.readLine());
			System.out.println("Read from file: ("+currentResult+","+r+").");
		}
	}
	
	private static long pow(long b,int e)	{
		long result=b;
		for (int i=2;i<=e;++i) result*=b;
		return result;
	}
	
	private static LongPair hermiteAlgorithmWithoutOverflow(long p)	{
		if (p<Integer.MAX_VALUE) return EulerUtils.hermiteAlgorithm(p);
		Pair<BigInteger,BigInteger> bigResult=EulerUtils.hermiteAlgorithm(BigInteger.valueOf(p));
		return LongPair.sorted(bigResult.first.longValueExact(),bigResult.second.longValueExact());
	}
	private static BaseSquareDecomposition hermiteAlgorithm(long p)	{
		return new BaseSquareDecomposition(hermiteAlgorithmWithoutOverflow(p));
	}
	
	// This is not the same as in problem 264! In this case we might "miss" intermediate values.
	private static class SquareSumFinder	{
		private final static BaseSquareDecomposition[] POINTER=new BaseSquareDecomposition[1];	// Sometimes I hate Java.
		private final PrimeDecomposer decomposer;
		private final LongObjMap<BaseSquareDecomposition> decompositions;
		public SquareSumFinder(int N)	{
			decomposer=new StandardPrimeDecomposer(20*N);
			decompositions=HashLongObjMaps.newMutableMap();
		}
		private BaseSquareDecomposition getForPrime(long prime)	{
			return decompositions.computeIfAbsent(prime,Euler730_2::hermiteAlgorithm);
		}
		private BaseSquareDecomposition getForPrimePower(long prime,int exp)	{
			long n=prime;
			BaseSquareDecomposition base=getForPrime(n);
			POINTER[0]=base;
			for (int i=2;i<=exp;++i)	{
				n*=prime;
				POINTER[0]=decompositions.computeIfAbsent(n,(long unused)->POINTER[0].combineWith(base));
			}
			return POINTER[0];
		}
		private BaseSquareDecomposition getFor(DivisorHolder holder)	{
			BaseSquareDecomposition result=null;
			LongIntMap decomp=holder.getFactorMap();
			long additionalFactor=1l;
			boolean scramble=false;
			for (LongIntCursor cursor=decomp.cursor();cursor.moveNext();) if (cursor.key()==2l)	{
				int exp=cursor.value();
				scramble=((exp&1)!=0);
				additionalFactor*=(1<<(exp>>1));
			}	else if ((cursor.key()%4)==3)	{
				int exp=cursor.value();
				if ((exp&1)!=0) return BaseSquareDecomposition.EMPTY;
				else additionalFactor*=pow(cursor.key(),exp>>1);
			}	else	{
				BaseSquareDecomposition tmpResult=getForPrimePower(cursor.key(),cursor.value());
				if (result==null) result=tmpResult;
				else result=result.combineWith(tmpResult);
			}
			if (result==null) return scramble?new BaseSquareDecomposition(additionalFactor,additionalFactor):BaseSquareDecomposition.EMPTY;
			if (additionalFactor>1) result=result.scale(additionalFactor);
			if (scramble) result=result.scramble();
			return result;
		}
		public BaseSquareDecomposition getForSquare(long r)	{
			DivisorHolder decomp1=decomposer.decompose(r);
			for (LongIntCursor cursor=decomp1.getFactorMap().cursor();cursor.moveNext();) cursor.setValue(2*cursor.value());
			return getFor(decomp1);
		}
		public BaseSquareDecomposition getFor(long a,long b)	{
			DivisorHolder decomp1=decomposer.decompose(a);
			DivisorHolder decomp2=decomposer.decompose(b);
			decomp1.combineDestructive(decomp2);
			return getFor(decomp1);
		}
		public BaseSquareDecomposition getFor(long n)	{
			BaseSquareDecomposition result=decompositions.get(n);
			if (result!=null) return result;
			return getFor(decomposer.decompose(n));
		}
	}
	
	private Euler730State state;
	
	public Euler730_2() {
		super(new InputThreadStopper());
		state=getInitialState();
	}

	public static void main(String[] args) throws IOException,ReflectiveOperationException	{
		new Euler730_2().start(Euler730State.class);
	}
	
	private static boolean canBeDiscardedFast(long n)	{
		if ((n&3)==3) return true;
		long mod9=n%9;
		if (mod9==3||mod9==6) return true;
		return (((n%7)==0)&&(n%49!=0));
	}

	/*
	 * I'm not proud of this, but after, uhm, less than two weeks I got the correct result.
	 * 1315965924
	 */
	@Override
	public void main(Euler730State state) {
		try	{
			long[] squareMap=new long[101];
			Arrays.fill(squareMap,-1l);
			for (int i=1;i<=10;++i) squareMap[(int)(i*i)]=i;
			this.state=state;
			SquareSumFinder sumDecomposer=new SquareSumFinder((int)N);
			long maxR=N/2;
			for (++state.r;state.r<=maxR;++state.r)	{
				if ((state.r%1000)==0) System.out.println(state.r+"...");
				long r2=state.r*state.r;
				for (int k=0;k<=M;++k)	{
					BaseSquareDecomposition squareSums;
					if (k==0) squareSums=sumDecomposer.getForSquare(state.r);
					else	{
						long sum=r2-k;
						if (sum<2) break;
						if (canBeDiscardedFast(sum)) continue;
						long root=squareMap[k];
						if (root>0) squareSums=sumDecomposer.getFor(state.r-root,state.r+root);
						else squareSums=sumDecomposer.getFor(sum);
					}
					for (LongPair pair:squareSums.getBaseCombinations())	{
						if (pair.x==0) continue;
						long n=pair.x+pair.y+state.r;
						if (n<=N)	{
							long g=EulerUtils.gcd(pair.x,pair.y);
							if ((g==1)||(EulerUtils.gcd(g,state.r)==1)) ++state.currentResult;
						}
					}
				}
				if (checkAndStop()) return;
			}
			System.out.println(state.currentResult);
		}	catch (IOException exc)	{
			System.out.println("Error de I/O");
		}
	}

	@Override
	public Path getFileName() {
		return TMP_FILE;
	}

	@Override
	public Euler730State getInitialState() {
		Euler730State result=new Euler730State();
		result.currentResult=0l;
		result.r=1l;
		return result;
	}

	@Override
	public Euler730State getCurrentState() {
		return state;
	}
}
