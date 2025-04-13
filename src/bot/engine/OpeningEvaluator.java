package bot.engine;

import GameLauncher.Board;
import GameLauncher.Type;
import pieces.Piece;

public class OpeningEvaluator implements GamePhaseEvaluator {
    private static final int CENTER_PAWN_BONUS = 40;
    private static final int DEVELOPMENT_BONUS = 30;
    private static final int EARLY_QUEEN_PENALTY = -30;
    private static final int KING_SAFETY_BONUS = 50;
    private static final int WING_PAWN_PENALTY = -15;
    private static final int KNIGHT_CENTER_BONUS = 25;
    private static final int BISHOP_DIAGONAL_BONUS = 20;
    private static final int FIANCHETTO_BONUS = 15;
    
    // Center squares for piece development evaluation
    private static final int[] CENTER_SQUARES = {27, 28, 35, 36}; // d4,e4,d5,e5
    private static final int[] EXTENDED_CENTER = {
        18, 19, 20, 21,  // c3,d3,e3,f3
        26, 27, 28, 29,  // c4,d4,e4,f4
        34, 35, 36, 37,  // c5,d5,e5,f5
        42, 43, 44, 45   // c6,d6,e6,f6
    };
    
    @Override
    public int evaluate(Board board) {
        int evaluation = 0;
        boolean whiteKingCastled = false;
        boolean blackKingCastled = false;
        int whiteDevelopedPieces = 0;
        int blackDevelopedPieces = 0;
        
        for (Piece piece : board.pieceList) {
            evaluation += evaluatePiece(piece, whiteDevelopedPieces, blackDevelopedPieces,
                                     whiteKingCastled, blackKingCastled);
        }
        
        return evaluation;
    }
    
    private int evaluatePiece(Piece piece, int whiteDev, int blackDev,
                             boolean whiteKingCastled, boolean blackKingCastled) {
        return switch (piece.type) {
            case PAWN -> evaluatePawn(piece);
            case KNIGHT, BISHOP -> evaluateDevelopment(piece, whiteDev, blackDev);
            case QUEEN -> evaluateQueen(piece);
            case KING -> evaluateKing(piece, whiteKingCastled, blackKingCastled);
            default -> 0;
        };
    }
    
    private int evaluatePawn(Piece pawn) {
        int eval = 0;
        if (isPawnControllingCenter(pawn)) {
            eval += pawn.isWhite ? CENTER_PAWN_BONUS : -CENTER_PAWN_BONUS;
        }
        if (isWingPawn(pawn) && hasMoved(pawn)) {
            eval += pawn.isWhite ? WING_PAWN_PENALTY : -WING_PAWN_PENALTY;
        }
        return eval;
    }
    
    private boolean isPawnControllingCenter(Piece pawn) {
        return pawn.col >= 3 && pawn.col <= 4 && pawn.row >= 3 && pawn.row <= 4;
    }
    
    private boolean isWingPawn(Piece pawn) {
        return pawn.col <= 1 || pawn.col >= 6;
    }
    
    private boolean hasMoved(Piece piece) {
        return piece.isWhite ? piece.row != 6 : piece.row != 1;
    }
    private int evaluateDevelopment(Piece piece, int whiteDev, int blackDev) {
        int eval = 0;
        
        if (piece.type == Type.KNIGHT) {
            eval += evaluateKnight(piece);
        } else if (piece.type == Type.BISHOP) {
            eval += evaluateBishop(piece);
        }
        
        // Basic development bonus if piece has moved from starting position
        if (hasDeveloped(piece)) {
            if (piece.isWhite) {
                eval += DEVELOPMENT_BONUS;
                whiteDev++;
            } else {
                eval -= DEVELOPMENT_BONUS;
                blackDev++;
            }
        }
        
        return eval;
    }
    
    private int evaluateKnight(Piece knight) {
        int eval = 0;
        
        // Bonus for knights in or near the center
        if (isInExtendedCenter(knight.col, knight.row)) {
            eval += knight.isWhite ? KNIGHT_CENTER_BONUS : -KNIGHT_CENTER_BONUS;
        }
        
        // Penalty for knights on the rim
        if (isOnRim(knight)) {
            eval += knight.isWhite ? -15 : 15;
        }
        
        return eval;
    }
    
