/**
 *
 */
package somipla;

import java.util.ArrayList;
import java.util.List;

class CellReport {
  private List<Point> unknownCells = new ArrayList<Point>();
  private List<Point> knownMineCells = new ArrayList<Point>();
  private List<Point> knownEmptyCells = new ArrayList<Point>();
  int x;
  int y;

  public CellReport(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public void addUnknown(int x, int y) {
    unknownCells.add(new Point(x, y));
  }

  public void addMine(int x, int y) {
    knownMineCells.add(new Point(x, y));
  }

  public void addEmpty(int x, int y) {
    knownEmptyCells.add(new Point(x, y));
  }

  public int getUnknownCount() {
    return unknownCells.size();
  }

  public int getEmptyCount() {
    return knownEmptyCells.size();
  }

  public int getMineCount() {
    return knownMineCells.size();
  }

  public int getTotalCount() {
    return getEmptyCount() + getMineCount() + getUnknownCount();
  }

  public Point[] getUnknown() {
    return (Point[]) unknownCells.toArray(new Point[unknownCells.size()]);
  }

  public void add(int x, int y, byte type) {
    switch (type) {
    case MineSweeper.mine:
      addMine(x, y);
      break;
    case MineSweeper.unknown:
      addUnknown(x, y);
      break;
    default:
      addEmpty(x, y);
    }
  }

  // public String toString()
  // {
  // String reply = "rapport("+x+","+y+")\n";
  // for (int j=1; j>=-1 ;--j)
  // {
  // for(int i=1; i>=-1 ;--i)
  // {
  // if (i!=1)
  // reply+=",";
  // CPoint p = new CPoint(x-i,y-j);
  // if (i==0 && j==0)
  // {
  // reply+=Play.mines[x][y];
  // }
  // else if (unknownCells.contains(p))
  // {
  // reply+="?";
  // }
  // else if (knownEmptyCells.contains(p))
  // {
  // reply+="0";
  // }
  // else if (knownMineCells.contains(p))
  // {
  // reply+="M";
  // }
  // else
  // reply+="X";
  // }
  // reply+="\n";
  // }
  //
  // for (int j=0; j<3 ;++j)
  // {
  // Vector c = null;
  // switch (j)
  // {
  // case 0 :
  // reply += "unknown : ";
  // c=unknownCells;
  // break;
  // case 1 :
  // reply += "\nempty : ";
  // c=knownEmptyCells;
  // break;
  // case 2 :
  // reply += "\nmines : ";
  // c=knownMineCells;
  // break;
  // }
  // for (int i=0; i<c.size(); ++i)
  // {
  // CPoint p =(CPoint)c.get(i);
  // reply+="("+p.x+","+p.y+")";
  // }
  // }
  // reply+="\n";
  // return reply;
  // }
}