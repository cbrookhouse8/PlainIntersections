package small.data.structures;

import utilities.Logger;

/**
 * TODO: stop modifying internal state of the object
 * TODO: instead always return a new matrix
 * @author charliebrookhouse
 *
 */
public class Matrix {
	// column major

	int n; 				// rows
	public int m; 		// cols
	public float[] M; 	// col major
	Logger log;
	
	public void print() {
		for (int j = 0; j < m; j++) {
			for (int i = 0; i < n; i++) {
				String full = String.valueOf(M[j * n + i]);
				String cut = full.substring(0, Math.min(4, full.length()));
				String msg = "M(" + j + "," + i +"): " + cut;
				System.out.println(msg);
			}
		}
	}
	
	/**
	 * Initialise an Identity Matrix
	 * @param n number of rows
 	 * @param m number of columns
	 */
	public Matrix(int n, int m) {
		log = new Logger(this);
		this.n = n;
		this.m = m;
		
		this.M = new float[n * m];
		for (int i = 0; i < n * m; i++) {
			this.M[i] = (i + this.n) % this.n == i / this.n ? 1 : 0;
		}
	}
	
	/**
	 * @param data matrix entries
	 * @param n number of rows
 	 * @param m number of columns
	 */
	public Matrix(float[] data, int n, int m) {
		this.n = n;
		this.m = m;
		this.M = new float[n * m];
		for (int i = 0; i < n * m; i++) {
			this.M[i] = data[i];
		}
	}

