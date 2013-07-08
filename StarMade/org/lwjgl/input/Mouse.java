package org.lwjgl.input;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.InputImplementation;

public class Mouse
{
  public static final int EVENT_SIZE = 22;
  private static boolean created;
  private static ByteBuffer buttons;
  private static int field_1821;
  private static int field_1822;
  private static int absolute_x;
  private static int absolute_y;
  private static IntBuffer coord_buffer;
  private static int field_1823;
  private static int field_1824;
  private static int dwheel;
  private static int buttonCount = -1;
  private static boolean hasWheel;
  private static Cursor currentCursor;
  private static String[] buttonName;
  private static final Map<String, Integer> buttonMap = new HashMap(16);
  private static boolean initialized;
  private static ByteBuffer readBuffer;
  private static int eventButton;
  private static boolean eventState;
  private static int event_dx;
  private static int event_dy;
  private static int event_dwheel;
  private static int event_x;
  private static int event_y;
  private static long event_nanos;
  private static int grab_x;
  private static int grab_y;
  private static int last_event_raw_x;
  private static int last_event_raw_y;
  private static final int BUFFER_SIZE = 50;
  private static boolean isGrabbed;
  private static InputImplementation implementation;
  private static final boolean emulateCursorAnimation = (LWJGLUtil.getPlatform() == 3) || (LWJGLUtil.getPlatform() == 2);
  private static boolean clipMouseCoordinatesToWindow = !getPrivilegedBoolean("org.lwjgl.input.Mouse.allowNegativeMouseCoords");
  
  public static Cursor getNativeCursor()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return currentCursor;
    }
  }
  
  public static Cursor setNativeCursor(Cursor cursor)
    throws LWJGLException
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      if ((Cursor.getCapabilities() & 0x1) == 0) {
        throw new IllegalStateException("Mouse doesn't support native cursors");
      }
      Cursor oldCursor = currentCursor;
      currentCursor = cursor;
      if (isCreated()) {
        if (currentCursor != null)
        {
          implementation.setNativeCursor(currentCursor.getHandle());
          currentCursor.setTimeout();
        }
        else
        {
          implementation.setNativeCursor(null);
        }
      }
      return oldCursor;
    }
  }
  
  public static boolean isClipMouseCoordinatesToWindow()
  {
    return clipMouseCoordinatesToWindow;
  }
  
  public static void setClipMouseCoordinatesToWindow(boolean clip)
  {
    clipMouseCoordinatesToWindow = clip;
  }
  
  public static void setCursorPosition(int new_x, int new_y)
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      if (!isCreated()) {
        throw new IllegalStateException("Mouse is not created");
      }
      field_1821 = Mouse.event_x = new_x;
      field_1822 = Mouse.event_y = new_y;
      if ((!isGrabbed()) && ((Cursor.getCapabilities() & 0x1) != 0))
      {
        implementation.setCursorPosition(field_1821, field_1822);
      }
      else
      {
        grab_x = new_x;
        grab_y = new_y;
      }
    }
  }
  
  private static void initialize()
  {
    Sys.initialize();
    buttonName = new String[16];
    for (int local_i = 0; local_i < 16; local_i++)
    {
      buttonName[local_i] = ("BUTTON" + local_i);
      buttonMap.put(buttonName[local_i], Integer.valueOf(local_i));
    }
    initialized = true;
  }
  
  private static void resetMouse()
  {
    field_1823 = Mouse.field_1824 = Mouse.dwheel = 0;
    readBuffer.position(readBuffer.limit());
  }
  
  static InputImplementation getImplementation()
  {
    return implementation;
  }
  
  private static void create(InputImplementation impl)
    throws LWJGLException
  {
    if (created) {
      return;
    }
    if (!initialized) {
      initialize();
    }
    implementation = impl;
    implementation.createMouse();
    hasWheel = implementation.hasWheel();
    created = true;
    buttonCount = implementation.getButtonCount();
    buttons = BufferUtils.createByteBuffer(buttonCount);
    coord_buffer = BufferUtils.createIntBuffer(3);
    if ((currentCursor != null) && (implementation.getNativeCursorCapabilities() != 0)) {
      setNativeCursor(currentCursor);
    }
    readBuffer = ByteBuffer.allocate(1100);
    readBuffer.limit(0);
    setGrabbed(isGrabbed);
  }
  
  public static void create()
    throws LWJGLException
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      if (!Display.isCreated()) {
        throw new IllegalStateException("Display must be created.");
      }
      create(OpenGLPackageAccess.createImplementation());
    }
  }
  
  public static boolean isCreated()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return created;
    }
  }
  
  public static void destroy()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      if (!created) {
        return;
      }
      created = false;
      buttons = null;
      coord_buffer = null;
      implementation.destroyMouse();
    }
  }
  
  public static void poll()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      if (!created) {
        throw new IllegalStateException("Mouse must be created before you can poll it");
      }
      implementation.pollMouse(coord_buffer, buttons);
      int poll_coord1 = coord_buffer.get(0);
      int poll_coord2 = coord_buffer.get(1);
      int poll_dwheel = coord_buffer.get(2);
      if (isGrabbed())
      {
        field_1823 += poll_coord1;
        field_1824 += poll_coord2;
        field_1821 += poll_coord1;
        field_1822 += poll_coord2;
        absolute_x += poll_coord1;
        absolute_y += poll_coord2;
      }
      else
      {
        field_1823 = poll_coord1 - absolute_x;
        field_1824 = poll_coord2 - absolute_y;
        absolute_x = Mouse.field_1821 = poll_coord1;
        absolute_y = Mouse.field_1822 = poll_coord2;
      }
      if (clipMouseCoordinatesToWindow)
      {
        field_1821 = Math.min(Display.getWidth() - 1, Math.max(0, field_1821));
        field_1822 = Math.min(Display.getHeight() - 1, Math.max(0, field_1822));
      }
      dwheel += poll_dwheel;
      read();
    }
  }
  
  private static void read()
  {
    readBuffer.compact();
    implementation.readMouse(readBuffer);
    readBuffer.flip();
  }
  
  public static boolean isButtonDown(int button)
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      if (!created) {
        throw new IllegalStateException("Mouse must be created before you can poll the button state");
      }
      if ((button >= buttonCount) || (button < 0)) {
        return false;
      }
      return buttons.get(button) == 1;
    }
  }
  
  public static String getButtonName(int button)
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      if ((button >= buttonName.length) || (button < 0)) {
        return null;
      }
      return buttonName[button];
    }
  }
  
  public static int getButtonIndex(String buttonName)
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      Integer ret = (Integer)buttonMap.get(buttonName);
      if (ret == null) {
        return -1;
      }
      return ret.intValue();
    }
  }
  
  public static boolean next()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      if (!created) {
        throw new IllegalStateException("Mouse must be created before you can read events");
      }
      if (readBuffer.hasRemaining())
      {
        eventButton = readBuffer.get();
        eventState = readBuffer.get() != 0;
        if (isGrabbed())
        {
          event_dx = readBuffer.getInt();
          event_dy = readBuffer.getInt();
          event_x += event_dx;
          event_y += event_dy;
          last_event_raw_x = event_x;
          last_event_raw_y = event_y;
        }
        else
        {
          int new_event_x = readBuffer.getInt();
          int new_event_y = readBuffer.getInt();
          event_dx = new_event_x - last_event_raw_x;
          event_dy = new_event_y - last_event_raw_y;
          event_x = new_event_x;
          event_y = new_event_y;
          last_event_raw_x = new_event_x;
          last_event_raw_y = new_event_y;
        }
        if (clipMouseCoordinatesToWindow)
        {
          event_x = Math.min(Display.getWidth() - 1, Math.max(0, event_x));
          event_y = Math.min(Display.getHeight() - 1, Math.max(0, event_y));
        }
        event_dwheel = readBuffer.getInt();
        event_nanos = readBuffer.getLong();
        return true;
      }
      return false;
    }
  }
  
  public static int getEventButton()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return eventButton;
    }
  }
  
  public static boolean getEventButtonState()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return eventState;
    }
  }
  
  public static int getEventDX()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return event_dx;
    }
  }
  
  public static int getEventDY()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return event_dy;
    }
  }
  
  public static int getEventX()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return event_x;
    }
  }
  
  public static int getEventY()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return event_y;
    }
  }
  
  public static int getEventDWheel()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return event_dwheel;
    }
  }
  
  public static long getEventNanoseconds()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return event_nanos;
    }
  }
  
  public static int getX()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return field_1821;
    }
  }
  
  public static int getY()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return field_1822;
    }
  }
  
  public static int getDX()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      int result = field_1823;
      field_1823 = 0;
      return result;
    }
  }
  
  public static int getDY()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      int result = field_1824;
      field_1824 = 0;
      return result;
    }
  }
  
  public static int getDWheel()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      int result = dwheel;
      dwheel = 0;
      return result;
    }
  }
  
  public static int getButtonCount()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return buttonCount;
    }
  }
  
  public static boolean hasWheel()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return hasWheel;
    }
  }
  
  public static boolean isGrabbed()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      return isGrabbed;
    }
  }
  
  public static void setGrabbed(boolean grab)
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      boolean grabbed = isGrabbed;
      isGrabbed = grab;
      if (isCreated())
      {
        if ((grab) && (!grabbed))
        {
          grab_x = field_1821;
          grab_y = field_1822;
        }
        else if ((!grab) && (grabbed) && ((Cursor.getCapabilities() & 0x1) != 0))
        {
          implementation.setCursorPosition(grab_x, grab_y);
        }
        implementation.grabMouse(grab);
        poll();
        event_x = field_1821;
        event_y = field_1822;
        last_event_raw_x = field_1821;
        last_event_raw_y = field_1822;
        resetMouse();
      }
    }
  }
  
  public static void updateCursor()
  {
    synchronized (OpenGLPackageAccess.global_lock)
    {
      if ((emulateCursorAnimation) && (currentCursor != null) && (currentCursor.hasTimedOut()) && (isInsideWindow()))
      {
        currentCursor.nextCursor();
        try
        {
          setNativeCursor(currentCursor);
        }
        catch (LWJGLException local_e)
        {
          if (LWJGLUtil.DEBUG) {
            local_e.printStackTrace();
          }
        }
      }
    }
  }
  
  static boolean getPrivilegedBoolean(String property_name)
  {
    Boolean value = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        return Boolean.valueOf(Boolean.getBoolean(this.val$property_name));
      }
    });
    return value.booleanValue();
  }
  
  public static boolean isInsideWindow()
  {
    return implementation.isInsideWindow();
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.lwjgl.input.Mouse
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */