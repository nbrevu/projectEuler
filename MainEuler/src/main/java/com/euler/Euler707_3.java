package com.euler;

import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.math.IntMath;

public class Euler707_3 {
	/*
	F(1,1)=2; log2(2)=1.
	F(1,2)=2; log2(2)=1.
	F(1,3)=8; log2(8)=3.
	F(1,4)=16; log2(16)=4.
	F(1,5)=16; log2(16)=4.
	F(1,6)=64; log2(64)=6.
	F(1,7)=128; log2(128)=7.
	F(1,8)=128; log2(128)=7.
	F(1,9)=512; log2(512)=9.
	F(1,10)=1024; log2(1024)=10.
	F(1,11)=1024; log2(1024)=10.
	F(1,12)=4096; log2(4096)=12.
	F(1,13)=8192; log2(8192)=13.
	F(1,14)=8192; log2(8192)=13.
	F(1,15)=32768; log2(32768)=15.
	F(1,16)=65536; log2(65536)=16.
	F(1,17)=65536; log2(65536)=16.
	F(1,18)=262144; log2(262144)=18.
	F(1,19)=524288; log2(524288)=19.
	F(1,20)=524288; log2(524288)=19.
	F(1,21)=2097152; log2(2097152)=21.
	F(1,22)=4194304; log2(4194304)=22.
	F(1,23)=4194304; log2(4194304)=22.
	F(1,24)=16777216; log2(16777216)=24.
	F(1,25)=33554432; log2(33554432)=25.
	F(1,26)=33554432; log2(33554432)=25.
	F(1,27)=134217728; log2(134217728)=27.
	F(1,28)=268435456; log2(268435456)=28.
	F(1,29)=268435456; log2(268435456)=28.
	F(1,30)=1073741824; log2(1073741824)=30.
	F(2,2)=16; log2(16)=4.
	F(2,3)=16; log2(16)=4.
	F(2,4)=256; log2(256)=8.
	F(2,5)=512; log2(512)=9.
	F(2,6)=4096; log2(4096)=12.
	F(2,7)=4096; log2(4096)=12.
	F(2,8)=65536; log2(65536)=16.
	F(2,9)=131072; log2(131072)=17.
	F(2,10)=1048576; log2(1048576)=20.
	F(2,11)=1048576; log2(1048576)=20.
	F(2,12)=16777216; log2(16777216)=24.
	F(2,13)=33554432; log2(33554432)=25.
	F(2,14)=268435456; log2(268435456)=28.
	F(2,15)=268435456; log2(268435456)=28.
	F(3,3)=512; log2(512)=9.
	F(3,4)=4096; log2(4096)=12.
	F(3,5)=4096; log2(4096)=12.
	F(3,6)=262144; log2(262144)=18.
	F(3,7)=2097152; log2(2097152)=21.
	F(3,8)=4194304; log2(4194304)=22.
	F(3,9)=134217728; log2(134217728)=27.
	F(3,10)=1073741824; log2(1073741824)=30.
	F(4,4)=4096; log2(4096)=12.
	F(4,5)=1048576; log2(1048576)=20.
	F(4,6)=16777216; log2(16777216)=24.
	F(4,7)=268435456; log2(268435456)=28.
	F(5,5)=8388608; log2(8388608)=23.
	F(5,6)=1073741824; log2(1073741824)=30.
	 */
	private static class CustomStorage	{
		private final boolean[] elements;
		private final Deque<Integer> queue;
		public CustomStorage(int size)	{
			elements=new boolean[size];
			queue=new ArrayDeque<>();
		}
		public void add(int element)	{
			if (!elements[element])	{
				queue.add(element);
				elements[element]=true;
			}
		}
		public int poll()	{
			int result=queue.poll();
			elements[result]=false;
			return result;
		}
		public boolean isEmpty()	{
			return queue.isEmpty();
		}
	}
	
	private static class Simulator	{
		private final int height;
		private final int width;
		private final boolean[] simulation;
		public Simulator(int height,int width)	{
			if (height*width>30) throw new RuntimeException("No te flipes.");
			this.height=height;
			this.width=width;
			simulation=new boolean[1<<(height*width)];
		}
		public int simulate()	{
			simulateFromStartingState(0);
			return countTrueValues();
		}
		private void simulateFromStartingState(int startingValue)	{
			CustomStorage pending=new CustomStorage(1<<(height*width));
			pending.add(startingValue);
			while (!pending.isEmpty())	{
				int value=pending.poll();
				if (simulation[value]) continue;
				simulation[value]=true;
				for (int i=0;i<height;++i) for (int j=0;j<width;++j)	{
					int newState=toggle(value,i,j);
					if (!simulation[newState]) pending.add(newState);
				}
			}
		}
		private int toggle(int currentState,int i,int j)	{
			int bitMask=0;
			if (i>0) bitMask+=getBit(i-1,j);
			if (j>0) bitMask+=getBit(i,j-1);
			bitMask+=getBit(i,j);
			if (i<height-1) bitMask+=getBit(i+1,j);
			if (j<width-1) bitMask+=getBit(i,j+1);
			return currentState^bitMask;
		}
		private int getBit(int i,int j)	{
			int bit=i*width+j;
			return 1<<bit;
		}
		private int countTrueValues()	{
			int result=0;
			for (int i=0;i<simulation.length;++i) if (simulation[i]) ++result;
			return result;
		}
	}
	
	private static void simulate(int height,int width)	{
		Simulator s=new Simulator(height,width);
		int result=s.simulate();
		int log=IntMath.log2(result,RoundingMode.UNNECESSARY);
		System.out.println("F("+height+","+width+")="+result+"; log2("+result+")="+log+".");
	}
	
	public static void main(String[] args)	{
		for (int i=1;i<=5;++i) for (int j=i;(j*i)<=30;++j) simulate(i,j);
	}
}
