package visible.objects;

import java.util.ArrayList;

import processing.core.PApplet;
import small.data.structures.Matrix;
import small.data.structures.Point;
import small.data.structures.Quaternion;
import utilities.Logger;

// x [-2, 2]
// y [-1.2, 1.2]
public class Hexagon {
	PApplet p;
	
	float radius;
	Matrix vertices;
	
	Matrix centre;
	Matrix scale;
	Matrix rotate;
	
	Logger log;
	int opacity;
	
	ArrayList<Point> points;
	
	public Hexagon(PApplet p) {
		this.p = p;
		
		this.opacity = 255; // default
		
		log = new Logger(this);
		
		// Hexagon
		this.radius = 1;
		
		// Translation matrix
		centre = new Matrix(4, 4);
		
		// translation to the centre
		centre.M[12] = 0;	// x
		centre.M[13] = 0; 	// y
		centre.M[14] = 0;
		
		scale = new Matrix(4, 4);
		
		// x [-2, 2]
		// y [-1.6, 1.6]
		
		scale.M[0] = 0.8f;
		scale.M[5] = 0.8f;
		
		log.info("Hex scale X:" + scale.M[0]);
		log.info("Hex scale Y: " + scale.M[5]);

		Quaternion q_random = new Quaternion(Math.random() * Math.PI, -Math.random(), Math.random(), Math.random());
		rotate = q_random.getR(4);
	
		this.initializeVertices();
	}
	
	private void initializeVertices() {
		 float[] pts = new float[4 * 6];
		 for (int j = 0; j < 6; j++) {
			     float theta = j * 2f * ((float) Math.PI) / 6f;
			     float x = this.radius * (float) Math.cos(theta);
			     float y = this.radius * (float) Math.sin(theta);
			     pts[j * 4 + 0] = x;
			     pts[j * 4 + 1] = y;
			     pts[j * 4 + 2] = 0;
			     pts[j * 4 + 3] = 1;
		 }
		 this.vertices = new Matrix(pts, 4, 6);
	}
	
	public void setOpacity(int n) {
		this.opacity = 100;
	}
	
	public Matrix getCentreTransform() {
		return this.centre;
	}
	
	public Matrix getScaleTransform() {
		return this.scale;
	}
	
	public Matrix getRotationTransform() {
		return this.rotate;
	}
	
	public void display(Matrix objectToWorld, Matrix worldToCamera, Matrix toDisplay) {
		
		p.noFill();
		p.stroke(255);
		Matrix screenPos = vertices
								//.mult(centre)
								.mult(scale)
								.mult(rotate)
								.mult(worldToCamera)
								.project(2)
								.mult(toDisplay);
		
//		p.fill(255);
//		p.rect(100, 100, 200, 200);
		p.stroke(255);
		p.beginShape();
			for (int j = 0; j < screenPos.m; j++) {
				
				float screenX = screenPos.M[j * 4 + 0];
				float screenY = screenPos.M[j * 4 + 1];
				
			     log.info("x:" + screenX);
			     log.info("y:" + screenY);
//					p.rect(screenX, screenX, 10, 10);
				p.vertex(screenX, screenY);
			}
		p.endShape(p.CLOSE);
	}
	
}
