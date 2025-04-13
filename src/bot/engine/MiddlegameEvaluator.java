package bot.engine;

import java.util.ArrayList;
import java.util.List;

import GameLauncher.Board;
import GameLauncher.Movements;
import GameLauncher.Type;
import pieces.Piece;

/**
 * Evaluator for middlegame positions
 * @author nkdkhtl
 * @version 2025-04-13
 */
public class MiddlegameEvaluator implements GamePhaseEvaluator {
    // Piece coordination bonuses
    private static final int BISHOP_PAIR_BONUS = 50;
    private static final int ROOK_OPEN_FILE = 25;
    private static final int ROOK_SEMI_OPEN_FILE = 15;
    private static final int ROOK_CONNECTED = 20;
    private static final int KNIGHT_OUTPOST = 30;
    
    // Pawn structure values
    private static final int DOUBLED_PAWN_PENALTY = -20;
    private static final int ISOLATED_PAWN_PENALTY = -15;
    private static final int BACKWARD_PAWN_PENALTY = -10;
    private static final int PASSED_PAWN_BONUS = 25;
    private static final int PROTECTED_PASSED_PAWN_BONUS = 40;
    
    // King safety values
    private static final int PAWN_SHIELD_BONUS = 10;
    private static final int KING_ZONE_ATTACK = -5;
    private static final int QUEEN_ATTACK_MULTIPLIER = 4;
    private static final int ROOK_ATTACK_MULTIPLIER = 2;
    private static final int BISHOP_ATTACK_MULTIPLIER = 1;
    private static final int KNIGHT_ATTACK_MULTIPLIER = 1;
    
    @Override
    public int evaluate(Board board) {
        return evaluatePiecePlacement(board) + 
               evaluatePawnStructure(board) +
               evaluateKingSafety(board);
    }
    
    private int evaluatePiecePlacement(Board board) {
        int eval = 0;
        int whiteBishops = 0;
        int blackBishops = 0;
        List<Piece> whiteRooks = new ArrayList<>();
        List<Piece> blackRooks = new ArrayList<>();
        
        for (Piece piece : board.pieceList) {
            switch (piece.type) {
                case BISHOP -> {
                    if (piece.isWhite) whiteBishops++; else blackBishops++;
                }
                case ROOK -> {
                    if (piece.isWhite) whiteRooks.add(piece); else blackRooks.add(piece);
                    eval += evaluateRook(board, piece);
                }
                case KNIGHT -> eval += evaluateKnight(board, piece);
            }
        }
        
        // Bishop pair bonus
        if (whiteBishops >= 2) eval += BISHOP_PAIR_BONUS;
        if (blackBishops >= 2) eval -= BISHOP_PAIR_BONUS;
        
        // Connected rooks
        eval += evaluateConnectedRooks(whiteRooks);
        eval -= evaluateConnectedRooks(blackRooks);
        
        return eval;
    }
    
    private int evaluateRook(Board board, Piece rook) {
        int eval = 0;
        
        // Check for open file
        boolean openFile = isOpenFile(board, rook.col);
        boolean semiOpenFile = isSemiOpenFile(board, rook.col, rook.isWhite);
        
        if (openFile) {
            eval += rook.isWhite ? ROOK_OPEN_FILE : -ROOK_OPEN_FILE;
        } else if (semiOpenFile) {
            eval += rook.isWhite ? ROOK_SEMI_OPEN_FILE : -ROOK_SEMI_OPEN_FILE;
        }
        
        return eval;
    }
    
    private int evaluateKnight(Board board, Piece knight) {
        int eval = 0;
        
        if (isKnightOutpost(board, knight)) {
            eval += knight.isWhite ? KNIGHT_OUTPOST : -KNIGHT_OUTPOST;
        }
        
        return eval;
    }
    
    private int evaluateConnectedRooks(List<Piece> rooks) {
        if (rooks.size() < 2) return 0;
        
        for (int i = 0; i < rooks.size() - 1; i++) {
            Piece rook1 = rooks.get(i);
            Piece rook2 = rooks.get(i + 1);
            if (rook1.row == rook2.row) {
                return ROOK_CONNECTED;
            }
        }
        return 0;
    }
    
