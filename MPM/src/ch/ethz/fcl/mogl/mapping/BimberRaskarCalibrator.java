/*
Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona, Eva Friedrich)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
 * Neither the name of ETH Zurich nor the names of its contributors may be 
  used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.fcl.mogl.mapping;

import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import ch.ethz.fcl.util.MathUtils;

public final class BimberRaskarCalibrator implements ICalibrator {

	private RealMatrix projectionMatrix;
	private RealMatrix modelviewMatrix;

	public BimberRaskarCalibrator() {
	}

	@Override
	public double calibrate(ArrayList<Vector3D> modelVertices, ArrayList<Vector3D> projectedVertices, double near, double far) {
		if (modelVertices.size() < 6) {
			throw new IllegalArgumentException("not enough points");
		}

		// Bimber Raskar Appendix A.1

		// step 1: fill matrix tha covers the constraining equations

		RealMatrix lhs = MatrixUtils.createRealMatrix(2 * modelVertices.size(), 12);
		for (int i = 0; i < modelVertices.size(); ++i) {
			Vector3D mp = modelVertices.get(i);
			Vector3D dp = projectedVertices.get(i);
			double x = mp.getX();
			double y = mp.getY();
			double z = mp.getZ();
			double u = dp.getX();
			double v = dp.getY();

			lhs.setEntry(2 * i, 0, x);
			lhs.setEntry(2 * i, 1, y);
			lhs.setEntry(2 * i, 2, z);
			lhs.setEntry(2 * i, 3, 1);
			lhs.setEntry(2 * i, 4, 0);
			lhs.setEntry(2 * i, 5, 0);
			lhs.setEntry(2 * i, 6, 0);
			lhs.setEntry(2 * i, 7, 0);
			lhs.setEntry(2 * i, 8, -u * x);
			lhs.setEntry(2 * i, 9, -u * y);
			lhs.setEntry(2 * i, 10, -u * z);
			lhs.setEntry(2 * i, 11, -u);

			lhs.setEntry(2 * i + 1, 0, 0);
			lhs.setEntry(2 * i + 1, 1, 0);
			lhs.setEntry(2 * i + 1, 2, 0);
			lhs.setEntry(2 * i + 1, 3, 0);
			lhs.setEntry(2 * i + 1, 4, x);
			lhs.setEntry(2 * i + 1, 5, y);
			lhs.setEntry(2 * i + 1, 6, z);
			lhs.setEntry(2 * i + 1, 7, 1);
			lhs.setEntry(2 * i + 1, 8, -v * x);
			lhs.setEntry(2 * i + 1, 9, -v * y);
			lhs.setEntry(2 * i + 1, 10, -v * z);
			lhs.setEntry(2 * i + 1, 11, -v);
		}

		// step 2: find u-vector corresponding to smallest singular value (S)
		// (=solution)

		// note that the Apache SVD implementation returns values in descending
		// order, so smallest column will always be last column and we can skip
		// search as done in the original Bimber-Raskar code.
		/*
		 * RealMatrix d = svd.getS(); RealMatrix u = svd.getU(); int smallestCol
		 * = 0; for (int j = 0; j < 12; ++j) { double a =
		 * d.getEntry(smallestCol, smallestCol); double b = d.getEntry(j, j); if
		 * (a * a > b * b) smallestCol = j; } RealVector s =
		 * u.getColumnVector(smallestCol);
		 */
		RealMatrix l = lhs.transpose().multiply(lhs);
		RealVector s = new SingularValueDecomposition(l).getU().getColumnVector(11);

		// step 3: write 12x1 vector as 3x4 matrix (row-wise)
		RealMatrix pmv = MatrixUtils.createRealMatrix(3, 4);
		pmv.setRowVector(0, s.getSubVector(0, 4));
		pmv.setRowVector(1, s.getSubVector(4, 4));
		pmv.setRowVector(2, s.getSubVector(8, 4));

		// step 4: decompose pmv into 4x4 projection and modelview matrices
		double scale = pmv.getSubMatrix(2, 2, 0, 2).getRowVector(0).getNorm();
		pmv = pmv.scalarMultiply(1.0 / scale);

		if (pmv.getEntry(2, 3) > 0)
			pmv = pmv.scalarMultiply(-1.0);

		Vector3D q0 = MathUtils.toVector3D(pmv.getSubMatrix(0, 0, 0, 2).getRowVector(0));
		Vector3D q1 = MathUtils.toVector3D(pmv.getSubMatrix(1, 1, 0, 2).getRowVector(0));
		Vector3D q2 = MathUtils.toVector3D(pmv.getSubMatrix(2, 2, 0, 2).getRowVector(0));
		double q03 = pmv.getEntry(0, 3);
		double q13 = pmv.getEntry(1, 3);
		double q23 = pmv.getEntry(2, 3);

		double tz = q23;
		double tzeps = 1.0;
		if (tz > 0.0)
			tzeps = -1.0;

		tz = tzeps * q23;

		Vector3D r2 = q2.scalarMultiply(tzeps);

		double u0 = q0.dotProduct(q2);
		double v0 = q1.dotProduct(q2);

		double a = q0.crossProduct(q2).getNorm();
		double b = q1.crossProduct(q2).getNorm();

		Vector3D r0 = q0.subtract(q2.scalarMultiply(u0)).scalarMultiply(tzeps / a);
		Vector3D r1 = q1.subtract(q2.scalarMultiply(v0)).scalarMultiply(tzeps / b);

		double tx = tzeps * (q03 - u0 * tz) / a;
		double ty = tzeps * (q13 - v0 * tz) / b;

		// create rotation matrix and translation vector
		// (skipped since not needed for our purpose here)

		// create 4x4 projection and modelview matrices
		projectionMatrix = MatrixUtils.createRealMatrix(4, 4);
		projectionMatrix.setEntry(0, 0, -a);
		projectionMatrix.setEntry(0, 1, 0.0);
		projectionMatrix.setEntry(0, 2, -u0);
		projectionMatrix.setEntry(0, 3, 0);
		projectionMatrix.setEntry(1, 0, 0);
		projectionMatrix.setEntry(1, 1, -b);
		projectionMatrix.setEntry(1, 2, -v0);
		projectionMatrix.setEntry(1, 3, 0);
		projectionMatrix.setEntry(2, 0, 0);
		projectionMatrix.setEntry(2, 1, 0);
		if (far >= Double.POSITIVE_INFINITY) {
			projectionMatrix.setEntry(2, 2, -1.0);
			projectionMatrix.setEntry(2, 3, -2.0 * near);
		} else {
			projectionMatrix.setEntry(2, 2, -(far + near) / (far - near));
			projectionMatrix.setEntry(2, 3, -2.0 * far * near / (far - near));
		}
		projectionMatrix.setEntry(3, 0, 0);
		projectionMatrix.setEntry(3, 1, 0);
		projectionMatrix.setEntry(3, 2, -1);
		projectionMatrix.setEntry(3, 3, 0);

		modelviewMatrix = MatrixUtils.createRealMatrix(4, 4);
		modelviewMatrix.setEntry(0, 0, r0.getX());
		modelviewMatrix.setEntry(0, 1, r0.getY());
		modelviewMatrix.setEntry(0, 2, r0.getZ());
		modelviewMatrix.setEntry(1, 0, r1.getX());
		modelviewMatrix.setEntry(1, 1, r1.getY());
		modelviewMatrix.setEntry(1, 2, r1.getZ());
		modelviewMatrix.setEntry(2, 0, r2.getX());
		modelviewMatrix.setEntry(2, 1, r2.getY());
		modelviewMatrix.setEntry(2, 2, r2.getZ());
		modelviewMatrix.setEntry(0, 3, tx);
		modelviewMatrix.setEntry(1, 3, ty);
		modelviewMatrix.setEntry(2, 3, tz);
		modelviewMatrix.setEntry(3, 3, 1.0);

		return getError(projectionMatrix, modelviewMatrix, modelVertices, projectedVertices);
	}

	@Override
	public double[] getProjectionMatrix() {
		return toDoubleArray(projectionMatrix);
	}

	@Override
	public double[] getModelviewMatrix() {
		return toDoubleArray(modelviewMatrix);
	}
	
	private double getError(RealMatrix projectionMatrix, RealMatrix modelviewMatrix, ArrayList<Vector3D> modelVertices, ArrayList<Vector3D> projectedVertices) {
		if (modelVertices.size() != projectedVertices.size())
			throw new IllegalArgumentException("lists of vectors do not have same size");

		RealMatrix pm = projectionMatrix.multiply(modelviewMatrix);
		ArrayList<Vector3D> projectedPoints = new ArrayList<Vector3D>(modelVertices.size());
		RealVector rp = new ArrayRealVector(4);
		for (Vector3D p : modelVertices) {
			rp.setEntry(0, p.getX());
			rp.setEntry(1, p.getY());
			rp.setEntry(2, p.getZ());
			rp.setEntry(3, 1.0);
			RealVector pp = pm.operate(rp);
			pp = pp.mapDivide(pp.getEntry(3));
			projectedPoints.add(new Vector3D(pp.getEntry(0), pp.getEntry(1), 0.0));
		}
		
		double error = 0.0;
		for (int i = 0; i < projectedPoints.size(); ++i) {
			error += projectedPoints.get(i).distance(projectedVertices.get(i));
		}
		return error;
	}
	
	private double[] toDoubleArray(RealMatrix m) {
		double[] a = new double[16];
		for (int i = 0; i < 16; ++i) {
			a[i] = m.getEntry(i % 4, i / 4);
		}
		return a;
	}
}
