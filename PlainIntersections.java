import java.util.ArrayList;

import manipulation.Dragger;
import manipulation.PositionTool;
import processing.core.PApplet;
import processing.core.PVector;
import small.data.structures.Matrix;
import small.data.structures.Quaternion;
import utilities.Logger;
import visible.objects.Axes;
import visible.objects.DepthRenderer;
import visible.objects.Grid;
import visible.objects.Hexagon;
import visible.objects.Plane;
import visible.objects.Texture;
import visible.objects.TileSet;
import visible.objects.Tileable;

/**
 * TODO: handle the offsets properly
 * TODO: far too much global state
 */
public class PlainIntersections extends PApplet {

	Logger log;

	Grid grid; 		// Nb. transformed to world space within the class
	Dragger dragger; 	// Object that handles changes in camera orientation due to mouse drag
	Axes world;

	Matrix camera, camRot, camTrans, cameraToWorld;
	Matrix camArm, camPivot;

	Matrix worldToCamera, toDisplay;

	Matrix R; // orientation of data's basis before/after PCA

	Matrix localAxes;

	int count; 		// Used in Animation Operations 1 to 4
	
	// float count;
	boolean orbiting;

	boolean showGrid;

	PositionTool alignCamera;
	
	Matrix fromRotArm;
	
	// represents an animated rotation of the camera arm
	// read as 'aligns the camera arm'
	Quaternion alArm; 

	PVector orbBut; 		// 0
	PVector gridBut; 		// 2

	Plane plane;
	Texture textureA;
	Texture textureB;
	Texture textureC;
	Texture textureD;
	
	Hexagon hexagon;
	TileSet tileset;
	
//	// Run this project as Java application and this
//	// method will launch the sketch
	public static void main(String[] args) {
		PApplet.main("PlainIntersections");
	}

	public void settings() {
		size(800, 800);
	}

	public void setup() {
		
		log = new Logger(this);
		background(0);
		
		world = new Axes(this);
		grid = new Grid(this);
		dragger = new Dragger();

		// camera's basis vectors
		// these can also be translated
		// therefore drop 1s in fourth row
		camera = new Matrix(4, 3);
		camera.M[3] = 1;
		camera.M[7] = 1;
		camera.M[11] = 1;

		camRot = new Matrix(4, 4);
		camTrans = new Matrix(4, 4);
		
		// camera rig
		camArm = new Matrix(new float[] { 0, 0, 6 }, 3, 1);
		camPivot = new Matrix(new float[] { 0, 1, 0 }, 3, 1);

		// translation vector of the camera from world-space origin
		Matrix buildTrans = Matrix.add(camArm, camPivot);

		// assign this vector as the fourth col of 4x4 camTrans matrix
		camTrans.M[12] = buildTrans.M[0];
		camTrans.M[13] = buildTrans.M[1];
		camTrans.M[14] = buildTrans.M[2];

		// need to clarify signs- left vs right handed coordinate system
		
		// TODO: breaks immutable model
		buildTrans.normalize();
		Matrix jVec = buildTrans.cross(new Matrix(new float[] { -1, 0, 0 }, 3, 1));
		jVec.normalize();

		// construct the matrix that describes the camera's orientation align
		// the camera's look-at vector (3rd col) with the translation vector
		camRot.M[4] = jVec.M[0];
		camRot.M[5] = jVec.M[1];
		camRot.M[6] = jVec.M[2];
		camRot.M[8] = -buildTrans.M[0];
		camRot.M[9] = -buildTrans.M[1];
		camRot.M[10] = -buildTrans.M[2];
		
		// Product of 4x4 Translation Matrix and 4x4 Rotation Matrix
		cameraToWorld = Matrix.mult(camTrans, camRot);
		worldToCamera = cameraToWorld.inverse();
		
		toDisplay = new Matrix(4, 4);
		// x and y scaling
		toDisplay.M[0] = 800;
		toDisplay.M[5] = 800;
		
		// translation
		toDisplay.M[12] = width / 2;
		toDisplay.M[13] = height / 2;

		stroke(255);
		initializeButtons();
		R = new Matrix(4, 4);
		
		showGrid = true;
		
		// Fill the world with interesting stuff
		
		plane = new Plane(this);
		
//		textureA = new Texture(this, loadImage("sky.jpg"));
//		textureB = new Texture(this, loadImage("kent.jpg"));
		textureA = new Texture(this, loadImage("spain.JPG"));
		textureB = new Texture(this, loadImage("spain.JPG"));
		textureC = new Texture(this, loadImage("spain.JPG"));
		textureD = new Texture(this, loadImage("spain.JPG"));
		
//		singleRender();
		
		/* ---------------------------------- */
		
		hexagon = new Hexagon(this);
		
		cameraToWorld = Matrix.mult(camTrans, camRot);
		worldToCamera = cameraToWorld.inverse();
		
		world.display(R, worldToCamera, toDisplay);

		Matrix objectToWorld = new Matrix(4, 4);
		
		// translation vector
		objectToWorld.M[12] = 0.5f;
		objectToWorld.M[13] = -0.1f;
		objectToWorld.M[14] = -0.05f;
		
//		hexagon.display(objectToWorld, worldToCamera, toDisplay);
		
		tileset = new TileSet(this);
		
		tileset.setSeed(hexagon);
		Hexagon hex2 = tileset.createLinkedTile(hexagon, 2);
		Hexagon hex3 = tileset.createLinkedTile(hex2, 5);
//		Hexagon hex4 = tileset.createLinkedTile(hex3, 4);
		
		tileset.addLinkedTile(hex2);
//		tileset.addLinkedTile(hex3);
//		tileset.addLinkedTile(hex4);
		
		tileset.display(objectToWorld, worldToCamera, toDisplay);
		
		/* ---------------------------------- */
		
	}
	
