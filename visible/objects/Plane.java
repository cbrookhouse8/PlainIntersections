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
		
		Matrix baseCorners;
		baseCorners = new Matrix(3, 4);
		
		// Top left
		baseCorners.M[0] = -10;	// x
		baseCorners.M[1] = 8; 	// y
		baseCorners.M[2] = 0;	// z
		
		// Top right
		baseCorners.M[3] = 10;	// x
		baseCorners.M[4] = 8;	// y
		baseCorners.M[5] = 0;	// z
		
		// Bottom right
		baseCorners.M[6] = 10;	// x
		baseCorners.M[7] = -8;	// y
		baseCorners.M[8] = 0;	// z
		
		// Bottom left
		baseCorners.M[9] = -10;	// x
		baseCorners.M[10] = -8;	// y
		baseCorners.M[11] = 0;	// z
		
		// apply a scaling
		Matrix scale = new Matrix(3,3);
		for (int k = 0; k < scale.M.length; k++) {
//			scale.M[k] *= (float) (Math.random() * 2);
			scale.M[k] *= 0.2f;
		}
		
		// apply an arbitrary rotation
		Quaternion q_random = new Quaternion(Math.random() * Math.PI, -Math.random(), Math.random(), Math.random());
		
		// apply transformations
		Matrix corners = baseCorners.mult(scale).mult(q_random.getR(3));
		
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

		Matrix vcs = this.vertices.mult(objectToWorld).mult(worldToCamera).project(2).mult(toDisplay);
		
		p.noFill();
		p.stroke(255);
		
		p.beginShape();
			
			for (int j = 0; j < vcs.m; j++) {
				p.vertex(vcs.M[j * 4], vcs.M[j * 4 + 1]);
			}
		
		p.endShape(p.CLOSE);
		
	}
	
}
