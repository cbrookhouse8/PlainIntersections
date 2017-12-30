package visible.objects;

import processing.core.PApplet;
import small.data.structures.Matrix;

public class Grid {
	Matrix Line;
	Matrix l2;
	PApplet p;

	public Grid(PApplet p) {
		this.p = p;
	}

	public void display(Matrix transforms, Matrix display, int intensity) {
		float span = 2;
		float nlines = 10;
		float tick;
		Line = new Matrix( 4, 10);
		
		Matrix vertices = new Matrix(4, 4);
		
		// Note translation -1 along y axis
		vertices.M = new float[] { 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0, 1 };

		p.stroke(255, PApplet.map(intensity, 0, 60, 0, 120));

		for (int i = 0; i <= nlines; i++) {

			// scaled by a factor of 2
			tick = -span + i * (2 * span / nlines);

			vertices.M[0] = -span;
			vertices.M[2] = tick;

			vertices.M[4] = span;
			vertices.M[6] = tick;

			vertices.M[8] = tick;
			vertices.M[10] = -span;

			vertices.M[12] = tick;
			vertices.M[14] = span;

			Matrix Coords = vertices.mult(transforms).project(2).mult(display);

			p.line(Coords.M[0], Coords.M[1], Coords.M[4], Coords.M[5]);
			p.line(Coords.M[8], Coords.M[9], Coords.M[12], Coords.M[13]);
		}
	}

} // end of class
