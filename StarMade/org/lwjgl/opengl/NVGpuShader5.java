package org.lwjgl.opengl;

import java.nio.LongBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;

public final class NVGpuShader5
{
  public static final int GL_INT64_NV = 5134;
  public static final int GL_UNSIGNED_INT64_NV = 5135;
  public static final int GL_INT8_NV = 36832;
  public static final int GL_INT8_VEC2_NV = 36833;
  public static final int GL_INT8_VEC3_NV = 36834;
  public static final int GL_INT8_VEC4_NV = 36835;
  public static final int GL_INT16_NV = 36836;
  public static final int GL_INT16_VEC2_NV = 36837;
  public static final int GL_INT16_VEC3_NV = 36838;
  public static final int GL_INT16_VEC4_NV = 36839;
  public static final int GL_INT64_VEC2_NV = 36841;
  public static final int GL_INT64_VEC3_NV = 36842;
  public static final int GL_INT64_VEC4_NV = 36843;
  public static final int GL_UNSIGNED_INT8_NV = 36844;
  public static final int GL_UNSIGNED_INT8_VEC2_NV = 36845;
  public static final int GL_UNSIGNED_INT8_VEC3_NV = 36846;
  public static final int GL_UNSIGNED_INT8_VEC4_NV = 36847;
  public static final int GL_UNSIGNED_INT16_NV = 36848;
  public static final int GL_UNSIGNED_INT16_VEC2_NV = 36849;
  public static final int GL_UNSIGNED_INT16_VEC3_NV = 36850;
  public static final int GL_UNSIGNED_INT16_VEC4_NV = 36851;
  public static final int GL_UNSIGNED_INT64_VEC2_NV = 36853;
  public static final int GL_UNSIGNED_INT64_VEC3_NV = 36854;
  public static final int GL_UNSIGNED_INT64_VEC4_NV = 36855;
  public static final int GL_FLOAT16_NV = 36856;
  public static final int GL_FLOAT16_VEC2_NV = 36857;
  public static final int GL_FLOAT16_VEC3_NV = 36858;
  public static final int GL_FLOAT16_VEC4_NV = 36859;
  public static final int GL_PATCHES = 14;
  
