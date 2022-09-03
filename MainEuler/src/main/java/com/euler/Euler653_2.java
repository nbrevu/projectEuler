package com.euler;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.koloboke.collect.IntCursor;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler653_2 {
	private final static int D=20;
	private final static int X=10000000;
	private final static long L=1000l*X;
	private final static int N=X+1;
	private final static int J=(X/2)+1;
	
	private final static long R_INITIAL=6563116;
	private final static long R_MOD=32745673;
	private final static long R_POSITION_MOD=1000;
	private final static long R_LIMIT=10_000_000;
	
	private static class Ball	{
		private long position;
		private boolean direction;	// False: leftwards; true: rightwards.
		private long lastUpdateTime;
		public Ball(long position,boolean direction)	{
			this.position=position;
			this.direction=direction;
			lastUpdateTime=0;
		}
		public long position()	{
			return position;
		}
		public boolean direction()	{
			return direction;
		}
		public void update(long currentTime)	{
			move(currentTime-lastUpdateTime);
		}
		public void move(long amount)	{
			if (direction) position+=amount;
			else position-=amount;
			lastUpdateTime+=amount;
		}
		public void collide()	{
			direction=!direction;
		}
	}
	
	private static class RandomGenerator	{
		private long r;
		public RandomGenerator()	{
			r=R_INITIAL;
		}
		public long next()	{
			long result=r;
			r=(r*r)%R_MOD;
			return result;
		}
	}
	
	/*
	 * Uses positions multiplied times two. This means that every single ball is either at an even or an odd position, so all the collision
	 * happen at integer positions, instead of half integers, and no stupid shit (either custom types or floating point) is necessary.
	 */
	private static class BallGenerator	{
		private long lastPos;
		private RandomGenerator generator;
		public BallGenerator()	{
			lastPos=-D;	// Silly trick to make the first border match the "0" position.
			generator=new RandomGenerator();
		}
		public Ball getNextBall()	{
			long r=generator.next();
			boolean direction=(r<=R_LIMIT);
			long gap=(r%R_POSITION_MOD)+1;
			long centreGap=gap+D;
			lastPos+=2*centreGap;
			return new Ball(lastPos,direction);
		}
	}
	
	private static class EventSet	{
		/*
		 * Each collision is represented by the index of the RIGHTMOST ball.
		 * This means that if the index is 0, it's the leftmost ball colliding with the left border.
		 * If the index is Integer.MAX_VALUE, it means that the current rightmost ball will exit at the speculated time.  
		 */
		private final NavigableMap<Long,IntSet> futureEvents;
		public EventSet()	{
			futureEvents=new TreeMap<>();
		}
		public void addEvent(Long plannedTime,int affectedBall)	{
			IntSet toAdd=futureEvents.computeIfAbsent(plannedTime,(Long unused)->HashIntSets.newMutableSet());
			toAdd.add(affectedBall);
		}
		public Map.Entry<Long,IntSet> getNextEventSet()	{
			return futureEvents.pollFirstEntry();
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Ball[] balls=new Ball[N];
		int lastPresentBall=N-1;
		BallGenerator gen=new BallGenerator();
		EventSet events=new EventSet();
		Ball lastBall=null;
		for (int i=0;i<N;++i)	{
			Ball newBall=gen.getNextBall();
			if (lastBall==null)	{
				if (!newBall.direction()) events.addEvent(newBall.position()-D,0);
			}	else if (!newBall.direction()&&lastBall.direction()) events.addEvent((newBall.position()-lastBall.position())/2-D,i);
			else if ((i==(N-1))&&newBall.direction()) events.addEvent(2*L-newBall.position(),Integer.MAX_VALUE);
			balls[i]=newBall;
			lastBall=newBall;
		}
		for (;;)	{
			// "Elapsed 193204.83237979803 seconds". I'm not proud of this.
			Map.Entry<Long,IntSet> nextEvents=events.getNextEventSet();
			long currentTime=nextEvents.getKey().longValue();
			IntSet affectedBalls=nextEvents.getValue();
			for (IntCursor cursor=affectedBalls.cursor();cursor.moveNext();)	{
				int ballIndex=cursor.elem();
				if (ballIndex==Integer.MAX_VALUE)	{
					// The last ball exits.
					if (lastPresentBall==J-1)	{
						// End result!
						StringBuilder result=new StringBuilder();
						result.append(currentTime/2);
						if ((currentTime&1)==1) result.append(".5");
						long tac=System.nanoTime();
						double seconds=(tac-tic)*1e-9;
						System.out.println(result.toString());
						System.out.println("Elapsed "+seconds+" seconds.");
						System.exit(0);
					}
					--lastPresentBall;
					balls[lastPresentBall].update(currentTime);
					if (balls[lastPresentBall].direction()) events.addEvent(currentTime+2*L-balls[lastPresentBall].position(),Integer.MAX_VALUE);
				}	else	{
					Ball affectedBall=balls[ballIndex];
					affectedBall.update(currentTime);
					affectedBall.collide();
					// Note that "affectedBall" must be moving to the right after the collision.
					if (ballIndex<lastPresentBall)	{
						Ball nextBall=balls[ballIndex+1];
						nextBall.update(currentTime);
						if (!nextBall.direction()) events.addEvent(currentTime+(nextBall.position()-affectedBall.position())/2-D,ballIndex+1);
					}	else events.addEvent(currentTime+2*L-affectedBall.position(),Integer.MAX_VALUE);
					if (ballIndex>0)	{
						Ball previousBall=balls[ballIndex-1];
						previousBall.update(currentTime);
						previousBall.collide();
						// Note that "previousBall" must be moving to the left after the collision.
						if (ballIndex==1) events.addEvent(currentTime+previousBall.position()-D,0);
						else	{
							Ball prePreviousBall=balls[ballIndex-2];
							prePreviousBall.update(currentTime);
							if (prePreviousBall.direction()) events.addEvent(currentTime+(previousBall.position()-prePreviousBall.position())/2-D,ballIndex-1);
						}
					}
				}
			}
		}
	}
}
