/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.pkumap.mathtool;

import java.io.Serializable;


/**
 * Encapsulates a 3D vector. Allows chaining operations by returning a reference
 * to itself in all modification methods.
 * 
 * @author badlogicgames@gmail.com
 */
public class Vector3D implements Serializable
{
	private static final long serialVersionUID = 3840054589595372522L;

	/** the x-component of this vector **/
	public double x;
	/** the x-component of this vector **/
	public double y;
	/** the x-component of this vector **/
	public double z;

	/**
	 * Static temporary vector. Use with care! Use only when sure other code
	 * will not also use this.
	 * 
	 * @see #tmp()
	 **/
	public final static Vector3D tmp = new Vector3D();
	/**
	 * Static temporary vector. Use with care! Use only when sure other code
	 * will not also use this.
	 * 
	 * @see #tmp()
	 **/
	public final static Vector3D tmp2 = new Vector3D();
	/**
	 * Static temporary vector. Use with care! Use only when sure other code
	 * will not also use this.
	 * 
	 * @see #tmp()
	 **/
	public final static Vector3D tmp3 = new Vector3D();

	public final static Vector3D X = new Vector3D(1, 0, 0);
	public final static Vector3D Y = new Vector3D(0, 1, 0);
	public final static Vector3D Z = new Vector3D(0, 0, 1);
	public final static Vector3D Zero = new Vector3D(0, 0, 0);

	/** Constructs a vector at (0,0,0) */
	public Vector3D()
	{
	}

	/**
	 * Creates a vector with the given components
	 * 
	 * @param x
	 *            The x-component
	 * @param y
	 *            The y-component
	 * @param z
	 *            The z-component
	 */
	public Vector3D(double x, double y, double z)
	{
		this.set(x, y, z);
	}

	/**
	 * Creates a vector from the given vector
	 * 
	 * @param vector
	 *            The vector
	 */
	public Vector3D(Vector3D vector)
	{
		this.set(vector);
	}

	/**
	 * Creates a vector from the given array. The array must have at least 3
	 * elements.
	 * 
	 * @param values
	 *            The array
	 */
	public Vector3D(double[] values)
	{
		this.set(values[0], values[1], values[2]);
	}