    private int evaluatePawnStructure(Board board) {
        int eval = 0;
        boolean[] whitePawnFiles = new boolean[8];
        boolean[] blackPawnFiles = new boolean[8];
        
        // First pass: record pawn files
        for (Piece piece : board.pieceList) {
            if (piece.type == Type.PAWN) {
                if (piece.isWhite) {
                    whitePawnFiles[piece.col] = true;
                } else {
                    blackPawnFiles[piece.col] = true;
                }
            }
        }
        
        // Second pass: evaluate each pawn
        for (Piece piece : board.pieceList) {
            if (piece.type == Type.PAWN) {
                // Doubled pawns
                if (isDoubledPawn(board, piece)) {
                    eval += piece.isWhite ? DOUBLED_PAWN_PENALTY : -DOUBLED_PAWN_PENALTY;
                }
                
                // Isolated pawns
                if (isIsolatedPawn(piece, piece.isWhite ? whitePawnFiles : blackPawnFiles)) {
                    eval += piece.isWhite ? ISOLATED_PAWN_PENALTY : -ISOLATED_PAWN_PENALTY;
                }
                
                // Backward pawns
                if (isBackwardPawn(board, piece)) {
                    eval += piece.isWhite ? BACKWARD_PAWN_PENALTY : -BACKWARD_PAWN_PENALTY;
                }
                
                // Passed pawns
                if (isPassedPawn(board, piece)) {
                    int bonus = PASSED_PAWN_BONUS;
                    if (isProtectedPassedPawn(board, piece)) {
                        bonus += PROTECTED_PASSED_PAWN_BONUS;
                    }
                    eval += piece.isWhite ? bonus : -bonus;
                }
            }
        }
        
        return eval;
    }
    
    private int evaluateKingSafety(Board board) {
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
        
        if (whiteKing != null) {
            eval += evaluateKingSafetyForSide(board, whiteKing, true);
        }
        if (blackKing != null) {
            eval -= evaluateKingSafetyForSide(board, blackKing, false);
        }
        
        return eval;
    }
    
