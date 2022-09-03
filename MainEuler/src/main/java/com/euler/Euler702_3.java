package com.euler;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;

import com.euler.common.Rational;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Euler702_3 {
	private final static int N=5;
	private final static int SIZE=80;
	private final static Color BACKGROUND_COLOUR=new Color(112,64,0);
	private final static Color LINES_COLOUR=Color.YELLOW;
	
	private final static double S3=Math.sqrt(3d);
	
	private static class RationalSqrt3	{
		public final Rational a;
		public final Rational b;
		public RationalSqrt3(long x)	{
			this(new Rational(x),Rational.ZERO);
		}
		public RationalSqrt3(Rational x)	{
			this(x,Rational.ZERO); 
		}
		public RationalSqrt3(Rational a,Rational b)	{
			this.a=a;
			this.b=b;
		}
		public RationalSqrt3 add(RationalSqrt3 other)	{
			return new RationalSqrt3(a.sum(other.a),b.sum(other.b));
		}
		public RationalSqrt3 subtract(RationalSqrt3 other)	{
			return new RationalSqrt3(a.subtract(other.a),b.subtract(other.b));
		}
		public RationalSqrt3 multiply(long integer)	{
			return new RationalSqrt3(a.multiply(integer),b.multiply(integer));
		}
		public RationalSqrt3 multiply(RationalSqrt3 other)	{
			return new RationalSqrt3(a.multiply(other.a).sum(b.multiply(other.b).multiply(3)),a.multiply(other.b).sum(b.multiply(other.a)));
		}
		public RationalSqrt3 divide(RationalSqrt3 other)	{
			// (a+Sb)/(c+Sd) = (a+Sb)*(c-Sd)/(c^2+3D^2).
			Rational numA=a.multiply(other.a).subtract(b.multiply(other.b).multiply(3));
			Rational numB=b.multiply(other.a).subtract(a.multiply(other.b));
			Rational denom=other.a.multiply(other.a).sum(other.b.multiply(other.b).multiply(3));
			return new RationalSqrt3(numA.divide(denom),numB.divide(denom));
		}
		public double toDouble()	{
			return a.toDouble()+S3*b.toDouble();
		}
		public int closestInt()	{
			return (int)Math.round(toDouble());
		}
	}
	
	private final static Rational HALF=new Rational(1,2);
	private final static Rational MINUS_HALF=HALF.negate();
	private final static RationalSqrt3 COS0=new RationalSqrt3(1);
	private final static RationalSqrt3 SIN0=new RationalSqrt3(0);
	private final static RationalSqrt3 COS60=new RationalSqrt3(HALF);
	private final static RationalSqrt3 SIN60=new RationalSqrt3(Rational.ZERO,HALF);
	private final static RationalSqrt3 COS120=new RationalSqrt3(MINUS_HALF);
	private final static RationalSqrt3 SIN120=SIN60;
	private final static RationalSqrt3 COS180=new RationalSqrt3(-1);
	private final static RationalSqrt3 SIN180=SIN0;
	private final static RationalSqrt3 COS240=COS120;
	private final static RationalSqrt3 SIN240=new RationalSqrt3(Rational.ZERO,MINUS_HALF);
	private final static RationalSqrt3 COS300=COS60;
	private final static RationalSqrt3 SIN300=SIN240;
	/*-
	private final static RationalSqrt3 THREE=new RationalSqrt3(new Rational(3),Rational.ZERO);
	private final static RationalSqrt3 SQRT_THREE=new RationalSqrt3(Rational.ZERO,new Rational(1));
	*/
	private final static RationalSqrt3 INVERSE_SQRT_THREE=new RationalSqrt3(Rational.ZERO,new Rational(1,3));
	
	private static enum CoordinateCalculator	{
		T	{
			@Override
			public double getCoordinate(RationalSqrt3 x, RationalSqrt3 y) {
				return y.divide(SIN60).toDouble()/SIZE;
			}
		},	U	{
			@Override
			public double getCoordinate(RationalSqrt3 x, RationalSqrt3 y) {
				// return SIN60.multiply(x).add(COS60.multiply(y)).toDouble()/(S3*SIZE/2);	<- ES FUNKTIONIERT.
				return x.add(INVERSE_SQRT_THREE.multiply(y)).toDouble()/SIZE;
			}
		},	V	{
			@Override
			public double getCoordinate(RationalSqrt3 x, RationalSqrt3 y) {
				return x.subtract(INVERSE_SQRT_THREE.multiply(y)).toDouble()/SIZE;
			}
		};
		public abstract double getCoordinate(RationalSqrt3 x,RationalSqrt3 y);
		public int getIntCoordinate(RationalSqrt3 x,RationalSqrt3 y)	{
			// This is wrong, some offset is probably needed.
			return (int)Math.floor(getCoordinate(x,y));
		}
	}
	
	private static class TriangleViewer extends Canvas	{
		private static final long serialVersionUID = 1349018092744600973L;
		private final int n;
		private final int size;
		private final int width;
		private final int height;
		private final int centerX;
		private final int centerY;
		public TriangleViewer(int n,int size)	{
			this.n=n;
			this.size=size;
			width=(N*2+1)*SIZE;
			height=(int)Math.ceil(width*S3/2);
			centerX=width/2;
			centerY=height/2;
			setPreferredSize(new Dimension(width,height));
			setBackground(BACKGROUND_COLOUR);
		}
		private void drawRelativeFromCenter(Graphics2D g2d,RationalSqrt3 x0,RationalSqrt3 y0,RationalSqrt3 xf,RationalSqrt3 yf)	{
			g2d.drawLine(centerX+x0.closestInt(),centerY+y0.closestInt(),centerX+xf.closestInt(),centerY+yf.closestInt());
		}
		/*-
		private void drawCircle(Graphics2D g2d,RationalSqrt3 x,RationalSqrt3 y)	{
			int intX=centerX+x.closestInt()-8;
			int intY=centerY+y.closestInt()-8;
			g2d.fillArc(intX,intY,16,16,0,360);
		}
		*/
		private void writeData(Graphics2D g2d,RationalSqrt3 baseX,RationalSqrt3 baseY)	{
			int x=baseX.closestInt()+centerX-4;
			int y=baseY.closestInt()+centerY-10;
			int t=CoordinateCalculator.T.getIntCoordinate(baseX,baseY);
			int u=CoordinateCalculator.U.getIntCoordinate(baseX,baseY);
			int v=CoordinateCalculator.V.getIntCoordinate(baseX,baseY);
			g2d.setColor(Color.orange);
			g2d.drawString(Integer.toString(t),x,y);
			g2d.setColor(Color.green);
			g2d.drawString(Integer.toString(u),x,y+15);
			g2d.setColor(Color.blue);
			g2d.drawString(Integer.toString(v),x,y+30);
		}
		@Override
		public void paint(Graphics g)	{
			Graphics2D g2d=(Graphics2D)g;
			g2d.setPaintMode();
			g2d.setColor(LINES_COLOUR);
			int scale=n*size;
			RationalSqrt3 d0x=COS0.multiply(scale);
			RationalSqrt3 d0y=SIN0.multiply(scale);
			RationalSqrt3 d1x=COS60.multiply(scale);
			RationalSqrt3 d1y=SIN60.multiply(scale);
			RationalSqrt3 d2x=COS120.multiply(scale);
			RationalSqrt3 d2y=SIN120.multiply(scale);
			RationalSqrt3 d3x=COS180.multiply(scale);
			RationalSqrt3 d3y=SIN180.multiply(scale);
			RationalSqrt3 d4x=COS240.multiply(scale);
			RationalSqrt3 d4y=SIN240.multiply(scale);
			RationalSqrt3 d5x=COS300.multiply(scale);
			RationalSqrt3 d5y=SIN300.multiply(scale);
			// First, horizontal lines, starting from the TOP!
			RationalSqrt3 halfXShift=new RationalSqrt3(HALF.multiply(size));
			RationalSqrt3 xShift=new RationalSqrt3(size);
			RationalSqrt3 yShift=SIN60.multiply(size);
			{
				RationalSqrt3 x0=d4x;
				RationalSqrt3 y0=d4y;
				RationalSqrt3 xf=d5x;
				RationalSqrt3 yf=d5y;
				drawRelativeFromCenter(g2d,x0,y0,xf,yf);
				for (int i=0;i<n;++i)	{
					x0=x0.subtract(halfXShift);
					xf=xf.add(halfXShift);
					y0=y0.add(yShift);
					yf=yf.add(yShift);
					drawRelativeFromCenter(g2d,x0,y0,xf,yf);
				}
				for (int i=0;i<n;++i)	{
					x0=x0.add(halfXShift);
					xf=xf.subtract(halfXShift);
					y0=y0.add(yShift);
					yf=yf.add(yShift);
					drawRelativeFromCenter(g2d,x0,y0,xf,yf);
				}
			}
			// Slanted lines like "/".
			{
				RationalSqrt3 x0=d4x;
				RationalSqrt3 y0=d4y;
				RationalSqrt3 xf=d3x;
				RationalSqrt3 yf=d3y;
				drawRelativeFromCenter(g2d,x0,y0,xf,yf);
				for (int i=0;i<n;++i)	{
					x0=x0.add(xShift);
					xf=xf.add(halfXShift);
					yf=yf.add(yShift);
					drawRelativeFromCenter(g2d,x0,y0,xf,yf);
				}
				for (int i=0;i<n;++i)	{
					x0=x0.add(halfXShift);
					xf=xf.add(xShift);
					y0=y0.add(yShift);
					drawRelativeFromCenter(g2d,x0,y0,xf,yf);
				}
			}
			// Slanted lines like "\".
			{
				RationalSqrt3 x0=d3x;
				RationalSqrt3 y0=d3y;
				RationalSqrt3 xf=d2x;
				RationalSqrt3 yf=d2y;
				drawRelativeFromCenter(g2d,x0,y0,xf,yf);
				for (int i=0;i<n;++i)	{
					x0=x0.add(halfXShift);
					xf=xf.add(xShift);
					y0=y0.subtract(yShift);
					drawRelativeFromCenter(g2d,x0,y0,xf,yf);
				}
				for (int i=0;i<n;++i)	{
					x0=x0.add(xShift);
					xf=xf.add(halfXShift);
					yf=yf.subtract(yShift);
					drawRelativeFromCenter(g2d,x0,y0,xf,yf);
				}
			}
			g2d.setColor(Color.red);
			// More emphasis for the main lines...
			{
				g2d.setColor(Color.black);
				drawRelativeFromCenter(g2d,d0x,d0y,d1x,d1y);
				drawRelativeFromCenter(g2d,d1x,d1y,d2x,d2y);
				drawRelativeFromCenter(g2d,d2x,d2y,d3x,d3y);
				drawRelativeFromCenter(g2d,d3x,d3y,d4x,d4y);
				drawRelativeFromCenter(g2d,d4x,d4y,d5x,d5y);
				drawRelativeFromCenter(g2d,d5x,d5y,d0x,d0y);
				RationalSqrt3 zero=new RationalSqrt3(Rational.ZERO,Rational.ZERO);
				drawRelativeFromCenter(g2d,zero,zero,d0x,d0y);
				drawRelativeFromCenter(g2d,zero,zero,d1x,d1y);
				drawRelativeFromCenter(g2d,zero,zero,d2x,d2y);
				drawRelativeFromCenter(g2d,zero,zero,d3x,d3y);
				drawRelativeFromCenter(g2d,zero,zero,d4x,d4y);
				drawRelativeFromCenter(g2d,zero,zero,d5x,d5y);
			}
			Font f=new Font(Font.MONOSPACED,Font.BOLD,15);
			g2d.setFont(f);
			g2d.setColor(Color.orange);
			g2d.drawString("These indicate \"t\".",0,15);
			g2d.setColor(Color.green);
			g2d.drawString("These indicate \"u\".",0,30);
			g2d.setColor(Color.blue);
			g2d.drawString("These indicate \"v\".",0,45);
			// And now we draw the text.
			{
				RationalSqrt3 rowX1=d4x;
				RationalSqrt3 rowY1=d4y.add(new RationalSqrt3(Rational.ZERO,new Rational(size,3)));
				RationalSqrt3 rowX2=d4x.add(halfXShift);
				RationalSqrt3 rowY2=d4y.add(new RationalSqrt3(Rational.ZERO,new Rational(size,6)));
				RationalSqrt3 x1=rowX1;
				RationalSqrt3 y1=rowY1;
				RationalSqrt3 x2=rowX2;
				RationalSqrt3 y2=rowY2;
				// First row...
				for (int j=0;j<=n;++j)	{
					writeData(g2d,x1,y1);
					x1=x1.add(xShift);
				}
				for (int j=0;j<n;++j)	{
					writeData(g2d,x2,y2);
					x2=x2.add(xShift);
				}
				for (int i=1;i<n;++i)	{
					rowX1=rowX1.subtract(halfXShift);
					rowY1=rowY1.add(yShift);
					rowX2=rowX2.subtract(halfXShift);
					rowY2=rowY2.add(yShift);
					x1=rowX1;
					y1=rowY1;
					x2=rowX2;
					y2=rowY2;
					for (int j=0;j<=n+i;++j)	{
						writeData(g2d,x1,y1);
						x1=x1.add(xShift);
					}
					for (int j=0;j<n+i;++j)	{
						writeData(g2d,x2,y2);
						x2=x2.add(xShift);
					}
				}
				rowX2=rowX2.subtract(xShift);
				for (int i=0;i<n;++i)	{
					rowX1=rowX1.add(halfXShift);
					rowY1=rowY1.add(yShift);
					rowX2=rowX2.add(halfXShift);
					rowY2=rowY2.add(yShift);
					x1=rowX1;
					y1=rowY1;
					x2=rowX2;
					y2=rowY2;
					for (int j=0;j<2*n-i-1;++j)	{
						writeData(g2d,x1,y1);
						x1=x1.add(xShift);
					}
					for (int j=0;j<=2*n-i-1;++j)	{
						writeData(g2d,x2,y2);
						x2=x2.add(xShift);
					}
				}
			}
			// Any additional shitty code for experiments goes here!
		}
	}
	
	private static class AbstractTriangleIdentifier	{
		private final int row;
		private final boolean isDouble;
		private final int indexInRow;
		public AbstractTriangleIdentifier(int row,boolean isDouble,int indexInRow)	{
			this.row=row;
			this.isDouble=isDouble;
			this.indexInRow=indexInRow;
		}
		@Override
		public int hashCode()	{
			return (row<<10)+(isDouble?32:0)+indexInRow;
		}
		@Override
		public boolean equals(Object other)	{
			AbstractTriangleIdentifier atiOther=(AbstractTriangleIdentifier)other;
			return (row==atiOther.row)&&(isDouble==atiOther.isDouble)&&(indexInRow==atiOther.indexInRow);
		}
		public int getMultiplicity()	{
			int result=isDouble?2:1;
			if (indexInRow*2==row) result*=2;
			return result;
		}
	}
	
	private static class CoordinateTriangleIdentifier	{
		private final int t;
		private final int u;
		private final int v;
		public CoordinateTriangleIdentifier(int t,int u,int v)	{
			this.t=t;
			this.u=u;
			this.v=v;
		}
		@Override
		public int hashCode()	{
			return (t<<20)+(u<<10)+v;
		}
		@Override
		public boolean equals(Object other)	{
			CoordinateTriangleIdentifier ctiOther=(CoordinateTriangleIdentifier)other;
			return (t==ctiOther.t)&&(u==ctiOther.u)&&(v==ctiOther.v);
		}
	}
	
	private static Map<CoordinateTriangleIdentifier,AbstractTriangleIdentifier> getTrianglesMap(int n)	{
		Map<CoordinateTriangleIdentifier,AbstractTriangleIdentifier> result=new HashMap<>();
		// First portion, double rows.
		for (int v=0;v<n;++v)	{
			int row=v;
			for (int t=0;t<=v;++t)	{
				int u=v-t;
				int indexInRow=Math.min(t,u);
				result.put(new CoordinateTriangleIdentifier(t,u,v),new AbstractTriangleIdentifier(row,true,indexInRow));
			}
		}
		// First portion, single rows.
		for (int v=1;v<n;++v)	{
			int row=v-1;
			for (int t=0;t<v;++t)	{
				int u=v-1-t;
				int indexInRow=Math.min(t,u);
				result.put(new CoordinateTriangleIdentifier(t,u,v),new AbstractTriangleIdentifier(row,false,indexInRow));
			}
		}
		// Second portion, double rows.
		for (int t=0;t<n;++t)	{
			int row=t;
			for (int v=0;v<=t;++v)	{
				int u=v-(t+1);
				int indexInRow=Math.min(-1-u,v);
				result.put(new CoordinateTriangleIdentifier(t,u,v),new AbstractTriangleIdentifier(row,true,indexInRow));
			}
		}
		// Second portion, single rows.
		for (int t=1;t<n;++t)	{
			int row=t-1;
			for (int v=0;v<t;++v)	{
				int u=v-t;
				int indexInRow=Math.min(-1-u,v);
				result.put(new CoordinateTriangleIdentifier(t,u,v),new AbstractTriangleIdentifier(row,false,indexInRow));
			}
		}
		// Third part, double rows.
		for (int u=-1;u>=-n;--u)	{
			int row=-1-u;
			for (int t=0;t<=-1-u;++t)	{
				int v=u+t;
				int indexInRow=Math.min(t,-1-v);
				result.put(new CoordinateTriangleIdentifier(t,u,v),new AbstractTriangleIdentifier(row,true,indexInRow));
			}
		}
		// Third part, single rows.
		for (int u=-2;u>=-n;--u)	{
			int row=-2-u;
			for (int t=0;t<=-2-u;++t)	{
				int v=u+1+t;
				int indexInRow=Math.min(t,-1-v);
				result.put(new CoordinateTriangleIdentifier(t,u,v),new AbstractTriangleIdentifier(row,false,indexInRow));
			}
		}
		// Fourth part, double rows.
		for (int v=-1;v>=-n;--v)	{
			int row=-1-v;
			for (int t=v;t<=-1;++t)	{
				int u=v-1-t;
				int indexInRow=Math.min(t-v,u-v);
				result.put(new CoordinateTriangleIdentifier(t,u,v),new AbstractTriangleIdentifier(row,true,indexInRow));
			}
		}
		// Fourth part, single rows.
		for (int v=-2;v>=-n;--v)	{
			int row=-2-v;
			for (int t=v+1;t<=-1;++t)	{
				int u=v-t;
				int indexInRow=Math.min(t-(v+1),u-(v+1));
				result.put(new CoordinateTriangleIdentifier(t,u,v),new AbstractTriangleIdentifier(row,false,indexInRow));
			}
		}
		// Fifth part, double rows.
		for (int t=-1;t>=-n;--t)	{
			int row=-1-t;
			for (int u=0;u<=-1-t;++u)	{
				int v=t+u;
				int indexInRow=Math.min(u,-1-v);
				result.put(new CoordinateTriangleIdentifier(t,u,v),new AbstractTriangleIdentifier(row,true,indexInRow));
			}
		}
		// Fifth part, single rows.
		for (int t=-2;t>=-n;--t)	{
			int row=-2-t;
			for (int u=0;u<=-2-t;++u)	{
				int v=t+1+u;
				int indexInRow=Math.min(u,-1-v);
				result.put(new CoordinateTriangleIdentifier(t,u,v),new AbstractTriangleIdentifier(row,false,indexInRow));
			}
		}
		// Sixth part, double rows.
		for (int u=0;u<n;++u)	{
			int row=u;
			for (int v=0;v<=u;++v)	{
				int t=v-(u+1);
				int indexInRow=Math.min(v,-1-t);
				result.put(new CoordinateTriangleIdentifier(t,u,v),new AbstractTriangleIdentifier(row,true,indexInRow));
			}
		}
		// Sixth part, single rows.
		for (int u=1;u<n;++u)	{
			int row=u-1;
			for (int v=0;v<u;++v)	{
				int t=v-u;
				int indexInRow=Math.min(v,-1-t);
				result.put(new CoordinateTriangleIdentifier(t,u,v),new AbstractTriangleIdentifier(row,false,indexInRow));
			}
		}
		return result;
	}
	
	// This lacks a denominator, because it's shared. Hackish, but saves a lot of space.
	private static class CoordinateTuple	{
		public final long t;
		public final long u;
		public final long v;
		public CoordinateTuple(long t,long u,long v)	{
			this.t=t;
			this.u=u;
			this.v=v;
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(t+u+v);
		}
		@Override
		public boolean equals(Object other)	{
			CoordinateTuple ctOther=(CoordinateTuple)other;
			return (t==ctOther.t)&&(u==ctOther.u)&&(v==ctOther.v);
		}
		public CoordinateTuple middlePoint(CoordinateTuple other)	{
			return new CoordinateTuple(t+other.t,u+other.u,v+other.v);
		}
	}
	
	private static class CoordinateRepository	{
		private final Map<CoordinateTriangleIdentifier,AbstractTriangleIdentifier> triangleMap;
		private final int n;
		public CoordinateRepository(int n)	{
			triangleMap=getTrianglesMap(n);
			this.n=n;
		}
		private static boolean isMultiple(long a,long b)	{
			return (a%b)==0;
		}
		private static boolean isEdgeOrCorner(CoordinateTuple pos,long denominator)	{
			return isMultiple(pos.t,denominator)||isMultiple(pos.u,denominator)||isMultiple(pos.v,denominator);
		}
		public ObjIntMap<AbstractTriangleIdentifier> getBestPaths()	{
			Set<AbstractTriangleIdentifier> missingTriangles=new HashSet<>(triangleMap.values());
			ObjIntMap<AbstractTriangleIdentifier> result=HashObjIntMaps.newMutableMap();
			CoordinateTuple[] corners=new CoordinateTuple[6];
			corners[0]=new CoordinateTuple(0,n,n);
			corners[1]=new CoordinateTuple(n,0,n);
			corners[2]=new CoordinateTuple(n,-n,0);
			corners[3]=new CoordinateTuple(0,-n,-n);
			corners[4]=new CoordinateTuple(-n,0,-n);
			corners[5]=new CoordinateTuple(-n,n,0);
			CoordinateTuple centre=new CoordinateTuple(0,0,0);
			CoordinateTuple gen1=centre.middlePoint(corners[0]);
			for (int i=0;i<6;++i) corners[i]=corners[i].middlePoint(corners[i]);
			Set<CoordinateTuple> currentGen=Stream.of(corners[0],corners[1],corners[2],corners[3]).map(gen1::middlePoint).collect(Collectors.toSet());
			for (int i=0;i<6;++i) corners[i]=corners[i].middlePoint(corners[i]);
			long denominator=4;
			int index=2;
			while (!missingTriangles.isEmpty())	{
				Set<CoordinateTuple> nextGen=new HashSet<>(6*currentGen.size());
				for (CoordinateTuple tuple:currentGen)	{
					if (!isEdgeOrCorner(tuple,denominator))	{
						int t=(int)LongMath.divide(tuple.t,denominator,RoundingMode.FLOOR);
						int u=(int)LongMath.divide(tuple.u,denominator,RoundingMode.FLOOR);
						int v=(int)LongMath.divide(tuple.v,denominator,RoundingMode.FLOOR);
						AbstractTriangleIdentifier triangle=triangleMap.get(new CoordinateTriangleIdentifier(t,u,v));
						if (missingTriangles.contains(triangle))	{
							missingTriangles.remove(triangle);
							result.put(triangle,index);
						}
					}
					for (CoordinateTuple corner:corners) nextGen.add(tuple.middlePoint(corner));
				}
				denominator*=2;
				++index;
				for (int i=0;i<6;++i) corners[i]=corners[i].middlePoint(corners[i]);
				currentGen=nextGen;
				System.out.println("Gen "+index+": "+currentGen.size()+" elements.");
			}
			return result;
		}
		public static int getSum(ObjIntMap<AbstractTriangleIdentifier> indices)	{
			int result=0;
			for (ObjIntCursor<AbstractTriangleIdentifier> cursor=indices.cursor();cursor.moveNext();) result+=cursor.value()*cursor.key().getMultiplicity();
			return result;
		}
		public static int getSimpleResult(int n)	{
			return getSum(new CoordinateRepository(n).getBestPaths());
		}
	}
	
	// Obvious realisation: I can work directly in (t,u,v) coordinates! I'm still retaining the "real" ones for the grid display.
	public static void main(String[] args)	{
		JFrame window=new JFrame("Triangle viewer for Project Euler 702.");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		TriangleViewer viewer=new TriangleViewer(N,SIZE);
		window.add(viewer);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		// The result is correct for N=3, but not for N=5 or N=123 :(.
		System.out.println(CoordinateRepository.getSimpleResult(5));
	}
}
