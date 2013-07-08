package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public class Object2ReferenceFunctions
{
  public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();
  
  public static <K, V> Object2ReferenceFunction<K, V> singleton(K key, V value)
  {
    return new Singleton(key, value);
  }
  
  public static <K, V> Object2ReferenceFunction<K, V> synchronize(Object2ReferenceFunction<K, V> local_f)
  {
    return new SynchronizedFunction(local_f);
  }
  
  public static <K, V> Object2ReferenceFunction<K, V> synchronize(Object2ReferenceFunction<K, V> local_f, Object sync)
  {
    return new SynchronizedFunction(local_f, sync);
  }
  
  public static <K, V> Object2ReferenceFunction<K, V> unmodifiable(Object2ReferenceFunction<K, V> local_f)
  {
    return new UnmodifiableFunction(local_f);
  }
  
  public static class UnmodifiableFunction<K, V>
    extends AbstractObject2ReferenceFunction<K, V>
    implements Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final Object2ReferenceFunction<K, V> function;
    
    protected UnmodifiableFunction(Object2ReferenceFunction<K, V> local_f)
    {
      if (local_f == null) {
        throw new NullPointerException();
      }
      this.function = local_f;
    }
    
    public int size()
    {
      return this.function.size();
    }
    
    public boolean containsKey(Object local_k)
    {
      return this.function.containsKey(local_k);
    }
    
    public V defaultReturnValue()
    {
      return this.function.defaultReturnValue();
    }
    
    public void defaultReturnValue(V defRetValue)
    {
      throw new UnsupportedOperationException();
    }
    
    public V put(K local_k, V local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public void clear()
    {
      throw new UnsupportedOperationException();
    }
    
    public String toString()
    {
      return this.function.toString();
    }
    
    public V remove(Object local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public V get(Object local_k)
    {
      return this.function.get(local_k);
    }
  }
  
  public static class SynchronizedFunction<K, V>
    extends AbstractObject2ReferenceFunction<K, V>
    implements Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final Object2ReferenceFunction<K, V> function;
    protected final Object sync;
    
    protected SynchronizedFunction(Object2ReferenceFunction<K, V> local_f, Object sync)
    {
      if (local_f == null) {
        throw new NullPointerException();
      }
      this.function = local_f;
      this.sync = sync;
    }
    
    protected SynchronizedFunction(Object2ReferenceFunction<K, V> local_f)
    {
      if (local_f == null) {
        throw new NullPointerException();
      }
      this.function = local_f;
      this.sync = this;
    }
    
    public int size()
    {
      synchronized (this.sync)
      {
        return this.function.size();
      }
    }
    
    public boolean containsKey(Object local_k)
    {
      synchronized (this.sync)
      {
        return this.function.containsKey(local_k);
      }
    }
    
    public V defaultReturnValue()
    {
      synchronized (this.sync)
      {
        return this.function.defaultReturnValue();
      }
    }
    
    public void defaultReturnValue(V defRetValue)
    {
      synchronized (this.sync)
      {
        this.function.defaultReturnValue(defRetValue);
      }
    }
    
    public V put(K local_k, V local_v)
    {
      synchronized (this.sync)
      {
        return this.function.put(local_k, local_v);
      }
    }
    
    public void clear()
    {
      synchronized (this.sync)
      {
        this.function.clear();
      }
    }
    
    public String toString()
    {
      synchronized (this.sync)
      {
        return this.function.toString();
      }
    }
    
    public V remove(Object local_k)
    {
      synchronized (this.sync)
      {
        return this.function.remove(local_k);
      }
    }
    
    public V get(Object local_k)
    {
      synchronized (this.sync)
      {
        return this.function.get(local_k);
      }
    }
  }
  
  public static class Singleton<K, V>
    extends AbstractObject2ReferenceFunction<K, V>
    implements Serializable, Cloneable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final K key;
    protected final V value;
    
    protected Singleton(K key, V value)
    {
      this.key = key;
      this.value = value;
    }
    
    public boolean containsKey(Object local_k)
    {
      return this.key == null ? false : local_k == null ? true : this.key.equals(local_k);
    }
    
    public V get(Object local_k)
    {
      if (this.key == null ? local_k == null : this.key.equals(local_k)) {
        return this.value;
      }
      return this.defRetValue;
    }
    
    public int size()
    {
      return 1;
    }
    
    public Object clone()
    {
      return this;
    }
  }
  
  public static class EmptyFunction<K, V>
    extends AbstractObject2ReferenceFunction<K, V>
    implements Serializable, Cloneable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    
    public V get(Object local_k)
    {
      return null;
    }
    
    public boolean containsKey(Object local_k)
    {
      return false;
    }
    
    public V defaultReturnValue()
    {
      return null;
    }
    
    public void defaultReturnValue(V defRetValue)
    {
      throw new UnsupportedOperationException();
    }
    
    public int size()
    {
      return 0;
    }
    
    public void clear() {}
    
    private Object readResolve()
    {
      return Object2ReferenceFunctions.EMPTY_FUNCTION;
    }
    
    public Object clone()
    {
      return Object2ReferenceFunctions.EMPTY_FUNCTION;
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.objects.Object2ReferenceFunctions
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */