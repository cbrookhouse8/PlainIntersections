package visible.objects;

import processing.core.PApplet;
import small.data.structures.Matrix;

public class Axes {

	public Matrix vcs;
	PApplet p;

	public Axes(PApplet p) {
		this.p = p;
		vcs = new Matrix(4, 4);
		for (int i = 3; i < 3 * 4; i += 4) {
			vcs.M[i] = 1;
		}
	}

	public void display(Matrix R, Matrix transforms, Matrix display) {

		Matrix vcsCopy = new Matrix(4, 3);
		Matrix centre = new Matrix(new float[] { 0, 0, 0, 1 }, 4, 1);

		for (int i = 0; i < 12; i++) {
			vcsCopy.M[i] = vcs.M[i];
		}
		
		Matrix limits = vcsCopy.mult(R).mult(transforms).project(2).mult(display);
		
		Matrix origin = centre.mult(transforms).project(2).mult(display);
		
		// p.textAlign(CENTER,CENTER);
		p.stroke(255, 0, 0); // x
		p.strokeWeight(2);
		p.fill(255, 0, 0);
		p.line(origin.M[0], origin.M[1], limits.M[0], limits.M[1]);
		p.text("X", limits.M[0], limits.M[1]);
		p.stroke(0, 0, 255); // z
		p.fill(0, 0, 255);
		p.line(origin.M[0], origin.M[1], limits.M[8], limits.M[9]);
		p.text("Z", limits.M[8], limits.M[9]);
		p.stroke(0, 255, 0); // y
		p.fill(0, 255, 0);
		p.line(origin.M[0], origin.M[1], limits.M[4], limits.M[5]);
		p.text("Y", limits.M[4], limits.M[5]);
		p.strokeWeight(1);
	}

}