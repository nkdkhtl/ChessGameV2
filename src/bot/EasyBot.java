package bot;

import java.util.List;
import java.util.Random;

import main.Board;
import main.Movements;

public class EasyBot extends Bot {
    public EasyBot(Board board, boolean isHardMode, boolean isWhiteBot) {
        super(board, isHardMode, isWhiteBot);
    }

    public Movements getRandomMove() {
        List<Movements> legalMoves = getAllLegalMoves(board.deepCopy());

        // Debugging: Print all available moves
        System.out.println("Total legal moves found: " + legalMoves.size());
        for (Movements move : legalMoves) {
            System.out.printf(" %s -> (%d, %d) \n", move.piece.type, move.newRow, move.newCol);
        }

        // Check if we still get an empty list
        if (legalMoves.isEmpty()) {
            System.out.println("No legal moves found! Returning null.");
            return null;
        }

        // Pick a random move
        Movements selectedMove = legalMoves.get(new Random().nextInt(legalMoves.size()));
        System.out.printf("Randomly selected move: %s -> (%d, %d)\n", 
                          selectedMove.piece.type, selectedMove.newRow, selectedMove.newCol);
        
        return selectedMove;
    }

    @Override
    public Movements getMove() {
        return getRandomMove();
    }
}
