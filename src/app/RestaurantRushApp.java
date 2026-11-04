package app;

import boundary.RestaurantUI;
import control.GameConfig;
import control.GameController;

// Starts the Restaurant Rush game.
public class RestaurantRushApp {

    public static void main(String[] args) {

        GameConfig config = new GameConfig();
        GameController controller = new GameController(config);
        RestaurantUI ui = new RestaurantUI(controller);

        ui.run();
    }
}
