package pieces;

import main.Board;
import main.Type;

public class Queen extends Piece {
	public Queen(Board board,boolean isWhite, int col, int row) {
		super(board);
		type = Type.QUEEN;
		
		this.col = col;
		this.row = row;
		
		this.xPos = col*Board.SQUARE_SIZE;
		this.yPos = row*Board.SQUARE_SIZE;

		this.isWhite = isWhite;
		
		if (isWhite) {
			image = getImage("wq");
		} else {
			image = getImage("bq");
		}
		
	}
	@Override
	public boolean isValidMovement(int col,int row) {
		return Math.abs(this.col - col) == Math.abs(this.row - row) || this.col == col || this.row == row;
	}
	
	@Override
	public boolean isCollide(int col,int row) {
		//if move like bishop
		if (Math.abs(this.col - col) == Math.abs(this.row - row)) {
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
		} else {
			//left 
			if (this.col > col) {
				for (int c = this.col-1;c>col;c--) {
					if (board.getPiece(c, this.row) != null) {
						return true;
					}
				}
			}
			
			//right
			if (this.col < col) {
				for (int c = this.col+1;c<col;c++) {
					if (board.getPiece(c, this.row) != null) {
						return true;
					}
				}
			}
			
			//up
			if (this.row > row) {
				for (int r = this.row-1;r>row;r--) {
					if (board.getPiece(this.col, r) != null) {
						return true;
					}
				}
			}
			
			//down
			if (this.row < row) {
				for (int r = this.row+1;r<row;r++) {
					if (board.getPiece(this.col, r) != null) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
}
