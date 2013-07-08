package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import org.lwjgl.LWJGLException;

final class WindowsDisplayPeerInfo
  extends WindowsPeerInfo
{
  final boolean egl;
  
  WindowsDisplayPeerInfo(boolean egl)
    throws LWJGLException
  {
    this.egl = egl;
    if (egl) {
      org.lwjgl.opengles.GLContext.loadOpenGLLibrary();
    } else {
      GLContext.loadOpenGLLibrary();
    }
  }
  
  void initDC(long hwnd, long hdc)
    throws LWJGLException
  {
    nInitDC(getHandle(), hwnd, hdc);
  }
  
  private static native void nInitDC(ByteBuffer paramByteBuffer, long paramLong1, long paramLong2);
  
  protected void doLockAndInitHandle()
    throws LWJGLException
  {}
  
  protected void doUnlock()
    throws LWJGLException
  {}
  
  public void destroy()
  {
    super.destroy();
    if (this.egl) {
      org.lwjgl.opengles.GLContext.unloadOpenGLLibrary();
    } else {
      GLContext.unloadOpenGLLibrary();
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.lwjgl.opengl.WindowsDisplayPeerInfo
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */