package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Long2FloatRBTreeMap
  extends AbstractLong2FloatSortedMap
  implements Serializable, Cloneable
{
  protected transient Entry tree;
  protected int count;
  protected transient Entry firstEntry;
  protected transient Entry lastEntry;
  protected volatile transient ObjectSortedSet<Long2FloatMap.Entry> entries;
  protected volatile transient LongSortedSet keys;
  protected volatile transient FloatCollection values;
  protected transient boolean modified;
  protected Comparator<? super Long> storedComparator;
  protected transient LongComparator actualComparator;
  public static final long serialVersionUID = -7046029254386353129L;
  private static final boolean ASSERTS = false;
  private transient boolean[] dirPath;
  private transient Entry[] nodePath;
  
  public Long2FloatRBTreeMap()
  {
    allocatePaths();
    this.tree = null;
    this.count = 0;
  }
  
  private void setActualComparator()
  {
    if ((this.storedComparator == null) || ((this.storedComparator instanceof LongComparator))) {
      this.actualComparator = ((LongComparator)this.storedComparator);
    } else {
      this.actualComparator = new LongComparator()
      {
        public int compare(long local_k1, long local_k2)
        {
          return Long2FloatRBTreeMap.this.storedComparator.compare(Long.valueOf(local_k1), Long.valueOf(local_k2));
        }
        
        public int compare(Long ok1, Long ok2)
        {
          return Long2FloatRBTreeMap.this.storedComparator.compare(ok1, ok2);
        }
      };
    }
  }
  
  public Long2FloatRBTreeMap(Comparator<? super Long> local_c)
  {
    this();
    this.storedComparator = local_c;
    setActualComparator();
  }
  
  public Long2FloatRBTreeMap(Map<? extends Long, ? extends Float> local_m)
  {
    this();
    putAll(local_m);
  }
  
  public Long2FloatRBTreeMap(SortedMap<Long, Float> local_m)
  {
    this(local_m.comparator());
    putAll(local_m);
  }
  
  public Long2FloatRBTreeMap(Long2FloatMap local_m)
  {
    this();
    putAll(local_m);
  }
  
  public Long2FloatRBTreeMap(Long2FloatSortedMap local_m)
  {
    this(local_m.comparator());
    putAll(local_m);
  }
  
  public Long2FloatRBTreeMap(long[] local_k, float[] local_v, Comparator<? super Long> local_c)
  {
    this(local_c);
    if (local_k.length != local_v.length) {
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + local_k.length + " and " + local_v.length + ")");
    }
    for (int local_i = 0; local_i < local_k.length; local_i++) {
      put(local_k[local_i], local_v[local_i]);
    }
  }
  
  public Long2FloatRBTreeMap(long[] local_k, float[] local_v)
  {
    this(local_k, local_v, null);
  }
  
  final int compare(long local_k1, long local_k2)
  {
    return this.actualComparator == null ? 1 : local_k1 == local_k2 ? 0 : local_k1 < local_k2 ? -1 : this.actualComparator.compare(local_k1, local_k2);
  }
  
  final Entry findKey(long local_k)
  {
    int cmp;
    for (Entry local_e = this.tree; (local_e != null) && ((cmp = compare(local_k, local_e.key)) != 0); local_e = cmp < 0 ? local_e.left() : local_e.right()) {}
    return local_e;
  }
  
  final Entry locateKey(long local_k)
  {
    Entry local_e = this.tree;
    Entry last = this.tree;
    int cmp = 0;
    while ((local_e != null) && ((cmp = compare(local_k, local_e.key)) != 0))
    {
      last = local_e;
      local_e = cmp < 0 ? local_e.left() : local_e.right();
    }
    return cmp == 0 ? local_e : last;
  }
  
  private void allocatePaths()
  {
    this.dirPath = new boolean[64];
    this.nodePath = new Entry[64];
  }
  
  public float put(long local_k, float local_v)
  {
    this.modified = false;
    int maxDepth = 0;
    if (this.tree == null)
    {
      this.count += 1;
      this.tree = (this.lastEntry = this.firstEntry = new Entry(local_k, local_v));
    }
    else
    {
      Entry local_p = this.tree;
      int local_i = 0;
      for (;;)
      {
        int cmp;
        if ((cmp = compare(local_k, local_p.key)) == 0)
        {
          float oldValue = local_p.value;
          local_p.value = local_v;
          while (local_i-- != 0) {
            this.nodePath[local_i] = null;
          }
          return oldValue;
        }
        this.nodePath[local_i] = local_p;
        if ((this.dirPath[(local_i++)] = cmp > 0 ? 1 : 0) != 0)
        {
          if (local_p.succ())
          {
            this.count += 1;
            Entry local_e = new Entry(local_k, local_v);
            if (local_p.right == null) {
              this.lastEntry = local_e;
            }
            local_e.left = local_p;
            local_e.right = local_p.right;
            local_p.right(local_e);
            break;
          }
          local_p = local_p.right;
        }
        else
        {
          if (local_p.pred())
          {
            this.count += 1;
            Entry local_e = new Entry(local_k, local_v);
            if (local_p.left == null) {
              this.firstEntry = local_e;
            }
            local_e.right = local_p;
            local_e.left = local_p.left;
            local_p.left(local_e);
            break;
          }
          local_p = local_p.left;
        }
      }
      Entry local_e;
      this.modified = true;
      maxDepth = local_i--;
      while ((local_i > 0) && (!this.nodePath[local_i].black())) {
        if (this.dirPath[(local_i - 1)] == 0)
        {
          Entry oldValue = this.nodePath[(local_i - 1)].right;
          if ((!this.nodePath[(local_i - 1)].succ()) && (!oldValue.black()))
          {
            this.nodePath[local_i].black(true);
            oldValue.black(true);
            this.nodePath[(local_i - 1)].black(false);
            local_i -= 2;
          }
          else
          {
            if (this.dirPath[local_i] == 0)
            {
              oldValue = this.nodePath[local_i];
            }
            else
            {
              Entry local_x = this.nodePath[local_i];
              oldValue = local_x.right;
              local_x.right = oldValue.left;
              oldValue.left = local_x;
              this.nodePath[(local_i - 1)].left = oldValue;
              if (oldValue.pred())
              {
                oldValue.pred(false);
                local_x.succ(oldValue);
              }
            }
            Entry local_x = this.nodePath[(local_i - 1)];
            local_x.black(false);
            oldValue.black(true);
            local_x.left = oldValue.right;
            oldValue.right = local_x;
            if (local_i < 2) {
              this.tree = oldValue;
            } else if (this.dirPath[(local_i - 2)] != 0) {
              this.nodePath[(local_i - 2)].right = oldValue;
            } else {
              this.nodePath[(local_i - 2)].left = oldValue;
            }
            if (!oldValue.succ()) {
              break;
            }
            oldValue.succ(false);
            local_x.pred(oldValue);
            break;
          }
        }
        else
        {
          Entry oldValue = this.nodePath[(local_i - 1)].left;
          if ((!this.nodePath[(local_i - 1)].pred()) && (!oldValue.black()))
          {
            this.nodePath[local_i].black(true);
            oldValue.black(true);
            this.nodePath[(local_i - 1)].black(false);
            local_i -= 2;
          }
          else
          {
            if (this.dirPath[local_i] != 0)
            {
              oldValue = this.nodePath[local_i];
            }
            else
            {
              Entry local_x = this.nodePath[local_i];
              oldValue = local_x.left;
              local_x.left = oldValue.right;
              oldValue.right = local_x;
              this.nodePath[(local_i - 1)].right = oldValue;
              if (oldValue.succ())
              {
                oldValue.succ(false);
                local_x.pred(oldValue);
              }
            }
            Entry local_x = this.nodePath[(local_i - 1)];
            local_x.black(false);
            oldValue.black(true);
            local_x.right = oldValue.left;
            oldValue.left = local_x;
            if (local_i < 2) {
              this.tree = oldValue;
            } else if (this.dirPath[(local_i - 2)] != 0) {
              this.nodePath[(local_i - 2)].right = oldValue;
            } else {
              this.nodePath[(local_i - 2)].left = oldValue;
            }
            if (!oldValue.pred()) {
              break;
            }
            oldValue.pred(false);
            local_x.succ(oldValue);
            break;
          }
        }
      }
    }
    this.tree.black(true);
    while (maxDepth-- != 0) {
      this.nodePath[maxDepth] = null;
    }
    return this.defRetValue;
  }
  
  public float remove(long local_k)
  {
    this.modified = false;
    if (this.tree == null) {
      return this.defRetValue;
    }
    Entry local_p = this.tree;
    int local_i = 0;
    long local_kk = local_k;
    int cmp;
    while ((cmp = compare(local_kk, local_p.key)) != 0)
    {
      this.dirPath[local_i] = (cmp > 0 ? 1 : false);
      this.nodePath[local_i] = local_p;
      if (this.dirPath[(local_i++)] != 0)
      {
        if ((local_p = local_p.right()) == null)
        {
          while (local_i-- != 0) {
            this.nodePath[local_i] = null;
          }
          return this.defRetValue;
        }
      }
      else if ((local_p = local_p.left()) == null)
      {
        while (local_i-- != 0) {
          this.nodePath[local_i] = null;
        }
        return this.defRetValue;
      }
    }
    if (local_p.left == null) {
      this.firstEntry = local_p.next();
    }
    if (local_p.right == null) {
      this.lastEntry = local_p.prev();
    }
    if (local_p.succ())
    {
      if (local_p.pred())
      {
        if (local_i == 0) {
          this.tree = local_p.left;
        } else if (this.dirPath[(local_i - 1)] != 0) {
          this.nodePath[(local_i - 1)].succ(local_p.right);
        } else {
          this.nodePath[(local_i - 1)].pred(local_p.left);
        }
      }
      else
      {
        local_p.prev().right = local_p.right;
        if (local_i == 0) {
          this.tree = local_p.left;
        } else if (this.dirPath[(local_i - 1)] != 0) {
          this.nodePath[(local_i - 1)].right = local_p.left;
        } else {
          this.nodePath[(local_i - 1)].left = local_p.left;
        }
      }
    }
    else
    {
      Entry local_r = local_p.right;
      if (local_r.pred())
      {
        local_r.left = local_p.left;
        local_r.pred(local_p.pred());
        if (!local_r.pred()) {
          local_r.prev().right = local_r;
        }
        if (local_i == 0) {
          this.tree = local_r;
        } else if (this.dirPath[(local_i - 1)] != 0) {
          this.nodePath[(local_i - 1)].right = local_r;
        } else {
          this.nodePath[(local_i - 1)].left = local_r;
        }
        boolean color = local_r.black();
        local_r.black(local_p.black());
        local_p.black(color);
        this.dirPath[local_i] = true;
        this.nodePath[(local_i++)] = local_r;
      }
      else
      {
        int local_j = local_i++;
        Entry local_s;
        for (;;)
        {
          this.dirPath[local_i] = false;
          this.nodePath[(local_i++)] = local_r;
          local_s = local_r.left;
          if (local_s.pred()) {
            break;
          }
          local_r = local_s;
        }
        this.dirPath[local_j] = true;
        this.nodePath[local_j] = local_s;
        if (local_s.succ()) {
          local_r.pred(local_s);
        } else {
          local_r.left = local_s.right;
        }
        local_s.left = local_p.left;
        if (!local_p.pred())
        {
          local_p.prev().right = local_s;
          local_s.pred(false);
        }
        local_s.right(local_p.right);
        boolean color = local_s.black();
        local_s.black(local_p.black());
        local_p.black(color);
        if (local_j == 0) {
          this.tree = local_s;
        } else if (this.dirPath[(local_j - 1)] != 0) {
          this.nodePath[(local_j - 1)].right = local_s;
        } else {
          this.nodePath[(local_j - 1)].left = local_s;
        }
      }
    }
    int color = local_i;
    if (local_p.black())
    {
      while (local_i > 0)
      {
        if (((this.dirPath[(local_i - 1)] != 0) && (!this.nodePath[(local_i - 1)].succ())) || ((this.dirPath[(local_i - 1)] == 0) && (!this.nodePath[(local_i - 1)].pred())))
        {
          Entry local_r = this.dirPath[(local_i - 1)] != 0 ? this.nodePath[(local_i - 1)].right : this.nodePath[(local_i - 1)].left;
          if (!local_r.black())
          {
            local_r.black(true);
            break;
          }
        }
        if (this.dirPath[(local_i - 1)] == 0)
        {
          Entry local_r = this.nodePath[(local_i - 1)].right;
          if (!local_r.black())
          {
            local_r.black(true);
            this.nodePath[(local_i - 1)].black(false);
            this.nodePath[(local_i - 1)].right = local_r.left;
            local_r.left = this.nodePath[(local_i - 1)];
            if (local_i < 2) {
              this.tree = local_r;
            } else if (this.dirPath[(local_i - 2)] != 0) {
              this.nodePath[(local_i - 2)].right = local_r;
            } else {
              this.nodePath[(local_i - 2)].left = local_r;
            }
            this.nodePath[local_i] = this.nodePath[(local_i - 1)];
            this.dirPath[local_i] = false;
            this.nodePath[(local_i - 1)] = local_r;
            if (color == local_i++) {
              color++;
            }
            local_r = this.nodePath[(local_i - 1)].right;
          }
          if (((local_r.pred()) || (local_r.left.black())) && ((local_r.succ()) || (local_r.right.black())))
          {
            local_r.black(false);
          }
          else
          {
            if ((local_r.succ()) || (local_r.right.black()))
            {
              Entry local_s = local_r.left;
              local_s.black(true);
              local_r.black(false);
              local_r.left = local_s.right;
              local_s.right = local_r;
              local_r = this.nodePath[(local_i - 1)].right = local_s;
              if (local_r.succ())
              {
                local_r.succ(false);
                local_r.right.pred(local_r);
              }
            }
            local_r.black(this.nodePath[(local_i - 1)].black());
            this.nodePath[(local_i - 1)].black(true);
            local_r.right.black(true);
            this.nodePath[(local_i - 1)].right = local_r.left;
            local_r.left = this.nodePath[(local_i - 1)];
            if (local_i < 2) {
              this.tree = local_r;
            } else if (this.dirPath[(local_i - 2)] != 0) {
              this.nodePath[(local_i - 2)].right = local_r;
            } else {
              this.nodePath[(local_i - 2)].left = local_r;
            }
            if (!local_r.pred()) {
              break;
            }
            local_r.pred(false);
            this.nodePath[(local_i - 1)].succ(local_r);
            break;
          }
        }
        else
        {
          Entry local_r = this.nodePath[(local_i - 1)].left;
          if (!local_r.black())
          {
            local_r.black(true);
            this.nodePath[(local_i - 1)].black(false);
            this.nodePath[(local_i - 1)].left = local_r.right;
            local_r.right = this.nodePath[(local_i - 1)];
            if (local_i < 2) {
              this.tree = local_r;
            } else if (this.dirPath[(local_i - 2)] != 0) {
              this.nodePath[(local_i - 2)].right = local_r;
            } else {
              this.nodePath[(local_i - 2)].left = local_r;
            }
            this.nodePath[local_i] = this.nodePath[(local_i - 1)];
            this.dirPath[local_i] = true;
            this.nodePath[(local_i - 1)] = local_r;
            if (color == local_i++) {
              color++;
            }
            local_r = this.nodePath[(local_i - 1)].left;
          }
          if (((local_r.pred()) || (local_r.left.black())) && ((local_r.succ()) || (local_r.right.black())))
          {
            local_r.black(false);
          }
          else
          {
            if ((local_r.pred()) || (local_r.left.black()))
            {
              Entry local_s = local_r.right;
              local_s.black(true);
              local_r.black(false);
              local_r.right = local_s.left;
              local_s.left = local_r;
              local_r = this.nodePath[(local_i - 1)].left = local_s;
              if (local_r.pred())
              {
                local_r.pred(false);
                local_r.left.succ(local_r);
              }
            }
            local_r.black(this.nodePath[(local_i - 1)].black());
            this.nodePath[(local_i - 1)].black(true);
            local_r.left.black(true);
            this.nodePath[(local_i - 1)].left = local_r.right;
            local_r.right = this.nodePath[(local_i - 1)];
            if (local_i < 2) {
              this.tree = local_r;
            } else if (this.dirPath[(local_i - 2)] != 0) {
              this.nodePath[(local_i - 2)].right = local_r;
            } else {
              this.nodePath[(local_i - 2)].left = local_r;
            }
            if (!local_r.succ()) {
              break;
            }
            local_r.succ(false);
            this.nodePath[(local_i - 1)].pred(local_r);
            break;
          }
        }
        local_i--;
      }
      if (this.tree != null) {
        this.tree.black(true);
      }
    }
    this.modified = true;
    this.count -= 1;
    while (color-- != 0) {
      this.nodePath[color] = null;
    }
    return local_p.value;
  }
  
  public Float put(Long local_ok, Float local_ov)
  {
    float oldValue = put(local_ok.longValue(), local_ov.floatValue());
    return this.modified ? null : Float.valueOf(oldValue);
  }
  
  public Float remove(Object local_ok)
  {
    float oldValue = remove(((Long)local_ok).longValue());
    return this.modified ? Float.valueOf(oldValue) : null;
  }
  
  public boolean containsValue(float local_v)
  {
    ValueIterator local_i = new ValueIterator(null);
    int local_j = this.count;
    while (local_j-- != 0)
    {
      float local_ev = local_i.nextFloat();
      if (local_ev == local_v) {
        return true;
      }
    }
    return false;
  }
  
  public void clear()
  {
    this.count = 0;
    this.tree = null;
    this.entries = null;
    this.values = null;
    this.keys = null;
    this.firstEntry = (this.lastEntry = null);
  }
  
  public boolean containsKey(long local_k)
  {
    return findKey(local_k) != null;
  }
  
  public int size()
  {
    return this.count;
  }
  
  public boolean isEmpty()
  {
    return this.count == 0;
  }
  
  public float get(long local_k)
  {
    Entry local_e = findKey(local_k);
    return local_e == null ? this.defRetValue : local_e.value;
  }
  
  public long firstLongKey()
  {
    if (this.tree == null) {
      throw new NoSuchElementException();
    }
    return this.firstEntry.key;
  }
  
  public long lastLongKey()
  {
    if (this.tree == null) {
      throw new NoSuchElementException();
    }
    return this.lastEntry.key;
  }
  
  public ObjectSortedSet<Long2FloatMap.Entry> long2FloatEntrySet()
  {
    if (this.entries == null) {
      this.entries = new AbstractObjectSortedSet()
      {
        final Comparator<? super Long2FloatMap.Entry> comparator = new Comparator()
        {
          public int compare(Long2FloatMap.Entry local_x, Long2FloatMap.Entry local_y)
          {
            return Long2FloatRBTreeMap.this.storedComparator.compare(local_x.getKey(), local_y.getKey());
          }
        };
        
        public Comparator<? super Long2FloatMap.Entry> comparator()
        {
          return this.comparator;
        }
        
        public ObjectBidirectionalIterator<Long2FloatMap.Entry> iterator()
        {
          return new Long2FloatRBTreeMap.EntryIterator(Long2FloatRBTreeMap.this);
        }
        
        public ObjectBidirectionalIterator<Long2FloatMap.Entry> iterator(Long2FloatMap.Entry from)
        {
          return new Long2FloatRBTreeMap.EntryIterator(Long2FloatRBTreeMap.this, ((Long)from.getKey()).longValue());
        }
        
        public boolean contains(Object local_o)
        {
          if (!(local_o instanceof Map.Entry)) {
            return false;
          }
          Map.Entry<Long, Float> local_e = (Map.Entry)local_o;
          Long2FloatRBTreeMap.Entry local_f = Long2FloatRBTreeMap.this.findKey(((Long)local_e.getKey()).longValue());
          return local_e.equals(local_f);
        }
        
        public boolean remove(Object local_o)
        {
          if (!(local_o instanceof Map.Entry)) {
            return false;
          }
          Map.Entry<Long, Float> local_e = (Map.Entry)local_o;
          Long2FloatRBTreeMap.Entry local_f = Long2FloatRBTreeMap.this.findKey(((Long)local_e.getKey()).longValue());
          if (local_f != null) {
            Long2FloatRBTreeMap.this.remove(local_f.key);
          }
          return local_f != null;
        }
        
        public int size()
        {
          return Long2FloatRBTreeMap.this.count;
        }
        
        public void clear()
        {
          Long2FloatRBTreeMap.this.clear();
        }
        
        public Long2FloatMap.Entry first()
        {
          return Long2FloatRBTreeMap.this.firstEntry;
        }
        
        public Long2FloatMap.Entry last()
        {
          return Long2FloatRBTreeMap.this.lastEntry;
        }
        
        public ObjectSortedSet<Long2FloatMap.Entry> subSet(Long2FloatMap.Entry from, Long2FloatMap.Entry local_to)
        {
          return Long2FloatRBTreeMap.this.subMap((Long)from.getKey(), (Long)local_to.getKey()).long2FloatEntrySet();
        }
        
        public ObjectSortedSet<Long2FloatMap.Entry> headSet(Long2FloatMap.Entry local_to)
        {
          return Long2FloatRBTreeMap.this.headMap((Long)local_to.getKey()).long2FloatEntrySet();
        }
        
        public ObjectSortedSet<Long2FloatMap.Entry> tailSet(Long2FloatMap.Entry from)
        {
          return Long2FloatRBTreeMap.this.tailMap((Long)from.getKey()).long2FloatEntrySet();
        }
      };
    }
    return this.entries;
  }
  
  public LongSortedSet keySet()
  {
    if (this.keys == null) {
      this.keys = new KeySet(null);
    }
    return this.keys;
  }
  
  public FloatCollection values()
  {
    if (this.values == null) {
      this.values = new AbstractFloatCollection()
      {
        public FloatIterator iterator()
        {
          return new Long2FloatRBTreeMap.ValueIterator(Long2FloatRBTreeMap.this, null);
        }
        
        public boolean contains(float local_k)
        {
          return Long2FloatRBTreeMap.this.containsValue(local_k);
        }
        
        public int size()
        {
          return Long2FloatRBTreeMap.this.count;
        }
        
        public void clear()
        {
          Long2FloatRBTreeMap.this.clear();
        }
      };
    }
    return this.values;
  }
  
  public LongComparator comparator()
  {
    return this.actualComparator;
  }
  
  public Long2FloatSortedMap headMap(long local_to)
  {
    return new Submap(0L, true, local_to, false);
  }
  
  public Long2FloatSortedMap tailMap(long from)
  {
    return new Submap(from, false, 0L, true);
  }
  
  public Long2FloatSortedMap subMap(long from, long local_to)
  {
    return new Submap(from, false, local_to, false);
  }
  
  public Long2FloatRBTreeMap clone()
  {
    Long2FloatRBTreeMap local_c;
    try
    {
      local_c = (Long2FloatRBTreeMap)super.clone();
    }
    catch (CloneNotSupportedException cantHappen)
    {
      throw new InternalError();
    }
    local_c.keys = null;
    local_c.values = null;
    local_c.entries = null;
    local_c.allocatePaths();
    if (this.count != 0)
    {
      Entry local_rp = new Entry();
      Entry local_rq = new Entry();
      Entry local_p = local_rp;
      local_rp.left(this.tree);
      Entry local_q = local_rq;
      local_rq.pred(null);
      for (;;)
      {
        if (!local_p.pred())
        {
          Entry cantHappen = local_p.left.clone();
          cantHappen.pred(local_q.left);
          cantHappen.succ(local_q);
          local_q.left(cantHappen);
          local_p = local_p.left;
          local_q = local_q.left;
        }
        else
        {
          while (local_p.succ())
          {
            local_p = local_p.right;
            if (local_p == null)
            {
              local_q.right = null;
              local_c.tree = local_rq.left;
              for (local_c.firstEntry = local_c.tree; local_c.firstEntry.left != null; local_c.firstEntry = local_c.firstEntry.left) {}
              for (local_c.lastEntry = local_c.tree; local_c.lastEntry.right != null; local_c.lastEntry = local_c.lastEntry.right) {}
              return local_c;
            }
            local_q = local_q.right;
          }
          local_p = local_p.right;
          local_q = local_q.right;
        }
        if (!local_p.succ())
        {
          Entry cantHappen = local_p.right.clone();
          cantHappen.succ(local_q.right);
          cantHappen.pred(local_q);
          local_q.right(cantHappen);
        }
      }
    }
    return local_c;
  }
  
  private void writeObject(ObjectOutputStream local_s)
    throws IOException
  {
    int local_n = this.count;
    EntryIterator local_i = new EntryIterator();
    local_s.defaultWriteObject();
    while (local_n-- != 0)
    {
      Entry local_e = local_i.nextEntry();
      local_s.writeLong(local_e.key);
      local_s.writeFloat(local_e.value);
    }
  }
  
  private Entry readTree(ObjectInputStream local_s, int local_n, Entry pred, Entry succ)
    throws IOException, ClassNotFoundException
  {
    if (local_n == 1)
    {
      Entry top = new Entry(local_s.readLong(), local_s.readFloat());
      top.pred(pred);
      top.succ(succ);
      top.black(true);
      return top;
    }
    if (local_n == 2)
    {
      Entry top = new Entry(local_s.readLong(), local_s.readFloat());
      top.black(true);
      top.right(new Entry(local_s.readLong(), local_s.readFloat()));
      top.right.pred(top);
      top.pred(pred);
      top.right.succ(succ);
      return top;
    }
    int top = local_n / 2;
    int leftN = local_n - top - 1;
    Entry top = new Entry();
    top.left(readTree(local_s, leftN, pred, top));
    top.key = local_s.readLong();
    top.value = local_s.readFloat();
    top.black(true);
    top.right(readTree(local_s, top, top, succ));
    if (local_n + 2 == (local_n + 2 & -(local_n + 2))) {
      top.right.black(false);
    }
    return top;
  }
  
  private void readObject(ObjectInputStream local_s)
    throws IOException, ClassNotFoundException
  {
    local_s.defaultReadObject();
    setActualComparator();
    allocatePaths();
    if (this.count != 0)
    {
      this.tree = readTree(local_s, this.count, null, null);
      for (Entry local_e = this.tree; local_e.left() != null; local_e = local_e.left()) {}
      this.firstEntry = local_e;
      for (local_e = this.tree; local_e.right() != null; local_e = local_e.right()) {}
      this.lastEntry = local_e;
    }
  }
  
  private void checkNodePath() {}
  
  private int checkTree(Entry local_e, int local_d, int local_D)
  {
    return 0;
  }
  
  private final class Submap
    extends AbstractLong2FloatSortedMap
    implements Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    long from;
    long field_47;
    boolean bottom;
    boolean top;
    protected volatile transient ObjectSortedSet<Long2FloatMap.Entry> entries;
    protected volatile transient LongSortedSet keys;
    protected volatile transient FloatCollection values;
    
    public Submap(long from, boolean bottom, long local_to, boolean top)
    {
      if ((!bottom) && (!top) && (Long2FloatRBTreeMap.this.compare(from, local_to) > 0)) {
        throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + local_to + ")");
      }
      this.from = from;
      this.bottom = bottom;
      this.field_47 = local_to;
      this.top = top;
      this.defRetValue = Long2FloatRBTreeMap.this.defRetValue;
    }
    
    public void clear()
    {
      SubmapIterator local_i = new SubmapIterator();
      while (local_i.hasNext())
      {
        local_i.nextEntry();
        local_i.remove();
      }
    }
    
    final boolean in1(long local_k)
    {
      return ((this.bottom) || (Long2FloatRBTreeMap.this.compare(local_k, this.from) >= 0)) && ((this.top) || (Long2FloatRBTreeMap.this.compare(local_k, this.field_47) < 0));
    }
    
    public ObjectSortedSet<Long2FloatMap.Entry> long2FloatEntrySet()
    {
      if (this.entries == null) {
        this.entries = new AbstractObjectSortedSet()
        {
          public ObjectBidirectionalIterator<Long2FloatMap.Entry> iterator()
          {
            return new Long2FloatRBTreeMap.Submap.SubmapEntryIterator(Long2FloatRBTreeMap.Submap.this);
          }
          
          public ObjectBidirectionalIterator<Long2FloatMap.Entry> iterator(Long2FloatMap.Entry from)
          {
            return new Long2FloatRBTreeMap.Submap.SubmapEntryIterator(Long2FloatRBTreeMap.Submap.this, ((Long)from.getKey()).longValue());
          }
          
          public Comparator<? super Long2FloatMap.Entry> comparator()
          {
            return Long2FloatRBTreeMap.this.long2FloatEntrySet().comparator();
          }
          
          public boolean contains(Object local_o)
          {
            if (!(local_o instanceof Map.Entry)) {
              return false;
            }
            Map.Entry<Long, Float> local_e = (Map.Entry)local_o;
            Long2FloatRBTreeMap.Entry local_f = Long2FloatRBTreeMap.this.findKey(((Long)local_e.getKey()).longValue());
            return (local_f != null) && (Long2FloatRBTreeMap.Submap.this.in1(local_f.key)) && (local_e.equals(local_f));
          }
          
          public boolean remove(Object local_o)
          {
            if (!(local_o instanceof Map.Entry)) {
              return false;
            }
            Map.Entry<Long, Float> local_e = (Map.Entry)local_o;
            Long2FloatRBTreeMap.Entry local_f = Long2FloatRBTreeMap.this.findKey(((Long)local_e.getKey()).longValue());
            if ((local_f != null) && (Long2FloatRBTreeMap.Submap.this.in1(local_f.key))) {
              Long2FloatRBTreeMap.Submap.this.remove(local_f.key);
            }
            return local_f != null;
          }
          
          public int size()
          {
            int local_c = 0;
            Iterator<?> local_i = iterator();
            while (local_i.hasNext())
            {
              local_c++;
              local_i.next();
            }
            return local_c;
          }
          
          public boolean isEmpty()
          {
            return !new Long2FloatRBTreeMap.Submap.SubmapIterator(Long2FloatRBTreeMap.Submap.this).hasNext();
          }
          
          public void clear()
          {
            Long2FloatRBTreeMap.Submap.this.clear();
          }
          
          public Long2FloatMap.Entry first()
          {
            return Long2FloatRBTreeMap.Submap.this.firstEntry();
          }
          
          public Long2FloatMap.Entry last()
          {
            return Long2FloatRBTreeMap.Submap.this.lastEntry();
          }
          
          public ObjectSortedSet<Long2FloatMap.Entry> subSet(Long2FloatMap.Entry from, Long2FloatMap.Entry local_to)
          {
            return Long2FloatRBTreeMap.Submap.this.subMap((Long)from.getKey(), (Long)local_to.getKey()).long2FloatEntrySet();
          }
          
          public ObjectSortedSet<Long2FloatMap.Entry> headSet(Long2FloatMap.Entry local_to)
          {
            return Long2FloatRBTreeMap.Submap.this.headMap((Long)local_to.getKey()).long2FloatEntrySet();
          }
          
          public ObjectSortedSet<Long2FloatMap.Entry> tailSet(Long2FloatMap.Entry from)
          {
            return Long2FloatRBTreeMap.Submap.this.tailMap((Long)from.getKey()).long2FloatEntrySet();
          }
        };
      }
      return this.entries;
    }
    
    public LongSortedSet keySet()
    {
      if (this.keys == null) {
        this.keys = new KeySet(null);
      }
      return this.keys;
    }
    
    public FloatCollection values()
    {
      if (this.values == null) {
        this.values = new AbstractFloatCollection()
        {
          public FloatIterator iterator()
          {
            return new Long2FloatRBTreeMap.Submap.SubmapValueIterator(Long2FloatRBTreeMap.Submap.this, null);
          }
          
          public boolean contains(float local_k)
          {
            return Long2FloatRBTreeMap.Submap.this.containsValue(local_k);
          }
          
          public int size()
          {
            return Long2FloatRBTreeMap.Submap.this.size();
          }
          
          public void clear()
          {
            Long2FloatRBTreeMap.Submap.this.clear();
          }
        };
      }
      return this.values;
    }
    
    public boolean containsKey(long local_k)
    {
      return (in1(local_k)) && (Long2FloatRBTreeMap.this.containsKey(local_k));
    }
    
    public boolean containsValue(float local_v)
    {
      SubmapIterator local_i = new SubmapIterator();
      while (local_i.hasNext())
      {
        float local_ev = local_i.nextEntry().value;
        if (local_ev == local_v) {
          return true;
        }
      }
      return false;
    }
    
    public float get(long local_k)
    {
      long local_kk = local_k;
      Long2FloatRBTreeMap.Entry local_e;
      return (in1(local_kk)) && ((local_e = Long2FloatRBTreeMap.this.findKey(local_kk)) != null) ? local_e.value : this.defRetValue;
    }
    
    public float put(long local_k, float local_v)
    {
      Long2FloatRBTreeMap.this.modified = false;
      if (!in1(local_k)) {
        throw new IllegalArgumentException("Key (" + local_k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.field_47)) + ")");
      }
      float oldValue = Long2FloatRBTreeMap.this.put(local_k, local_v);
      return Long2FloatRBTreeMap.this.modified ? this.defRetValue : oldValue;
    }
    
    public Float put(Long local_ok, Float local_ov)
    {
      float oldValue = put(local_ok.longValue(), local_ov.floatValue());
      return Long2FloatRBTreeMap.this.modified ? null : Float.valueOf(oldValue);
    }
    
    public float remove(long local_k)
    {
      Long2FloatRBTreeMap.this.modified = false;
      if (!in1(local_k)) {
        return this.defRetValue;
      }
      float oldValue = Long2FloatRBTreeMap.this.remove(local_k);
      return Long2FloatRBTreeMap.this.modified ? oldValue : this.defRetValue;
    }
    
    public Float remove(Object local_ok)
    {
      float oldValue = remove(((Long)local_ok).longValue());
      return Long2FloatRBTreeMap.this.modified ? Float.valueOf(oldValue) : null;
    }
    
    public int size()
    {
      SubmapIterator local_i = new SubmapIterator();
      int local_n = 0;
      while (local_i.hasNext())
      {
        local_n++;
        local_i.nextEntry();
      }
      return local_n;
    }
    
    public boolean isEmpty()
    {
      return !new SubmapIterator().hasNext();
    }
    
    public LongComparator comparator()
    {
      return Long2FloatRBTreeMap.this.actualComparator;
    }
    
    public Long2FloatSortedMap headMap(long local_to)
    {
      if (this.top) {
        return new Submap(Long2FloatRBTreeMap.this, this.from, this.bottom, local_to, false);
      }
      return Long2FloatRBTreeMap.this.compare(local_to, this.field_47) < 0 ? new Submap(Long2FloatRBTreeMap.this, this.from, this.bottom, local_to, false) : this;
    }
    
    public Long2FloatSortedMap tailMap(long from)
    {
      if (this.bottom) {
        return new Submap(Long2FloatRBTreeMap.this, from, false, this.field_47, this.top);
      }
      return Long2FloatRBTreeMap.this.compare(from, this.from) > 0 ? new Submap(Long2FloatRBTreeMap.this, from, false, this.field_47, this.top) : this;
    }
    
    public Long2FloatSortedMap subMap(long from, long local_to)
    {
      if ((this.top) && (this.bottom)) {
        return new Submap(Long2FloatRBTreeMap.this, from, false, local_to, false);
      }
      if (!this.top) {
        local_to = Long2FloatRBTreeMap.this.compare(local_to, this.field_47) < 0 ? local_to : this.field_47;
      }
      if (!this.bottom) {
        from = Long2FloatRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
      }
      if ((!this.top) && (!this.bottom) && (from == this.from) && (local_to == this.field_47)) {
        return this;
      }
      return new Submap(Long2FloatRBTreeMap.this, from, false, local_to, false);
    }
    
    public Long2FloatRBTreeMap.Entry firstEntry()
    {
      if (Long2FloatRBTreeMap.this.tree == null) {
        return null;
      }
      Long2FloatRBTreeMap.Entry local_e;
      Long2FloatRBTreeMap.Entry local_e;
      if (this.bottom)
      {
        local_e = Long2FloatRBTreeMap.this.firstEntry;
      }
      else
      {
        local_e = Long2FloatRBTreeMap.this.locateKey(this.from);
        if (Long2FloatRBTreeMap.this.compare(local_e.key, this.from) < 0) {
          local_e = local_e.next();
        }
      }
      if ((local_e == null) || ((!this.top) && (Long2FloatRBTreeMap.this.compare(local_e.key, this.field_47) >= 0))) {
        return null;
      }
      return local_e;
    }
    
    public Long2FloatRBTreeMap.Entry lastEntry()
    {
      if (Long2FloatRBTreeMap.this.tree == null) {
        return null;
      }
      Long2FloatRBTreeMap.Entry local_e;
      Long2FloatRBTreeMap.Entry local_e;
      if (this.top)
      {
        local_e = Long2FloatRBTreeMap.this.lastEntry;
      }
      else
      {
        local_e = Long2FloatRBTreeMap.this.locateKey(this.field_47);
        if (Long2FloatRBTreeMap.this.compare(local_e.key, this.field_47) >= 0) {
          local_e = local_e.prev();
        }
      }
      if ((local_e == null) || ((!this.bottom) && (Long2FloatRBTreeMap.this.compare(local_e.key, this.from) < 0))) {
        return null;
      }
      return local_e;
    }
    
    public long firstLongKey()
    {
      Long2FloatRBTreeMap.Entry local_e = firstEntry();
      if (local_e == null) {
        throw new NoSuchElementException();
      }
      return local_e.key;
    }
    
    public long lastLongKey()
    {
      Long2FloatRBTreeMap.Entry local_e = lastEntry();
      if (local_e == null) {
        throw new NoSuchElementException();
      }
      return local_e.key;
    }
    
    public Long firstKey()
    {
      Long2FloatRBTreeMap.Entry local_e = firstEntry();
      if (local_e == null) {
        throw new NoSuchElementException();
      }
      return local_e.getKey();
    }
    
    public Long lastKey()
    {
      Long2FloatRBTreeMap.Entry local_e = lastEntry();
      if (local_e == null) {
        throw new NoSuchElementException();
      }
      return local_e.getKey();
    }
    
    private final class SubmapValueIterator
      extends Long2FloatRBTreeMap.Submap.SubmapIterator
      implements FloatListIterator
    {
      private SubmapValueIterator()
      {
        super();
      }
      
      public float nextFloat()
      {
        return nextEntry().value;
      }
      
      public float previousFloat()
      {
        return previousEntry().value;
      }
      
      public void set(float local_v)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(float local_v)
      {
        throw new UnsupportedOperationException();
      }
      
      public Float next()
      {
        return Float.valueOf(nextEntry().value);
      }
      
      public Float previous()
      {
        return Float.valueOf(previousEntry().value);
      }
      
      public void set(Float local_ok)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(Float local_ok)
      {
        throw new UnsupportedOperationException();
      }
    }
    
    private final class SubmapKeyIterator
      extends Long2FloatRBTreeMap.Submap.SubmapIterator
      implements LongListIterator
    {
      public SubmapKeyIterator()
      {
        super();
      }
      
      public SubmapKeyIterator(long from)
      {
        super(from);
      }
      
      public long nextLong()
      {
        return nextEntry().key;
      }
      
      public long previousLong()
      {
        return previousEntry().key;
      }
      
      public void set(long local_k)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(long local_k)
      {
        throw new UnsupportedOperationException();
      }
      
      public Long next()
      {
        return Long.valueOf(nextEntry().key);
      }
      
      public Long previous()
      {
        return Long.valueOf(previousEntry().key);
      }
      
      public void set(Long local_ok)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(Long local_ok)
      {
        throw new UnsupportedOperationException();
      }
    }
    
    private class SubmapEntryIterator
      extends Long2FloatRBTreeMap.Submap.SubmapIterator
      implements ObjectListIterator<Long2FloatMap.Entry>
    {
      SubmapEntryIterator()
      {
        super();
      }
      
      SubmapEntryIterator(long local_k)
      {
        super(local_k);
      }
      
      public Long2FloatMap.Entry next()
      {
        return nextEntry();
      }
      
      public Long2FloatMap.Entry previous()
      {
        return previousEntry();
      }
      
      public void set(Long2FloatMap.Entry local_ok)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(Long2FloatMap.Entry local_ok)
      {
        throw new UnsupportedOperationException();
      }
    }
    
    private class SubmapIterator
      extends Long2FloatRBTreeMap.TreeIterator
    {
      SubmapIterator()
      {
        super();
        this.next = Long2FloatRBTreeMap.Submap.this.firstEntry();
      }
      
      SubmapIterator(long local_k)
      {
        this();
        if (this.next != null) {
          if ((!Long2FloatRBTreeMap.Submap.this.bottom) && (Long2FloatRBTreeMap.this.compare(local_k, this.next.key) < 0))
          {
            this.prev = null;
          }
          else if ((!Long2FloatRBTreeMap.Submap.this.top) && (Long2FloatRBTreeMap.this.compare(local_k, (this.prev = Long2FloatRBTreeMap.Submap.this.lastEntry()).key) >= 0))
          {
            this.next = null;
          }
          else
          {
            this.next = Long2FloatRBTreeMap.this.locateKey(local_k);
            if (Long2FloatRBTreeMap.this.compare(this.next.key, local_k) <= 0)
            {
              this.prev = this.next;
              this.next = this.next.next();
            }
            else
            {
              this.prev = this.next.prev();
            }
          }
        }
      }
      
      void updatePrevious()
      {
        this.prev = this.prev.prev();
        if ((!Long2FloatRBTreeMap.Submap.this.bottom) && (this.prev != null) && (Long2FloatRBTreeMap.this.compare(this.prev.key, Long2FloatRBTreeMap.Submap.this.from) < 0)) {
          this.prev = null;
        }
      }
      
      void updateNext()
      {
        this.next = this.next.next();
        if ((!Long2FloatRBTreeMap.Submap.this.top) && (this.next != null) && (Long2FloatRBTreeMap.this.compare(this.next.key, Long2FloatRBTreeMap.Submap.this.field_47) >= 0)) {
          this.next = null;
        }
      }
    }
    
    private class KeySet
      extends AbstractLong2FloatSortedMap.KeySet
    {
      private KeySet()
      {
        super();
      }
      
      public LongBidirectionalIterator iterator()
      {
        return new Long2FloatRBTreeMap.Submap.SubmapKeyIterator(Long2FloatRBTreeMap.Submap.this);
      }
      
      public LongBidirectionalIterator iterator(long from)
      {
        return new Long2FloatRBTreeMap.Submap.SubmapKeyIterator(Long2FloatRBTreeMap.Submap.this, from);
      }
    }
  }
  
  private final class ValueIterator
    extends Long2FloatRBTreeMap.TreeIterator
    implements FloatListIterator
  {
    private ValueIterator()
    {
      super();
    }
    
    public float nextFloat()
    {
      return nextEntry().value;
    }
    
    public float previousFloat()
    {
      return previousEntry().value;
    }
    
    public void set(float local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(float local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public Float next()
    {
      return Float.valueOf(nextEntry().value);
    }
    
    public Float previous()
    {
      return Float.valueOf(previousEntry().value);
    }
    
    public void set(Float local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Float local_ok)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class KeySet
    extends AbstractLong2FloatSortedMap.KeySet
  {
    private KeySet()
    {
      super();
    }
    
    public LongBidirectionalIterator iterator()
    {
      return new Long2FloatRBTreeMap.KeyIterator(Long2FloatRBTreeMap.this);
    }
    
    public LongBidirectionalIterator iterator(long from)
    {
      return new Long2FloatRBTreeMap.KeyIterator(Long2FloatRBTreeMap.this, from);
    }
  }
  
  private final class KeyIterator
    extends Long2FloatRBTreeMap.TreeIterator
    implements LongListIterator
  {
    public KeyIterator()
    {
      super();
    }
    
    public KeyIterator(long local_k)
    {
      super(local_k);
    }
    
    public long nextLong()
    {
      return nextEntry().key;
    }
    
    public long previousLong()
    {
      return previousEntry().key;
    }
    
    public void set(long local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(long local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public Long next()
    {
      return Long.valueOf(nextEntry().key);
    }
    
    public Long previous()
    {
      return Long.valueOf(previousEntry().key);
    }
    
    public void set(Long local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Long local_ok)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class EntryIterator
    extends Long2FloatRBTreeMap.TreeIterator
    implements ObjectListIterator<Long2FloatMap.Entry>
  {
    EntryIterator()
    {
      super();
    }
    
    EntryIterator(long local_k)
    {
      super(local_k);
    }
    
    public Long2FloatMap.Entry next()
    {
      return nextEntry();
    }
    
    public Long2FloatMap.Entry previous()
    {
      return previousEntry();
    }
    
    public void set(Long2FloatMap.Entry local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Long2FloatMap.Entry local_ok)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class TreeIterator
  {
    Long2FloatRBTreeMap.Entry prev;
    Long2FloatRBTreeMap.Entry next;
    Long2FloatRBTreeMap.Entry curr;
    int index = 0;
    
    TreeIterator()
    {
      this.next = Long2FloatRBTreeMap.this.firstEntry;
    }
    
    TreeIterator(long local_k)
    {
      if ((this.next = Long2FloatRBTreeMap.this.locateKey(local_k)) != null) {
        if (Long2FloatRBTreeMap.this.compare(this.next.key, local_k) <= 0)
        {
          this.prev = this.next;
          this.next = this.next.next();
        }
        else
        {
          this.prev = this.next.prev();
        }
      }
    }
    
    public boolean hasNext()
    {
      return this.next != null;
    }
    
    public boolean hasPrevious()
    {
      return this.prev != null;
    }
    
    void updateNext()
    {
      this.next = this.next.next();
    }
    
    Long2FloatRBTreeMap.Entry nextEntry()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      this.curr = (this.prev = this.next);
      this.index += 1;
      updateNext();
      return this.curr;
    }
    
    void updatePrevious()
    {
      this.prev = this.prev.prev();
    }
    
    Long2FloatRBTreeMap.Entry previousEntry()
    {
      if (!hasPrevious()) {
        throw new NoSuchElementException();
      }
      this.curr = (this.next = this.prev);
      this.index -= 1;
      updatePrevious();
      return this.curr;
    }
    
    public int nextIndex()
    {
      return this.index;
    }
    
    public int previousIndex()
    {
      return this.index - 1;
    }
    
    public void remove()
    {
      if (this.curr == null) {
        throw new IllegalStateException();
      }
      if (this.curr == this.prev) {
        this.index -= 1;
      }
      this.next = (this.prev = this.curr);
      updatePrevious();
      updateNext();
      Long2FloatRBTreeMap.this.remove(this.curr.key);
      this.curr = null;
    }
    
    public int skip(int local_n)
    {
      int local_i = local_n;
      while ((local_i-- != 0) && (hasNext())) {
        nextEntry();
      }
      return local_n - local_i - 1;
    }
    
    public int back(int local_n)
    {
      int local_i = local_n;
      while ((local_i-- != 0) && (hasPrevious())) {
        previousEntry();
      }
      return local_n - local_i - 1;
    }
  }
  
  private static final class Entry
    implements Cloneable, Long2FloatMap.Entry
  {
    private static final int BLACK_MASK = 1;
    private static final int SUCC_MASK = -2147483648;
    private static final int PRED_MASK = 1073741824;
    long key;
    float value;
    Entry left;
    Entry right;
    int info;
    
    Entry() {}
    
    Entry(long local_k, float local_v)
    {
      this.key = local_k;
      this.value = local_v;
      this.info = -1073741824;
    }
    
    Entry left()
    {
      return (this.info & 0x40000000) != 0 ? null : this.left;
    }
    
    Entry right()
    {
      return (this.info & 0x80000000) != 0 ? null : this.right;
    }
    
    boolean pred()
    {
      return (this.info & 0x40000000) != 0;
    }
    
    boolean succ()
    {
      return (this.info & 0x80000000) != 0;
    }
    
    void pred(boolean pred)
    {
      if (pred) {
        this.info |= 1073741824;
      } else {
        this.info &= -1073741825;
      }
    }
    
    void succ(boolean succ)
    {
      if (succ) {
        this.info |= -2147483648;
      } else {
        this.info &= 2147483647;
      }
    }
    
    void pred(Entry pred)
    {
      this.info |= 1073741824;
      this.left = pred;
    }
    
    void succ(Entry succ)
    {
      this.info |= -2147483648;
      this.right = succ;
    }
    
    void left(Entry left)
    {
      this.info &= -1073741825;
      this.left = left;
    }
    
    void right(Entry right)
    {
      this.info &= 2147483647;
      this.right = right;
    }
    
    boolean black()
    {
      return (this.info & 0x1) != 0;
    }
    
    void black(boolean black)
    {
      if (black) {
        this.info |= 1;
      } else {
        this.info &= -2;
      }
    }
    
    Entry next()
    {
      Entry next = this.right;
      if ((this.info & 0x80000000) == 0) {
        while ((next.info & 0x40000000) == 0) {
          next = next.left;
        }
      }
      return next;
    }
    
    Entry prev()
    {
      Entry prev = this.left;
      if ((this.info & 0x40000000) == 0) {
        while ((prev.info & 0x80000000) == 0) {
          prev = prev.right;
        }
      }
      return prev;
    }
    
    public Long getKey()
    {
      return Long.valueOf(this.key);
    }
    
    public long getLongKey()
    {
      return this.key;
    }
    
    public Float getValue()
    {
      return Float.valueOf(this.value);
    }
    
    public float getFloatValue()
    {
      return this.value;
    }
    
    public float setValue(float value)
    {
      float oldValue = this.value;
      this.value = value;
      return oldValue;
    }
    
    public Float setValue(Float value)
    {
      return Float.valueOf(setValue(value.floatValue()));
    }
    
    public Entry clone()
    {
      Entry local_c;
      try
      {
        local_c = (Entry)super.clone();
      }
      catch (CloneNotSupportedException cantHappen)
      {
        throw new InternalError();
      }
      local_c.key = this.key;
      local_c.value = this.value;
      local_c.info = this.info;
      return local_c;
    }
    
    public boolean equals(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Long, Float> local_e = (Map.Entry)local_o;
      return (this.key == ((Long)local_e.getKey()).longValue()) && (this.value == ((Float)local_e.getValue()).floatValue());
    }
    
    public int hashCode()
    {
      return HashCommon.long2int(this.key) ^ HashCommon.float2int(this.value);
    }
    
    public String toString()
    {
      return this.key + "=>" + this.value;
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.longs.Long2FloatRBTreeMap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */