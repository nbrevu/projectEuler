package com.euler;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Euler252 {
	private final static int POINTS=500;
	
	private static class Point	{
		public final long x;
		public final long y;
		public Point(long x,long y)	{
			this.x=x;
			this.y=y;
		}
		@Override
		public String toString()	{
			return "("+x+","+y+")";
		}
	}
	
	private static class CoordGenerator implements Iterable<Long>,Iterator<Long>	{
		private long s;
		public CoordGenerator()	{
			s=290797l;
		}
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public Long next() {
			s*=s;
			s%=50515093l;
			return (s%2000)-1000;
		}
		@Override
		public Iterator<Long> iterator() {
			return this;
		}
	}
	
	private static class Triangle	{
		public final int a,b,c;	// These are indices;
		public final long doubleArea; 
		public Triangle(int a,int b,int c,Point[] points)	{
			this.a=a;
			this.b=b;
			this.c=c;
			doubleArea=calculateDoubleArea(points[a],points[b],points[c]);
		}
		private static boolean isPointInsideTriangle(Point a,Point b,Point c,Point s)	{
			long asX=s.x-a.x;
			long asY=s.y-a.y;
			boolean sAB=((b.x-a.x)*asY-(b.y-a.y)*asX)>0;
			if ((((c.x-a.x)*asY-(c.y-a.y)*asX)>0)==sAB) return false;
			else return (((c.x-b.x)*(s.y-b.y)-(c.y-b.y)*(s.x-b.x))>0)==sAB;
		}
		// Heartfelt thanks to John Bananas. https://stackoverflow.com/a/9755252
		/*
		bool intpoint_inside_trigon(intPoint s, intPoint a, intPoint b, intPoint c)
		{
		    int as_x = s.x-a.x;
		    int as_y = s.y-a.y;
		
		    bool s_ab = (b.x-a.x)*as_y-(b.y-a.y)*as_x > 0;
		
		    if((c.x-a.x)*as_y-(c.y-a.y)*as_x > 0 == s_ab) return false;
		
		    if((c.x-b.x)*(s.y-b.y)-(c.y-b.y)*(s.x-b.x) > 0 != s_ab) return false;
		
		    return true;
		}
		*/
		public boolean isAtomic(Point[] points)	{
			Point pa=points[a];
			Point pb=points[b];
			Point pc=points[c];
			for (int i=0;i<points.length;++i)	{
				if ((i==a)||(i==b)||(i==c)) continue;
				if (isPointInsideTriangle(pa,pb,pc,points[i])) return false;
			}
			return true;
		}
		public long getDoubleArea()	{
			return doubleArea;
		}
		private static long calculateDoubleArea(Point a,Point b,Point c)	{
			/*
			 * Matrix method.
			 * | 1 a.x a.y |
			 * | 1 b.x b.y | = (b.x*c.y)-(b.y*c.x)+(a.y*c.x)-(a.x*c.y)+(a.x*b.y)-(a.y*b.x).
			 * | 1 c.x c.y |
			 */
			return Math.abs(-b.y*c.x+a.y*(-b.x+c.x)+a.x*(b.y-c.y)+b.x*c.y);
		}
		public int getOtherVertex(int v1,int v2)	{
			// v1 and v2 are in {a,b,c}. This method returns the OTHER vertex.
			if (a==v1) return (b==v2)?c:b;
			else if (a==v2) return (b==v1)?c:b;
			else return a;
		}
	}
	
	private static class Polygon	{
		private final int[] indices;
		private final long doubleArea;
		public Polygon(int[] indices,long doubleArea)	{
			// We reorder so that the array contains a canonical representation of the polygon.
			this.indices=reorder(indices);
			this.doubleArea=doubleArea;
		}
		public Polygon(Triangle triangle)	{
			this(new int[]{triangle.a,triangle.b,triangle.c},triangle.doubleArea);
		}
		private static int[] reorder(int[] unordered)	{
			int N=unordered.length;
			int posMin=findMin(unordered);
			int minNext=(posMin+1)%N;
			int minPrev=(posMin+N-1)%N;
			boolean isReverse=(minPrev<=minNext);
			if ((posMin==0)&&!isReverse) return unordered;
			int[] result=new int[N];
			result[0]=unordered[posMin];
			if (isReverse) for (int i=1;i<N;++i) result[i]=unordered[(posMin-i+N)%N];
			else for (int i=1;i<N;++i) result[i]=unordered[(posMin+i)%N];
			return result;
		}
		private static int findMin(int[] array)	{
			int result=0;
			for (int i=1;i<array.length;++i) if (array[i]<array[result]) result=i;
			return result;
		}
		public boolean containsVertex(int vertex)	{
			for (int i=0;i<indices.length;++i) if (indices[i]==vertex) return true;
			return false;
		}
		public long getDoubleArea()	{
			return doubleArea;
		}
		@Override
		public boolean equals(Object other)	{
			Polygon pOther=(Polygon)other;
			return Arrays.equals(indices,pOther.indices);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(indices);
		}
		public boolean isConvex(Point[] points)	{
			// Thanks, https://stackoverflow.com/questions/471962/how-do-i-efficiently-determine-if-a-polygon-is-convex-non-convex-or-complex.
			long crossProduct=getCrossProduct(points[indices[0]],points[indices[1]],points[indices[2]]);
			int N=indices.length;
			for (int i=1;i<N;++i)	{
				long otherCrossProduct=getCrossProduct(points[indices[i]],points[indices[(i+1)%N]],points[indices[(i+2)%N]]);
				if (Math.signum(crossProduct)!=Math.signum(otherCrossProduct)) return false;
			}
			return true;
		}
		private long getCrossProduct(Point p1,Point p2,Point p3)	{
			long dx1=p2.x-p1.x;
			long dy1=p2.y-p1.y;
			long dx2=p3.x-p2.x;
			long dy2=p3.y-p2.y;
			return dx1*dy2-dy1*dx2;
		}
		public Polygon getChild(int additionalVertex,int newVertexPosition,long additionalDoubleArea)	{
			int[] newIndices=new int[1+indices.length];
			System.arraycopy(indices,0,newIndices,0,newVertexPosition);
			newIndices[newVertexPosition]=additionalVertex;
			System.arraycopy(indices,newVertexPosition,newIndices,1+newVertexPosition,indices.length-newVertexPosition);
			return new Polygon(newIndices,doubleArea+additionalDoubleArea);
		}
		public int[] getIndices()	{
			return indices;
		}
	}
	
	private static Point[] generatePoints(int howMany)	{
		Point[] result=new Point[howMany];
		CoordGenerator generator=new CoordGenerator();
		for (int i=0;i<howMany;++i)	{
			long x=generator.next();
			long y=generator.next();
			result[i]=new Point(x,y);
		}
		return result;
	}
	
	private static class TriangleTable	{
		private final Table<Integer,Integer,Set<Triangle>> trianglesTable;
		public TriangleTable()	{
			trianglesTable=HashBasedTable.create();
		}
		public void addTriangle(Triangle tri)	{
			// We store the triangle using two indices, but always in ascending order.
			if (tri.a<tri.b) addTriangle(tri.a,tri.b,tri);
			else addTriangle(tri.b,tri.a,tri);
			if (tri.a<tri.c) addTriangle(tri.a,tri.c,tri);
			else addTriangle(tri.c,tri.a,tri);
			if (tri.b<tri.c) addTriangle(tri.b,tri.c,tri);
			else addTriangle(tri.c,tri.b,tri);
		}
		private void addTriangle(int pos1,int pos2,Triangle tri)	{
			Set<Triangle> currentSet=trianglesTable.get(pos1,pos2);
			if (currentSet==null)	{
				currentSet=new HashSet<>();
				trianglesTable.put(pos1,pos2,currentSet);
			}
			currentSet.add(tri);
		}
		public Set<Triangle> queryTwoVertices(int a,int b)	{
			return (a<b)?queryOrdered(a,b):queryOrdered(b,a);
		}
		private Set<Triangle> queryOrdered(int a,int b)	{
			Set<Triangle> result=trianglesTable.get(a,b);
			return (result==null)?Collections.emptySet():result;
		}
	}
	
	// Culling makes it way faster but there is a tiny possibility that it doesn't find the result. Without culling it's still reasonably fast,
	// so I'm removing the culling.
	public static void main(String[] args)	{
		Point[] points=generatePoints(POINTS);
		int limitA=POINTS-2;
		int limitB=POINTS-1;
		TriangleTable atomicTriangles=new TriangleTable();
		Collection<Polygon> currentPolygonSet=new HashSet<>();
		long maxDoubleArea=0;
		for (int a=0;a<limitA;++a) for (int b=a+1;b<limitB;++b) for (int c=b+1;c<POINTS;++c)	{
			Triangle tri=new Triangle(a,b,c,points);
			long doubleArea=tri.getDoubleArea();
			if (doubleArea==0) continue;
			if (tri.isAtomic(points))	{
				maxDoubleArea=Math.max(doubleArea,maxDoubleArea);
				atomicTriangles.addTriangle(tri);
				currentPolygonSet.add(new Polygon(tri));
			}
		}
		while (!currentPolygonSet.isEmpty())	{
			Set<Polygon> newPolygonSet=new HashSet<>();
			System.out.println(currentPolygonSet.size()+"...");
			for (Polygon poly:currentPolygonSet)	{
				int[] vertices=poly.getIndices();
				int N=vertices.length;
				for (int i=0;i<N;++i)	{
					int v1=vertices[i];
					int v2=vertices[(i+1)%N];
					Set<Triangle> candidates=atomicTriangles.queryTwoVertices(v1,v2);
					for (Triangle tri:candidates)	{
						int v3=tri.getOtherVertex(v1,v2);
						if (poly.containsVertex(v3)) continue;
						Polygon newPoly=poly.getChild(v3,i+1,tri.getDoubleArea());
						if (newPoly.isConvex(points))	{
							newPolygonSet.add(newPoly);
							maxDoubleArea=Math.max(newPoly.getDoubleArea(),maxDoubleArea);
						}
					}
				}
			}
			currentPolygonSet=newPolygonSet;	// And around we go.
		}
		long result=maxDoubleArea/2;
		String suffix=((result%2l)==1l)?".5":".0";
		System.out.println(""+result+suffix);
	}
}
