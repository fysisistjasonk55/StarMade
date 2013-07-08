package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

public class Char2IntSortedMaps
{
  public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();
  
  public static Comparator<? super Map.Entry<Character, ?>> entryComparator(CharComparator comparator)
  {
    new Comparator()
    {
      public int compare(Map.Entry<Character, ?> local_x, Map.Entry<Character, ?> local_y)
      {
        return this.val$comparator.compare(local_x.getKey(), local_y.getKey());
      }
    };
  }
  
  public static Char2IntSortedMap singleton(Character key, Integer value)
  {
    return new Singleton(key.charValue(), value.intValue());
  }
  
  public static Char2IntSortedMap singleton(Character key, Integer value, CharComparator comparator)
  {
    return new Singleton(key.charValue(), value.intValue(), comparator);
  }
  
  public static Char2IntSortedMap singleton(char key, int value)
  {
    return new Singleton(key, value);
  }
  
  public static Char2IntSortedMap singleton(char key, int value, CharComparator comparator)
  {
    return new Singleton(key, value, comparator);
  }
  
  public static Char2IntSortedMap synchronize(Char2IntSortedMap local_m)
  {
    return new SynchronizedSortedMap(local_m);
  }
  
  public static Char2IntSortedMap synchronize(Char2IntSortedMap local_m, Object sync)
  {
    return new SynchronizedSortedMap(local_m, sync);
  }
  
  public static Char2IntSortedMap unmodifiable(Char2IntSortedMap local_m)
  {
    return new UnmodifiableSortedMap(local_m);
  }
  
  public static class UnmodifiableSortedMap
    extends Char2IntMaps.UnmodifiableMap
    implements Char2IntSortedMap, Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final Char2IntSortedMap sortedMap;
    
    protected UnmodifiableSortedMap(Char2IntSortedMap local_m)
    {
      super();
      this.sortedMap = local_m;
    }
    
    public CharComparator comparator()
    {
      return this.sortedMap.comparator();
    }
    
    public ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet()
    {
      if (this.entries == null) {
        this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2IntEntrySet());
      }
      return (ObjectSortedSet)this.entries;
    }
    
    public ObjectSortedSet<Map.Entry<Character, Integer>> entrySet()
    {
      return char2IntEntrySet();
    }
    
    public CharSortedSet keySet()
    {
      if (this.keys == null) {
        this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
      }
      return (CharSortedSet)this.keys;
    }
    
    public Char2IntSortedMap subMap(char from, char local_to)
    {
      return new UnmodifiableSortedMap(this.sortedMap.subMap(from, local_to));
    }
    
    public Char2IntSortedMap headMap(char local_to)
    {
      return new UnmodifiableSortedMap(this.sortedMap.headMap(local_to));
    }
    
    public Char2IntSortedMap tailMap(char from)
    {
      return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
    }
    
    public char firstCharKey()
    {
      return this.sortedMap.firstCharKey();
    }
    
    public char lastCharKey()
    {
      return this.sortedMap.lastCharKey();
    }
    
    public Character firstKey()
    {
      return (Character)this.sortedMap.firstKey();
    }
    
    public Character lastKey()
    {
      return (Character)this.sortedMap.lastKey();
    }
    
    public Char2IntSortedMap subMap(Character from, Character local_to)
    {
      return new UnmodifiableSortedMap(this.sortedMap.subMap(from, local_to));
    }
    
    public Char2IntSortedMap headMap(Character local_to)
    {
      return new UnmodifiableSortedMap(this.sortedMap.headMap(local_to));
    }
    
    public Char2IntSortedMap tailMap(Character from)
    {
      return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
    }
  }
  
  public static class SynchronizedSortedMap
    extends Char2IntMaps.SynchronizedMap
    implements Char2IntSortedMap, Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final Char2IntSortedMap sortedMap;
    
    protected SynchronizedSortedMap(Char2IntSortedMap local_m, Object sync)
    {
      super(sync);
      this.sortedMap = local_m;
    }
    
    protected SynchronizedSortedMap(Char2IntSortedMap local_m)
    {
      super();
      this.sortedMap = local_m;
    }
    
    public CharComparator comparator()
    {
      synchronized (this.sync)
      {
        return this.sortedMap.comparator();
      }
    }
    
    public ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet()
    {
      if (this.entries == null) {
        this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2IntEntrySet(), this.sync);
      }
      return (ObjectSortedSet)this.entries;
    }
    
    public ObjectSortedSet<Map.Entry<Character, Integer>> entrySet()
    {
      return char2IntEntrySet();
    }
    
    public CharSortedSet keySet()
    {
      if (this.keys == null) {
        this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
      }
      return (CharSortedSet)this.keys;
    }
    
    public Char2IntSortedMap subMap(char from, char local_to)
    {
      return new SynchronizedSortedMap(this.sortedMap.subMap(from, local_to), this.sync);
    }
    
    public Char2IntSortedMap headMap(char local_to)
    {
      return new SynchronizedSortedMap(this.sortedMap.headMap(local_to), this.sync);
    }
    
    public Char2IntSortedMap tailMap(char from)
    {
      return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
    }
    
    public char firstCharKey()
    {
      synchronized (this.sync)
      {
        return this.sortedMap.firstCharKey();
      }
    }
    
    public char lastCharKey()
    {
      synchronized (this.sync)
      {
        return this.sortedMap.lastCharKey();
      }
    }
    
    public Character firstKey()
    {
      synchronized (this.sync)
      {
        return (Character)this.sortedMap.firstKey();
      }
    }
    
    public Character lastKey()
    {
      synchronized (this.sync)
      {
        return (Character)this.sortedMap.lastKey();
      }
    }
    
    public Char2IntSortedMap subMap(Character from, Character local_to)
    {
      return new SynchronizedSortedMap(this.sortedMap.subMap(from, local_to), this.sync);
    }
    
    public Char2IntSortedMap headMap(Character local_to)
    {
      return new SynchronizedSortedMap(this.sortedMap.headMap(local_to), this.sync);
    }
    
    public Char2IntSortedMap tailMap(Character from)
    {
      return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
    }
  }
  
  public static class Singleton
    extends Char2IntMaps.Singleton
    implements Char2IntSortedMap, Serializable, Cloneable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final CharComparator comparator;
    
    protected Singleton(char key, int value, CharComparator comparator)
    {
      super(value);
      this.comparator = comparator;
    }
    
    protected Singleton(char key, int value)
    {
      this(key, value, null);
    }
    
    final int compare(char local_k1, char local_k2)
    {
      return this.comparator == null ? 1 : local_k1 == local_k2 ? 0 : local_k1 < local_k2 ? -1 : this.comparator.compare(local_k1, local_k2);
    }
    
    public CharComparator comparator()
    {
      return this.comparator;
    }
    
    public ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet()
    {
      if (this.entries == null) {
        this.entries = ObjectSortedSets.singleton(new Char2IntMaps.Singleton.SingletonEntry(this), Char2IntSortedMaps.entryComparator(this.comparator));
      }
      return (ObjectSortedSet)this.entries;
    }
    
    public ObjectSortedSet<Map.Entry<Character, Integer>> entrySet()
    {
      return char2IntEntrySet();
    }
    
    public CharSortedSet keySet()
    {
      if (this.keys == null) {
        this.keys = CharSortedSets.singleton(this.key, this.comparator);
      }
      return (CharSortedSet)this.keys;
    }
    
    public Char2IntSortedMap subMap(char from, char local_to)
    {
      if ((compare(from, this.key) <= 0) && (compare(this.key, local_to) < 0)) {
        return this;
      }
      return Char2IntSortedMaps.EMPTY_MAP;
    }
    
    public Char2IntSortedMap headMap(char local_to)
    {
      if (compare(this.key, local_to) < 0) {
        return this;
      }
      return Char2IntSortedMaps.EMPTY_MAP;
    }
    
    public Char2IntSortedMap tailMap(char from)
    {
      if (compare(from, this.key) <= 0) {
        return this;
      }
      return Char2IntSortedMaps.EMPTY_MAP;
    }
    
    public char firstCharKey()
    {
      return this.key;
    }
    
    public char lastCharKey()
    {
      return this.key;
    }
    
    public Char2IntSortedMap headMap(Character oto)
    {
      return headMap(oto.charValue());
    }
    
    public Char2IntSortedMap tailMap(Character ofrom)
    {
      return tailMap(ofrom.charValue());
    }
    
    public Char2IntSortedMap subMap(Character ofrom, Character oto)
    {
      return subMap(ofrom.charValue(), oto.charValue());
    }
    
    public Character firstKey()
    {
      return Character.valueOf(firstCharKey());
    }
    
    public Character lastKey()
    {
      return Character.valueOf(lastCharKey());
    }
  }
  
  public static class EmptySortedMap
    extends Char2IntMaps.EmptyMap
    implements Char2IntSortedMap, Serializable, Cloneable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    
    public CharComparator comparator()
    {
      return null;
    }
    
    public ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet()
    {
      return ObjectSortedSets.EMPTY_SET;
    }
    
    public ObjectSortedSet<Map.Entry<Character, Integer>> entrySet()
    {
      return ObjectSortedSets.EMPTY_SET;
    }
    
    public CharSortedSet keySet()
    {
      return CharSortedSets.EMPTY_SET;
    }
    
    public Char2IntSortedMap subMap(char from, char local_to)
    {
      return Char2IntSortedMaps.EMPTY_MAP;
    }
    
    public Char2IntSortedMap headMap(char local_to)
    {
      return Char2IntSortedMaps.EMPTY_MAP;
    }
    
    public Char2IntSortedMap tailMap(char from)
    {
      return Char2IntSortedMaps.EMPTY_MAP;
    }
    
    public char firstCharKey()
    {
      throw new NoSuchElementException();
    }
    
    public char lastCharKey()
    {
      throw new NoSuchElementException();
    }
    
    public Char2IntSortedMap headMap(Character oto)
    {
      return headMap(oto.charValue());
    }
    
    public Char2IntSortedMap tailMap(Character ofrom)
    {
      return tailMap(ofrom.charValue());
    }
    
    public Char2IntSortedMap subMap(Character ofrom, Character oto)
    {
      return subMap(ofrom.charValue(), oto.charValue());
    }
    
    public Character firstKey()
    {
      return Character.valueOf(firstCharKey());
    }
    
    public Character lastKey()
    {
      return Character.valueOf(lastCharKey());
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.chars.Char2IntSortedMaps
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */