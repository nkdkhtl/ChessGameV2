package bot.engine;

import java.util.ArrayList;
import java.util.List;

import GameLauncher.Board;
import GameLauncher.Movements;
import GameLauncher.Type;
import pieces.Piece;

/**
 * Chess engine search implementation
 */
public class SearchEngine {
    private final Evaluator evaluator;
    private static final int MAX_DEPTH = 20;
    
    public SearchEngine() {
        this.evaluator = new Evaluator();
    }
    
    public Movements findBestMove(Board board, boolean isWhite) {
        int depth = calculateDepth(board);
        SearchResult result = alphaBeta(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isWhite);
        return result.bestMove;
    }
    
    private int calculateDepth(Board board) {
        int moveNumber = board.getFullmoveNumber();
        int pieces = board.pieceList.size();
        
        // Use a lower depth in early game and when there are many pieces
        if (moveNumber <= 10 || pieces >= 20) {
            return 4;
        }
        // Use medium depth in middle game
        if (pieces >= 10) {
            return 5;
        }
        // Only use max depth in endgame with few pieces
        return MAX_DEPTH;
    }
    
    private SearchResult alphaBeta(Board board, int depth, int alpha, int beta, boolean maximizing) {
        if (depth == 0 || board.isGameOver) {
            return new SearchResult(null, evaluator.evaluate(board));
        }
        
        List<Movements> moves = generateLegalMoves(board, maximizing);
        moves.sort((m1, m2) -> scoreMoveForSorting(board, m2) - scoreMoveForSorting(board, m1));
        
        return maximizing ? 
            maxNode(board, moves, depth, alpha, beta) :
            minNode(board, moves, depth, alpha, beta);
    }
    
    /**
     * Generates all legal moves for the current position
     */
    private List<Movements> generateLegalMoves(Board board, boolean isWhite) {
        List<Movements> legalMoves = new ArrayList<>();
        
        for (Piece piece : board.pieceList) {
            if (piece.isWhite == isWhite) {
                // Try all possible squares for this piece
                for (int newRow = 0; newRow < 8; newRow++) {
                    for (int newCol = 0; newCol < 8; newCol++) {
                    	board.selectedPiece = piece;
                        Movements move = new Movements(board, piece, newCol, newRow);
                        if (board.isValidMove(move)) {
                            legalMoves.add(move);
                        }
                    }
                }
            }
        }
        
        return legalMoves;
    }
    
    private SearchResult maxNode(Board board, List<Movements> moves, int depth, int alpha, int beta) {
        Movements bestMove = null;
        int bestValue = Integer.MIN_VALUE;
        
        for (Movements move : moves) {
            board.makeMove(move);
            int value = alphaBeta(board, depth - 1, alpha, beta, false).score;
            board.undoMove();
            
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
            alpha = Math.max(alpha, bestValue);
            if (beta <= alpha) break;
        }
        
        return new SearchResult(bestMove, bestValue);
    }
    
    private SearchResult minNode(Board board, List<Movements> moves, int depth, int alpha, int beta) {
        Movements bestMove = null;
        int bestValue = Integer.MAX_VALUE;
        
        for (Movements move : moves) {
            board.makeMove(move);
            int value = alphaBeta(board, depth - 1, alpha, beta, true).score;
            board.undoMove();
            
            if (value < bestValue) {
                bestValue = value;
                bestMove = move;
            }
            beta = Math.min(beta, bestValue);
            if (beta <= alpha) break;
        }
        
        return new SearchResult(bestMove, bestValue);
    }
    
    private int scoreMoveForSorting(Board board, Movements move) {
        int score = 0;
        
        // Captures
        if (move.capture != null) {
            score += 10 * getPieceValue(move.capture) - getPieceValue(move.piece);
        }
        
        // Center control
        if (isInCenter(move.newCol, move.newRow)) {
            score += 50;
        }
        
        // Piece development in opening
        if (board.getFullmoveNumber() <= 10 && isGoodDevelopmentMove(move)) {
            score += 30;
        }
        
        return score;
    }
    
    private boolean isInCenter(int col, int row) {
        return col >= 3 && col <= 4 && row >= 3 && row <= 4;
    }
    
    private int getPieceValue(Piece piece) {
        return switch (piece.type) {
            case PAWN -> 100;
            case KNIGHT, BISHOP -> 300;
            case ROOK -> 500;
            case QUEEN -> 900;
            case KING -> 10000;
        };
    }
    
    private boolean isGoodDevelopmentMove(Movements move) {
        // Consider a move good development if:
        // 1. Moving a minor piece (knight/bishop)
        // 2. Not moving the same piece twice in opening
        // 3. Moving towards the center
        if (move.piece.type == Type.KNIGHT || move.piece.type == Type.BISHOP) {
            int homeRank = move.piece.isWhite ? 7 : 0;
            return move.oldRow == homeRank && 
                   Math.abs(move.newCol - 3.5) <= 2.5; // Moving towards center
        }
        return false;
    }
    
    private static class SearchResult {
        final Movements bestMove;
        final int score;
        
        SearchResult(Movements move, int score) {
            this.bestMove = move;
            this.score = score;
        }
    }
}