	public void singleRender() {
		background(0);
		
		// find world to camera matrix
		cameraToWorld = Matrix.mult(camTrans, camRot);
		worldToCamera = cameraToWorld.inverse();
		
		// show grid
//		grid.display(worldToCamera, toDisplay, 60);
		
		world.display(R, worldToCamera, toDisplay);

		Matrix objectToWorld = new Matrix(4, 4);
		
		// translation vector
		objectToWorld.M[12] = 0.5f;
		objectToWorld.M[13] = -0.1f;
		objectToWorld.M[14] = -0.05f;
		
		ArrayList<Texture> texList = new ArrayList<Texture>();
		texList.add(textureA);
		texList.add(textureB);
		texList.add(textureC);
//		texList.add(textureD);
		
		DepthRenderer depthRenderer = new DepthRenderer(this, texList);
		
		depthRenderer.calculateAndDisplay(objectToWorld, worldToCamera, toDisplay);
	}
	
	public void draw() {
		
	}
	
//	public void draw() {
//		/*
//		 	redraw the background
//		 */
//		background(0);
//		textAlign(LEFT, CENTER);
//		fill(255);
//		text("count = " + count, 50, 50);
//		noFill();
//		
//		/* check if dragging the camera with mouse */
//		if (dragger.check(pmouseX, pmouseY, mouseX, mouseY)) { 
//			Matrix camera2 = Matrix.mult(camRot, camera);
//			Matrix rotUpdate = dragger.reorientCamera(camera2);
//			camRot.mult(rotUpdate); // update camera's orientation
//		}
//
//		/* If orbiting the data, update camera's orientation and position */
//		if (orbiting) {
//			if (count < 80) {
//				Quaternion interpQuat = new Quaternion(dtEase(80, count), 0, -1, 0);
//
//				camRot = camRot.mult(interpQuat.getR(4));
//				camArm = camArm.mult(interpQuat.getR(3));
//				camPivot = camPivot.mult(interpQuat.getR(3)); // may not be necessary depending on the pivot
//
//				camTrans.M[12] = camArm.M[0] + camPivot.M[0];
//				camTrans.M[13] = camArm.M[1] + camPivot.M[1];
//				camTrans.M[14] = camArm.M[2] + camPivot.M[2];
//
//				count++;
//			} else {
//				orbiting = false;
//				count = 0;
//			}
//		}
//
//		/* ---- CALCULATE worldToCamera matrix ---- */
//
//		/* perhaps too many unnecessary multiplications here */
//		cameraToWorld = Matrix.mult(camTrans, camRot);
//		worldToCamera = cameraToWorld.inverse();
//
//		/* ---------------------------------------- */
//
//		if (showGrid) {
//			grid.display(worldToCamera, toDisplay, 60);
//		}
//
//		world.display(R, worldToCamera, toDisplay);
//
//		/* Display buttons and mouse */
//		showButtons();
//		
//		Matrix objectToWorld = new Matrix(4, 4);
//		
//		// translation vector
//		objectToWorld.M[12] = 0.5f;
//		objectToWorld.M[13] = -0.1f;
//		objectToWorld.M[14] = -0.05f;
//		
//		
//		if (frameCount % 60 == 0) {
//			plane = new Plane(this);
//		}
//		
//		/* ---------- */
//		
//		plane.display(objectToWorld, worldToCamera, toDisplay);
//		texture.display(objectToWorld, worldToCamera, toDisplay);
//		textureB.display(objectToWorld, worldToCamera, toDisplay);
//	
//	}

	void initializeButtons() {
		orbBut = new PVector(width - 40, 180);
		gridBut = new PVector(width - 40, 210);
	}

	void showButtons() {
		textAlign(RIGHT, CENTER);
		noStroke();
//		fill(#FC9D03);
		rectMode(CENTER);

		// fill(#164FF5);
		rect(orbBut.x, orbBut.y, 20, 20);
		fill(255);
		text("Spin", orbBut.x - 14, orbBut.y);

		fill(200);
		rect(gridBut.x, gridBut.y, 20, 20);
		fill(255);
		text("Grid", gridBut.x - 14, gridBut.y);
	}

	int checkButtonClick() {
		PVector[] locs = new PVector[] { orbBut, gridBut };
		int rval = -1;
		for (int i = 0; i < locs.length; i++) {
			if ((mouseX > locs[i].x - 20 / 2) && mouseX < locs[i].x + 20 / 2) {
				if ((mouseY > locs[i].y - 20 / 2) && mouseY < locs[i].y + 20 / 2) {
					rval = i;
				}
			}
		}
		log.info("" + rval);
		return rval;
	}

	float dtEase(float totalSums, float oC) {
		float step = 1 / totalSums;
		float dtheta = PI * oC / 80;
		float distance = 2; // cos(0) - cos(PI) == 2
		float incang = (PI * sin(dtheta) * step) * PI / (2 * distance);

		return incang;
	}

	public void mousePressed() {
		int val = checkButtonClick();
		if (val == -1) {
			dragger.pressed();
		} else {
			if (val == 0) {
				orbiting = true;
			} else if (val == 1) {
				showGrid = !showGrid;
			}
		}
	}

	public void mouseReleased() {
		dragger.released();
	}

	public void keyPressed() {
		
		if (key == 'm') {
//			textureA = new Texture(this, loadImage("sky.jpg"));
//			textureB = new Texture(this, loadImage("kent.jpg"));
			textureA = new Texture(this, loadImage("spain.JPG"));
			textureB = new Texture(this, loadImage("spain.JPG"));
			textureC = new Texture(this, loadImage("spain.JPG"));
			textureD = new Texture(this, loadImage("spain.JPG"));
			singleRender();
		}
		
		if (key == 'l') {
			saveFrame("output/pi-######.png");
		}
	}

} // end of PApplet extension