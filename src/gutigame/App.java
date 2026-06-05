package gutigame;

import gutigame.controller.GameController;
import gutigame.model.GameSession;
import gutigame.view.GameView;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        GameSession session = new GameSession();
        GameView view = new GameView(session);
        GameController controller = new GameController(session, view);
        view.setController(controller);
        view.refresh();
        stage.setTitle("12 Guti");
        stage.setScene(view.getScene());
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
