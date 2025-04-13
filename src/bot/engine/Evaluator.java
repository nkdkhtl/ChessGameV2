package bot.engine;

import GameLauncher.Board;
import pieces.Piece;

/**
 * Position evaluation engine
 * @author nkdkhtl
 * @version 2025-04-13
 */
public class Evaluator {
    // Piece values
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 300;
    private static final int BISHOP_VALUE = 320;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 10000;
    
    // Positional bonuses
    private static final int CENTER_PAWN_BONUS = 40;
    private static final int DEVELOPMENT_BONUS = 30;
    private static final int EARLY_QUEEN_PENALTY = -30;
    private static final int KING_SAFETY_BONUS = 50;
    
    private final GamePhaseEvaluator openingEval;
    private final GamePhaseEvaluator middlegameEval;
    private final GamePhaseEvaluator endgameEval;
    
    public Evaluator() {
        this.openingEval = new OpeningEvaluator();
        this.middlegameEval = new MiddlegameEvaluator();
        this.endgameEval = new EndgameEvaluator();
    }
    
    public int evaluate(Board board) {
        int moveNumber = board.getFullmoveNumber();
        int evaluation = evaluateMaterial(board);
        
        if (moveNumber <= 10) {
            evaluation += openingEval.evaluate(board);
        } else if (moveNumber <= 30) {
            evaluation += middlegameEval.evaluate(board);
        } else {
            evaluation += endgameEval.evaluate(board);
        }
        
        return evaluation;
    }
    
    private int evaluateMaterial(Board board) {
        int eval = 0;
        for (Piece piece : board.pieceList) {
            eval += getPieceValue(piece) + piece.getPositionValue();
        }
        return eval;
    }
    
    private int getPieceValue(Piece piece) {
        int value = switch (piece.type) {
            case PAWN -> PAWN_VALUE;
            case KNIGHT -> KNIGHT_VALUE;
            case BISHOP -> BISHOP_VALUE;
            case ROOK -> ROOK_VALUE;
            case QUEEN -> QUEEN_VALUE;
            case KING -> KING_VALUE;
        };
        return piece.isWhite ? value : -value;
    }
}