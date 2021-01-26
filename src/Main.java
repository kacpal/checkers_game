public class Main {
    public static void main(String[] args) {
        boolean aiOnly = false, playerGoesFirst = false, playersOnly = false;
        int boardSize = 10, pawnRows = 4, difficulty = 4;
        String interspace = "\t";

        for (String argument : args) {
            if (argument.equals("--first"))     playerGoesFirst = true;
            if (argument.equals("--ai-only"))   aiOnly = true;
            if (argument.equals("--players-only"))  playersOnly = true;
            if (argument.equals("--spaces"))    interspace = " ";
            if (argument.equals("--hard"))      difficulty = 5;
            if (argument.equals("--easy"))      difficulty = 3;
            if (argument.equals("--board-8")) {
                boardSize = 8;
                pawnRows = 3;
            } if (argument.equals("--board-12")) {
                boardSize = 12;
                pawnRows = 5;
            }
        }

        UI ui = new ConsoleUI(interspace);
        Board board = new Board(ui, boardSize, pawnRows);

        Player a, b;

        if (playersOnly) {
            a = new HumanPlayer(board, Pawn.BLACK, ui);
            b = new HumanPlayer(board, Pawn.WHITE, ui);
        } else if (aiOnly) {
            a = new ComputerPlayer(board, Pawn.BLACK, difficulty, ui);
            b = new ComputerPlayer(board, Pawn.WHITE, difficulty, ui);
        } else if (playerGoesFirst) {
            a = new HumanPlayer(board, Pawn.BLACK, ui);
            b = new ComputerPlayer(board, Pawn.WHITE, difficulty, ui);
        } else {
            a = new ComputerPlayer(board, Pawn.BLACK, difficulty, ui);
            b = new HumanPlayer(board, Pawn.WHITE, ui);
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
