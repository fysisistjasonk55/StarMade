package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Float2FloatMaps
{
  public static final EmptyMap EMPTY_MAP = new EmptyMap();
  
  public static Float2FloatMap singleton(float key, float value)
  {
    return new Singleton(key, value);
  }
  
  public static Float2FloatMap singleton(Float key, Float value)
  {
    return new Singleton(key.floatValue(), value.floatValue());
  }
  
  public static Float2FloatMap synchronize(Float2FloatMap local_m)
  {
    return new SynchronizedMap(local_m);
  }
  
  public static Float2FloatMap synchronize(Float2FloatMap local_m, Object sync)
  {
    return new SynchronizedMap(local_m, sync);
  }
  
  public static Float2FloatMap unmodifiable(Float2FloatMap local_m)
  {
    return new UnmodifiableMap(local_m);
  }
  
  public static class UnmodifiableMap
    extends Float2FloatFunctions.UnmodifiableFunction
    implements Float2FloatMap, Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final Float2FloatMap map;
    protected volatile transient ObjectSet<Float2FloatMap.Entry> entries;
    protected volatile transient FloatSet keys;
    protected volatile transient FloatCollection values;
    
    protected UnmodifiableMap(Float2FloatMap local_m)
    {
      super();
      this.map = local_m;
    }
    
    public int size()
    {
      return this.map.size();
    }
    
    public boolean containsKey(float local_k)
    {
      return this.map.containsKey(local_k);
    }
    
    public boolean containsValue(float local_v)
    {
      return this.map.containsValue(local_v);
    }
    
    public float defaultReturnValue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void defaultReturnValue(float defRetValue)
    {
      throw new UnsupportedOperationException();
    }
    
    public float put(float local_k, float local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public void putAll(Map<? extends Float, ? extends Float> local_m)
    {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Float2FloatMap.Entry> float2FloatEntrySet()
    {
      if (this.entries == null) {
        this.entries = ObjectSets.unmodifiable(this.map.float2FloatEntrySet());
      }
      return this.entries;
    }
    
    public FloatSet keySet()
    {
      if (this.keys == null) {
        this.keys = FloatSets.unmodifiable(this.map.keySet());
      }
      return this.keys;
    }
    
    public FloatCollection values()
    {
      if (this.values == null) {
        return FloatCollections.unmodifiable(this.map.values());
      }
      return this.values;
    }
    
    public void clear()
    {
      throw new UnsupportedOperationException();
    }
    
    public String toString()
    {
      return this.map.toString();
    }
    
    public Float put(Float local_k, Float local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public float remove(float local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public float get(float local_k)
    {
      return this.map.get(local_k);
    }
    
    public boolean containsKey(Object local_ok)
    {
      return this.map.containsKey(local_ok);
    }
    
    public boolean containsValue(Object local_ov)
    {
      return this.map.containsValue(local_ov);
    }
    
    public boolean isEmpty()
    {
      return this.map.isEmpty();
    }
    
    public ObjectSet<Map.Entry<Float, Float>> entrySet()
    {
      return ObjectSets.unmodifiable(this.map.entrySet());
    }
  }
  
  public static class SynchronizedMap
    extends Float2FloatFunctions.SynchronizedFunction
    implements Float2FloatMap, Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final Float2FloatMap map;
    protected volatile transient ObjectSet<Float2FloatMap.Entry> entries;
    protected volatile transient FloatSet keys;
    protected volatile transient FloatCollection values;
    
    protected SynchronizedMap(Float2FloatMap local_m, Object sync)
    {
      super(sync);
      this.map = local_m;
    }
    
    protected SynchronizedMap(Float2FloatMap local_m)
    {
      super();
      this.map = local_m;
    }
    
    public int size()
    {
      synchronized (this.sync)
      {
        return this.map.size();
      }
    }
    
    public boolean containsKey(float local_k)
    {
      synchronized (this.sync)
      {
        return this.map.containsKey(local_k);
      }
    }
    
    public boolean containsValue(float local_v)
    {
      synchronized (this.sync)
      {
        return this.map.containsValue(local_v);
      }
    }
    
    public float defaultReturnValue()
    {
      synchronized (this.sync)
      {
        return this.map.defaultReturnValue();
      }
    }
    
    public void defaultReturnValue(float defRetValue)
    {
      synchronized (this.sync)
      {
        this.map.defaultReturnValue(defRetValue);
      }
    }
    
    public float put(float local_k, float local_v)
    {
      synchronized (this.sync)
      {
        return this.map.put(local_k, local_v);
      }
    }
    
    public void putAll(Map<? extends Float, ? extends Float> local_m)
    {
      synchronized (this.sync)
      {
        this.map.putAll(local_m);
      }
    }
    
    public ObjectSet<Float2FloatMap.Entry> float2FloatEntrySet()
    {
      if (this.entries == null) {
        this.entries = ObjectSets.synchronize(this.map.float2FloatEntrySet(), this.sync);
      }
      return this.entries;
    }
    
    public FloatSet keySet()
    {
      if (this.keys == null) {
        this.keys = FloatSets.synchronize(this.map.keySet(), this.sync);
      }
      return this.keys;
    }
    
    public FloatCollection values()
    {
      if (this.values == null) {
        return FloatCollections.synchronize(this.map.values(), this.sync);
      }
      return this.values;
    }
    
    public void clear()
    {
      synchronized (this.sync)
      {
        this.map.clear();
      }
    }
    
    public String toString()
    {
      synchronized (this.sync)
      {
        return this.map.toString();
      }
    }
    
    public Float put(Float local_k, Float local_v)
    {
      synchronized (this.sync)
      {
        return (Float)this.map.put(local_k, local_v);
      }
    }
    
    public float remove(float local_k)
    {
      synchronized (this.sync)
      {
        return this.map.remove(local_k);
      }
    }
    
    public float get(float local_k)
    {
      synchronized (this.sync)
      {
        return this.map.get(local_k);
      }
    }
    
    public boolean containsKey(Object local_ok)
    {
      synchronized (this.sync)
      {
        return this.map.containsKey(local_ok);
      }
    }
    
    public boolean containsValue(Object local_ov)
    {
      synchronized (this.sync)
      {
        return this.map.containsValue(local_ov);
      }
    }
    
    public boolean isEmpty()
    {
      synchronized (this.sync)
      {
        return this.map.isEmpty();
      }
    }
    
    public ObjectSet<Map.Entry<Float, Float>> entrySet()
    {
      synchronized (this.sync)
      {
        return this.map.entrySet();
      }
    }
    
    public int hashCode()
    {
      synchronized (this.sync)
      {
        return this.map.hashCode();
      }
    }
    
    public boolean equals(Object local_o)
    {
      synchronized (this.sync)
      {
        return this.map.equals(local_o);
      }
    }
  }
  
  public static class Singleton
    extends Float2FloatFunctions.Singleton
    implements Float2FloatMap, Serializable, Cloneable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected volatile transient ObjectSet<Float2FloatMap.Entry> entries;
    protected volatile transient FloatSet keys;
    protected volatile transient FloatCollection values;
    
    protected Singleton(float key, float value)
    {
      super(value);
    }
    
    public boolean containsValue(float local_v)
    {
      return this.value == local_v;
    }
    
    public boolean containsValue(Object local_ov)
    {
      return ((Float)local_ov).floatValue() == this.value;
    }
    
    public void putAll(Map<? extends Float, ? extends Float> local_m)
    {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Float2FloatMap.Entry> float2FloatEntrySet()
    {
      if (this.entries == null) {
        this.entries = ObjectSets.singleton(new SingletonEntry());
      }
      return this.entries;
    }
    
    public FloatSet keySet()
    {
      if (this.keys == null) {
        this.keys = FloatSets.singleton(this.key);
      }
      return this.keys;
    }
    
    public FloatCollection values()
    {
      if (this.values == null) {
        this.values = FloatSets.singleton(this.value);
      }
      return this.values;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public ObjectSet<Map.Entry<Float, Float>> entrySet()
    {
      return float2FloatEntrySet();
    }
    
    public int hashCode()
    {
      return HashCommon.float2int(this.key) ^ HashCommon.float2int(this.value);
    }
    
    public boolean equals(Object local_o)
    {
      if (local_o == this) {
        return true;
      }
      if (!(local_o instanceof Map)) {
        return false;
      }
      Map<?, ?> local_m = (Map)local_o;
      if (local_m.size() != 1) {
        return false;
      }
      return ((Map.Entry)entrySet().iterator().next()).equals(local_m.entrySet().iterator().next());
    }
    
    public String toString()
    {
      return "{" + this.key + "=>" + this.value + "}";
    }
    
    protected class SingletonEntry
      implements Float2FloatMap.Entry, Map.Entry<Float, Float>
    {
      protected SingletonEntry() {}
      
      public Float getKey()
      {
        return Float.valueOf(Float2FloatMaps.Singleton.this.key);
      }
      
      public Float getValue()
      {
        return Float.valueOf(Float2FloatMaps.Singleton.this.value);
      }
      
      public float getFloatKey()
      {
        return Float2FloatMaps.Singleton.this.key;
      }
      
      public float getFloatValue()
      {
        return Float2FloatMaps.Singleton.this.value;
      }
      
      public float setValue(float value)
      {
        throw new UnsupportedOperationException();
      }
      
      public Float setValue(Float value)
      {
        throw new UnsupportedOperationException();
      }
      
      public boolean equals(Object local_o)
      {
        if (!(local_o instanceof Map.Entry)) {
          return false;
        }
        Map.Entry<?, ?> local_e = (Map.Entry)local_o;
        return (Float2FloatMaps.Singleton.this.key == ((Float)local_e.getKey()).floatValue()) && (Float2FloatMaps.Singleton.this.value == ((Float)local_e.getValue()).floatValue());
      }
      
      public int hashCode()
      {
        return HashCommon.float2int(Float2FloatMaps.Singleton.this.key) ^ HashCommon.float2int(Float2FloatMaps.Singleton.this.value);
      }
      
      public String toString()
      {
        return Float2FloatMaps.Singleton.this.key + "->" + Float2FloatMaps.Singleton.this.value;
      }
    }
  }
  
  public static class EmptyMap
    extends Float2FloatFunctions.EmptyFunction
    implements Float2FloatMap, Serializable, Cloneable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    
    public boolean containsValue(float local_v)
    {
      return false;
    }
    
    public void putAll(Map<? extends Float, ? extends Float> local_m)
    {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Float2FloatMap.Entry> float2FloatEntrySet()
    {
      return ObjectSets.EMPTY_SET;
    }
    
    public FloatSet keySet()
    {
      return FloatSets.EMPTY_SET;
    }
    
    public FloatCollection values()
    {
      return FloatSets.EMPTY_SET;
    }
    
    public boolean containsValue(Object local_ov)
    {
      return false;
    }
    
    private Object readResolve()
    {
      return Float2FloatMaps.EMPTY_MAP;
    }
    
    public Object clone()
    {
      return Float2FloatMaps.EMPTY_MAP;
    }
    
    public boolean isEmpty()
    {
      return true;
    }
    
    public ObjectSet<Map.Entry<Float, Float>> entrySet()
    {
      return float2FloatEntrySet();
    }
    
    public int hashCode()
    {
      return 0;
    }
    
    public boolean equals(Object local_o)
    {
      if (!(local_o instanceof Map)) {
        return false;
      }
      return ((Map)local_o).isEmpty();
    }
    
    public String toString()
    {
      return "{}";
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.floats.Float2FloatMaps
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */