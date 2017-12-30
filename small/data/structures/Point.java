package small.data.structures;

import processing.core.PApplet;

public class Point implements Comparable<Point> {
	
	Matrix A;
	
	float r;
	float g;
	float b;
	
	public Point(Matrix A, float r, float g, float b) {
		this.A = A.getCopy();
		
		// color
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public float getDepth() {
		return this.A.M[2];
	}
	
	public void display(PApplet p, Matrix toDisplay) {
		// Perspective projection, shift and scale to display
		Matrix screenPos = A.project(2).mult(toDisplay);
		p.stroke(this.r, this.g, this.b);
		p.point(screenPos.M[0], screenPos.M[1]);
	}

	@Override
	public int compareTo(Point p) {
		if (A.M[2] - p.getDepth() > 0) {
			return -1;
		} else {
			return 1;
		}
	}

}
