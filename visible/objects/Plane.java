package visible.objects;

import processing.core.PApplet;
import small.data.structures.Matrix;
import small.data.structures.Quaternion;
import utilities.Logger;

public class Plane {
	PApplet p;
	
	Matrix vertices;
	Logger log;
	
	public Plane(PApplet p) {
		this.p = p;
		log = new Logger(this);
		
		Matrix corners;
		corners = new Matrix(3, 4);
		
		// Top left
		corners.M[0] = -10;	// x
		corners.M[1] = 8; 	// y
		corners.M[2] = 0;	// z
		
		// Top right
		corners.M[3] = 10;	// x
		corners.M[4] = 8;	// y
		corners.M[5] = 0;	// z
		
		// Bottom right
		corners.M[6] = 10;	// x
		corners.M[7] = -8;	// y
		corners.M[8] = 0;	// z
		
		// Bottom left
		corners.M[9] = -10;	// x
		corners.M[10] = -8;	// y
		corners.M[11] = 0;	// z
		
		// apply a scaling
		Matrix scale = new Matrix(3,3);
		for (int k = 0; k < scale.M.length; k++) {
//			scale.M[k] *= (float) (Math.random() * 2);
			scale.M[k] *= 0.2f;
		}
		
		// apply an arbitrary rotation
		Quaternion q_random = new Quaternion(Math.random() * Math.PI, -Math.random(), Math.random(), Math.random());
		
		// apply transformations
		corners.mult(scale).mult(q_random.getR(3));
		
		vertices = new Matrix(4, 4);
		
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 3; i++) {
				vertices.M[j * 4 + i] = corners.M[j * 3 + i];
//				log.info(i + ": " + corners.M[j * 3 + i]);
			}
			// final row, so that plane can be translated
			vertices.M[j * 4 + 3] = 1;
		}
	}
	
	public void display(Matrix objectToWorld, Matrix worldToCamera, Matrix toDisplay) {
		// this makes the case for an immutable Matrix class
		Matrix vcsCopy = this.vertices.getCopy();
		vcsCopy.mult(objectToWorld).mult(worldToCamera);
		vcsCopy.project(2).mult(toDisplay);
		
		p.noFill();
		p.stroke(255);
		
		p.beginShape();
			
			for (int j = 0; j < vcsCopy.m; j++) {
				p.vertex(vcsCopy.M[j * 4], vcsCopy.M[j * 4 + 1]);
			}
		
		p.endShape(p.CLOSE);
		
	}
	
}
