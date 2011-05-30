package edu.ucla.cs.m117.grad;

import edu.ucla.cs.m117.grad.ICallBack;

interface ISensorService2 {
	void registerCallback(ICallBack cb);
	void unregisterCallback(ICallBack cb);
	void stopAcc();
	void startAcc();
	void setSensitivity(int a);
	void stopService();
}

