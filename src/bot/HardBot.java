package bot;

import GameLauncher.Board;
import GameLauncher.Movements;
import bot.engine.SearchEngine;

public class HardBot extends Bot {
    private final SearchEngine engine;
    
    public HardBot(Board board, boolean isHardMode, boolean isWhiteBot) {
        super(board, isHardMode, isWhiteBot);
        this.engine = new SearchEngine();
    }
    
    @Override
    public Movements getMove() {
        System.out.println("HardBot is thinking...");
        Movements bestMove = engine.findBestMove(board.deepCopy(), isWhiteBot);
        System.out.println("Move found: " + describeMove(bestMove));
        return bestMove;
    }
    
    private String describeMove(Movements move) {
        return String.format("%s (%d,%d) -> (%d,%d)",
            move.piece.type,
            move.oldCol, move.oldRow,
            move.newCol, move.newRow);
    }
}