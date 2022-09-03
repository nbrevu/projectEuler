package com.euler;
import java.util.TreeSet;

public class Euler165 {
	public static int[] data = new int[20000];
	public static Line[] lines = new Line[5000];
	public static double EPS = 1e-9;
	public static void main(String[] args) {
		genRandom();
		for (int n = 0; n < 4 * 5000; n += 4)
		lines[n / 4] = new Line(data[n], data[n + 1], data[n + 2],
		data[n + 3]);
		TreeSet<Point> ts = new TreeSet<Point>();
		for (int i = 0; i < 5000; i++)
		for (int j = i + 1; j < 5000; j++)
		if (lines[i].isTrueBoth(lines[j]))
		ts.add(lines[i].point(lines[i].intersect(lines[j])));
		System.out.println(ts.size());
	}
	public static void genRandom() {
		long s = 290797;
		for (int i = 0; i < 20000; i++) {
			s = (s * s) % 50515093L;
			data[i] = (int) (s % 500L);
		}
	}
	public static class Line {
		public int x0, y0, x1, y1;
		public Line(int px0, int py0, int px1, int py1) {
			x0 = px0;
			y0 = py0;
			x1 = px1;
			y1 = py1;
		}
		public boolean isTrueBoth(Line line) {
			return this.isTrueIntersection(line) && line.isTrueIntersection(this);
		}
		public boolean isTrueIntersection(Line line) {
			int mx = x1 - x0, my = y1 - y0;
			int kx = line.x1 - line.x0, ky = line.y1 - line.y0;
			int t = (kx * (y0 - line.y0) + ky * (line.x0 - x0));
			int b = (mx * ky - my * kx);
			if ((t == 0 || b == 0) || (b > 0 && t < 0) || (b < 0 && t > 0)) return false;
			if (b < 0) return -t < -b;
			return t < b;
		}
		public double intersect(Line line) {
			double mx = x1 - x0, my = y1 - y0;
			double kx = line.x1 - line.x0, ky = line.y1 - line.y0;
			return (kx * (y0 - line.y0) + ky * (line.x0 - x0)) / (mx * ky - my * kx);
		}
		public Point point(double t) {
			return new Point(x0 + (x1 - x0) * t, y0 + (y1 - y0) * t);
		}
	}
	public static class Point implements Comparable<Object> {
		double x, y;
		public Point(double px, double py) {
			x = px;
			y = py;
		}
		public int compareTo(Object o) {
			Point p = (Point) o;
			if (Math.abs(p.x - x) > EPS) return (int) Math.signum(p.x - x);
			if (Math.abs(p.y - y) > EPS) return (int) Math.signum(p.y - y);
			return 0;
		}
	}
}
