package com.euler;

public class Euler752 {
	private static class State	{
		public final int a;
		public final int b;
		private State(int a,int b)	{
			this.a=a;
			this.b=b;
		}
		public State()	{
			this(1,1);
		}
		public State next(int mod)	{
			int newA=(a+7*b)%mod;
			int newB=(a+b)%mod;
			return new State(newA,newB);
		}
		@Override
		public String toString()	{
			return String.format("(%d,%d)",a,b);
		}
		public boolean isFinal()	{
			return (a==1)&&(b==0);
		}
	}
	
	public static void main(String[] args)	{
		// Vale, parece que los ciclos de "b" tienen longitud fija :). Si es así, es muy fácil.
		int x=11;
		State s=new State();
		for (int i=1;!s.isFinal();++i)	{
			State next=s.next(x);
			System.out.println(String.format("n=%d: s=%s.",i,next));
			s=next;
		}
	}
}
