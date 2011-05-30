/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:/Users/spradhan/CSM117 Project/mactm/android/mactm-activity/src/edu/ucla/cs/m117/grad/ISensorService2.aidl
 */
package edu.ucla.cs.m117.grad;
public interface ISensorService2 extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements edu.ucla.cs.m117.grad.ISensorService2
{
private static final java.lang.String DESCRIPTOR = "edu.ucla.cs.m117.grad.ISensorService2";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an edu.ucla.cs.m117.grad.ISensorService2 interface,
 * generating a proxy if needed.
 */
public static edu.ucla.cs.m117.grad.ISensorService2 asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof edu.ucla.cs.m117.grad.ISensorService2))) {
return ((edu.ucla.cs.m117.grad.ISensorService2)iin);
}
return new edu.ucla.cs.m117.grad.ISensorService2.Stub.Proxy(obj);
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
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
edu.ucla.cs.m117.grad.ICallBack _arg0;
_arg0 = edu.ucla.cs.m117.grad.ICallBack.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
edu.ucla.cs.m117.grad.ICallBack _arg0;
_arg0 = edu.ucla.cs.m117.grad.ICallBack.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_stopAcc:
{
data.enforceInterface(DESCRIPTOR);
this.stopAcc();
reply.writeNoException();
return true;
}
case TRANSACTION_startAcc:
{
data.enforceInterface(DESCRIPTOR);
this.startAcc();
reply.writeNoException();
return true;
}
case TRANSACTION_setSensitivity:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setSensitivity(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_stopService:
{
data.enforceInterface(DESCRIPTOR);
this.stopService();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements edu.ucla.cs.m117.grad.ISensorService2
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
public void registerCallback(edu.ucla.cs.m117.grad.ICallBack cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void unregisterCallback(edu.ucla.cs.m117.grad.ICallBack cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void stopAcc() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopAcc, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void startAcc() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startAcc, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setSensitivity(int a) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(a);
mRemote.transact(Stub.TRANSACTION_setSensitivity, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void stopService() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopService, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stopAcc = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_startAcc = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setSensitivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_stopService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public void registerCallback(edu.ucla.cs.m117.grad.ICallBack cb) throws android.os.RemoteException;
public void unregisterCallback(edu.ucla.cs.m117.grad.ICallBack cb) throws android.os.RemoteException;
public void stopAcc() throws android.os.RemoteException;
public void startAcc() throws android.os.RemoteException;
public void setSensitivity(int a) throws android.os.RemoteException;
public void stopService() throws android.os.RemoteException;
}
