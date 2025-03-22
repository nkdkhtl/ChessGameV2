package bot;

import java.util.List;

import main.Board;
import main.Movements;
import pieces.Piece;

public class HardBot extends Bot {
    public HardBot(Board board, boolean isHardMode, boolean isWhiteBot) {
        super(board, isHardMode, isWhiteBot);
    }

    @Override
    public Movements getMove() {
        int depth = (board.pieceList.size() > 10) ? 3 : 4;
        return getBestMove(depth);
    }

    private Movements getBestMove(int depth) {
        return minimax(depth, true).move;
    }

    private static class MoveEvaluation {
        public final Movements move;
        public final int evaluation;

        public MoveEvaluation(Movements move, int evaluation) {
            this.move = move;
            this.evaluation = evaluation;
        }
    }

    private MoveEvaluation minimax(int depth, boolean maximizing) {
        if (depth == 0 || board.isGameOver) {
            return new MoveEvaluation(null, evaluateBoard());
        }

        List<Movements> legalMoves = getAllLegalMoves();
        Movements bestMove = null;
        int bestEvaluation = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Movements move : legalMoves) {
            board.makeMove(move);
            int evaluation = minimax(depth - 1, !maximizing).evaluation;
            board.undoMove();

            if ((maximizing && evaluation > bestEvaluation) || (!maximizing && evaluation < bestEvaluation)) {
                bestEvaluation = evaluation;
                bestMove = move;
            }
        }
        return new MoveEvaluation(bestMove, bestEvaluation);
    }

    private int evaluateBoard() {
        int evaluation = 0;
        for (Piece piece : board.pieceList) {
            evaluation += getPieceValue(piece);
        }
        return evaluation;
    }

    private int getPieceValue(Piece piece) {
        switch (piece.type) {
            case PAWN: return piece.isWhite ? 1 : -1;
            case KNIGHT: return piece.isWhite ? 3 : -3;
            case BISHOP: return piece.isWhite ? 3 : -3;
            case ROOK: return piece.isWhite ? 5 : -5;
            case QUEEN: return piece.isWhite ? 9 : -9;
            case KING: return piece.isWhite ? 100 : -100;
            default: return 0;
        }
    }
}
