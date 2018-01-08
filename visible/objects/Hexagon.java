package visible.objects;

import java.util.ArrayList;

import processing.core.PApplet;
import small.data.structures.Matrix;
import small.data.structures.Point;
import small.data.structures.Quaternion;
import utilities.Logger;

// x [-2, 2]
// y [-1.2, 1.2]
public class Hexagon implements Tileable {
	PApplet p;
	
	float radius;
	Matrix vertices;
	
	Matrix objectSpaceTransform;
	
	Logger log;
	
	float r;
	float g;
	float b;
	int opacity;
	
	ArrayList<Point> points;
	
	public Hexagon(PApplet p) {
		this.p = p;
		
		this.r = (float) Math.random() * 155;
		this.g = (float) Math.random() * 155;
		this.b = (float) Math.random() * 155;
		this.opacity = 255; // default
		
		
		
		log = new Logger(this);
		
		// Hexagon
		this.radius = 1;
		
		this.initializeVertices();
		this.createArbitraryObjectSpaceTransformation();
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
	
	private void createArbitraryObjectSpaceTransformation() {
		// define a set of transformations to apply
		// to the vertices

		Matrix scale;
		Matrix rotate;
		Matrix translate;
		
		// Scaling
		scale = new Matrix(4, 4);

		// x [-2, 2]
		// y [-1.6, 1.6]

		scale.M[0] = 0.3f;
		scale.M[5] = 0.3f;

		log.info("Hex scale X:" + scale.M[0]);
		log.info("Hex scale Y: " + scale.M[5]);

		Quaternion q_random = new Quaternion(Math.random() * Math.PI, -Math.random(), Math.random(), Math.random());
		
		// Rotation
		rotate = q_random.getR(4);
		
		// Translation (in object space)
		translate = new Matrix(4, 4);
		translate.M[12] = 0.6f;
		translate.M[13] = 0.7f;
		
		objectSpaceTransform = scale.mult(rotate).mult(translate);
	}
	
	public void setOpacity(int n) {
		this.opacity = 100;
	}
	
	@Override
	public void setObjectSpaceTransform(Matrix tfm) {
		// seems to be the safest way to do this
		this.objectSpaceTransform = new Matrix(tfm.M, 4, 4);
	}
	
	@Override
	public Matrix getObjectSpaceTransform() {
		return this.objectSpaceTransform;
	}
	
	@Override
	public Matrix getObjectSpaceVertices() {
		return this.vertices.mult(objectSpaceTransform);
	}
	
	@Override
	public Matrix getObjectSpaceCentre() {
		// this is the convention used for hexagons in this program
		Matrix centre = new Matrix(new float[] { 0, 0, 0, 1}, 4, 1);
		
		return centre.mult(objectSpaceTransform);
	}
	
	@Override
	public void display(Matrix objectToWorld, Matrix worldToCamera, Matrix toDisplay) {

		p.noFill();
		p.stroke(100 + r, 100 + g, 100 + b);
		Matrix screenPos = vertices.mult(objectSpaceTransform)
								.mult(worldToCamera)
								.project(2)
								.mult(toDisplay);
		
		p.beginShape();
			for (int j = 0; j < screenPos.m; j++) {
				
				float screenX = screenPos.M[j * 4 + 0];
				float screenY = screenPos.M[j * 4 + 1];
				
//			     log.info("x:" + screenX);
//			     log.info("y:" + screenY);
				p.vertex(screenX, screenY);
			}
		p.endShape(p.CLOSE);
	}
	
}
