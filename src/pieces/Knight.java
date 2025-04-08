package pieces;

import GameLauncher.Board;
import GameLauncher.Type;

public class Knight extends Piece {
	
    private static final int[][] KNIGHT_EVALUATION = {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}
    };
	
	public Knight(Board board,boolean isWhite, int col, int row) {
		super(board);
		
		type = Type.KNIGHT;
		this.col = col;
		this.row = row;
		
		this.xPos = col*Board.SQUARE_SIZE;
		this.yPos = row*Board.SQUARE_SIZE;

		this.isWhite = isWhite;
		
		if (isWhite) {
			image = getImage("wn");
		} else {
			image = getImage("bn");
		}
	}
	
	@Override
	public boolean isValidMovement(int col,int row) {
		return Math.abs(col - this.col) * Math.abs(row - this.row) == 2;
	}

	@Override
	public boolean isCollide(int col, int row) {
		return false;
	}
	
    @Override
    public int getPositionValue() {
        return isWhite ? KNIGHT_EVALUATION[row][col] : -KNIGHT_EVALUATION[7 - row][col];
    }
	
}