    // Helper methods
    private boolean isOpenFile(Board board, int col) {
        for (Piece piece : board.pieceList) {
            if (piece.type == Type.PAWN && piece.col == col) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isSemiOpenFile(Board board, int col, boolean isWhite) {
        boolean foundOwnPawn = false;
        boolean foundEnemyPawn = false;
        
        for (Piece piece : board.pieceList) {
            if (piece.type == Type.PAWN && piece.col == col) {
                if (piece.isWhite == isWhite) foundOwnPawn = true;
                else foundEnemyPawn = true;
            }
        }
        
        return !foundOwnPawn && foundEnemyPawn;
    }
    
    private boolean isKnightOutpost(Board board, Piece knight) {
        if (!isAdvancedSquare(knight)) return false;
        
        // Check if protected by pawn
        int supportRow = knight.isWhite ? knight.row + 1 : knight.row - 1;
        return hasProtectingPawn(board, knight.col - 1, supportRow, knight.isWhite) ||
               hasProtectingPawn(board, knight.col + 1, supportRow, knight.isWhite);
    }
    
    private boolean isAdvancedSquare(Piece piece) {
        return piece.isWhite ? piece.row < 4 : piece.row > 3;
    }
    
    private boolean hasProtectingPawn(Board board, int col, int row, boolean isWhite) {
        if (col < 0 || col > 7 || row < 0 || row > 7) return false;
        
        for (Piece piece : board.pieceList) {
            if (piece.type == Type.PAWN && piece.isWhite == isWhite &&
                piece.col == col && piece.row == row) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isDoubledPawn(Board board, Piece pawn) {
        for (Piece piece : board.pieceList) {
            if (piece != pawn && piece.type == Type.PAWN &&
                piece.isWhite == pawn.isWhite && piece.col == pawn.col) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isIsolatedPawn(Piece pawn, boolean[] pawnFiles) {
        int col = pawn.col;
        boolean hasNeighbor = false;
        
        if (col > 0) hasNeighbor |= pawnFiles[col - 1];
        if (col < 7) hasNeighbor |= pawnFiles[col + 1];
        
        return !hasNeighbor;
    }
    
    private boolean isBackwardPawn(Board board, Piece pawn) {
        int stopSquare = pawn.isWhite ? pawn.row - 1 : pawn.row + 1;
        int col = pawn.col;
        
        // Check if neighbor pawns are more advanced
        for (int c = Math.max(0, col - 1); c <= Math.min(7, col + 1); c++) {
            if (c != col) {
                boolean foundSupport = false;
                for (Piece piece : board.pieceList) {
                    if (piece.type == Type.PAWN && piece.isWhite == pawn.isWhite && 
                        piece.col == c) {
                        if (pawn.isWhite && piece.row < stopSquare ||
                            !pawn.isWhite && piece.row > stopSquare) {
                            foundSupport = true;
                            break;
                        }
                    }
                }
                if (!foundSupport) return true;
            }
        }
        return false;
    }
    
    private boolean isPassedPawn(Board board, Piece pawn) {
        int col = pawn.col;
        int stopRank = pawn.isWhite ? 0 : 7;
        int direction = pawn.isWhite ? -1 : 1;
        
        // Check if there are any enemy pawns that can block the path
        for (int r = pawn.row + direction; r != stopRank; r += direction) {
            for (int c = Math.max(0, col - 1); c <= Math.min(7, col + 1); c++) {
                for (Piece piece : board.pieceList) {
                    if (piece.type == Type.PAWN && piece.isWhite != pawn.isWhite &&
                        piece.col == c && piece.row == r) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private boolean isProtectedPassedPawn(Board board, Piece pawn) {
        if (!isPassedPawn(board, pawn)) return false;
        
        // Check if protected by pieces
        for (Piece piece : board.pieceList) {
            if (piece.isWhite == pawn.isWhite && piece != pawn) {
                Movements move = new Movements(board, piece, pawn.col, pawn.row);
                if (board.isValidMove(move)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private int evaluateKingSafetyForSide(Board board, Piece king, boolean isWhite) {
        int safety = 0;
        
        // Evaluate pawn shield
        safety += evaluatePawnShield(board, king);
        
        // Evaluate attacking pieces in king zone
        safety += evaluateKingZoneAttacks(board, king);
        
        return safety;
    }
    
    private int evaluatePawnShield(Board board, Piece king) {
        int safety = 0;
        int baseRank = king.isWhite ? 7 : 0;
        int pawnRank = king.isWhite ? 6 : 1;
        
        // Check pawns in front of king
        for (int col = Math.max(0, king.col - 1); col <= Math.min(7, king.col + 1); col++) {
            for (Piece piece : board.pieceList) {
                if (piece.type == Type.PAWN && piece.isWhite == king.isWhite &&
                    piece.col == col && piece.row == pawnRank) {
                    safety += PAWN_SHIELD_BONUS;
                }
            }
        }
        
        return safety;
    }
    
    private int evaluateKingZoneAttacks(Board board, Piece king) {
        int safety = 0;
        
        // Define king zone
        int startCol = Math.max(0, king.col - 2);
        int endCol = Math.min(7, king.col + 2);
        int startRow = Math.max(0, king.row - 2);
        int endRow = Math.min(7, king.row + 2);
        
        // Count attacking pieces in king zone
        for (Piece attacker : board.pieceList) {
            if (attacker.isWhite != king.isWhite) {
                for (int row = startRow; row <= endRow; row++) {
                    for (int col = startCol; col <= endCol; col++) {
                        Movements move = new Movements(board, attacker, col, row);
                        if (board.isValidMove(move)) {
                            safety += getAttackWeight(attacker.type) * KING_ZONE_ATTACK;
                            break;
                        }
                    }
                }
            }
        }
        
        return safety;
    }
    
    private int getAttackWeight(Type pieceType) {
        return switch (pieceType) {
            case QUEEN -> QUEEN_ATTACK_MULTIPLIER;
            case ROOK -> ROOK_ATTACK_MULTIPLIER;
            case BISHOP -> BISHOP_ATTACK_MULTIPLIER;
            case KNIGHT -> KNIGHT_ATTACK_MULTIPLIER;
            default -> 0;
        };
    }
}