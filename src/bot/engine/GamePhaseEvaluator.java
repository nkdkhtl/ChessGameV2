package bot.engine;

import GameLauncher.Board;

/**
 * Interface for phase-specific evaluation
 */
public interface GamePhaseEvaluator {
    int evaluate(Board board);
}