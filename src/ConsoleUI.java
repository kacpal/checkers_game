import java.io.Serializable;

public class ConsoleUI implements UI, Serializable {
    @Override
    public void displayBoard(Board b) {
        System.out.print("-\t");
        var tab = b.tab;
        for (int i = 0; i < tab[0].length; i++)
            System.out.print(i + "\t");
        System.out.println();
        for (int y = 0; y < tab.length; y++) {
            System.out.print((char)(y+'a') + "\t");
            for (int x = 0; x < tab[y].length; x++) {
                String c;
                switch (tab[y][x].content) {
                    case 0: c = "."; break;
                    case 1: c = "x"; break;
                    case 2: c = "o"; break;
                    case 5: c = "X"; break;
                    case 6: c = "O"; break;
                    default: c = " "; break;
                }
                System.out.print(c + "\t");
            }
            System.out.println();
        }
    }

    @Override
    public void displayResults(int result) {
        System.out.println("Game finished. The result is: ");
        if (result == 3) System.out.println("DRAW");
        if (result == 2) System.out.println("PLAYER 2 WINS");
        if (result == 1) System.out.println("PLAYER 1 WINS");
    }
}
