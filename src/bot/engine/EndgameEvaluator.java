package bot.engine;

import java.util.ArrayList;
import java.util.List;

import GameLauncher.Board;
import GameLauncher.Type;
import pieces.Piece;

/**
 * Evaluator for endgame positions
 * @author nkdkhtl
 * @version 2025-04-13
 */
public class EndgameEvaluator implements GamePhaseEvaluator {
    // Pawn advancement
    private static final int PAWN_ADVANCE_BONUS = 10;
    private static final int PROTECTED_PASSED_PAWN = 30;
    private static final int CONNECTED_PASSED_PAWNS = 40;
    
    // King activity
    private static final int KING_CENTRALIZATION = 5;
    private static final int KING_TROPISM = 3;
    private static final int KING_OPPOSITION = 15;
    
    // Piece coordination
    private static final int ROOK_BEHIND_PAWN = 20;
    private static final int BISHOP_PAIR_ENDGAME = 60;
    private static final int WRONG_COLORED_BISHOP = -50;
    
    @Override
    public int evaluate(Board board) {
        return evaluatePawnAdvancement(board) + 
               evaluateKingCentralization(board) +
               evaluatePieceCoordination(board) +
               evaluateSpecialEndgames(board);
    }
    
    private int evaluatePawnAdvancement(Board board) {
        int eval = 0;
        List<Piece> whitePawns = new ArrayList<>();
        List<Piece> blackPawns = new ArrayList<>();
        
        // Collect pawns
        for (Piece piece : board.pieceList) {
            if (piece.type == Type.PAWN) {
                if (piece.isWhite) whitePawns.add(piece);
                else blackPawns.add(piece);
            }
        }
        
        // Evaluate white pawns
        for (Piece pawn : whitePawns) {
            // Basic advancement bonus
            eval += (7 - pawn.row) * PAWN_ADVANCE_BONUS;
            
            // Passed pawn evaluation
            if (isPassedPawn(board, pawn)) {
                int bonus = calculatePassedPawnBonus(board, pawn);
                eval += bonus;
                
                // Check if protected
                if (isProtectedPawn(board, pawn)) {
                    eval += PROTECTED_PASSED_PAWN;
                }
            }
            
            // Connected passed pawns
            if (hasConnectedPassedPawn(board, pawn)) {
                eval += CONNECTED_PASSED_PAWNS;
            }
        }
        
        // Evaluate black pawns
        for (Piece pawn : blackPawns) {
            // Basic advancement bonus
            eval -= pawn.row * PAWN_ADVANCE_BONUS;
            
            // Passed pawn evaluation
            if (isPassedPawn(board, pawn)) {
                int bonus = calculatePassedPawnBonus(board, pawn);
                eval -= bonus;
                
                // Check if protected
                if (isProtectedPawn(board, pawn)) {
                    eval -= PROTECTED_PASSED_PAWN;
                }
            }
            
            // Connected passed pawns
            if (hasConnectedPassedPawn(board, pawn)) {
                eval -= CONNECTED_PASSED_PAWNS;
            }
        }
        
        return eval;
    }
    
    private int evaluateKingCentralization(Board board) {
        int eval = 0;
        Piece whiteKing = null;
        Piece blackKing = null;
        
        // Find kings
        for (Piece piece : board.pieceList) {
            if (piece.type == Type.KING) {
                if (piece.isWhite) whiteKing = piece;
                else blackKing = piece;
            }
        }
        
        if (whiteKing != null && blackKing != null) {
            // Evaluate centralization
            eval += calculateKingCentralization(whiteKing);
            eval -= calculateKingCentralization(blackKing);
            
            // Evaluate king tropism (distance between kings)
            eval += calculateKingTropism(whiteKing, blackKing);
            
            // Evaluate king opposition
            if (hasKingOpposition(whiteKing, blackKing)) {
                eval += KING_OPPOSITION;
            }
        }
        
        return eval;
    }
    
