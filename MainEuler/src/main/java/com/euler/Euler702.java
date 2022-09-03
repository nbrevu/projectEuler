package com.euler;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;

import com.euler.common.Rational;

public class Euler702 {
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
	
	public static void main(String[] args)	{
		JFrame window=new JFrame("Triangle viewer for Project Euler 702.");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		TriangleViewer viewer=new TriangleViewer(N,SIZE);
		window.add(viewer);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
}
