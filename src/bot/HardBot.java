package bot;

import java.util.List;

import GameLauncher.Board;
import GameLauncher.Movements;
import pieces.Piece;

public class HardBot extends Bot {

    public HardBot(Board board, boolean isHardMode, boolean isWhiteBot) {
        super(board, isHardMode, isWhiteBot);
    }

    @Override
    public Movements getMove() {
        int depth = (board.pieceList.size() > 10) ? 3 : 4;
        System.out.println("HardBot is thinking with depth: " + depth);
        Movements bestMove = getBestMove(depth);
        return bestMove;
    }
    
    private Movements getBestMove(int depth) {
        return minimax(board.deepCopy(), depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE).move;
    }
    
    private static class MoveEvaluation {
        public final Movements move;
        public final int evaluation;

        public MoveEvaluation(Movements move, int evaluation) {
            this.move = move;
            this.evaluation = evaluation;
        }
    }
    
    private MoveEvaluation minimax(Board virtualBoard, int depth, boolean maximizing, int alpha, int beta) {
        if (depth == 0 || virtualBoard.isGameOver) {
            return new MoveEvaluation(null, evaluateBoard(virtualBoard));
        }

        List<Movements> legalMoves = getAllLegalMoves(virtualBoard);
        Movements bestMove = null;
        int bestEvaluation = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Movements move : legalMoves) {
            virtualBoard.makeMove(move);
            int evaluation = minimax(virtualBoard.deepCopy(), depth - 1, !maximizing, alpha, beta).evaluation;
            virtualBoard.undoMove();

            if (maximizing) {
                if (evaluation > bestEvaluation) {
                    bestEvaluation = evaluation;
                    bestMove = move;
                }
                alpha = Math.max(alpha, bestEvaluation);
            } else {
                if (evaluation < bestEvaluation) {
                    bestEvaluation = evaluation;
                    bestMove = move;
                }
                beta = Math.min(beta, bestEvaluation);
            }

            if (beta <= alpha) {
                break; // Alpha-beta pruning
            }
        }
        return new MoveEvaluation(bestMove, bestEvaluation);
    }
    
    private int evaluateBoard(Board virtualBoard) {
        int evaluation = 0;
        for (Piece piece : virtualBoard.pieceList) {
            evaluation += getPieceValue(piece);
        }
        return evaluation;
    }

    private int getPieceValue(Piece piece) {
        switch (piece.type) {
            case PAWN: return piece.isWhite ? 100 : -100;
            case KNIGHT: return piece.isWhite ? 300 : -300;
            case BISHOP: return piece.isWhite ? 320 : -320;
            case ROOK: return piece.isWhite ? 500 : -500;
            case QUEEN: return piece.isWhite ? 900 : -900;
            case KING: return piece.isWhite ? 10000 : -10000;
            default: return 0;
        }
    }
}