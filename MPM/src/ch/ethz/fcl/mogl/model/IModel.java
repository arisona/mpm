package ch.ethz.fcl.mogl.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Interface for all models to be rendered / interacted with. At this point, the
 * 'model' is just a marker interface, and not used by the mogl package. Later
 * on this is likely to change.
 * 
 * @author radar
 * 
 */
public interface IModel {
	Vector3D getExtentMin();

	Vector3D getExtentMax();
}
