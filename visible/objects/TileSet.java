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
		tiles.add(seed);
	}

	/**
	 * 
	 * @param seed
	 * @param joinEdge [1,6]
	 * @return
	 */
	public Hexagon createLinkedTile(Hexagon seed, int joinEdge) {
		
		if (joinEdge < 1 || joinEdge > 6) {
			throw new IllegalArgumentException("joinEdge < 1 || joinEdge > 6");
		}
		
		// Object space transformation is a 
		// scaling, rotation and then translation
		// of the hexagon defined with the origin 
		// as its centre
		Matrix tfm = seed.getObjectSpaceTransform();
		
		// the origin-orbiting vertices are transformed
		// by the objectSpaceTransform and the result
		// is this set of vertices:
		
		// tfm produces this Scale_Rotation_Translation_vertices:
		Matrix SRT_vertices = seed.getObjectSpaceVertices();
		
		// We now need to run this process in reverse
		
		// Extract the inverse translation (just -ve vector)
		
		// Translation from object space back to the origin, where hexagons are defined
		Matrix centreAtOrigin = new Matrix(4, 4);
		
		// Only in the first instance will this be centreAtOrigin
//		centreAtOrigin.M[12] = -tfm.M[12];
//		centreAtOrigin.M[13] = -tfm.M[13];
		
		// vertex0 + 0.5 * (vertex3 - vertex0)
		
		// these represent opposing vertices
		float[] u = new float[] { SRT_vertices.M[0 * 4 + 0], SRT_vertices.M[0 * 4 + 1], SRT_vertices.M[0 * 4 + 2] };
		float[] v = new float[] { SRT_vertices.M[3 * 4 + 0], SRT_vertices.M[3 * 4 + 1], SRT_vertices.M[3 * 4 + 2] };
		
		float transX = u[0] + 0.5f * (v[0] - u[0]);
		float transY = u[1] + 0.5f * (v[1] - u[1]);
		float transZ = u[2] + 0.5f * (v[2] - u[2]);
		
		centreAtOrigin.M[12] = -transX;
		centreAtOrigin.M[13] = -transY;
		centreAtOrigin.M[14] = -transZ;
		
		// Vertices without the translation, but still after
		// the scaling and rotation at the origin
		Matrix vcs = SRT_vertices.mult(centreAtOrigin);
		
		// Select an edge of the hexagon onto which we
		// want to join another hexagon
		
		// An edge is defined as the vector from one vertex
		// to another neighbouring vertex. These are the two
		// vertices we'll select:
		
		int j = joinEdge - 1;
		int k = joinEdge == 6 ? 0 : joinEdge;

		float[] v_1 = new float[] { vcs.M[j * 4 + 0], vcs.M[j * 4 + 1], vcs.M[j * 4 + 2] };
		float[] v_2 = new float[] { vcs.M[k * 4 + 0], vcs.M[k * 4 + 1], vcs.M[k * 4 + 2] };
		
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
				v_1[0] + (v_2[0] - v_1[0]) * 0.5f, 
				v_1[1] + (v_2[1] - v_1[1]) * 0.5f, 
				v_1[2] + (v_2[2] - v_1[2]) * 0.5f };
		
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
		
		// angle greater that 90 degrees...
		float angle = (float) (Math.random() * Math.PI / 2 + Math.PI / 2);
	
		Quaternion q_random = new Quaternion(angle, 
				 								(v_2[0] - v_1[0]), 	// axis x
				 								(v_2[1] - v_1[1]), 	// axis y
				 								(v_2[2] - v_1[2])); // axis z
		
		// Get the rotation as a matrix
		Matrix rotation = q_random.getR(4);
				
		// The final objectSpaceTransform for the tile is:
		// translation of centre to origin -> 
		// translation of join edge such that it is passing through the origin ->
		// rotation about the join edge ->
		// inverse translation of join edge to origin
		
		// TODO: NOTE THIS ONLY WORKS FOR THE INITIAL HEXAGON. FIX
		
		Matrix obTransform = tfm.mult(centreAtOrigin).mult(shift)
								.mult(rotation)
							    .mult(shift.inverse()).mult(centreAtOrigin.inverse());
		
		Hexagon connector = new Hexagon(p);
		connector.setObjectSpaceTransform(obTransform);
		return connector;
	}
	
	public void addLinkedTile(Hexagon hex) {
		tiles.add(hex);
	}
	
	public void display(Matrix objectToWorld, Matrix worldToCamera, Matrix toDisplay) {
		// TODO: ideally this would be generic
		for (Hexagon tile : tiles) {
			tile.display(objectToWorld, worldToCamera, toDisplay);
		}
	}
	
	
}
