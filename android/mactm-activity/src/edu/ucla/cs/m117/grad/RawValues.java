// RawValue implementation
package edu.ucla.cs.m117.grad;

public class RawValues 
{		
	private float[] acceleration = new float[3];
	private long timeStamp;

	// constructor
	public RawValues() 
	{
		this.acceleration[0] = 0;
		this.acceleration[1] = 0;
		this.acceleration[2] = 0;
	}

	// user-defined constructor
	public RawValues( float[] acc) 
	{
		this.timeStamp = System.currentTimeMillis();		
		System.arraycopy(acc, 0, acceleration, 0, 3);
	}	
	
	// return time stamp
	public long getTimeStamp() 
	{
		return timeStamp;
	}

	// set sample time stamp
	public void setTimeStamp(long timeStamp) 
	{
		this.timeStamp = timeStamp;
	}

	// return the buffer with acceleration values
	public float[] getAcceleration() 
	{
		return acceleration;
	}

	public void setAcceleration(float[] acceleration) 
	{
		this.acceleration = acceleration;
	}
}
