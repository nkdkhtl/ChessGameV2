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
        Movements bestMove = engine.findBestMove(board.deepCopy(), isWhiteBot);
        return bestMove;
    }
}