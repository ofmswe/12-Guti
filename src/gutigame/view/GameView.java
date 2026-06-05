package gutigame.view;

import gutigame.controller.GameController;
import gutigame.model.GameSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Alert;

public class GameView {

    // MVC references
    private GameController controller;
    private final GameSession session;

    // Main UI
    private final BoardCanvas canvas;
    private final StackPane root;
    private final Scene scene;

    // UI controls
    private final Label turnLabel;
    private final Label infoLabel;
    private final Button endTurnBtn;

    // Game-over overlay
    private StackPane overlay;

    public GameView(GameSession session) {
        this.session = session;
        canvas = new BoardCanvas();

        // Top bar
        turnLabel = makeLabel("", 18, Color.WHITE);
        infoLabel = makeLabel("", 13, Color.rgb(180, 220, 180));

        VBox topBar = new VBox(4, turnLabel, infoLabel);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(12, 0, 10, 0));
        topBar.setStyle("-fx-background-color: #1e2a35;");

        //rules button for v-2.0
        Button rulesBtn = makeButton("Game Rules", "#2980b9");
        rulesBtn.setOnAction(e -> showRulesPopup());

        // End-turn button
        endTurnBtn = makeButton("End Turn", "#e67e22");
        endTurnBtn.setVisible(false);
        endTurnBtn.setOnAction(e -> controller.handleEndTurn());

        // Save / Load buttons
        Button saveBtn = makeButton("Save", "#2980b9");
        saveBtn.setOnAction(e -> controller.handleSave());

        Button loadBtn = makeButton("Load", "#7f5fb5");
        loadBtn.setOnAction(e -> controller.handleLoad());

        HBox rightBox = new HBox(12, saveBtn, loadBtn);

        // Bottom bar
        BorderPane bottomBar = new BorderPane();
        bottomBar.setLeft(rulesBtn);
        bottomBar.setCenter(endTurnBtn);
        bottomBar.setRight(rightBox);

        bottomBar.setPadding(new Insets(10, 16, 10, 16));
        bottomBar.setStyle(
                "-fx-background-color: #f0ece4;" +
                        "-fx-border-color: #c8bfb0;" +
                        "-fx-border-width: 1 0 0 0;"
        );

        // Board clicks
        canvas.setOnMouseClicked(event -> {
            int[] cell = canvas.pixelToCell(event.getX(), event.getY());
            controller.handleClick(cell[0], cell[1]);
        });

        // Board container
        StackPane canvasHolder = new StackPane(canvas);
        canvasHolder.setAlignment(Pos.CENTER);
        canvasHolder.setStyle("-fx-background-color: #2c2c2c;");

        // Main layout
        BorderPane layout = new BorderPane();
        layout.setTop(topBar);
        layout.setCenter(canvasHolder);
        layout.setBottom(bottomBar);

        root = new StackPane(layout);
        scene = new Scene(root, BoardCanvas.WIDTH, BoardCanvas.HEIGHT + 100);
    }

    public void setController(GameController c) {this.controller = c;}

    public Scene getScene() {return scene;}

    public void refresh() {
        // Redraw board
        canvas.draw(
                session.getBoard(),
                controller != null ? controller.getSelectedR() : -1,
                controller != null ? controller.getSelectedC() : -1,
                session.getChainR(),
                session.getChainC(),
                session.isChainActive()
        );

        updateTopBar();
        updateBottomBar();

        // Remove old popup
        if (overlay != null) {
            root.getChildren().remove(overlay);
            overlay = null;
        }

        // Show popup if game ended
        if (session.isGameOver()) {
            showGameOverPopup();
        }
    }

    private void updateTopBar() {
        if (session.isGameOver()) {
            turnLabel.setText("12 Guti");
            turnLabel.setTextFill(Color.WHITE);
            infoLabel.setText("");
            return;
        }

        int turn = session.getCurrentTurn();

        if (turn == 1) {
            turnLabel.setText("Player-1's turn");
            turnLabel.setTextFill(Color.rgb(255, 110, 100));
        } else {
            turnLabel.setText("Player-2's turn");
            turnLabel.setTextFill(Color.rgb(100, 175, 255));
        }

        if (session.isChainActive()) infoLabel.setText("Jump again or press End turn button.");
        else {
            int p1 = session.getBoard().countPieces(1);
            int p2 = session.getBoard().countPieces(2);

            infoLabel.setText("pieces- Red: " + p1 + " Blue: " + p2 + " | Wins- P1: " + session.getWinsPlayer1() + " P2: " + session.getWinsPlayer2());
        }
    }

    // Update bottom controls
    private void updateBottomBar() {endTurnBtn.setVisible(session.isChainActive());}

    //show rules v2.0
    private void showRulesPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("12-Guti - Game Rules");
        alert.setContentText(
                "Board:\n" +
                "• The game is played on a 5×5 board with 25 positions.\n" +
                "• Each player starts with 12 pieces.\n" +
                "• Player 1 uses Red pieces.\n" +
                "• Player 2 uses Blue pieces.\n\n" +

                "Moves:\n" +
                "• Walk – Move a piece to an adjacent empty position.\n" +
                "• Jump – Jump over an opponent's piece to capture it.\n" +
                "• Chain Jump – After a successful jump, if the same piece can make another jump, the player may continue jumping with that piece in the same turn.\n\n" +

                "Winning Conditions:\n" +
                "A player wins by:\n" +
                "• Capturing all of the opponent's pieces, or\n" +
                "• Blocking the opponent so that they have no legal moves remaining.\n"
        );
        alert.showAndWait();
    }

    // Show game-over popup
    private void showGameOverPopup() {
        int winner = session.getWinner();

        Color winColor = (winner == 1) ? Color.rgb(220, 70, 60) : Color.rgb(60, 140, 220);

        Label titleLabel = makeLabel("Game Over!", 26, winColor);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 26));

        Label msgLabel = makeLabel(session.getResultMessage(), 16, Color.WHITE);

        Label scoreLabel = makeLabel("Wins — P1: " + session.getWinsPlayer1() + "   P2: " + session.getWinsPlayer2(), 13, Color.LIGHTGRAY);

        Button newRoundBtn = makeButton("New Round", "#27ae60");

        newRoundBtn.setOnAction(e -> controller.handleNewRound());

        VBox popupBox = new VBox(14, titleLabel, msgLabel, scoreLabel, newRoundBtn);

        popupBox.setAlignment(Pos.CENTER);
        popupBox.setPadding(new Insets(32));
        popupBox.setMaxWidth(320);

        popupBox.setStyle(
                "-fx-background-color: rgba(15,15,25,0.95);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: rgba(255,255,255,0.12);" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        // Background dim
        Region dimLayer = new Region();
        dimLayer.setStyle(
                "-fx-background-color: rgba(0,0,0,0.55);"
        );

        overlay = new StackPane(dimLayer, popupBox);

        root.getChildren().add(overlay);
    }

    // Create label
    private Label makeLabel(String text, int size, Color color) {
        Label label = new Label(text);

        label.setFont(Font.font("System", size));
        label.setTextFill(color);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(400);

        return label;
    }

    // Create button
    private Button makeButton(String text, String hexColor) {
        Button button = new Button(text);

        button.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        13
                )
        );

        button.setTextFill(Color.WHITE);

        button.setStyle(
                "-fx-background-color: " + hexColor + ";" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 7 16;" +
                        "-fx-cursor: hand;"
        );

        // Hover effect
        button.setOnMouseEntered(e -> button.setOpacity(0.80));
        button.setOnMouseExited(e -> button.setOpacity(1.00));

        return button;
    }
}