    private int evaluateBishop(Piece bishop) {
        int eval = 0;
        
        // Bonus for bishops on long diagonals
        if (isOnLongDiagonal(bishop)) {
            eval += bishop.isWhite ? BISHOP_DIAGONAL_BONUS : -BISHOP_DIAGONAL_BONUS;
        }
        
        // Bonus for fianchettoed bishops
        if (isFianchettoed(bishop)) {
            eval += bishop.isWhite ? FIANCHETTO_BONUS : -FIANCHETTO_BONUS;
        }
        
        return eval;
    }
    
    private int evaluateQueen(Piece queen) {
        int eval = 0;
        
        // Penalize early queen development
        if (hasMovedFromStartingSquare(queen)) {
            // Higher penalty in the very early game (first 5 moves)
            int moveNumber = queen.board.getFullmoveNumber();
            int penalty = moveNumber <= 5 ? EARLY_QUEEN_PENALTY * 2 : EARLY_QUEEN_PENALTY;
            eval += queen.isWhite ? penalty : -penalty;
            
            // Additional penalty for queen in center too early
            if (isInCenter(queen.col, queen.row) && moveNumber <= 7) {
                eval += queen.isWhite ? -20 : 20;
            }
        }
        
        return eval;
    }
    
    private int evaluateKing(Piece king, boolean whiteKingCastled, boolean blackKingCastled) {
        int eval = 0;
        
        if (king.isWhite) {
            // Check if white has castled
            if (hasCastled(king)) {
                eval += KING_SAFETY_BONUS;
                whiteKingCastled = true;
            }
            // Penalize moving king without castling
            else if (hasMovedFromStartingSquare(king)) {
                eval -= 40;
            }
            // Bonus for maintaining castling rights
            else if (canStillCastle(king)) {
                eval += 15;
            }
        } else {
            // Check if black has castled
            if (hasCastled(king)) {
                eval -= KING_SAFETY_BONUS;
                blackKingCastled = true;
            }
            // Penalize moving king without castling
            else if (hasMovedFromStartingSquare(king)) {
                eval += 40;
            }
            // Bonus for maintaining castling rights
            else if (canStillCastle(king)) {
                eval -= 15;
            }
        }
        
        return eval;
    }
    
    // Helper methods
    private boolean hasDeveloped(Piece piece) {
        int homeRank = piece.isWhite ? 7 : 0;
        return piece.row != homeRank;
    }
    
    private boolean isInCenter(int col, int row) {
        return col >= 3 && col <= 4 && row >= 3 && row <= 4;
    }
    
    private boolean isInExtendedCenter(int col, int row) {
        int square = row * 8 + col;
        for (int centerSquare : EXTENDED_CENTER) {
            if (square == centerSquare) return true;
        }
        return false;
    }
    
    private boolean isOnRim(Piece knight) {
        return knight.col == 0 || knight.col == 7 || knight.row == 0 || knight.row == 7;
    }
    
    private boolean isOnLongDiagonal(Piece bishop) {
        return Math.abs(bishop.col - bishop.row) == 0 || 
               Math.abs(bishop.col - (7 - bishop.row)) == 0;
    }
    
    private boolean isFianchettoed(Piece bishop) {
        if (bishop.isWhite) {
            return (bishop.col == 1 || bishop.col == 6) && bishop.row == 6;
        } else {
            return (bishop.col == 1 || bishop.col == 6) && bishop.row == 1;
        }
    }
    
    private boolean hasMovedFromStartingSquare(Piece piece) {
        int homeRank = piece.isWhite ? 7 : 0;
        if (piece.type == Type.QUEEN) {
            return piece.row != homeRank || piece.col != 3;
        }
        return piece.row != homeRank || piece.col != 4; // For king
    }
    
    private boolean hasCastled(Piece king) {
        int homeRank = king.isWhite ? 7 : 0;
        return king.row == homeRank && (king.col == 6 || king.col == 2);
    }
    
    private boolean canStillCastle(Piece king) {
        int homeRank = king.isWhite ? 7 : 0;
        return king.row == homeRank && king.col == 4;
    }
    
}