package com.euler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.euler.common.Primes;
import com.euler.threads.InputThreadStopper;
import com.euler.threads.InterruptableProgram;
import com.euler.threads.ProgramState;
import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;
import com.koloboke.collect.IntCollection;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.ObjLongCursor;
import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;
import com.koloboke.collect.map.hash.HashObjLongMaps;

public class Euler362 extends InterruptableProgram<Euler362.Euler362State> {
	private final static long LIMIT=LongMath.pow(10l,10);
	
	public final static Path FILE_PATH=Paths.get("C:\\362tmp.txt");
	
	public static class Euler362State implements ProgramState	{
		public final ObjLongMap<FactorCollection> stateCount;
		public long currentNumber;
		public Euler362State()	{
			stateCount=HashObjLongMaps.newMutableMap();
			currentNumber=2;
		}
		@Override
		public void saveToFile(BufferedWriter writer) throws IOException {
			System.out.println("Saving to file: ("+currentNumber+","+stateCount.size()+" objects).");
			writer.append(Long.toString(currentNumber));
			writer.newLine();
			writer.append(Integer.toString(stateCount.size()));
			writer.newLine();
			for (ObjLongCursor<FactorCollection> cursor=stateCount.cursor();cursor.moveNext();)	{
				writer.append(cursor.key().toString());
				writer.newLine();
				writer.append(Long.toString(cursor.value()));
				writer.newLine();
			}
		}

		@Override
		public void readStateFromFile(BufferedReader reader) throws IOException {
			// ACHTUNG! If the LAST number we used was X, the next operation must start with X+1!!
			currentNumber=1+Long.parseLong(reader.readLine());
			stateCount.clear();
			int size=Integer.parseInt(reader.readLine());
			for (int i=0;i<size;++i)	{
				FactorCollection factors=FactorCollection.fromString(reader.readLine());
				long counter=Long.parseLong(reader.readLine());
				stateCount.put(factors,counter);
			}
			System.out.println("Read from file: ("+currentNumber+","+stateCount.size()+" objects).");
		}
		
	}
	
	public static class FactorCollection	{
		public final int[] exponents;
		public FactorCollection(LongIntMap sample)	{
			exponents=getExponents(sample);
		}
		public FactorCollection(int[] exponents)	{
			this.exponents=exponents;
		}
		private static int[] getExponents(LongIntMap factors)	{
			IntCollection exponentsCollection=factors.values();
			int[] exponentsArray=Ints.toArray(exponentsCollection);
			Arrays.sort(exponentsArray);
			return exponentsArray;
		}
		@Override
		public boolean equals(Object other)	{
			try	{
				FactorCollection fcOther=(FactorCollection)other;
				return Arrays.equals(exponents,fcOther.exponents);
			}	catch (ClassCastException exc)	{
				return false;
			}
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(exponents);
		}
		@Override
		public String toString()	{
			return Arrays.toString(exponents);
		}
		public static FactorCollection fromString(String in)	{
			in=in.substring(1,in.length()-1);
			String[] split=in.split(",");
			int[] ints=new int[split.length];
			for (int i=0;i<split.length;++i) ints[i]=Integer.parseInt(split[i].trim());
			return new FactorCollection(ints);
		}
	}
	
	private Euler362State state;
	
	public Euler362() {
		super(new InputThreadStopper());
		state=getInitialState();
	}

	public static void main(String[] args) throws IOException, ReflectiveOperationException	{
		new Euler362().start(Euler362State.class);
	}
	
	private FactorCollection factor(long in,long[] primes)	{
		LongIntMap factors=HashLongIntMaps.newMutableMap();
		for (long p:primes)	{
			if ((p*p)>in) break;
			while ((in%p)==0l)	{
				in/=p;
				factors.addValue(p,1);
			}
		}
		if (in>1) factors.addValue(in,1);
		return new FactorCollection(factors);
	}

	@Override
	public void main(Euler362State state) {
		try	{
			this.state=state;
			long[] primes=Primes.listLongPrimes(LongMath.sqrt(LIMIT,RoundingMode.DOWN)).stream().mapToLong(Long::longValue).toArray();
			for (;state.currentNumber<=LIMIT;++state.currentNumber)	{
				if ((state.currentNumber%10000000)==0) System.out.println(state.currentNumber);
				state.stateCount.addValue(factor(state.currentNumber,primes),1l);
				if (checkAndStop()) return;
			}
			stop();	// Write to file, the next program will continue.
		}	catch (IOException exc)	{
			System.out.println("Error de I/O");
		}
	}

	@Override
	public Path getFileName() {
		return FILE_PATH;
	}

	@Override
	public Euler362State getInitialState() {
		return new Euler362State();
	}

	@Override
	public Euler362State getCurrentState() {
		return state;
	}
}
