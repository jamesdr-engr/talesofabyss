package talesofabyss;

public class TalesOfTheAbyss {
    public static void main(String[] args) {
        LoginSystem loginSystem = new LoginSystem();
        if (loginSystem.start()) {
            Game game = new Game(loginSystem);
            game.start();
        }
        loginSystem.getDatabaseManager().close();
    }
}
