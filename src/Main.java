public class Main {
    public static void main(String[] args) {
        boolean playerGoesFirst = (args.length > 0 && args[0].equals("--first"));

        UI ui = new ConsoleUI();
        Board board = new Board(ui);

        Player a, b;

        if (playerGoesFirst) {
            a = new HumanPlayer(board, 1, ui);
            b = new ComputerPlayer(board, 2);
        } else {
            a = new ComputerPlayer(board, 1);
            b = new HumanPlayer(board, 2, ui);
        }

        ui.displayHello();

        // main game loop
        while (board.gameState.gameover() == 0) {
                board.display();
                a.nextMove();
                board.display();
                b.nextMove();
        }

        board.display();
        ui.displayResults(board.gameState.gameover());

        return;
    }
}
