package bot.engine;

import GameLauncher.Board;

public interface GamePhaseEvaluator {
    int evaluate(Board board);
}