  public static void glUniform1i64NV(int location, long local_x)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform1i64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglUniform1i64NV(location, local_x, function_pointer);
  }
  
  static native void nglUniform1i64NV(int paramInt, long paramLong1, long paramLong2);
  
  public static void glUniform2i64NV(int location, long local_x, long local_y)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform2i64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglUniform2i64NV(location, local_x, local_y, function_pointer);
  }
  
  static native void nglUniform2i64NV(int paramInt, long paramLong1, long paramLong2, long paramLong3);
  
  public static void glUniform3i64NV(int location, long local_x, long local_y, long local_z)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform3i64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglUniform3i64NV(location, local_x, local_y, local_z, function_pointer);
  }
  
  static native void nglUniform3i64NV(int paramInt, long paramLong1, long paramLong2, long paramLong3, long paramLong4);
  
  public static void glUniform4i64NV(int location, long local_x, long local_y, long local_z, long local_w)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform4i64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglUniform4i64NV(location, local_x, local_y, local_z, local_w, function_pointer);
  }
  
  static native void nglUniform4i64NV(int paramInt, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5);
  
  public static void glUniform1NV(int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform1i64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglUniform1i64vNV(location, value.remaining(), MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglUniform1i64vNV(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static void glUniform2NV(int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform2i64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglUniform2i64vNV(location, value.remaining() >> 1, MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglUniform2i64vNV(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static void glUniform3NV(int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform3i64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglUniform3i64vNV(location, value.remaining() / 3, MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglUniform3i64vNV(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static void glUniform4NV(int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform4i64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglUniform4i64vNV(location, value.remaining() >> 2, MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglUniform4i64vNV(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static void glUniform1ui64NV(int location, long local_x)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform1ui64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglUniform1ui64NV(location, local_x, function_pointer);
  }
  
  static native void nglUniform1ui64NV(int paramInt, long paramLong1, long paramLong2);
  
  public static void glUniform2ui64NV(int location, long local_x, long local_y)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform2ui64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglUniform2ui64NV(location, local_x, local_y, function_pointer);
  }
  
  static native void nglUniform2ui64NV(int paramInt, long paramLong1, long paramLong2, long paramLong3);
  
  public static void glUniform3ui64NV(int location, long local_x, long local_y, long local_z)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform3ui64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglUniform3ui64NV(location, local_x, local_y, local_z, function_pointer);
  }
  
  static native void nglUniform3ui64NV(int paramInt, long paramLong1, long paramLong2, long paramLong3, long paramLong4);
  
  public static void glUniform4ui64NV(int location, long local_x, long local_y, long local_z, long local_w)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform4ui64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglUniform4ui64NV(location, local_x, local_y, local_z, local_w, function_pointer);
  }
  
  static native void nglUniform4ui64NV(int paramInt, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5);
  
  public static void glUniform1uNV(int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform1ui64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglUniform1ui64vNV(location, value.remaining(), MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglUniform1ui64vNV(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static void glUniform2uNV(int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform2ui64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglUniform2ui64vNV(location, value.remaining() >> 1, MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglUniform2ui64vNV(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static void glUniform3uNV(int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform3ui64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglUniform3ui64vNV(location, value.remaining() / 3, MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglUniform3ui64vNV(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static void glUniform4uNV(int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glUniform4ui64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglUniform4ui64vNV(location, value.remaining() >> 2, MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglUniform4ui64vNV(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static void glGetUniformNV(int program, int location, LongBuffer params)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glGetUniformi64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkBuffer(params, 1);
    nglGetUniformi64vNV(program, location, MemoryUtil.getAddress(params), function_pointer);
  }
  
  static native void nglGetUniformi64vNV(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static void glGetUniformuNV(int program, int location, LongBuffer params)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glGetUniformui64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkBuffer(params, 1);
    nglGetUniformui64vNV(program, location, MemoryUtil.getAddress(params), function_pointer);
  }
  
  static native void nglGetUniformui64vNV(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static void glProgramUniform1i64NV(int program, int location, long local_x)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform1i64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglProgramUniform1i64NV(program, location, local_x, function_pointer);
  }
  
  static native void nglProgramUniform1i64NV(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static void glProgramUniform2i64NV(int program, int location, long local_x, long local_y)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform2i64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglProgramUniform2i64NV(program, location, local_x, local_y, function_pointer);
  }
  
  static native void nglProgramUniform2i64NV(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3);
  
  public static void glProgramUniform3i64NV(int program, int location, long local_x, long local_y, long local_z)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform3i64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglProgramUniform3i64NV(program, location, local_x, local_y, local_z, function_pointer);
  }
  
  static native void nglProgramUniform3i64NV(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3, long paramLong4);
  
  public static void glProgramUniform4i64NV(int program, int location, long local_x, long local_y, long local_z, long local_w)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform4i64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglProgramUniform4i64NV(program, location, local_x, local_y, local_z, local_w, function_pointer);
  }
  
  static native void nglProgramUniform4i64NV(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5);
  
  public static void glProgramUniform1NV(int program, int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform1i64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglProgramUniform1i64vNV(program, location, value.remaining(), MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglProgramUniform1i64vNV(int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2);
  
  public static void glProgramUniform2NV(int program, int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform2i64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglProgramUniform2i64vNV(program, location, value.remaining() >> 1, MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglProgramUniform2i64vNV(int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2);
  
  public static void glProgramUniform3NV(int program, int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform3i64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglProgramUniform3i64vNV(program, location, value.remaining() / 3, MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglProgramUniform3i64vNV(int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2);
  
  public static void glProgramUniform4NV(int program, int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform4i64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglProgramUniform4i64vNV(program, location, value.remaining() >> 2, MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglProgramUniform4i64vNV(int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2);
  
  public static void glProgramUniform1ui64NV(int program, int location, long local_x)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform1ui64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglProgramUniform1ui64NV(program, location, local_x, function_pointer);
  }
  
  static native void nglProgramUniform1ui64NV(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static void glProgramUniform2ui64NV(int program, int location, long local_x, long local_y)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform2ui64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglProgramUniform2ui64NV(program, location, local_x, local_y, function_pointer);
  }
  
  static native void nglProgramUniform2ui64NV(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3);
  
  public static void glProgramUniform3ui64NV(int program, int location, long local_x, long local_y, long local_z)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform3ui64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglProgramUniform3ui64NV(program, location, local_x, local_y, local_z, function_pointer);
  }
  
  static native void nglProgramUniform3ui64NV(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3, long paramLong4);
  
  public static void glProgramUniform4ui64NV(int program, int location, long local_x, long local_y, long local_z, long local_w)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform4ui64NV;
    BufferChecks.checkFunctionAddress(function_pointer);
    nglProgramUniform4ui64NV(program, location, local_x, local_y, local_z, local_w, function_pointer);
  }
  
  static native void nglProgramUniform4ui64NV(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5);
  
  public static void glProgramUniform1uNV(int program, int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform1ui64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglProgramUniform1ui64vNV(program, location, value.remaining(), MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglProgramUniform1ui64vNV(int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2);
  
  public static void glProgramUniform2uNV(int program, int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform2ui64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglProgramUniform2ui64vNV(program, location, value.remaining() >> 1, MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglProgramUniform2ui64vNV(int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2);
  
  public static void glProgramUniform3uNV(int program, int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform3ui64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglProgramUniform3ui64vNV(program, location, value.remaining() / 3, MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglProgramUniform3ui64vNV(int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2);
  
  public static void glProgramUniform4uNV(int program, int location, LongBuffer value)
  {
    ContextCapabilities caps = GLContext.getCapabilities();
    long function_pointer = caps.glProgramUniform4ui64vNV;
    BufferChecks.checkFunctionAddress(function_pointer);
    BufferChecks.checkDirect(value);
    nglProgramUniform4ui64vNV(program, location, value.remaining() >> 2, MemoryUtil.getAddress(value), function_pointer);
  }
  
  static native void nglProgramUniform4ui64vNV(int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2);
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.lwjgl.opengl.NVGpuShader5
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */