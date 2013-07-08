package it.unimi.dsi.fastutil.ints;

public class IntSemiIndirectHeaps
{
  public static int downHeap(int[] refArray, int[] heap, int size, int local_i, IntComparator local_c)
  {
    if (local_i >= size) {
      throw new IllegalArgumentException("Heap position (" + local_i + ") is larger than or equal to heap size (" + size + ")");
    }
    int local_e = heap[local_i];
    int local_E = refArray[local_e];
    if (local_c == null)
    {
      int child;
      while ((child = 2 * local_i + 1) < size)
      {
        if ((child + 1 < size) && (refArray[heap[(child + 1)]] < refArray[heap[child]])) {
          child++;
        }
        if (local_E <= refArray[heap[child]]) {
          break;
        }
        heap[local_i] = heap[child];
        local_i = child;
      }
    }
    int child;
    while ((child = 2 * local_i + 1) < size)
    {
      if ((child + 1 < size) && (local_c.compare(refArray[heap[(child + 1)]], refArray[heap[child]]) < 0)) {
        child++;
      }
      if (local_c.compare(local_E, refArray[heap[child]]) <= 0) {
        break;
      }
      heap[local_i] = heap[child];
      local_i = child;
    }
    heap[local_i] = local_e;
    return local_i;
  }
  
  public static int upHeap(int[] refArray, int[] heap, int size, int local_i, IntComparator local_c)
  {
    if (local_i >= size) {
      throw new IllegalArgumentException("Heap position (" + local_i + ") is larger than or equal to heap size (" + size + ")");
    }
    int local_e = heap[local_i];
    int local_E = refArray[local_e];
    if (local_c == null)
    {
      int parent;
      while ((local_i != 0) && ((parent = (local_i - 1) / 2) >= 0) && (refArray[heap[parent]] > local_E))
      {
        heap[local_i] = heap[parent];
        local_i = parent;
      }
    }
    int parent;
    while ((local_i != 0) && ((parent = (local_i - 1) / 2) >= 0) && (local_c.compare(refArray[heap[parent]], local_E) > 0))
    {
      heap[local_i] = heap[parent];
      local_i = parent;
    }
    heap[local_i] = local_e;
    return local_i;
  }
  
  public static void makeHeap(int[] refArray, int offset, int length, int[] heap, IntComparator local_c)
  {
    IntArrays.ensureOffsetLength(refArray, offset, length);
    if (heap.length < length) {
      throw new IllegalArgumentException("The heap length (" + heap.length + ") is smaller than the number of elements (" + length + ")");
    }
    int local_i = length;
    while (local_i-- != 0) {
      heap[local_i] = (offset + local_i);
    }
    local_i = length / 2;
    while (local_i-- != 0) {
      downHeap(refArray, heap, length, local_i, local_c);
    }
  }
  
  public static int[] makeHeap(int[] refArray, int offset, int length, IntComparator local_c)
  {
    int[] heap = length <= 0 ? IntArrays.EMPTY_ARRAY : new int[length];
    makeHeap(refArray, offset, length, heap, local_c);
    return heap;
  }
  
  public static void makeHeap(int[] refArray, int[] heap, int size, IntComparator local_c)
  {
    int local_i = size / 2;
    while (local_i-- != 0) {
      downHeap(refArray, heap, size, local_i, local_c);
    }
  }
  
  public static int front(int[] refArray, int[] heap, int size, int[] local_a)
  {
    int top = refArray[heap[0]];
    int local_j = 0;
    int local_l = 0;
    int local_r = 1;
    int local_f = 0;
    for (int local_i = 0; local_i < local_r; local_i++)
    {
      if (local_i == local_f)
      {
        if (local_l >= local_r) {
          break;
        }
        local_f = (local_f << 1) + 1;
        local_i = local_l;
        local_l = -1;
      }
      if (top == refArray[heap[local_i]])
      {
        local_a[(local_j++)] = heap[local_i];
        if (local_l == -1) {
          local_l = local_i * 2 + 1;
        }
        local_r = Math.min(size, local_i * 2 + 3);
      }
    }
    return local_j;
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.ints.IntSemiIndirectHeaps
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */