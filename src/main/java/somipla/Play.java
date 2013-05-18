package somipla;

import java.util.*;

/**
 * <p>
 * Title: Win Mine Player
 * </p>
 * <p>
 * Description: Plays winmine
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author Julien Le Dem
 * @version 0.3
 */

public class Play {

  static MineSweeper mineSweeper;

  static int totminecount = 0;
  static int won = 0;
  static long origin;

  private static byte[][] mines;
  private static boolean[][] done;
  private static CellReport[][] reports;

  private static int minecount;

  public static void main(String[] args) throws Exception {
    try {
      mineSweeper = new WinMineSweeper();
      mines = new byte[mineSweeper.getColCount()][mineSweeper.getRowCount()];
      done = new boolean[mineSweeper.getColCount()][mineSweeper.getRowCount()];
      reports = new CellReport[mineSweeper.getColCount()][mineSweeper
          .getRowCount()];

      origin = System.currentTimeMillis();
      for (int c = 0; c < 100; ++c) {
        minecount = 0;
        for (int i = 0; i < mines.length; ++i) {
          for (int j = 0; j < mines[i].length; ++j) {
            mines[i][j] = MineSweeper.unknown;
            done[i][j] = false;
            reports[i][j] = null;
          }
        }
        boolean finished = mineSweeper.readState(mines);
        do {
          if (unknownCount() == 0) {
            won++;
            mineSweeper.resetGame();
            minecount = mineSweeper.getMineCount();
            break;
          }
          int rx;
          int ry;
          do {
            rx = (int) (Math.random() * mineSweeper.getColCount());
            ry = (int) (Math.random() * mineSweeper.getRowCount());
          } while (mines[rx][ry] != MineSweeper.unknown);
          mineSweeper.clic(rx, ry);

          do {
            finished = mineSweeper.readState(mines);
          } while (!finished && cogite());
        } while (!finished);

        long timeSpent = System.currentTimeMillis() - origin;
        totminecount += minecount;
        System.out
            .println(minecount == mineSweeper.getMineCount() ? "Partie finie à 100% : GAGNE ! "
                : "Partie finie à "
                    + (minecount * 100 / mineSweeper.getMineCount()) + "%");
        System.out.println("Temps écoulé : " + (timeSpent / 1000) + " s");
        System.out.println("gagnées : " + won + "/" + (c + 1)
            + " taux de réussite : " + (won * 100 / (c + 1)) + "%");
        System.out.println(((c + 1) * 60000 / timeSpent)
            + " parties par minute, " + (won * 60000 / timeSpent)
            + " parties gagnées par minute");
        System.out.println((timeSpent / 1000 == 0 ? 0
            : (totminecount / (timeSpent / 1000)))
            + " mines découvertes par seconde");
        System.out.println("taux moyen de résolution de partie : "
            + (totminecount * 100 / ((c + 1) * mineSweeper.getMineCount()))
            + "%");
        mineSweeper.newGame();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      mineSweeper.endGame();
    }
  }

  static int unknownCount() {
    int count = 0;
    for (int i = 0; i < mines.length; ++i) {
      for (int j = 0; j < mines[i].length; ++j) {
        if (mines[i][j] == MineSweeper.unknown)
          count++;
      }
    }
    return count;
  }

  static boolean cogite() throws Exception {
    boolean changed = false;
    for (int i = 0; i < mines.length; ++i) {
      for (int j = 0; j < mines[i].length; ++j) {
        if (mines[i][j] != MineSweeper.unknown) {
          if (mines[i][j] > 0 && !done[i][j]) {
            CellReport c = getReport(i, j);
            reports[i][j] = c;
            if ((mines[i][j] - c.getMineCount()) == c.getUnknownCount()
                && c.getUnknownCount() > 0) {
              Point[] unknowns = c.getUnknown();
              for (int k = 0; k < unknowns.length; ++k) {
                mark(unknowns[k].x, unknowns[k].y);
              }
              done[i][j] = true;
              changed = true;
            } else if (c.getMineCount() == mines[i][j]
                && c.getUnknownCount() > 0) {
              Point[] unknowns = c.getUnknown();
              for (int k = 0; k < unknowns.length; ++k) {
                mineSweeper.clic(unknowns[k].x, unknowns[k].y);
              }
              done[i][j] = true;
              return true;
            }
          }
        }
      }
    }

    if (!changed) {
      changed = cogite2();
    }

    return changed;
  }

  static boolean cogite2() throws Exception {
    boolean changed = false;
    for (int i = 0; i < mines.length; ++i) {
      for (int j = 0; j < mines[i].length; ++j) {
        if (mines[i][j] == MineSweeper.unknown) {
          CellReport[] reports = getNearReports(i, j);
          if (reports.length > 1) {
            Vector sets = new Vector();
            for (int i2 = 0; i2 < reports.length; ++i2) {
              Set currentSet = new HashSet();
              currentSet.addAll(Arrays.asList(reports[i2].getUnknown()));
              sets.add(currentSet);
            }
            for (int i2 = 0; i2 < sets.size() - 1; ++i2) {
              Set a = (Set) sets.get(i2);
              CellReport ra = reports[i2];
              for (int j2 = i2 + 1; j2 < sets.size(); ++j2) {
                Set b = (Set) sets.get(j2);
                CellReport rb = reports[j2];
                if (mines[ra.x][ra.y] - ra.getMineCount() == mines[rb.x][rb.y]
                    - rb.getMineCount()) {
                  Set diff = new HashSet();
                  if (a.containsAll(b)) {
                    diff.addAll(a);
                    diff.removeAll(b);
                  } else if (b.containsAll(a)) {
                    diff.addAll(b);
                    diff.removeAll(a);
                  }
                  if (diff.size() > 0) {
                    Point[] toclick = (Point[]) diff.toArray(new Point[diff
                        .size()]);
                    for (int i3 = 0; i3 < toclick.length; i3++) {
                      mineSweeper.clic(toclick[i3].x, toclick[i3].y);
                    }
                    return true;
                  }
                } else {
                  int ca = mines[ra.x][ra.y] - ra.getMineCount();
                  int cb = mines[rb.x][rb.y] - rb.getMineCount();
                  Set diff = new HashSet();
                  int togo = 0;
                  if (ca > cb) {
                    diff.addAll(a);
                    diff.removeAll(b);
                    togo = ca - cb;
                  } else {
                    diff.addAll(b);
                    diff.removeAll(a);
                    togo = cb - ca;
                  }
                  if (diff.size() > 0 && diff.size() == togo) {
                    Point[] tomark = (Point[]) diff.toArray(new Point[diff
                        .size()]);
                    for (int i3 = 0; i3 < tomark.length; i3++) {
                      mark(tomark[i3].x, tomark[i3].y);
                    }
                    return true;
                  }
                }
              }
            }
          }
        }
      }
    }

    return changed;
  }

  static void addNearReport(int i, int j, Vector nreports) {
    if (!done[i][j] && reports[i][j] != null
        && reports[i][j].getUnknownCount() > 0) {
      nreports.add(reports[i][j]);
    }
  }

  static void addColNearReport(int j, int c, Vector nreports) {
    if (j != 0) {
      addNearReport(c, j - 1, nreports);
    }

    addNearReport(c, j, nreports);

    if (j != mineSweeper.getRowCount() - 1) {
      addNearReport(c, j + 1, nreports);
    }
  }

  public static CellReport[] getNearReports(int i, int j) {
    Vector nreports = new Vector();

    if (i != 0) {
      addColNearReport(j, i - 1, nreports);
    }

    addColNearReport(j, i, nreports);

    if (i != mineSweeper.getColCount() - 1) {
      addColNearReport(j, i + 1, nreports);
    }

    return (CellReport[]) nreports.toArray(new CellReport[nreports.size()]);
  }

  static void addColReport(int i, int j, int c, CellReport report) {
    if (j != 0) {
      report.add(c, j - 1, mines[c][j - 1]);
    }

    report.add(c, j, mines[c][j]);

    if (j != mineSweeper.getRowCount() - 1) {
      report.add(c, j + 1, mines[c][j + 1]);
    }
  }

  static CellReport getReport(int i, int j) {
    CellReport report = new CellReport(i, j);
    if (i != 0) {
      addColReport(i, j, i - 1, report);
    }

    addColReport(i, j, i, report);

    if (i != mineSweeper.getColCount() - 1) {
      addColReport(i, j, i + 1, report);
    }
    return report;
  }

  static void mark(int x, int y) {
    if (mines[x][y] != MineSweeper.mine) {
      // mineSweeper.mark(x,y);
      mines[x][y] = MineSweeper.mine;
      minecount++;
    }
  }
}