package gutigame.controller;

import gutigame.model.Board;
import gutigame.model.GameSession;
import gutigame.view.GameView;

public class GameController {

    private final GameSession session;
    private final GameView view;

    // Selected piece
    private int selectedR = -1;
    private int selectedC = -1;

    public GameController(GameSession session, GameView view) {
        this.session = session;
        this.view = view;
    }

    public void handleClick(int row, int col) {
        // Ignore invalid clicks
        if (row < 0 || session.isGameOver()) return;

        Board board = session.getBoard();
        int turn = session.getCurrentTurn();

        // Chain capture mode
        if (session.isChainActive()) {
            int cr = session.getChainR();
            int cc = session.getChainC();

            // Select chain piece
            if (row == cr && col == cc) {
                selectedR = cr;
                selectedC = cc;
            }

            // Try move
            else if (selectedR >= 0) {
                boolean moved = session.applyMove(selectedR, selectedC, row, col);

                if (moved && session.isChainActive()) {
                    selectedR = session.getChainR();
                    selectedC = session.getChainC();
                }
                else if (moved) {
                    selectedR = selectedC = -1;
                }
            }

            view.refresh();
            return;
        }

        // Select own piece
        if (board.getOccupant(row, col) == turn) {

            // Deselect
            if (row == selectedR && col == selectedC) {
                selectedR = selectedC = -1;
            }

            // Select
            else {
                selectedR = row;
                selectedC = col;
            }
        }

        // Try move
        else if (selectedR >= 0) {

            boolean moved = session.applyMove(selectedR, selectedC, row, col);

            if (moved && session.isChainActive()) {
                selectedR = session.getChainR();
                selectedC = session.getChainC();
            }
            else {
                selectedR = selectedC = -1;
            }
        }

        view.refresh();
    }

    // End chain turn
    public void handleEndTurn() {
        session.endChainEarly();
        selectedR = selectedC = -1;
        view.refresh();
    }

    // Start new round
    public void handleNewRound() {
        session.startNewRound();
        selectedR = selectedC = -1;
        view.refresh();
    }

    // Save game
    public void handleSave() {
        if (session.saveGame()) System.out.println("Game saved!");
        else System.out.println("Save failed!");
    }

    // Load game
    public void handleLoad() {
        if (session.loadGame()) {
            selectedR = selectedC = -1;
            view.refresh();
        }
    }
    public int getSelectedR() {return selectedR;}
    public int getSelectedC() {return selectedC;}
}