package com.euler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.euler.threads.InputThreadStopper;
import com.euler.threads.InterruptableProgram;
import com.euler.threads.ProgramState;

public class Euler803_7 extends InterruptableProgram<Euler803_7.Euler803State> {
	public final static Path FILE_PATH=Paths.get("F:\\Trabajo\\Programación\\803tmp.txt");
	
	private final static long F=25214903917l;
	private final static long K=11l;
	
	// private final static long A0=123456l;
	// private final static long A0=78580612777175l;
	private final static long A0=144933752257087l;
	private final static String SEARCH="LuckyText";
	
	public static class Euler803State implements ProgramState	{
		public long a;
		public int automataState;
		public long iterations;
		public Euler803State()	{
			a=A0;
			automataState=0;
			iterations=0;
		}
		@Override
		public void saveToFile(BufferedWriter writer) throws IOException {
			System.out.println(String.format("Saving to file: (%d, %d, %d).",a,automataState,iterations));
			writer.append(Long.toString(a));
			writer.newLine();
			writer.append(Integer.toString(automataState));
			writer.newLine();
			writer.append(Long.toString(iterations));
			writer.newLine();
		}
		@Override
		public void readStateFromFile(BufferedReader reader) throws IOException {
			a=Long.parseLong(reader.readLine());
			automataState=Integer.parseInt(reader.readLine());
			iterations=Long.parseLong(reader.readLine());
			System.out.println(String.format("Read from file: (%d, %d, %d).",a,automataState,iterations));
		}
	}
	
	private Euler803State state;
	
	public Euler803_7() {
		super(new InputThreadStopper());
		state=getInitialState();
	}

	public static void main(String[] args) throws IOException, ReflectiveOperationException	{
		new Euler803_7().start(Euler803State.class);
	}
	
	@Override
	public Path getFileName() {
		return FILE_PATH;
	}

	@Override
	public Euler803State getInitialState() {
		return new Euler803State();
	}

	@Override
	public Euler803State getCurrentState() {
		return state;
	}

	private static int[] cToB(String in)	{
		char[] cs=in.toCharArray();
		int[] result=new int[cs.length];
		for (int i=0;i<cs.length;++i)	{
			char c=cs[i];
			if ((c>='a')&&(c<='z')) result[i]=c-'a';
			else if ((c>='A')&&(c<='Z')) result[i]=c-'A'+26;
			else throw new IllegalArgumentException("Invalid character: \""+c+"\".");
		}
		return result;
	}
	
	private static class SimpleAutomata	{
		private static interface State	{
			public int nextState(int b);
		}
		private static class BaseState implements State	{
			private final int startChar;
			public BaseState(int startChar)	{
				this.startChar=startChar;
			}
			@Override
			public int nextState(int b)	{
				return (b==startChar)?1:0;
			}
		}
		private static class StandardState implements State	{
			private final int startChar;
			private final int myChar;
			private final int nextState;
			public StandardState(int startChar,int myChar,int nextState)	{
				this.startChar=startChar;
				this.myChar=myChar;
				this.nextState=nextState;
			}
			@Override
			public int nextState(int b)	{
				if (b==startChar) return 1;
				else if (b==myChar) return nextState;
				else return 0;
			}
		}
		private final State[] states;
		private State current;
		public SimpleAutomata(String str)	{
			int[] bs=cToB(str);
			states=new State[bs.length];
			states[0]=new BaseState(bs[0]);
			for (int i=1;i<states.length;++i) states[i]=new StandardState(bs[0],bs[i],i+1);
			current=states[0];
		}
		public boolean run(int b)	{
			int next=current.nextState(b);
			if (next>=states.length)	{
				current=states[0];
				return true;
			}
			current=states[next];
			return false;
		}
		public void setState(int index)	{
			current=states[index];
		}
		public int getCurrentIndex()	{
			for (int i=0;i<states.length;++i) if (current==states[i]) return i;
			throw new IllegalStateException();
		}
	}
	
	private static class Calculator48	{
		private final static long MASK=(1l<<32)-1;
		private final static long SMALL_MASK=(1l<<16)-1;
		private final long lowF;
		private final long highF;
		private final long k;
		public Calculator48(long f,long k)	{
			lowF=f&MASK;
			highF=f>>>32;
			this.k=k;
		}
		public long next(long n)	{
			long lowN=n&MASK;
			long highN=n>>>32;
			long base=(lowN*lowF)+k;
			long carry=base>>>32;
			long lowResult=base&MASK;
			long highResult=lowN*highF+lowF*highN+carry;
			highResult&=SMALL_MASK;
			return (highResult<<32)+lowResult;
		}
	}
	
	@Override
	public void main(Euler803State state) {
		try	{
			this.state=state;
			long a=state.a;
			Calculator48 calc=new Calculator48(F,K);
			SimpleAutomata automata=new SimpleAutomata(SEARCH);
			automata.setState(state.automataState);
			for (long i=state.iterations+1;;++i)	{
				int b=(int)((a>>16)%52);
				if (automata.run(b))	{
					System.out.println("Iterations: "+(i-SEARCH.length()));
					System.out.println("Last an: "+a+". Last an BINARY: "+Long.toString(a,2)+".");
				}
				a=calc.next(a);
				if (checkAndStop())	{
					state.a=a;
					state.automataState=automata.getCurrentIndex();
					state.iterations=i;
					break;
				}
			}
			stop();	// Write to file, the next program will continue.
		}	catch (IOException exc)	{
			System.out.println("Error de I/O");
		}
	}
}
