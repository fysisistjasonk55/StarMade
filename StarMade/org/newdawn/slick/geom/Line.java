package org.newdawn.slick.geom;

public class Line
  extends Shape
{
  private Vector2f start;
  private Vector2f end;
  private Vector2f vec;
  private float lenSquared;
  private Vector2f loc = new Vector2f(0.0F, 0.0F);
  private Vector2f field_368 = new Vector2f(0.0F, 0.0F);
  private Vector2f field_369 = new Vector2f(0.0F, 0.0F);
  private Vector2f proj = new Vector2f(0.0F, 0.0F);
  private Vector2f closest = new Vector2f(0.0F, 0.0F);
  private Vector2f other = new Vector2f(0.0F, 0.0F);
  private boolean outerEdge = true;
  private boolean innerEdge = true;
  
  public Line(float local_x, float local_y, boolean inner, boolean outer)
  {
    this(0.0F, 0.0F, local_x, local_y);
  }
  
  public Line(float local_x, float local_y)
  {
    this(local_x, local_y, true, true);
  }
  
  public Line(float local_x1, float local_y1, float local_x2, float local_y2)
  {
    this(new Vector2f(local_x1, local_y1), new Vector2f(local_x2, local_y2));
  }
  
  public Line(float local_x1, float local_y1, float local_dx, float local_dy, boolean dummy)
  {
    this(new Vector2f(local_x1, local_y1), new Vector2f(local_x1 + local_dx, local_y1 + local_dy));
  }
  
  public Line(float[] start, float[] end)
  {
    set(start, end);
  }
  
  public Line(Vector2f start, Vector2f end)
  {
    set(start, end);
  }
  
  public void set(float[] start, float[] end)
  {
    set(start[0], start[1], end[0], end[1]);
  }
  
  public Vector2f getStart()
  {
    return this.start;
  }
  
  public Vector2f getEnd()
  {
    return this.end;
  }
  
  public float length()
  {
    return this.vec.length();
  }
  
  public float lengthSquared()
  {
    return this.vec.lengthSquared();
  }
  
  public void set(Vector2f start, Vector2f end)
  {
    this.pointsDirty = true;
    if (this.start == null) {
      this.start = new Vector2f();
    }
    this.start.set(start);
    if (this.end == null) {
      this.end = new Vector2f();
    }
    this.end.set(end);
    this.vec = new Vector2f(end);
    this.vec.sub(start);
    this.lenSquared = this.vec.lengthSquared();
  }
  
  public void set(float local_sx, float local_sy, float local_ex, float local_ey)
  {
    this.pointsDirty = true;
    this.start.set(local_sx, local_sy);
    this.end.set(local_ex, local_ey);
    float local_dx = local_ex - local_sx;
    float local_dy = local_ey - local_sy;
    this.vec.set(local_dx, local_dy);
    this.lenSquared = (local_dx * local_dx + local_dy * local_dy);
  }
  
  public float getDX()
  {
    return this.end.getX() - this.start.getX();
  }
  
  public float getDY()
  {
    return this.end.getY() - this.start.getY();
  }
  
  public float getX()
  {
    return getX1();
  }
  
  public float getY()
  {
    return getY1();
  }
  
  public float getX1()
  {
    return this.start.getX();
  }
  
  public float getY1()
  {
    return this.start.getY();
  }
  
  public float getX2()
  {
    return this.end.getX();
  }
  
  public float getY2()
  {
    return this.end.getY();
  }
  
  public float distance(Vector2f point)
  {
    return (float)Math.sqrt(distanceSquared(point));
  }
  
  public boolean on(Vector2f point)
  {
    getClosestPoint(point, this.closest);
    return point.equals(this.closest);
  }
  
  public float distanceSquared(Vector2f point)
  {
    getClosestPoint(point, this.closest);
    this.closest.sub(point);
    float result = this.closest.lengthSquared();
    return result;
  }
  
  public void getClosestPoint(Vector2f point, Vector2f result)
  {
    this.loc.set(point);
    this.loc.sub(this.start);
    float projDistance = this.vec.dot(this.loc);
    projDistance /= this.vec.lengthSquared();
    if (projDistance < 0.0F)
    {
      result.set(this.start);
      return;
    }
    if (projDistance > 1.0F)
    {
      result.set(this.end);
      return;
    }
    result.field_1680 = (this.start.getX() + projDistance * this.vec.getX());
    result.field_1681 = (this.start.getY() + projDistance * this.vec.getY());
  }
  
  public String toString()
  {
    return "[Line " + this.start + "," + this.end + "]";
  }
  
  public Vector2f intersect(Line other)
  {
    return intersect(other, false);
  }
  
  public Vector2f intersect(Line other, boolean limit)
  {
    Vector2f temp = new Vector2f();
    if (!intersect(other, limit, temp)) {
      return null;
    }
    return temp;
  }
  
  public boolean intersect(Line other, boolean limit, Vector2f result)
  {
    float dx1 = this.end.getX() - this.start.getX();
    float dx2 = other.end.getX() - other.start.getX();
    float dy1 = this.end.getY() - this.start.getY();
    float dy2 = other.end.getY() - other.start.getY();
    float denom = dy2 * dx1 - dx2 * dy1;
    if (denom == 0.0F) {
      return false;
    }
    float local_ua = dx2 * (this.start.getY() - other.start.getY()) - dy2 * (this.start.getX() - other.start.getX());
    local_ua /= denom;
    float local_ub = dx1 * (this.start.getY() - other.start.getY()) - dy1 * (this.start.getX() - other.start.getX());
    local_ub /= denom;
    if ((limit) && ((local_ua < 0.0F) || (local_ua > 1.0F) || (local_ub < 0.0F) || (local_ub > 1.0F))) {
      return false;
    }
    float local_u = local_ua;
    float local_ix = this.start.getX() + local_u * (this.end.getX() - this.start.getX());
    float local_iy = this.start.getY() + local_u * (this.end.getY() - this.start.getY());
    result.set(local_ix, local_iy);
    return true;
  }
  
  protected void createPoints()
  {
    this.points = new float[4];
    this.points[0] = getX1();
    this.points[1] = getY1();
    this.points[2] = getX2();
    this.points[3] = getY2();
  }
  
  public Shape transform(Transform transform)
  {
    float[] temp = new float[4];
    createPoints();
    transform.transform(this.points, 0, temp, 0, 2);
    return new Line(temp[0], temp[1], temp[2], temp[3]);
  }
  
  public boolean closed()
  {
    return false;
  }
  
  public boolean intersects(Shape shape)
  {
    if ((shape instanceof Circle)) {
      return shape.intersects(this);
    }
    return super.intersects(shape);
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.newdawn.slick.geom.Line
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */