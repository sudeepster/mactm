// AccelerometerBuffer implementation
// - this caches the last 512 accelerometer entries
package edu.ucla.cs.m117.grad;

import edu.ucla.cs.m117.grad.RawValues;

public class AccelerometerBuffer {

	private static final int 	BUFFER_LENGTH 			= 256;
	private static RawValues[] 	data 					= new RawValues[BUFFER_LENGTH];
	private static int 			occupiedBuffer1Count 	= 0;
	private static int 			readLocation1 = 0, writeLocation1 = 0;

	// Empty Constructor
	public AccelerometerBuffer() 
	{
	}

	// FUNCTION: set (ensure that multiple threads of execution do not mess this up)
	public synchronized void set(RawValues values) 
	{
		while (occupiedBuffer1Count == data.length) 
		{
			try 
			{
				wait();
			} 
			catch (InterruptedException exception) 
			{
				exception.printStackTrace();
			}
		}

		data[writeLocation1] = values;
		++occupiedBuffer1Count;
		writeLocation1 = (writeLocation1 + 1) % data.length;
		notify();
	}

	// FUNCTION: get (ensure that multiple threads of execution do not mess this up)
	public synchronized RawValues get() 
	{
		while (occupiedBuffer1Count == 0) 
		{
			try 
			{
				wait();
			} 
			catch (InterruptedException exception) 
			{
				exception.printStackTrace();
			}
		}
		RawValues val = data[readLocation1];
		--occupiedBuffer1Count;
		readLocation1 = (readLocation1 + 1) % data.length;
		notify();
		return val;
	}

	// FUNCTION: isEmpty -> check if buffer is empty
	public boolean isEmpty() 
	{
		return (occupiedBuffer1Count == 0);
	}

	// FUNCTION: isFull -> check if buffer is full
	public boolean isFull() 
	{
		return (occupiedBuffer1Count == data.length);
	}

	// // FUNCTION: capacity -> return buffer capacity
	public int capacity() 
	{
		return (occupiedBuffer1Count);
	}	
}