package ch.ethz.fcl.util;

// quick and dirty. we probably better use an existing library (trove, or similar)
public final class FloatList {
	float[] array;
	int size;

	public FloatList() {
		this(0);
	}
	
	public FloatList(int initialCapacity) {
		array = new float[initialCapacity];
	}
	
	public void add(float value) {
		reserve(1);
		array[size++] = value;
	}
	
	public void addAll(float[] values) {
		reserve(values.length);
		System.arraycopy(values, 0, array, size, values.length);
		size += values.length;
	}
	
	public int size() {
		return size;
	}
	
	public void clear() {
		size = 0;
	}
	
	public float[] toArray() {
		return array;
	}
	
	private void reserve(int extra) {
		if (size + extra > array.length) {
			float[] a = new float[size + extra];
			System.arraycopy(array, 0, a, 0, size);
			array = a;
		}
	}
}
