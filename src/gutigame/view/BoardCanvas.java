package gutigame.view;

import gutigame.model.Board;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BoardCanvas extends Canvas {

    // Board size
    private static final int CELL   = 100;
    private static final int MARGIN = 100;

    public static final int WIDTH  = 4 * CELL + 2 * MARGIN;
    public static final int HEIGHT = 4 * CELL + 2 * MARGIN;

    // Colors
    private static final Color BG = Color.rgb(245, 240, 230);
    private static final Color LINE = Color.rgb(80, 65, 50);
    private static final Color P1_COLOR = Color.rgb(200, 50, 40);
    private static final Color P2_COLOR = Color.rgb(40, 110, 190);
    private static final Color HIGHLIGHT = Color.rgb(240, 190, 10);
    private static final Color CHAIN_COL = Color.rgb(40, 195, 100);

    public BoardCanvas() {
        super(WIDTH, HEIGHT);
    }

    // Draw board and pieces
    public void draw(Board board, int selR, int selC, int chainR, int chainC, boolean chainActive) {
        GraphicsContext gc = getGraphicsContext2D();

        gc.setFill(BG);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        drawLines(gc, board);
        drawPieces(gc, board, selR, selC, chainR, chainC, chainActive);
    }

    // Draw board connections
    private void drawLines(GraphicsContext gc, Board board) {
        gc.setStroke(LINE);
        gc.setLineWidth(2.0);

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                for (int[] nb : board.getNeighbours(r, c)) {
                    boolean isForward = nb[0] > r || (nb[0] == r && nb[1] > c);
                    if (isForward)
                        gc.strokeLine(px(c), py(r), px(nb[1]), py(nb[0]));
                }
            }
        }
    }

    // Draw all pieces
    private void drawPieces(GraphicsContext gc, Board board, int selR, int selC, int chainR, int chainC, boolean chainActive) {
        double r = CELL * 0.25;

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                int occ = board.getOccupant(row, col);

                double x = px(col);
                double y = py(row);

                if (occ == 0) continue;

                gc.setFill(occ == 1 ? P1_COLOR : P2_COLOR);

                gc.fillOval(x - r, y - r, r * 2, r * 2);

                // Highlight selected piece
                boolean isSelected = (row == selR && col == selC);

                // Highlight chain piece
                boolean isChain = (chainActive && row == chainR && col == chainC);

                if (isSelected || isChain) {
                    gc.setStroke(isChain ? CHAIN_COL : HIGHLIGHT);
                    gc.setLineWidth(3.5);
                } else {
                    gc.setStroke(LINE);
                    gc.setLineWidth(1.5);
                }

                gc.strokeOval(x - r, y - r, r * 2, r * 2);
            }
        }
    }

    // Column -> pixel
    public double px(int col) {
        return MARGIN + col * CELL;
    }

    // Row -> pixel
    public double py(int row) {
        return MARGIN + row * CELL;
    }

    // Mouse position -> board cell
    public int[] pixelToCell(double mouseX, double mouseY) {
        double snapRadius = CELL * 0.40;

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                double dx = mouseX - px(c);
                double dy = mouseY - py(r);

                if (dx * dx + dy * dy <= snapRadius * snapRadius)
                    return new int[]{r, c};
            }
        }

        return new int[]{-1, -1};
    }
}