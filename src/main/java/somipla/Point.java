/**
 *
 */
package somipla;

class Point {
  public final int x;

  public final int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public int hashCode() {
    return (x ^ y);
  }

  @Override
  public boolean equals(Object o) {
    return equals((Point) o);
  }

  public boolean equals(Point p) {
    return (p.x == x && p.y == y);
  }

}