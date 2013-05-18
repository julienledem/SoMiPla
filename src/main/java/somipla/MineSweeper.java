package somipla;

public interface MineSweeper {

  static final byte unknown = -1;

  static final byte mine = -2;

  static final byte mineaff = -4;

  static final byte empty = 0;

  static final byte one = 1;

  static final byte two = 2;

  static final byte three = 3;

  static final byte four = 4;

  static final byte five = 5;

  static final byte six = 6;

  static final byte seven = 7;

  static final byte eight = 8;

  boolean readState(byte[][] mines) throws MineSweeperClosedException;

  int getColCount();

  int getRowCount();

  void resetGame();

  void clic(int x, int y) throws MineSweeperClosedException;

  void mark(int x, int y) throws MineSweeperClosedException;

  public abstract void newGame() throws MineSweeperClosedException;

  public abstract void endGame();

  public abstract int getMineCount();

}