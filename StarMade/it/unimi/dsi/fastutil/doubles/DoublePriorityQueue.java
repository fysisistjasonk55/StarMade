package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.PriorityQueue;

public abstract interface DoublePriorityQueue
  extends PriorityQueue<Double>
{
  public abstract void enqueue(double paramDouble);
  
  public abstract double dequeueDouble();
  
  public abstract double firstDouble();
  
  public abstract double lastDouble();
  
  public abstract DoubleComparator comparator();
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.doubles.DoublePriorityQueue
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */