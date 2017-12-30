package visible.objects;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import small.data.structures.Matrix;
import small.data.structures.Point;
import small.data.structures.Quaternion;
import utilities.Logger;

// x [-2, 2]
// y [-1.2, 1.2]
public class Texture {
	PApplet p;
	PImage img;
	
	Matrix vertices;
	
	Matrix centre;
	Matrix scale;
	Matrix rotate;
	
	Logger log;
	int nth;
	int opacity;
	
	ArrayList<Point> points;
	
	public Texture(PApplet p, PImage img) {
		this.p = p;
		
		this.img = img;
		
		this.nth = 4; // default
		this.opacity = 255; // default
		
		log = new Logger(this);
		
		// Translation matrix
		centre = new Matrix(4, 4);
		
		centre.M[12] = -img.width / 2;	// x
		centre.M[13] = -img.height / 2; 	// y
		centre.M[14] = 0;
		
		scale = new Matrix(4, 4);
		
		// x [-2, 2]
		// y [-1.6, 1.6]
		
//		scale.M[0] = 4f / img.width;
//		scale.M[5] = 3.2f / img.height;
		
		scale.M[0] = 3f / img.width;
		scale.M[5] = 2.8f / img.height;
		
		log.info("" + scale.M[0]);
		log.info("" + scale.M[5]);

		Quaternion q_random = new Quaternion(Math.random() * Math.PI, -Math.random(), Math.random(), Math.random());
		rotate = q_random.getR(4);
	}
	
	public void setNth(int n) {
		this.nth = n;
	}
	
	public void setOpacity(int n) {
		this.opacity = 100;
	}
 	
	public PImage getImg() {
		return this.img;
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
		
			for (int j = 0; j < img.width; j++) {
				
				if (j % nth == 0) continue;
				
				for (int i = 0; i < img.height; i++) {
					
					if (i % nth == 0) continue;
					
					Matrix imgPoint = new Matrix(new float[] { j, i, 0, 1 }, 4, 1);

					Matrix screenPos = imgPoint.mult(centre)
												.mult(scale)
												.mult(rotate)
												//.mult(objectToWorld)
												.mult(worldToCamera)
												.project(2)
												.mult(toDisplay);
					
//					screenPos.print();
					
					int c = img.get(j, i);
					float r = p.red(c);
					float g = p.green(c);
					float b = p.blue(c);

					p.stroke(r, g, b, this.opacity);
					p.point(screenPos.M[0], screenPos.M[1]);
				}
			}
		
	}
	
}

