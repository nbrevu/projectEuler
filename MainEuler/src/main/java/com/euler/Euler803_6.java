package com.euler;

public class Euler803_6 {
	private final static long F=25214903917l;
	private final static long K=11l;
	
	// private final static long A0=123456l;
	private final static long A0=78580612777175l;
	
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
	
	public static void main(String[] args)	{
		String search="Puzzle";
		SimpleAutomata automata=new SimpleAutomata(search);
		long a=A0;
		Calculator48 calc=new Calculator48(F,K);
		for (long i=1;;++i)	{
			int b=(int)((a>>16)%52);
			if (automata.run(b))	{
				System.out.println("Iterations: "+(i-search.length()));
				System.out.println("Last an: "+a+". Last an BINARY: "+Long.toString(a,2)+".");
			}
			a=calc.next(a);
		}
	}
}
