public class Main {
    public static void main(String[] args) {
        UI ui = new ConsoleUI();
        Board b = new Board(ui);
        Player p1 = new ComputerPlayer(b, 1);
        Player p2 = new HumanPlayer(b, 2);
        while (b.gameState.gameover() == 0) {
                b.display();
                p1.nextMove();
                b.display();
                p2.nextMove();
        }
        b.display();
        ui.displayResults(b.gameState.gameover());
    }
}
