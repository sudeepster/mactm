/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:/Users/spradhan/CSM117 Project/mactm/android/mactm-activity/src/edu/ucla/cs/m117/grad/ICallBack.aidl
 */
package edu.ucla.cs.m117.grad;
public interface ICallBack extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements edu.ucla.cs.m117.grad.ICallBack
{
private static final java.lang.String DESCRIPTOR = "edu.ucla.cs.m117.grad.ICallBack";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an edu.ucla.cs.m117.grad.ICallBack interface,
 * generating a proxy if needed.
 */
public static edu.ucla.cs.m117.grad.ICallBack asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof edu.ucla.cs.m117.grad.ICallBack))) {
return ((edu.ucla.cs.m117.grad.ICallBack)iin);
}
return new edu.ucla.cs.m117.grad.ICallBack.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements edu.ucla.cs.m117.grad.ICallBack
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
}
}
}
