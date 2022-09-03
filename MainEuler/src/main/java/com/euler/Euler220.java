package com.euler;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Euler220 {
	private final static int DRAGON_SIZE=50;
	private final static long POSITION=1000000000000l;
	
	private static enum Heading	{
		RIGHT,UP,LEFT,DOWN;
		public Heading rotateLeft()	{
			switch(this)	{
			case RIGHT:return UP;
			case UP:return LEFT;
			case LEFT:return DOWN;
			case DOWN:return RIGHT;
			default:return null;
			}
		}
		public Heading rotateRight()	{
			switch(this)	{
			case RIGHT:return DOWN;
			case UP:return RIGHT;
			case LEFT:return UP;
			case DOWN:return LEFT;
			default:return null;
			}
		}
		public Heading opposite()	{
			switch(this)	{
			case RIGHT:return LEFT;
			case UP:return DOWN;
			case LEFT:return RIGHT;
			case DOWN:return UP;
			default:return null;
			}
		}
	}
	
	private static class Path	{
		public final long x;
		public final long y;
		public final Heading heading;
		public final long steps;
		private Path(long x,long y,Heading heading,long steps)	{
			this.x=x;
			this.y=y;
			this.heading=heading;
			this.steps=steps;
		}
		public final static Path EMPTY=new Path(0,0,Heading.UP,0);
		public final static Path F=new Path(0,1,Heading.UP,1);
		public final static Path L=new Path(0,0,Heading.LEFT,0);
		public final static Path R=new Path(0,0,Heading.RIGHT,0);
		public Path advance(Path followingPath)	{
			long xx=0,yy=0;
			switch (this.heading)	{
				case RIGHT:	{
					xx=followingPath.y;
					yy=-followingPath.x;
					break;
				}	case UP:	{
					xx=followingPath.x;
					yy=followingPath.y;
					break;
				}	case LEFT:	{
					xx=-followingPath.y;
					yy=followingPath.x;
					break;
				}	case DOWN:	{
					xx=-followingPath.x;
					yy=-followingPath.y;
					break;
				}	default:return null;
			}
			Heading h=this.heading;
			switch (followingPath.heading)	{
			case RIGHT:h=h.rotateRight();break;
			case LEFT:h=h.rotateLeft();break;
			case DOWN:h=h.opposite();
			default:
			}
			return new Path(x+xx,y+yy,h,steps+followingPath.steps);
		}
		@Override
		public String toString()	{
			return "("+x+","+y+";"+heading+")";
		}
	}
	
	private static class PathHistory	{
		private final List<PathHistory> compositePath;
		public final Path finalPath;
		public PathHistory(Path simplePath)	{
			compositePath=null;
			finalPath=simplePath;
		}
		public PathHistory(List<PathHistory> compositePath)	{
			this.compositePath=compositePath;
			finalPath=calculatePath(compositePath);
		}
		public final static PathHistory F=new PathHistory(Path.F);
		public final static PathHistory L=new PathHistory(Path.L);
		public final static PathHistory R=new PathHistory(Path.R);
		private static Path calculatePath(Collection<PathHistory> paths)	{
			Path result=Path.EMPTY;
			for (PathHistory next:paths) result=result.advance(next.finalPath);
			return result;
		}
		public Path searchForPath(long steps)	{
			if (steps>finalPath.steps) return null;
			Path prev=Path.EMPTY;
			for (PathHistory path:compositePath)	{
				Path next=prev.advance(path.finalPath);
				if (next.steps==steps) return next;
				else if (next.steps>steps)	{
					long diff=steps-prev.steps;
					Path fractionalPath=path.searchForPath(diff);
					return prev.advance(fractionalPath);
				}
				prev=next;
			}
			return null;
		}
		@Override
		public String toString()	{
			return finalPath.toString();
		}
	}
	
	public static PathHistory[] dragonBaseRecursive(int depth)	{
		PathHistory[] result=new PathHistory[2];
		if (depth==0)	{
			result[0]=new PathHistory(Collections.emptyList());
			result[1]=result[0];
		}	else	{
			PathHistory[] prev=dragonBaseRecursive(depth-1);
			result[0]=new PathHistory(Arrays.asList(prev[0],PathHistory.R,prev[1],PathHistory.F,PathHistory.R));
			result[1]=new PathHistory(Arrays.asList(PathHistory.L,PathHistory.F,prev[0],PathHistory.L,prev[1]));
		}
		return result;
	}
	
	public static PathHistory dragon(int depth)	{
		PathHistory[] base=dragonBaseRecursive(depth);
		return new PathHistory(Arrays.asList(PathHistory.F,base[0]));
	}
	
	public static void main(String[] args)	{
		PathHistory bigDragon=dragon(DRAGON_SIZE);
		Path position=bigDragon.searchForPath(POSITION);
		System.out.println(""+position.x+","+position.y);
	}
}
