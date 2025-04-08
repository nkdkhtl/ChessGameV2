package bot;

import java.util.List;

import GameLauncher.Board;
import GameLauncher.Movements;
import GameLauncher.Type;
import pieces.Piece;

public class HardBot extends Bot {

    public HardBot(Board board, boolean isHardMode, boolean isWhiteBot) {
        super(board, isHardMode, isWhiteBot);
    }

    @Override
    public Movements getMove() {
        int depth = calculateDepth();
        System.out.println("HardBot is thinking with depth: " + depth);
        MoveEvaluation bestMoveEval = getBestMove(depth);
        System.out.println("Best move found: " + bestMoveEval.move + " with evaluation: " + bestMoveEval.evaluation);
        return bestMoveEval.move;
    }

    private int calculateDepth() {
        return (board.pieceList.size() > 10) ? 3 : 4;
    }

    private MoveEvaluation getBestMove(int depth) {
        return max(board.deepCopy(), depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
    }

    private static class MoveEvaluation {
        public final Movements move;
        public final int evaluation;

        public MoveEvaluation(Movements move, int evaluation) {
            this.move = move;
            this.evaluation = evaluation;
        }
    }

    private MoveEvaluation max(Board virtualBoard, int depth, int alpha, int beta, int currentDepth) {
        if (depth == 0 || virtualBoard.isGameOver) {
            int eval = evaluateBoard(virtualBoard);
            return new MoveEvaluation(null, eval);
        }

        List<Movements> legalMoves = getAllLegalMoves(virtualBoard);
        Movements bestMove = null;
        int bestEvaluation = Integer.MIN_VALUE;

        legalMoves.sort((m1, m2) -> evaluateMove(virtualBoard, m2) - evaluateMove(virtualBoard, m1)); //Move ordering

        for (Movements move : legalMoves) {
            virtualBoard.makeMove(move);
            int evaluation = min(virtualBoard.deepCopy(), depth - 1, alpha, beta, currentDepth + 1).evaluation;
            virtualBoard.undoMove();

            if (evaluation >= bestEvaluation) {
                bestEvaluation = evaluation;
                bestMove = move;
            }
            alpha = Math.max(alpha, bestEvaluation);
            if (beta <= alpha) {
                break; // Alpha-beta pruning
            }
        }
        return new MoveEvaluation(bestMove, bestEvaluation);
    }

    private MoveEvaluation min(Board virtualBoard, int depth, int alpha, int beta, int currentDepth) {
        if (depth == 0 || virtualBoard.isGameOver) {
            int eval = evaluateBoard(virtualBoard);
            return new MoveEvaluation(null, eval);
        }

        List<Movements> legalMoves = getAllLegalMoves(virtualBoard);
        Movements bestMove = null;
        int bestEvaluation = Integer.MAX_VALUE;

        legalMoves.sort((m1, m2) -> evaluateMove(virtualBoard, m1) - evaluateMove(virtualBoard, m2)); //Move ordering

        for (Movements move : legalMoves) {
            virtualBoard.makeMove(move);
            int evaluation = max(virtualBoard.deepCopy(), depth - 1, alpha, beta, currentDepth + 1).evaluation;
            virtualBoard.undoMove();

            if (evaluation <= bestEvaluation) {
                bestEvaluation = evaluation;
                bestMove = move;
            }
            beta = Math.min(beta, bestEvaluation);
            if (beta <= alpha) {
                break; // Alpha-beta pruning
            }
        }
        return new MoveEvaluation(bestMove, bestEvaluation);
    }

    private int evaluateBoard(Board virtualBoard) {
        int evaluation = 0;
        for (Piece piece : virtualBoard.pieceList) {
            evaluation += getPieceValue(piece) + piece.getPositionValue();
            evaluation += getPawnStructure(virtualBoard, piece);
        }
        evaluation += getPawnControlCenter(virtualBoard);
        return evaluation;
    }
    
    private int getPawnControlCenter(Board virtualBoard) {
        int evaluation = 0;
        int[] centerSquares = {27, 28, 35, 36}; // d4, e4, d5, e5 (Assuming 0-63 index)

        for (int square : centerSquares) {
            int row = square / 8;
            int col = square % 8;

            for (Piece piece : virtualBoard.pieceList) {
                if (piece.type == Type.PAWN) {
                    if (piece.isWhite) {
                        if ((piece.row == row + 1 && piece.col == col - 1) || (piece.row == row + 1 && piece.col == col + 1)) {
                            evaluation += 25; // White pawn controls
                        }
                    } else {
                        if ((piece.row == row - 1 && piece.col == col - 1) || (piece.row == row - 1 && piece.col == col + 1)) {
                            evaluation -= 25; // Black pawn controls
                        }
                    }
                }
            }
        }
        return evaluation;
    }

    private int getPieceValue(Piece piece) {
        int value = 0;
        switch (piece.type) {
            case PAWN: value = piece.isWhite ? 100 : -100; break;
            case KNIGHT: value = piece.isWhite ? 300 : -300; break;
            case BISHOP: value = piece.isWhite ? 320 : -320; break;
            case ROOK: value = piece.isWhite ? 500 : -500; break;
            case QUEEN: value = piece.isWhite ? 900 : -900; break;
            case KING: value = piece.isWhite ? 10000 : -10000; break;
            default: value = 0; break;
        }
        return value;
    }
    
    private int getPawnStructure(Board virtualBoard, Piece piece) {
        // Simple pawn structure evaluation (can be improved)
        if (piece.type == Type.PAWN) {
            int row = piece.row;
            int col = piece.col;
            int value = 0;

            // Simple doubled pawn penalty
            for (Piece other : virtualBoard.pieceList) {
                if (other != piece && other.type == Type.PAWN && other.col == col && other.isWhite == piece.isWhite) {
                    value -= 50;
                }
            }
            return value;
        }
        return 0;
    }

    private int evaluateMove(Board virtualBoard, Movements move) {
        virtualBoard.makeMove(move);
        int score = evaluateBoard(virtualBoard);
        virtualBoard.undoMove();
        return score;
    }
}