package ch.ethz.fcl.mogl.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public final class ModelUtils {
	// @formatter:off
	public static final float[] UNIT_CUBE_FACES = {
		1, 1, 0, 0, 1, 0, 1, 1, 1,
		0, 1, 0, 0, 1, 1, 1, 1, 1,

		0, 1, 0, 0, 0, 0, 0, 0, 1,
		0, 1, 0, 0, 0, 1, 0, 1, 1,

		0, 0, 0, 1, 0, 0, 1, 0, 1,
		0, 0, 0, 1, 0, 1, 0, 0, 1,
		
		1, 0, 0, 1, 1, 0, 1, 1, 1,
		1, 0, 0, 1, 1, 1, 1, 0, 1,
		
		0, 0, 1, 1, 0, 1, 1, 1, 1,
		0, 0, 1, 1, 1, 1, 0, 1, 1,
	};
	// @formatter:on

	public static float[] calculateNormals(float[] faces) {
		float[] normals = new float[faces.length];
		for (int i = 0; i < faces.length; i += 9) {
			Vector3D n = null;
			try {
				Vector3D a = new Vector3D(faces[i + 3] - faces[i + 0], faces[i + 4] - faces[i + 1], faces[i + 5] - faces[i + 2]);
				Vector3D b = new Vector3D(faces[i + 6] - faces[i + 0], faces[i + 7] - faces[i + 1], faces[i + 8] - faces[i + 2]);
				n = Vector3D.crossProduct(a, b).normalize();
			} catch (Exception e) {
				n = new Vector3D(0, 0, 1);
			}
			normals[i + 0] = normals[i + 3] = normals[i + 6] = (float) n.getX();
			normals[i + 1] = normals[i + 4] = normals[i + 7] = (float) n.getY();
			normals[i + 2] = normals[i + 5] = normals[i + 8] = (float) n.getZ();
		}
		return normals;
	}

	public static void addCube(float[] destination, int index, float tx, float ty, float sx, float sy, float sz) {
		int o = index * UNIT_CUBE_FACES.length;
		int i = 0;
		for (int j = 0; j < UNIT_CUBE_FACES.length; j += 3) {
			destination[o + i++] = (UNIT_CUBE_FACES[j] * sx) + tx;
			destination[o + i++] = (UNIT_CUBE_FACES[j + 1] * sy) + ty;
			destination[o + i++] = (UNIT_CUBE_FACES[j + 2] * sz);
		}
	}

}
