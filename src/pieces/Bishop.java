package pieces;

import GameLauncher.Board;
import GameLauncher.Type;

public class Bishop extends Piece {
	
	private static final int[][] BISHOP_EVALUATION = {
	        {-20, -10, -10, -10, -10, -10, -10, -20},
	        {-10, 5, 0, 0, 0, 0, 5, -10},
	        {-10, 10, 10, 10, 10, 10, 10, -10},
	        {-10, 0, 10, 10, 10, 10, 0, -10},
	        {-10, 5, 5, 10, 10, 5, 5, -10},
	        {-10, 0, 5, 10, 10, 5, 0, -10},
	        {-10, 0, 0, 0, 0, 0, 0, -10},
	        {-20, -10, -10, -10, -10, -10, -10, -20}
	};
	
	public Bishop(Board board,boolean isWhite, int col, int row) {
 		super(board);
 		type = Type.BISHOP;
 
 		this.col = col;
 		this.row = row;
 
 		this.xPos = col*Board.SQUARE_SIZE;
 		this.yPos = row*Board.SQUARE_SIZE;
 
 		this.isWhite = isWhite;
 		
 		if (isWhite) {
			image = getImage("wb");
		} else {
			image = getImage("bb");
		}
 	}
	 	
	@Override
	public boolean isValidMovement(int col,int row) {
		return Math.abs(this.col - col) == Math.abs(this.row - row);
	}
	
	
	@Override
	public boolean isCollide(int col,int row) {
		//up left
		if (this.col > col && this.row > row) {
			for (int i = 1;i< Math.abs(this.col-col);i++) {
				if (board.getPiece(this.col - i, this.row - i) != null) {
					return true;
				}
			}
		}
		//up right
		if (this.col < col && this.row > row) {
			for (int i = 1;i< Math.abs(this.col-col);i++) {
				if (board.getPiece(this.col + i, this.row - i) != null) {
					return true;
				}
			}
		}
		
		//down left
		if (this.col > col && this.row < row) {
			for (int i = 1;i< Math.abs(this.col-col);i++) {
				if (board.getPiece(this.col - i, this.row + i) != null) {
					return true;
				}
			}
		}
		
		//down right
		if (this.col < col && this.row < row) {
			for (int i = 1;i< Math.abs(this.col-col);i++) {
				if (board.getPiece(this.col + i, this.row + i) != null) {
					return true;
				}
			}
		}
		return false;
	}
	
	
    @Override
    public int getPositionValue() {
        return isWhite ? BISHOP_EVALUATION[row][col] : -BISHOP_EVALUATION[7 - row][col];
    }
	
}
