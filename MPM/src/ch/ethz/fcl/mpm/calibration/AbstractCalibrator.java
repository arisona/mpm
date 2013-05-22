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
package ch.ethz.fcl.mpm.calibration;

import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;


public abstract class AbstractCalibrator implements ICalibrator {
	protected double getError(RealMatrix projectionMatrix, RealMatrix modelviewMatrix, ArrayList<Vector3D> modelVertices, ArrayList<Vector3D> projectedVertices) {
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
	
	double[] toDoubleArray(RealMatrix m) {
		double[] a = new double[16];
		for (int i = 0; i < 16; ++i) {
			a[i] = m.getEntry(i % 4, i / 4);
		}
		return a;
	}
}
