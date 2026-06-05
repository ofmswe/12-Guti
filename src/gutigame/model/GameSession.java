package gutigame.model;

import java.io.*;
import java.util.Random;

public class GameSession {

    private Board board;
    private int currentTurn;
    private boolean gameOver;
    private int winner;
    private String resultMessage = "";

    private boolean chainActive;
    private int chainR = -1, chainC = -1;

    private int winsPlayer1 = 0;
    private int winsPlayer2 = 0;

    private static final String SAVE_FILE = "game_save.dat";

    public GameSession() {
        startNewRound();
    }

    public void startNewRound() {
        board = new Board();
        Random r = new Random(); currentTurn = r.nextInt(2) + 1;
        gameOver = false;
        winner = 0;
        resultMessage = "";
        chainActive = false;
        chainR = chainC = -1;
    }

    public boolean applyMove(int fr, int fc, int tr, int tc) {
        if (gameOver) return false;

        if (chainActive && (fr != chainR || fc != chainC)) return false;

        if (board.isSimpleMove(fr, fc, tr, tc)) {
            if (chainActive) return false; // chain mode এ শুধু capture
            board.applySimple(fr, fc, tr, tc);
            switchTurn();
            return true;
        }

        if (board.isCaptureMove(fr, fc, tr, tc, currentTurn)) {
            board.applyCapture(fr, fc, tr, tc);
            checkWin();
            if (!gameOver && board.canJumpAgain(tr, tc)) {
                chainActive = true;
                chainR = tr;
                chainC = tc;
            } else {
                chainActive = false;
                chainR = chainC = -1;
                if (!gameOver) switchTurn();
            }
            return true;
        }

        return false;
    }

    public void endChainEarly() {
        if (!chainActive) return;
        chainActive = false;
        chainR = chainC = -1;
        switchTurn();
    }

    private void switchTurn() {
        currentTurn = Board.opponent(currentTurn);
        checkWin();
    }

    private void checkWin() {
        int p1 = board.countPieces(1);
        int p2 = board.countPieces(2);

        if (p1 == 0) {
            declareWinner(2, "Player-2(Blue) is winner!\nAll red pieces are finished.");
        } else if (p2 == 0) {
            declareWinner(1, "Player-1(Red) is winner!\nAll blue pieces are finished.");
        } else if (!board.hasLegalMove(currentTurn)) {
            int w = Board.opponent(currentTurn);
            declareWinner(w, w == 1 ? "Player-1(Red) is winner!\nPlayer-2 has no move." : "Player-2(Blue) is winner!\nPlayer-1 has no move.");
        }
    }

    private void declareWinner(int w, String msg) {
        gameOver      = true;
        winner        = w;
        resultMessage = msg;
        if (w == 1) winsPlayer1++;
        else        winsPlayer2++;
    }

    //Save-Load

    public boolean saveGame() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SAVE_FILE))) {
            pw.println(winsPlayer1);
            pw.println(winsPlayer2);
            pw.println(currentTurn);
            pw.println(gameOver);
            for (int r = 0; r < 5; r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < 5; c++) {
                    if (c > 0) sb.append(',');
                    sb.append(board.getOccupant(r, c));
                }
                pw.println(sb);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            winsPlayer1 = Integer.parseInt(br.readLine().trim());
            winsPlayer2 = Integer.parseInt(br.readLine().trim());
            currentTurn = Integer.parseInt(br.readLine().trim());
            gameOver    = Boolean.parseBoolean(br.readLine().trim());
            board       = new Board();
            for (int r = 0; r < 5; r++) {
                String[] cells = br.readLine().trim().split(",");
                for (int c = 0; c < 5; c++)
                    board.setOccupant(r, c, Integer.parseInt(cells[c]));
            }
            winner = 0; resultMessage = "";
            chainActive = false; chainR = chainC = -1;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Getters
    public Board getBoard() { return board; }
    public int getCurrentTurn() { return currentTurn; }
    public boolean isGameOver() { return gameOver; }
    public int getWinner() { return winner; }
    public String getResultMessage() { return resultMessage; }
    public boolean isChainActive() { return chainActive; }
    public int getChainR() { return chainR; }
    public int getChainC() { return chainC; }
    public int getWinsPlayer1() { return winsPlayer1; }
    public int getWinsPlayer2() { return winsPlayer2; }
}