	/**
	 * Initialise a Rotation matrix from an axis-angle pair
	 * Defaults to 3 x 3 if n is not 3 or 4
	 * @param angle
	 * @param axis
	 * @param n number of rows
	 */
	Matrix(float angle, Matrix axis, int n) {
		this.n = n < 3 || n > 4 ? 3 : n;
		this.m = n;

		this.M = new float[this.m * this.n];

		// Equation 4.20 in Lengyel, for rotation of P about A by theta:
		// P' = PCos(theta) + (AxP)Sin(theta) + A(A.P)(1-Cos(theta))

		// term 1 - identity matrix
		float[] identity_elems = new Matrix(3, 3).M;

		// term 2 - expression of the cross product as a linear transformation
		// 3x3
		float[] cross = new float[] { 0, axis.M[2], -axis.M[1], 
							-axis.M[2], 0, axis.M[0], 
							axis.M[1], -axis.M[0], 0 };

		// term 3 - outer product of axis with itself
		Matrix C = new Matrix(3, 3);

		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 3; i++) {
				float scaleFac = (float) (1 - Math.cos(angle));
				C.M[j * C.n + i] = (float) (axis.M[i] * axis.M[j]) * scaleFac;
			}
		}

		Matrix A = Matrix.scale(new Matrix(identity_elems, 3, 3), (float) Math.cos(angle));
		Matrix B = Matrix.scale(new Matrix(cross, 3, 3), (float) Math.sin(angle));

		Matrix D = A.add(B).add(C);

		if (n == 3) {
			for (int i = 0; i < 9; i++) {
				this.M[i] = D.M[i];
			}
		} else {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					M[j * 4 + i] = i < 3 && j < 3 ? D.M[j * 3 + i] : i == j ? 1 : 0;
				}
			}
		}
	}

	/**
	 * Linear transformation of this matrix: A * this
	 * @param A the linear transformation
	 * @return
	 */
	public Matrix mult(Matrix A) {
		
		if (A.m != this.n) {
			throw new ArithmeticException("inner dimensions don't agree");
		}
		
		float[] product = new float[A.n * this.m];
		
		for (int k = 0; k < this.m; k++) {
			for (int i = 0; i < A.n; i++) {
				// dot product
				float dp = 0;
				// dot product of each row(A) with each col(this)
				for (int j = 0; j < this.n; j++) {
					dp += A.M[j * A.n + i] * this.M[k * this.n + j];
				}
				product[k * A.n + i] = dp;
			}
		}

		return new Matrix(product, A.n, this.m);
	}
	
	/**
	 * 
	 * @param a Linear Transformation
	 * @param b Operand
	 * @return transformation of `b` by `a`
	 */
	public static Matrix mult(Matrix a, Matrix b) {
		if (a.m != b.n) {
			throw new ArithmeticException("Inner dimensions don't agree.");
		}

		float[] product = new float[a.n * b.m];
		
		for (int k = 0; k < b.m; k++) {
			for (int i = 0; i < a.n; i++) {
				float dp = 0;
				// dot product
				for (int j = 0; j < a.m; j++) {
					dp += a.M[j * a.n + i] * b.M[k * b.n + j];
				}
				product[k * a.n + i] = dp;
			}
		}

		return new Matrix(product, a.n, b.m);
	}
	
	/**
	 * Subtract the matrix A from this
	 * @param A
	 * @return
	 */
	public Matrix sub(Matrix A) {
		Matrix rval = new Matrix(this.n < A.n ? this.n : A.n, 
								this.m < A.m ? this.m : A.m);

		for (int j = 0; j < rval.m; j++) {
			for (int i = 0; i < rval.n; i++) {
				rval.M[j * rval.n + i] = this.M[j * this.n + i] - A.M[j * A.n + i];
			}
		}

		return rval;
	}
	
	/**
	 * Find a-b
	 * @param a matrix
	 * @param b matrix
	 * @return
	 */
	public static Matrix sub(Matrix a, Matrix b) {
		Matrix rval = new Matrix(a.n < b.n ? a.n : b.n, a.m < b.m ? a.m : b.m);

		for (int j = 0; j < rval.m; j++) {
			for (int i = 0; i < rval.n; i++) {
				rval.M[j * rval.n + i] = a.M[j * a.n + i] - b.M[j * b.n + i];
			}
		}

		return rval;
	}

	/**
	 * Add Matrix a to this
	 * @param a
	 * @return
	 */
	public Matrix add(Matrix a) {
		int upperI = a.n < this.n ? a.n : this.n;
		int upperJ = a.m < this.m ? a.m : this.m;

		float[] sum = new float[this.n * this.m];
		
		for (int j = 0; j < upperJ; j++) {
			for (int i = 0; i < upperI; i++)
				sum[j * this.n + i] = this.M[j * this.n + i] + a.M[j * a.n + i];
		}
		return new Matrix(sum, this.n, this.m);
	}
		
	/**
	 * Add matrices a and b
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix add(Matrix a, Matrix b) {
		if ((a.n - b.n) + (a.m - b.m) != 0) {
			throw new ArithmeticException("Matrix dimensions don't agree.");
		}
		
		float[] sum = new float[b.n * b.m];
		
		for (int j = 0; j < a.m; j++) {
			for (int i = 0; i < a.n; i++)
				sum[j * b.n + i] = b.M[j * b.n + i] + a.M[j * a.n + i];
		}
		return new Matrix(sum, b.n, b.m);
	}

	public Matrix scale(float d) {
		float[] scaled = new float[this.n * this.m];
		for (int i = 0; i < this.M.length; i++) {
			scaled[i] = this.M[i] * d;
		}
		return new Matrix(scaled, this.n, this.m);
	}

	public static Matrix scale(Matrix A, float d) {
		float[] scaled = new float[A.n * A.m];
		for (int i = 0; i < A.M.length; i++) {
			scaled[i] = A.M[i] * d;
		}
		return new Matrix(scaled, A.n, A.m);
	}
	
	/**
	 * 
	 * @param Q 3 x 1 Matrix i.e a 3-Vector
	 * @return Vector that is perpendicular to `this` and Q
	 */
	public Matrix cross(Matrix Q) {
		if (Q.M.length != 3 || this.M.length != 3) {
			throw new ArithmeticException("Cross-product is only designed for 3-Vectors");
		}
		
		float[] perp;
		
		perp = new float[] { 
				-this.M[2] * Q.M[1] + this.M[1] * Q.M[2], 
				this.M[2] * Q.M[0] + -this.M[0] * Q.M[2],
				-this.M[1] * Q.M[0] + this.M[0] * Q.M[1] };

		return new Matrix(perp, 3, 1);
	}
	
	/**
	 * Cross product of U and V
	 * @param U 3-Vector
	 * @param V 3-Vector
	 */
	public static Matrix cross(Matrix U, Matrix V) {
		if (U.M.length != 3 || V.M.length != 3) {
			throw new ArithmeticException("Cross-product is only designed for 3-Vectors");
		}
		
		float[] perp;
		
		perp = new float[] { 
				-U.M[2] * V.M[1] + U.M[1] * V.M[2], 
				U.M[2] * V.M[0] + -U.M[0] * V.M[2],
				-U.M[1] * V.M[0] + U.M[0] * V.M[1] };

		return new Matrix(perp, 3, 1);
	}
	
	/**
	 * Dot product of `this` with Q. Both must be vectors of equal length.
	 * @param Q vector of the same length as `this`
	 * @return scalar value representing |this||Q|cos(alpha)
	 */
	public float dot(Matrix Q) {
		if (Q.m != 1) {
			String msg = ".dot() should only be called on vectors. ";
			msg += "Input param is not a vector.";
			throw new ArithmeticException(msg);
		}
		
		if (this.m != 1) {
			String msg = ".dot() should only be called on vectors. ";
			msg += "`this` is not a vector.";
			throw new ArithmeticException(msg);
		}
		
		if (this.n != Q.n) {
			throw new ArithmeticException("Tried to take dot product of vectors of different lengths.");
		}
		
		float dp = 0;
		for (int i = 0; i < this.M.length; i++) {
			dp += this.M[i] * Q.M[i];
		}
		
		return dp;
	}

	public static float dot(Matrix A, Matrix B) {
		if (A.m * B.m != 1) {
			throw new ArithmeticException("Can only take dot product of vectors.");
		}
		
		if (A.n != B.n) {
			throw new ArithmeticException("Vectors are not equal length");
		}
		
		float dp = 0;
		for (int i = 0; i < A.M.length; i++) {
			dp += A.M[i] * B.M[i];
		}
		
		return dp;
	}
	
	// This outer Q
	Matrix outer(Matrix Q) {
		Matrix rval = new Matrix(this.n, Q.m);
		for (int j = 0; j < Q.m; j++) {
			for (int i = 0; i < this.n; i++) {
				rval.M[j * this.n + i] = this.M[i] * Q.M[j];
			}
		}
		return rval;
	}

	/**
	 * TODO: review the outer product of two matrices
	 * @param A
	 * @param Q
	 * @return
	 */
	public static Matrix outer(Matrix A, Matrix Q) {
		
		float[] elems = new float[A.n * Q.m];
		
		for (int j = 0; j < Q.m; j++) {
			for (int i = 0; i < A.n; i++) {
				elems[j * A.n + i] = A.M[i] * Q.M[j];
			}
		}
		return new Matrix(elems, A.n, Q.m);
	}
	
	// many assumptions here
	// would be much better if this func normalized
	// all of the column vectors- as though normalizing
	// vectors of a basis
	public void normalize() {
		
		if (this.m != 1) {
			throw new UnsupportedOperationException("normalize() is currently only defined for vectors.");
		}
		
		float mag = (float) Math.sqrt(this.dot(this));
		for (int i = 0; i < this.M.length; i++) {
			this.M[i] /= mag;
		}
	}

	/**
	 * Normalize a vector U
	 * @param U
	 * @return
	 */
	public static Matrix normalize(Matrix U) {
		if (U.m != 1) {
			throw new UnsupportedOperationException("normalize() is currently only defined for vectors.");
		}
		
		float[] elems = new float[U.n];
		
		// U dot U is just mag(U) squared
		float dp = 0;
		for (int i = 0; i < U.M.length; i++) {
			dp += U.M[i] * U.M[i];
		}
		
		float mag = (float) Math.sqrt(dp);
		
		for (int i = 0; i < U.M.length; i++) {
			elems[i] = U.M[i] / mag;
		}
		
		return new Matrix(elems, U.n, 1);
	}
	
	/**
	 * TODO: refactor to getMag()
	 * @return
	 */
	public float mag() {
		try {
			return (float) Math.sqrt(this.dot(this));
		} catch(ArithmeticException e) {
			throw e;
		}
	}

	/**
	 * TODO: static version of this
	 * @param row
	 * @return
	 */
	public Matrix project(int row) {
		Matrix rval = new Matrix(this.M, this.n, this.m);
		for (int j = 0; j < m; j++) {
			for (int i = 0; i <= row; i++) {
				rval.M[j * n + i] = rval.M[j * n + i] / rval.M[j * n + row];
			}
		}
		return rval;
	}

	/**
	 * TODO: refactor to just transpose()
	 * @return
	 */
	public Matrix transpose() {
		Matrix rval = new Matrix(m, n);
		for (int j = 0; j < m; j++) {
			for (int i = 0; i < n; i++) {
				rval.M[i * m + j] = M[j * n + i];
			}
		}
		return rval;
	}

	public Matrix inverse() {
		Matrix rval = new Matrix(n, n);
		if (n != m) {
			log.info("This is not a square matrix");
			throw new ArithmeticException("Can only find the inverse for square matrices.");
		} else {
			rval.M = invert(this.M);
		}
		return rval;
	}

	/**
	 * TODO: ideally reduce any dependence on this method
	 * @return
	 */
	public Matrix getCopy() {
		Matrix rval = new Matrix(this.n, this.m);
		for (int i = 0; i < this.n * this.m; i++)
			rval.M[i] = this.M[i];
		return rval;
	}

	/**
	 * TODO: error checks
	 * Return rectangular portion of this matrix
	 * @param iStart
	 * @param jStart
	 * @param iRange
	 * @param jRange
	 * @return
	 */
	public Matrix isolate(int iStart, int jStart, int iRange, int jRange) {
		
		Matrix rval = new Matrix(iRange, jRange);
		for (int j = 0; j < jRange; j++) {
			for (int i = 0; i < iRange; i++) {
				rval.M[j * iRange + i] = this.M[(jStart - 1 + j) * this.n + i];
			}
		}
		return rval;
	}

	public static Matrix isolate(Matrix A, int iStart, int jStart, int iRange, int jRange) {
		Matrix rval = new Matrix(iRange, jRange);
		for (int j = 0; j < jRange; j++) {
			for (int i = 0; i < iRange; i++) {
				rval.M[j * iRange + i] = A.M[(jStart - 1 + j) * A.n + i];
			}
		}
		return rval;
	}
	
	// ------------------------------
	// ------- MATRIX INVERSE -------
	// ------------------------------

	// for square matrices only
	float[] invert(float[] _mat) {
		int n = (int) Math.round(Math.sqrt(_mat.length));
		float[] mat = new float[_mat.length];

		// copy
		for (int i = 0; i < mat.length; i++)
			mat[i] = _mat[i];

		float[] identity = new float[mat.length];

		// build the identity matrix
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++)
				identity[j * n + i] = i == j ? 1 : 0;
		}

		// Gauss-Jordan Elimination, Algorithm 3.13 in Lengyel, p. 42
		for (int j = 0; j < n; j++) { // B. Set column j = 1
			int i = j;
			int sto = i;
			float stoVal = mat[j * n + i];

			while (i < n) {
				if (Math.abs(mat[j * n + i]) > Math.abs(stoVal)) {
					stoVal = mat[j * n + i];
					sto = i;
				}

				i++;
			} // end of while

			if (stoVal == 0) {
				log.info("not invertible");
			} else {
				if (sto != j) {
					// pivot
					exchange(sto, j, mat);
					exchange(sto, j, identity);
				}

				stoVal = mat[j * n + j];

				weight(j, 1 / stoVal, mat);
				weight(j, 1 / stoVal, identity);

				for (int r = 0; r < n; r++) {
					if (r != j) {
						stoVal = -mat[j * n + r];

						combine(j, stoVal, r, mat);
						combine(j, stoVal, r, identity);
					}
				}
			}
		} // end of loop

		return identity;
	} // end of invert()

	// ---------------------------------
	// ----Elementary Row Operations----
	// ---------------------------------

	// add a multiple of one row
	// (w*r1) to another row (r2)
	private void combine(int r1, float w, int r2, float[] m) {
		int dim = (int) Math.round(Math.sqrt(m.length));
		float weighted = 0;

		for (int j = 0; j < dim; j++) {
			weighted = m[j * dim + r1] * w; // weight r1 element
			m[j * dim + r2] += weighted; // add weighted r1 element to r2 element
		}
	}

	// multiply row r1 by a non-zero scalar
	private void weight(int r1, float w, float[] m) {
		int dim = (int) Math.round(Math.sqrt(m.length));
		for (int j = 0; j < dim; j++)
			m[j * dim + r1] *= w;
	}

	// exchange rows r1 and r2
	private void exchange(int r1, int r2, float[] m) {
		int dim = (int) Math.round(Math.sqrt(m.length));
		float hold = 0;

		for (int j = 0; j < dim; j++) {
			hold = m[j * dim + r1]; // save r1 element
			m[j * dim + r1] = m[j * dim + r2]; // replaced present r1 element with r2 element
			m[j * dim + r2] = hold; // place saved r1 element where r2 element was
		}
	}

}
