package visible.objects;

import small.data.structures.Matrix;

public interface Tileable {

	Matrix getObjectSpaceVertices();
	
	Matrix getObjectSpaceCentre();
	
	Matrix getObjectSpaceTransform();
	
	void setObjectSpaceTransform(Matrix transform);
	
	void display(Matrix objectToWorld, Matrix worldToCamera, Matrix toDisplay);

}