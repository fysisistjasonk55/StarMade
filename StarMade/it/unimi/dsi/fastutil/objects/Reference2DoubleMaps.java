package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Reference2DoubleMaps
{
  public static final EmptyMap EMPTY_MAP = new EmptyMap();
  
  public static <K> Reference2DoubleMap<K> singleton(K key, double value)
  {
    return new Singleton(key, value);
  }
  
  public static <K> Reference2DoubleMap<K> singleton(K key, Double value)
  {
    return new Singleton(key, value.doubleValue());
  }
  
  public static <K> Reference2DoubleMap<K> synchronize(Reference2DoubleMap<K> local_m)
  {
    return new SynchronizedMap(local_m);
  }
  
  public static <K> Reference2DoubleMap<K> synchronize(Reference2DoubleMap<K> local_m, Object sync)
  {
    return new SynchronizedMap(local_m, sync);
  }
  
  public static <K> Reference2DoubleMap<K> unmodifiable(Reference2DoubleMap<K> local_m)
  {
    return new UnmodifiableMap(local_m);
  }
  
  public static class UnmodifiableMap<K>
    extends Reference2DoubleFunctions.UnmodifiableFunction<K>
    implements Reference2DoubleMap<K>, Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final Reference2DoubleMap<K> map;
    protected volatile transient ObjectSet<Reference2DoubleMap.Entry<K>> entries;
    protected volatile transient ReferenceSet<K> keys;
    protected volatile transient DoubleCollection values;
    
    protected UnmodifiableMap(Reference2DoubleMap<K> local_m)
    {
      super();
      this.map = local_m;
    }
    
    public int size()
    {
      return this.map.size();
    }
    
    public boolean containsKey(Object local_k)
    {
      return this.map.containsKey(local_k);
    }
    
    public boolean containsValue(double local_v)
    {
      return this.map.containsValue(local_v);
    }
    
    public double defaultReturnValue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void defaultReturnValue(double defRetValue)
    {
      throw new UnsupportedOperationException();
    }
    
    public double put(K local_k, double local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public void putAll(Map<? extends K, ? extends Double> local_m)
    {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet()
    {
      if (this.entries == null) {
        this.entries = ObjectSets.unmodifiable(this.map.reference2DoubleEntrySet());
      }
      return this.entries;
    }
    
    public ReferenceSet<K> keySet()
    {
      if (this.keys == null) {
        this.keys = ReferenceSets.unmodifiable(this.map.keySet());
      }
      return this.keys;
    }
    
    public DoubleCollection values()
    {
      if (this.values == null) {
        return DoubleCollections.unmodifiable(this.map.values());
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
    
    public boolean containsValue(Object local_ov)
    {
      return this.map.containsValue(local_ov);
    }
    
    public double removeDouble(Object local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public double getDouble(Object local_k)
    {
      return this.map.getDouble(local_k);
    }
    
    public boolean isEmpty()
    {
      return this.map.isEmpty();
    }
    
    public ObjectSet<Map.Entry<K, Double>> entrySet()
    {
      return ObjectSets.unmodifiable(this.map.entrySet());
    }
  }
  
  public static class SynchronizedMap<K>
    extends Reference2DoubleFunctions.SynchronizedFunction<K>
    implements Reference2DoubleMap<K>, Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final Reference2DoubleMap<K> map;
    protected volatile transient ObjectSet<Reference2DoubleMap.Entry<K>> entries;
    protected volatile transient ReferenceSet<K> keys;
    protected volatile transient DoubleCollection values;
    
    protected SynchronizedMap(Reference2DoubleMap<K> local_m, Object sync)
    {
      super(sync);
      this.map = local_m;
    }
    
    protected SynchronizedMap(Reference2DoubleMap<K> local_m)
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
    
    public boolean containsKey(Object local_k)
    {
      synchronized (this.sync)
      {
        return this.map.containsKey(local_k);
      }
    }
    
    public boolean containsValue(double local_v)
    {
      synchronized (this.sync)
      {
        return this.map.containsValue(local_v);
      }
    }
    
    public double defaultReturnValue()
    {
      synchronized (this.sync)
      {
        return this.map.defaultReturnValue();
      }
    }
    
    public void defaultReturnValue(double defRetValue)
    {
      synchronized (this.sync)
      {
        this.map.defaultReturnValue(defRetValue);
      }
    }
    
    public double put(K local_k, double local_v)
    {
      synchronized (this.sync)
      {
        return this.map.put(local_k, local_v);
      }
    }
    
    public void putAll(Map<? extends K, ? extends Double> local_m)
    {
      synchronized (this.sync)
      {
        this.map.putAll(local_m);
      }
    }
    
    public ObjectSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet()
    {
      if (this.entries == null) {
        this.entries = ObjectSets.synchronize(this.map.reference2DoubleEntrySet(), this.sync);
      }
      return this.entries;
    }
    
    public ReferenceSet<K> keySet()
    {
      if (this.keys == null) {
        this.keys = ReferenceSets.synchronize(this.map.keySet(), this.sync);
      }
      return this.keys;
    }
    
    public DoubleCollection values()
    {
      if (this.values == null) {
        return DoubleCollections.synchronize(this.map.values(), this.sync);
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
    
    public Double put(K local_k, Double local_v)
    {
      synchronized (this.sync)
      {
        return (Double)this.map.put(local_k, local_v);
      }
    }
    
    public boolean containsValue(Object local_ov)
    {
      synchronized (this.sync)
      {
        return this.map.containsValue(local_ov);
      }
    }
    
    public double removeDouble(Object local_k)
    {
      synchronized (this.sync)
      {
        return this.map.removeDouble(local_k);
      }
    }
    
    public double getDouble(Object local_k)
    {
      synchronized (this.sync)
      {
        return this.map.getDouble(local_k);
      }
    }
    
    public boolean isEmpty()
    {
      synchronized (this.sync)
      {
        return this.map.isEmpty();
      }
    }
    
    public ObjectSet<Map.Entry<K, Double>> entrySet()
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
  
  public static class Singleton<K>
    extends Reference2DoubleFunctions.Singleton<K>
    implements Reference2DoubleMap<K>, Serializable, Cloneable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected volatile transient ObjectSet<Reference2DoubleMap.Entry<K>> entries;
    protected volatile transient ReferenceSet<K> keys;
    protected volatile transient DoubleCollection values;
    
    protected Singleton(K key, double value)
    {
      super(value);
    }
    
    public boolean containsValue(double local_v)
    {
      return this.value == local_v;
    }
    
    public boolean containsValue(Object local_ov)
    {
      return ((Double)local_ov).doubleValue() == this.value;
    }
    
    public void putAll(Map<? extends K, ? extends Double> local_m)
    {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet()
    {
      if (this.entries == null) {
        this.entries = ObjectSets.singleton(new SingletonEntry());
      }
      return this.entries;
    }
    
    public ReferenceSet<K> keySet()
    {
      if (this.keys == null) {
        this.keys = ReferenceSets.singleton(this.key);
      }
      return this.keys;
    }
    
    public DoubleCollection values()
    {
      if (this.values == null) {
        this.values = DoubleSets.singleton(this.value);
      }
      return this.values;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public ObjectSet<Map.Entry<K, Double>> entrySet()
    {
      return reference2DoubleEntrySet();
    }
    
    public int hashCode()
    {
      return (this.key == null ? 0 : System.identityHashCode(this.key)) ^ HashCommon.double2int(this.value);
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
      implements Reference2DoubleMap.Entry<K>, Map.Entry<K, Double>
    {
      protected SingletonEntry() {}
      
      public K getKey()
      {
        return Reference2DoubleMaps.Singleton.this.key;
      }
      
      public Double getValue()
      {
        return Double.valueOf(Reference2DoubleMaps.Singleton.this.value);
      }
      
      public double getDoubleValue()
      {
        return Reference2DoubleMaps.Singleton.this.value;
      }
      
      public double setValue(double value)
      {
        throw new UnsupportedOperationException();
      }
      
      public Double setValue(Double value)
      {
        throw new UnsupportedOperationException();
      }
      
      public boolean equals(Object local_o)
      {
        if (!(local_o instanceof Map.Entry)) {
          return false;
        }
        Map.Entry<?, ?> local_e = (Map.Entry)local_o;
        return (Reference2DoubleMaps.Singleton.this.key == local_e.getKey()) && (Reference2DoubleMaps.Singleton.this.value == ((Double)local_e.getValue()).doubleValue());
      }
      
      public int hashCode()
      {
        return (Reference2DoubleMaps.Singleton.this.key == null ? 0 : System.identityHashCode(Reference2DoubleMaps.Singleton.this.key)) ^ HashCommon.double2int(Reference2DoubleMaps.Singleton.this.value);
      }
      
      public String toString()
      {
        return Reference2DoubleMaps.Singleton.this.key + "->" + Reference2DoubleMaps.Singleton.this.value;
      }
    }
  }
  
  public static class EmptyMap<K>
    extends Reference2DoubleFunctions.EmptyFunction<K>
    implements Reference2DoubleMap<K>, Serializable, Cloneable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    
    public boolean containsValue(double local_v)
    {
      return false;
    }
    
    public void putAll(Map<? extends K, ? extends Double> local_m)
    {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet()
    {
      return ObjectSets.EMPTY_SET;
    }
    
    public ReferenceSet<K> keySet()
    {
      return ReferenceSets.EMPTY_SET;
    }
    
    public DoubleCollection values()
    {
      return DoubleSets.EMPTY_SET;
    }
    
    public boolean containsValue(Object local_ov)
    {
      return false;
    }
    
    private Object readResolve()
    {
      return Reference2DoubleMaps.EMPTY_MAP;
    }
    
    public Object clone()
    {
      return Reference2DoubleMaps.EMPTY_MAP;
    }
    
    public boolean isEmpty()
    {
      return true;
    }
    
    public ObjectSet<Map.Entry<K, Double>> entrySet()
    {
      return reference2DoubleEntrySet();
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
 * Qualified Name:     it.unimi.dsi.fastutil.objects.Reference2DoubleMaps
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */