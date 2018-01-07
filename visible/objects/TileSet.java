package visible.objects;
import java.util.ArrayList;

import processing.core.PApplet;
import small.data.structures.Matrix;
import small.data.structures.Quaternion;

/**
 * TODO: use generics for this
 * 
 * A tile structure is created in object space
 * 
 * @author charlesbrookhouse1
 *
 */
public class TileSet {
	
	Hexagon seed;
	ArrayList<Hexagon> tiles;
	PApplet p;
	
	public TileSet(PApplet p) {
		this.p = p;
		tiles = new ArrayList<>();
	}
	
	public void setSeed(Hexagon seed) {
		this.p = p;
		this.seed = seed;
		//tiles.add(seed);
	}

	public void solveJoinEdge() {
		// Object space transformation is a 
		// scaling, rotation and then translation
		// the hexagon is fortunately defined at the origin
		Matrix tfm = seed.getObjectSpaceTransform();
		
		// the origin-orbiting vertices are transformed
		// by the objectSpaceTransform and the result
		// is this set of vertices:
		
		// tfm produces this Scale_Rotation_Translation_vertices:
		Matrix SRT_vertices = seed.getObjectSpaceVertices();
		
		// We now need to run this process in reverse
		
		// Extract the inverse translation (just -ve vector)
		
		// Translation from object space back to the origin, where hexagons are defined
		Matrix translation = new Matrix(4, 4);
		translation.M[12] = -tfm.M[12];
		translation.M[13] = -tfm.M[13];
		
		// Vertices without the translation, but still after
		// the scaling and rotation at the origin
		Matrix vcs = SRT_vertices.mult(translation);
		
		Hexagon hex1 = new Hexagon(p);
		Hexagon hex2 = new Hexagon(p);
		Hexagon hex3 = new Hexagon(p);
		
		// Select an edge of the hexagon onto which we
		// want to join another hexagon
		
		// An edge is defined as the vector from one vertex
		// to another neighbouring vertex. These are the two
		// vertices we'll select:
		
		float[] v_1 = new float[] { vcs.M[0], vcs.M[1], vcs.M[2] };
		float[] v_2 = new float[] { vcs.M[4], vcs.M[5], vcs.M[6] };
		
		// When we create another hexagon to join onto this edge,
		// we'll also want to apply some kind of rotation to it
		// to that it is joined to that edge but explores a different
		// plane.
		
		// This means our new tile must have the same orientation as
		// the seed hexagon to start, then we translate it so that it's
		// join edge runs through the origin. We apply the rotation
		// and then translate the tile so that it's edge joins with
		// the edge of the seed hexagon.
		
		// Perpendicular distance of seed join edge to origin
		// This is used to translate the tile's join edge to the origin. 
		float[] toAxis = new float[] { 
				vcs.M[0] + (vcs.M[4] - vcs.M[0]) * 0.5f, 
				vcs.M[1] + (vcs.M[5] - vcs.M[1]) * 0.5f, 
				vcs.M[2] + (vcs.M[6] - vcs.M[2]) * 0.5f };
		
		// Construct the translation as a Matrix, using the
		// fourth column to hold the translation
		Matrix shift = new Matrix(4, 4);
		shift.M[12] = toAxis[0];
		shift.M[13] = toAxis[1];
		shift.M[14] = toAxis[2];
		
		// The rotation requires an angle-axis pair
		
		// The axis for the rotation is just the edge vector
		// to which we want to join the tile from the seed
		// hexagon. This vector was part of an interpolation
		// in the toAxis translation (see above)
		
		float angle = (float) (Math.random() * Math.PI);
	
		Quaternion q_random = new Quaternion(angle, 
				 								(vcs.M[4] - vcs.M[0]), 	// axis x
				 								(vcs.M[5] - vcs.M[1]), 	// axis y
				 								(vcs.M[6] - vcs.M[2])); // axis z
		
		// Get the rotation as a matrix
		Matrix rotation = q_random.getR(4);
				
		hex1.setObjectSpaceTransform(tfm);
		hex2.setObjectSpaceTransform(tfm.mult(translation).mult(translation.inverse()));
		
		// The final objectSpaceTransform for the tile is:
		// translation of centre to origin -> 
		// translation of join edge such that it is passing through the origin ->
		// rotation about the join edge ->
		// inverse translation of join edge to origin
		
		hex3.setObjectSpaceTransform(tfm.mult(translation).mult(shift).mult(rotation).mult(shift.inverse()).mult(translation.inverse()));
//		hex3.setObjectSpaceTransform(tfm.mult(translation).mult(shift).mult(rotation).mult(shift.inverse()));
		
		
		tiles.add(hex1);
//		tiles.add(hex2);
		tiles.add(hex3);
		
		// now just figure out how to initialize another hexagon that joins this one,
		// along hte first edge
		
		
	}
	
	public void display(Matrix objectToWorld, Matrix worldToCamera, Matrix toDisplay) {
		// TODO: ideally this would be generic
		for (Hexagon tile : tiles) {
			tile.display(objectToWorld, worldToCamera, toDisplay);
		}
	}
	
	
}
