package visible.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import processing.core.PApplet;
import processing.core.PImage;
import small.data.structures.Matrix;
import small.data.structures.Point;

public class DepthRenderer {

	ArrayList<Texture> textures;
	PApplet p;
	public DepthRenderer(PApplet p, ArrayList<Texture> textures) {
		this.p = p;
		this.textures = textures;
	}
	
	/**
	 * 
	 * @param objectToWorld
	 * @param worldToCamera
	 * @param toDisplay
	 */
	public void calculateAndDisplay(Matrix objectToWorld, Matrix worldToCamera, Matrix toDisplay) {
		
		// calculate array length
		int len = 0;
		for (Texture t : textures) {
			PImage img = t.getImg();
			len += img.width * img.height;
		}
		
		ArrayList<Point> depthTextureStore = new ArrayList<>(len);
		
		for (Texture t : textures) {
			PImage img = t.getImg();
			
			Matrix centre = t.getCentreTransform();
			Matrix scale = t.getScaleTransform();
			Matrix rotate = t.getRotationTransform();
			
			for (int j = 0; j < img.width; j++) {
				for (int i = 0; i < img.height; i++) {
					
					Matrix imgPoint = new Matrix(new float[] { j, i, 0, 1 }, 4, 1);
	
					Matrix inCameraPos = imgPoint.mult(centre)
												.mult(scale)
												.mult(rotate)
												//.mult(objectToWorld)
												.mult(worldToCamera);
												//.project(2)
												//.mult(toDisplay);
					
					int c = img.get(j, i);

					depthTextureStore.add(new Point(inCameraPos, p.red(c), p.green(c), p.blue(c)));
				}
			}
		}
		
		List<Point> depthSortedPoints = 
					depthTextureStore.stream().sorted((pointA, pointB) -> pointA.compareTo(pointB))
							.collect(Collectors.toList());
		
		for (Point pt : depthSortedPoints) {
			pt.display(p, toDisplay);
		}
		
	}
	
}