	/**
	 * Sets the vector to the given components
	 * 
	 * @param x
	 *            The x-component
	 * @param y
	 *            The y-component
	 * @param z
	 *            The z-component
	 * @return this vector for chaining
	 */
	public Vector3D set(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	/**
	 * Sets the components of the given vector
	 * 
	 * @param vector
	 *            The vector
	 * @return This vector for chaining
	 */
	public Vector3D set(Vector3D vector)
	{
		return this.set(vector.x, vector.y, vector.z);
	}

	/**
	 * Sets the components from the array. The array must have at least 3
	 * elements
	 * 
	 * @param values
	 *            The array
	 * @return this vector for chaining
	 */
	public Vector3D set(double[] values)
	{
		return this.set(values[0], values[1], values[2]);
	}

	/** @return a copy of this vector */
	public Vector3D cpy()
	{
		return new Vector3D(this);
	}

	/**
	 * NEVER EVER SAVE THIS REFERENCE! Do not use this unless you are aware of
	 * the side-effects, e.g. other methods might call this as well.
	 * 
	 * @return a temporary copy of this vector
	 */
	public Vector3D tmp()
	{
		return tmp.set(this);
	}

	/**
	 * NEVER EVER SAVE THIS REFERENCE! Do not use this unless you are aware of
	 * the side-effects, e.g. other methods might call this as well.
	 * 
	 * @return a temporary copy of this vector
	 */
	public Vector3D tmp2()
	{
		return tmp2.set(this);
	}

	/**
	 * NEVER EVER SAVE THIS REFERENCE! Do not use this unless you are aware of
	 * the side-effects, e.g. other methods might call this as well.
	 * 
	 * @return a temporary copy of this vector
	 */
	Vector3D tmp3()
	{
		return tmp3.set(this);
	}

	/**
	 * Adds the given vector to this vector
	 * 
	 * @param vector
	 *            The other vector
	 * @return This vector for chaining
	 */
	public Vector3D add(Vector3D vector)
	{
		return this.add(vector.x, vector.y, vector.z);
	}

	/**
	 * Adds the given vector to this component
	 * 
	 * @param x
	 *            The x-component of the other vector
	 * @param y
	 *            The y-component of the other vector
	 * @param z
	 *            The z-component of the other vector
	 * @return This vector for chaining.
	 */
	public Vector3D add(double x, double y, double z)
	{
		return this.set(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Adds the given value to all three components of the vector.
	 * 
	 * @param values
	 *            The value
	 * @return This vector for chaining
	 */
	public Vector3D add(double values)
	{
		return this.set(this.x + values, this.y + values, this.z + values);
	}

	/**
	 * Subtracts the given vector from this vector
	 * 
	 * @param a_vec
	 *            The other vector
	 * @return This vector for chaining
	 */
	public Vector3D sub(Vector3D a_vec)
	{
		return this.sub(a_vec.x, a_vec.y, a_vec.z);
	}

	/**
	 * Subtracts the other vector from this vector.
	 * 
	 * @param x
	 *            The x-component of the other vector
	 * @param y
	 *            The y-component of the other vector
	 * @param z
	 *            The z-component of the other vector
	 * @return This vector for chaining
	 */
	public Vector3D sub(double x, double y, double z)
	{
		return this.set(this.x - x, this.y - y, this.z - z);
	}

	/**
	 * Subtracts the given value from all components of this vector
	 * 
	 * @param value
	 *            The value
	 * @return This vector for chaining
	 */
	public Vector3D sub(double value)
	{
		return this.set(this.x - value, this.y - value, this.z - value);
	}

	/**
	 * Multiplies all components of this vector by the given value
	 * 
	 * @param value
	 *            The value
	 * @return This vector for chaining
	 */
	public Vector3D mul(double value)
	{
		return this.set(this.x * value, this.y * value, this.z * value);
	}

	/**
	 * Divides all components of this vector by the given value
	 * 
	 * @param value
	 *            The value
	 * @return This vector for chaining
	 */
	public Vector3D div(double value)
	{
		double d = 1 / value;
		return this.set(this.x * d, this.y * d, this.z * d);
	}

	/** @return The euclidian length */
	public double len()
	{
		return (double) Math.sqrt(x * x + y * y + z * z);
	}

	/** @return The squared euclidian length */
	public double len2()
	{
		return x * x + y * y + z * z;
	}

	/**
	 * @param vector
	 *            The other vector
	 * @return Wether this and the other vector are equal
	 */
	public boolean idt(Vector3D vector)
	{
		return x == vector.x && y == vector.y && z == vector.z;
	}

	/**
	 * @param vector
	 *            The other vector
	 * @return The euclidian distance between this and the other vector
	 */
	public double dst(Vector3D vector)
	{
		double a = vector.x - x;
		double b = vector.y - y;
		double c = vector.z - z;

		a *= a;
		b *= b;
		c *= c;

		return (double) Math.sqrt(a + b + c);
	}

	/**
	 * Normalizes this vector to unit length
	 * 
	 * @return This vector for chaining
	 */
	public Vector3D nor()
	{
		double len = this.len();
		if (len == 0)
		{
			return this;
		} else
		{
			return this.div(len);
		}
	}

	/**
	 * @param vector
	 *            The other vector
	 * @return The dot product between this and the other vector
	 */
	public double dot(Vector3D vector)
	{
		return x * vector.x + y * vector.y + z * vector.z;
	}

	/**
	 * Sets this vector to the cross product between it and the other vector.
	 * 
	 * @param vector
	 *            The other vector
	 * @return This vector for chaining
	 */
	public Vector3D crs(Vector3D vector)
	{
		return this.set(y * vector.z - z * vector.y, z * vector.x - x * vector.z, x * vector.y - y * vector.x);
	}

	/**
	 * Sets this vector to the cross product between it and the other vector.
	 * 
	 * @param x
	 *            The x-component of the other vector
	 * @param y
	 *            The y-component of the other vector
	 * @param z
	 *            The z-component of the other vector
	 * @return This vector for chaining
	 */
	public Vector3D crs(double x, double y, double z)
	{
		return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}


	/** @return Wether this vector is a unit length vector */
	public boolean isUnit()
	{
		return this.len() == 1;
	}

	/** @return Wether this vector is a zero vector */
	public boolean isZero()
	{
		return x == 0 && y == 0 && z == 0;
	}

	/**
	 * Linearly interpolates between this vector and the target vector by alpha
	 * which is in the range [0,1]. The result is stored in this vector.
	 * 
	 * @param target
	 *            The target vector
	 * @param alpha
	 *            The interpolation coefficient
	 * @return This vector for chaining.
	 */
	public Vector3D lerp(Vector3D target, double alpha)
	{
		Vector3D r = this.mul(1.0f - alpha);
		r.add(target.tmp().mul(alpha));
		return r;
	}

	/**
	 * Spherically interpolates between this vector and the target vector by
	 * alpha which is in the range [0,1]. The result is stored in this vector.
	 * 
	 * @param target
	 *            The target vector
	 * @param alpha
	 *            The interpolation coefficient
	 * @return This vector for chaining.
	 */
	public Vector3D slerp(Vector3D target, double alpha)
	{
		double dot = dot(target);
		if (dot > 0.99995 || dot < 0.9995)
		{
			this.add(target.tmp().sub(this).mul(alpha));
			this.nor();
			return this;
		}

		if (dot > 1)
			dot = 1;
		if (dot < -1)
			dot = -1;

		double theta0 = (double) Math.acos(dot);
		double theta = theta0 * alpha;
		Vector3D v2 = target.tmp().sub(x * dot, y * dot, z * dot);
		v2.nor();
		return this.mul((double) Math.cos(theta)).add(v2.mul((double) Math.sin(theta))).nor();
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return x + "," + y + "," + z;
	}

	/**
	 * Returns the dot product between this and the given vector.
	 * 
	 * @param x
	 *            The x-component of the other vector
	 * @param y
	 *            The y-component of the other vector
	 * @param z
	 *            The z-component of the other vector
	 * @return The dot product
	 */
	public double dot(double x, double y, double z)
	{
		return this.x * x + this.y * y + this.z * z;
	}

	/**
	 * Returns the squared distance between this point and the given point
	 * 
	 * @param point
	 *            The other point
	 * @return The squared distance
	 */
	public double dst2(Vector3D point)
	{

		double a = point.x - x;
		double b = point.y - y;
		double c = point.z - z;

		a *= a;
		b *= b;
		c *= c;

		return a + b + c;
	}

	/**
	 * Returns the squared distance between this point and the given point
	 * 
	 * @param x
	 *            The x-component of the other point
	 * @param y
	 *            The y-component of the other point
	 * @param z
	 *            The z-component of the other point
	 * @return The squared distance
	 */
	public double dst2(double x, double y, double z)
	{
		double a = x - this.x;
		double b = y - this.y;
		double c = z - this.z;

		a *= a;
		b *= b;
		c *= c;

		return a + b + c;
	}

	public double dst(double x, double y, double z)
	{
		return (double) Math.sqrt(dst2(x, y, z));
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits((float) x);
		result = prime * result + NumberUtils.floatToIntBits((float) y);
		result = prime * result + NumberUtils.floatToIntBits((float) z);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector3D other = (Vector3D) obj;
		if (NumberUtils.floatToIntBits((float) x) != NumberUtils.floatToIntBits((float) other.x))
			return false;
		if (NumberUtils.floatToIntBits((float) y) != NumberUtils.floatToIntBits((float) other.y))
			return false;
		if (NumberUtils.floatToIntBits((float) z) != NumberUtils.floatToIntBits((float) other.z))
			return false;
		return true;
	}

	/**
	 * Compares this vector with the other vector, using the supplied epsilon
	 * for fuzzy equality testing.
	 * 
	 * @param obj
	 * @param epsilon
	 * @return whether the vectors are the same.
	 */
	public boolean epsilonEquals(Vector3D obj, double epsilon)
	{
		if (obj == null)
			return false;
		if (Math.abs(obj.x - x) > epsilon)
			return false;
		if (Math.abs(obj.y - y) > epsilon)
			return false;
		if (Math.abs(obj.z - z) > epsilon)
			return false;
		return true;
	}

	/**
	 * Compares this vector with the other vector, using the supplied epsilon
	 * for fuzzy equality testing.
	 * 
	 * @return whether the vectors are the same.
	 */
	public boolean epsilonEquals(double x, double y, double z, double epsilon)
	{
		if (Math.abs(x - this.x) > epsilon)
			return false;
		if (Math.abs(y - this.y) > epsilon)
			return false;
		if (Math.abs(z - this.z) > epsilon)
			return false;
		return true;
	}

	/**
	 * Scales the vector components by the given scalars.
	 * 
	 * @param scalarX
	 * @param scalarY
	 * @param scalarZ
	 */
	public Vector3D scale(double scalarX, double scalarY, double scalarZ)
	{
		x *= scalarX;
		y *= scalarY;
		z *= scalarZ;
		return this;
	}
}
