package somipla;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class WinMineSweeper implements MineSweeper {

  private static final byte[] typesList = new byte[] { unknown, one, two,
      three, four, five, six, seven, eight, mine, mineaff, empty };

  private static int cw = 16;
  private static int ch = 16;

  private Map types = new HashMap();

  private int colCount = 30;
  private int rowCount = 16;
  private int mineCount = 99;
  private int ox;
  private int oy;
  private int curseurx = 0;
  private int curseury = 0;

  private Robot robot;
  private boolean winmineclosed = false;
  private Dimension screensize;
  private BufferedImage smiley;

  public WinMineSweeper() {
    super();
    try {
      robot = new Robot();
      screensize = Toolkit.getDefaultToolkit().getScreenSize();
      smiley = loadSmiley();
      addType("unknown", unknown);
      addType("empty", empty);
      addType("1", one);
      addType("2", two);
      addType("3", three);
      addType("4", four);
      addType("5", five);
      addType("6", six);
      addType("7", seven);
      addType("8", eight);
      addType("flag", mine);
      addType("mine", mineaff);
      addType("minerouge", mineaff);

      final Process process = Runtime.getRuntime().exec("winmine.exe");
      new Thread() {
        @Override
        public void run() {
          try {
            process.waitFor();
            System.out.println("WINMINE ferm�");
            winmineclosed = true;
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }.start();

      int i = 0;
      Point p;
      do {
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          // don't care
        }
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_P);
        robot.keyRelease(KeyEvent.VK_P);
        robot.keyPress(KeyEvent.VK_E);
        robot.keyRelease(KeyEvent.VK_E);
        robot.keyRelease(KeyEvent.VK_ALT);
        p = findSmiley();
      } while (p.x == 0 && p.y == 0 && i++ < 10);
      if (p.x == 0 && p.y == 0) {
        throw new RuntimeException("winmine not found");
      }
      ox = p.x - 230;
      oy = p.y + 37;

    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (AWTException e) {
      throw new RuntimeException(e);
    }
  }

  private Point findSmiley() {
    BufferedImage img = robot.createScreenCapture(new Rectangle(screensize));

    // Graphics2D g = img.createGraphics();
    int x = 0;
    int y = 0;
    boolean imgeq = true;
    for (int i = 0; i < screensize.width - 20; ++i) {
      for (int j = 0; j < screensize.height - 20; ++j) {
        imgeq = true;
        for (int ii = 0; ii < 20; ++ii) {
          for (int ij = 0; ij < 20; ++ij) {
            if (img.getRGB(i + ii, j + ij) != smiley.getRGB(ii, ij)) {
              imgeq = false;
              break;
            }
          }
          if (!imgeq) {
            break;
          }
        }
        if (imgeq) {
          y = j;
          break;
        }
      }
      if (imgeq) {
        x = i;
        break;
      }
    }
    return new Point(x, y);
  }

  private BufferedImage loadSmiley() {
    InputStream is = Play.class.getResourceAsStream("icons/smiley");
    BufferedImage bi;
    try {
      try {
        bi = new BufferedImage(20, 20, 1);
        bi.setRGB(0, 0, 20, 20, (int[]) new ObjectInputStream(is).readObject(),
            0, 20);
      } finally {
        is.close();
      }
    } catch (IOException e) {
      // never happens
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      // never happens
      throw new RuntimeException(e);
    }
    return bi;
  }

  private void addType(String fileName, byte type) {
    InputStream is = this.getClass().getResourceAsStream(
        "icons/" + fileName + ".t");
    try {
      try {
        int[] typeArray = (int[]) new ObjectInputStream(is).readObject();

        BufferedImage ct = new BufferedImage(cw, ch, 1);
        ct.setRGB(0, 0, cw, ch, typeArray, 0, cw);
        types.put(new Byte(type), ct);
      } finally {
        is.close();
      }
    } catch (IOException e) {
      // never happens
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      // never happens
      throw new RuntimeException(e);
    }
  }

  private BufferedImage screenShot() throws MineSweeperClosedException {
    if (winmineclosed) {
      throw new MineSweeperClosedException();
    }
    return robot.createScreenCapture(new Rectangle(ox, oy, 480, 256));
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.somipla.MineSweeper#readState(byte[][])
   */
  @Override
  public boolean readState(byte[][] mines) throws MineSweeperClosedException {
    BufferedImage pad = screenShot();
    for (int i = 0; i < mines.length; ++i) {
      for (int j = 0; j < mines[i].length; ++j) {
        if (mines[i][j] == unknown) {
          mines[i][j] = getType(pad, i, j);
          if (mines[i][j] == mineaff) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private static final int[] abs = new int[] { 0, 3, 6 };
  private static final int[] ord = new int[] { 0, 4, 6 };

  private byte getType(BufferedImage im, int x, int y) {
    BufferedImage test = im.getSubimage(x * cw, y * ch, cw, ch);
    boolean found = true;
    for (int t = 0; t < typesList.length; ++t) {
      found = true;
      BufferedImage ct = (BufferedImage) types.get(new Byte(typesList[t]));
      for (int i = 0; i < abs.length; i++) {
        if (ct.getRGB(abs[i], ord[i]) != test.getRGB(abs[i], ord[i])) {
          found = false;
          break;
        }
      }

      if (found) {
        return typesList[t];
      }
    }

    return unknown;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.somipla.MineSweeper#getColCount()
   */
  @Override
  public int getColCount() {
    return colCount;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.somipla.MineSweeper#getRowCount()
   */
  @Override
  public int getRowCount() {
    return rowCount;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.somipla.MineSweeper#resetGame()
   */
  @Override
  public void resetGame() {
    // Thread.(10000);
    robot.keyPress(KeyEvent.VK_ENTER);
    robot.keyRelease(KeyEvent.VK_ENTER);
    robot.keyPress(KeyEvent.VK_ENTER);
    robot.keyRelease(KeyEvent.VK_ENTER);
  }

  private void clic(int x, int y, int buttons)
      throws MineSweeperClosedException {
    if (winmineclosed) {
      throw new MineSweeperClosedException();
    }
    // interpole(ox+(curseurx*cw)+cw/2,oy+(curseury*ch)+ch/2,ox+(x*cw)+cw/2,oy+(y*ch)+ch/2);
    robot.mouseMove(ox + (x * cw) + cw / 2, oy + (y * ch) + ch / 2);
    curseurx = x;
    curseury = y;
    robot.mousePress(buttons);
    robot.mouseRelease(buttons);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.somipla.MineSweeper#clic(int, int)
   */
  @Override
  public void clic(int x, int y) throws MineSweeperClosedException {
    clic(x, y, InputEvent.BUTTON1_MASK);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.somipla.MineSweeper#mark(int, int)
   */
  @Override
  public void mark(int x, int y) throws MineSweeperClosedException {
    clic(x, y, InputEvent.BUTTON3_MASK);
  }

  private void interpole(int fromx, int fromy, int tox, int toy) {
    int length = (int) Math.sqrt(Math.pow(fromx - tox, 2)
        + Math.pow(fromy - toy, 2));
    for (int i = 0; i < length; ++i) {
      robot.mouseMove(fromx + ((tox - fromx) * i) / length, fromy
          + ((toy - fromy) * i) / length);
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        // don't care
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.somipla.MineSweeper#newGame()
   */
  @Override
  public void newGame() throws MineSweeperClosedException {
    if (winmineclosed) {
      throw new MineSweeperClosedException();
    }
    robot.keyPress(KeyEvent.VK_F2);
    robot.keyRelease(KeyEvent.VK_F2);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.somipla.MineSweeper#endGame()
   */
  @Override
  public void endGame() {
    if (!winmineclosed) {
      robot.keyPress(KeyEvent.VK_ALT);
      robot.keyPress(KeyEvent.VK_F4);
      robot.keyRelease(KeyEvent.VK_F4);
      robot.keyRelease(KeyEvent.VK_ALT);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.somipla.MineSweeper#getMineCount()
   */
  @Override
  public int getMineCount() {
    return mineCount;
  }
}