    private int evaluatePieceCoordination(Board board) {
        int eval = 0;
        int whiteBishops = 0;
        int blackBishops = 0;
        
        for (Piece piece : board.pieceList) {
            switch (piece.type) {
                case ROOK -> eval += evaluateRookEndgame(board, piece);
                case BISHOP -> {
                    if (piece.isWhite) whiteBishops++;
                    else blackBishops++;
                    eval += evaluateBishopEndgame(board, piece);
                }
            }
        }
        
        // Bishop pair bonus in endgame
        if (whiteBishops >= 2) eval += BISHOP_PAIR_ENDGAME;
        if (blackBishops >= 2) eval -= BISHOP_PAIR_ENDGAME;
        
        return eval;
    }
    
    private int evaluateSpecialEndgames(Board board) {
        // Implement special endgame patterns
        if (isKBNEndgame(board)) {
            return evaluateKBNEndgame(board);
        }
        if (isKRPEndgame(board)) {
            return evaluateKRPEndgame(board);
        }
        return 0;
    }
    
    // Helper methods
    private boolean isPassedPawn(Board board, Piece pawn) {
        int col = pawn.col;
        int direction = pawn.isWhite ? -1 : 1;
        int currentRow = pawn.row;
        
        while (currentRow != (pawn.isWhite ? 0 : 7)) {
            currentRow += direction;
            for (int c = Math.max(0, col - 1); c <= Math.min(7, col + 1); c++) {
                if (hasEnemyPawnInSquare(board, c, currentRow, pawn.isWhite)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean hasEnemyPawnInSquare(Board board, int col, int row, boolean isWhite) {
        for (Piece piece : board.pieceList) {
            if (piece.type == Type.PAWN && piece.isWhite != isWhite &&
                piece.col == col && piece.row == row) {
                return true;
            }
        }
        return false;
    }
    
    private int calculatePassedPawnBonus(Board board, Piece pawn) {
        int rank = pawn.isWhite ? 7 - pawn.row : pawn.row;
        return PAWN_ADVANCE_BONUS * (rank * rank) / 2;
    }
    
    private boolean isProtectedPawn(Board board, Piece pawn) {
        int supportCol = pawn.col - 1;
        int supportRow = pawn.isWhite ? pawn.row + 1 : pawn.row - 1;
        
        for (int c = Math.max(0, pawn.col - 1); c <= Math.min(7, pawn.col + 1); c++) {
            if (c != pawn.col) {
                if (hasFriendlyPawnInSquare(board, c, supportRow, pawn.isWhite)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean hasFriendlyPawnInSquare(Board board, int col, int row, boolean isWhite) {
        for (Piece piece : board.pieceList) {
            if (piece.type == Type.PAWN && piece.isWhite == isWhite &&
                piece.col == col && piece.row == row) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasConnectedPassedPawn(Board board, Piece pawn) {
        if (!isPassedPawn(board, pawn)) return false;
        
        for (int c = Math.max(0, pawn.col - 1); c <= Math.min(7, pawn.col + 1); c++) {
            if (c != pawn.col) {
                for (Piece other : board.pieceList) {
                    if (other.type == Type.PAWN && other.isWhite == pawn.isWhite &&
                        other.col == c && isPassedPawn(board, other)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private int calculateKingCentralization(Piece king) {
        int fileDistance = Math.min(king.col, 7 - king.col);
        int rankDistance = Math.min(king.row, 7 - king.row);
        return (fileDistance + rankDistance) * KING_CENTRALIZATION;
    }
    
    private int calculateKingTropism(Piece whiteKing, Piece blackKing) {
        int distance = Math.max(
            Math.abs(whiteKing.col - blackKing.col),
            Math.abs(whiteKing.row - blackKing.row)
        );
        return (8 - distance) * KING_TROPISM;
    }
    
    private boolean hasKingOpposition(Piece whiteKing, Piece blackKing) {
        return Math.abs(whiteKing.col - blackKing.col) == 2 &&
               whiteKing.row == blackKing.row;
    }
    
    private int evaluateRookEndgame(Board board, Piece rook) {
        int eval = 0;
        
        // Rook behind passed pawn
        for (Piece pawn : board.pieceList) {
            if (pawn.type == Type.PAWN && pawn.isWhite == rook.isWhite &&
                isPassedPawn(board, pawn) && pawn.col == rook.col &&
                (rook.isWhite ? rook.row > pawn.row : rook.row < pawn.row)) {
                eval += rook.isWhite ? ROOK_BEHIND_PAWN : -ROOK_BEHIND_PAWN;
            }
        }
        
        return eval;
    }
    
    private int evaluateBishopEndgame(Board board, Piece bishop) {
        int eval = 0;
        
        // Wrong colored bishop with pawns
        if (isWrongColoredBishop(board, bishop)) {
            eval += bishop.isWhite ? WRONG_COLORED_BISHOP : -WRONG_COLORED_BISHOP;
        }
        
        return eval;
    }
    
    private boolean isWrongColoredBishop(Board board, Piece bishop) {
        boolean bishopOnWhite = (bishop.row + bishop.col) % 2 == 0;
        int pawnsOnWrongColor = 0;
        int pawnsOnRightColor = 0;
        
        for (Piece pawn : board.pieceList) {
            if (pawn.type == Type.PAWN && pawn.isWhite == bishop.isWhite) {
                boolean pawnSquareWhite = (pawn.row + pawn.col) % 2 == 0;
                if (pawnSquareWhite == bishopOnWhite) {
                    pawnsOnWrongColor++;
                } else {
                    pawnsOnRightColor++;
                }
            }
        }
        
        return pawnsOnWrongColor > pawnsOnRightColor;
    }
    
    private boolean isKBNEndgame(Board board) {
        int whitePieces = 0;
        int blackPieces = 0;
        boolean hasWhiteBishop = false;
        boolean hasWhiteKnight = false;
        boolean hasBlackBishop = false;
        boolean hasBlackKnight = false;
        
        for (Piece piece : board.pieceList) {
            if (piece.isWhite) {
                whitePieces++;
                if (piece.type == Type.BISHOP) hasWhiteBishop = true;
                if (piece.type == Type.KNIGHT) hasWhiteKnight = true;
            } else {
                blackPieces++;
                if (piece.type == Type.BISHOP) hasBlackBishop = true;
                if (piece.type == Type.KNIGHT) hasBlackKnight = true;
            }
        }
        
        return (whitePieces == 3 && hasWhiteBishop && hasWhiteKnight) ||
               (blackPieces == 3 && hasBlackBishop && hasBlackKnight);
    }
    
    private int evaluateKBNEndgame(Board board) {
        // Implement KBN vs K endgame evaluation
        // This is a complex endgame that requires special handling
        return 0; // Placeholder
    }
    
    private boolean isKRPEndgame(Board board) {
        int whitePieces = 0;
        int blackPieces = 0;
        boolean hasWhiteRook = false;
        boolean hasWhitePawn = false;
        boolean hasBlackRook = false;
        boolean hasBlackPawn = false;
        
        for (Piece piece : board.pieceList) {
            if (piece.isWhite) {
                whitePieces++;
                if (piece.type == Type.ROOK) hasWhiteRook = true;
                if (piece.type == Type.PAWN) hasWhitePawn = true;
            } else {
                blackPieces++;
                if (piece.type == Type.ROOK) hasBlackRook = true;
                if (piece.type == Type.PAWN) hasBlackPawn = true;
            }
        }
        
        return (whitePieces == 3 && hasWhiteRook && hasWhitePawn) ||
               (blackPieces == 3 && hasBlackRook && hasBlackPawn);
    }
    
    private int evaluateKRPEndgame(Board board) {
        // Implement Rook and Pawn vs Rook endgame evaluation
        // This requires understanding of Philidor and Lucena positions
        return 0; // Placeholder
    }
}