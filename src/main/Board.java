package main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import panels.GameOverPanel;
import panels.PromotionPanel;
import panels.TimerPanel;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;
import utils.ChessTimer;

public class Board extends JPanel {
	public static final int SQUARE_SIZE = 80;
	public static final int BOARD_RADIUS = 40;
	int cols = 8;
	int rows = 8;
	
	ArrayList<Piece> pieceList = new ArrayList<>();
	
	public Piece selectedPiece;
	
	ArrayList<Point> validMoves = new ArrayList<>();
	
	public MouseInput input = new MouseInput(this);
	
	public int enPassantSquare = -1;
	
	public CheckFinder checkFinder = new CheckFinder(this);
	
	
	public ChessTimer whiteTimer;
	public ChessTimer blackTimer;
	private boolean isWhiteToMove = true;
	private boolean isGameOver = false;
	
	private boolean firstMoveMade = false;
	
	private GameOverPanel gameOverPanel;
	private TimerPanel timerPanel;


	public Board() {
		this.setPreferredSize(new Dimension(cols * SQUARE_SIZE, rows * SQUARE_SIZE));
	    this.setLayout(null);  // No layout manager
	    this.addMouseListener(input);
	    this.addMouseMotionListener(input);
	    this.setOpaque(false);
	    
	    timerPanel = new TimerPanel();
	    timerPanel.setBounds(720, 50, 160, 100);
	    this.add(timerPanel);
	    
	    whiteTimer = new ChessTimer(this,timerPanel, true, 1);
	    blackTimer = new ChessTimer(this,timerPanel, false, 1);
		
	    addPieces();
	    gameOverPanel = new GameOverPanel(this,"");
	    gameOverPanel.setBounds(0, 0, cols * SQUARE_SIZE, rows * SQUARE_SIZE);
        gameOverPanel.setVisible(false); 
        this.add(gameOverPanel);
        
	}
	
	
	public Piece getPiece(int col,int row) {
		
		for (Piece p : pieceList) {
			if (p.col == col && p.row == row) {
				return p;
			}
		}
		
		return null;
	}
	
	public void makeMove(Movements move) {
		
		if (move.piece.type == Type.PAWN) {
			movePawn(move);
		} else if (move.piece.type == Type.KING) {
			moveKing(move);
		}
		
		move.piece.col = move.newCol;
		move.piece.row = move.newRow;
		move.piece.xPos = move.newCol * SQUARE_SIZE;
		move.piece.yPos = move.newRow * SQUARE_SIZE;
		
		move.piece.isFirstMove = false;
		
		capture(move.capture);
		

	    if (!firstMoveMade) {
	        firstMoveMade = true;
	        whiteTimer.stop();
	        blackTimer.start();  // Start Black's timer after White moves
	    } else {
	        // Normal timer switching after the first move
	        if (isWhiteToMove) {
	            whiteTimer.stop();
	            blackTimer.start();
	        } else {
	            blackTimer.stop();
	            whiteTimer.start();
	        }
	    }

	    
		isWhiteToMove = !isWhiteToMove;

		
		updateGame();
		
		selectedPiece = null;
        validMoves.clear();
        repaint();
        
	}
	
	public void moveKing(Movements move) {
		if (Math.abs(move.piece.col - move.newCol) == 2) {
			Piece rook;
			if (move.piece.col < move.newCol) {
				rook = getPiece(7,move.piece.row);
				rook.col = 5;
			} else {
				rook = getPiece(0,move.piece.row);
				rook.col = 3;
			}
			rook.xPos = rook.col * SQUARE_SIZE;
		}
	}
	
	public void movePawn(Movements move) {
		//enpassant
		int colorIdx = move.piece.isWhite? 1 : -1;
		
		if (getSquareNum(move.newCol,move.newRow) == enPassantSquare) {
			move.capture = getPiece(move.newCol,move.newRow + colorIdx);
		}
		
		if (Math.abs(move.piece.row - move.newRow) == 2) {
			enPassantSquare = getSquareNum(move.newCol,move.newRow + colorIdx);
		} else {
			enPassantSquare = -1;
		}
		
		//promotion
		//white == 0
		//black == 7
		colorIdx = move.piece.isWhite? 0:7;
		if (move.newRow == colorIdx) {
			promotion(move);
		}
		
		move.piece.col = move.newCol;
		move.piece.row = move.newRow;
		move.piece.xPos = move.newCol * SQUARE_SIZE;
		move.piece.yPos = move.newRow * SQUARE_SIZE;
		
		move.piece.isFirstMove = false;
		
		capture(move.capture);
		
		selectedPiece = null;
        validMoves.clear();
        repaint();
	}
	
	private void promotion(Movements move) {
	    SwingUtilities.invokeLater(() -> {
	        PromotionPanel panel = new PromotionPanel((JFrame) SwingUtilities.getWindowAncestor(this), move.piece.isWhite);
	        String choice = panel.getSelectedPiece();
	        
	        Piece newPiece;
	        switch (choice) {
	            case "Rook":
	                newPiece = new Rook(this, move.piece.isWhite, move.newCol, move.newRow);
	                break;
	            case "Bishop":
	                newPiece = new Bishop(this, move.piece.isWhite, move.newCol, move.newRow);
	                break;
	            case "Knight":
	                newPiece = new Knight(this, move.piece.isWhite, move.newCol, move.newRow);
	                break;
	            default:
	                newPiece = new Queen(this, move.piece.isWhite, move.newCol, move.newRow);
	                break;
	        }
	        
	        
	        pieceList.remove(move.piece);
	        pieceList.add(newPiece);
	        capture(move.capture);
	        updateGame();
	        repaint();
	    });
	}

	
	public void capture(Piece piece) {
		pieceList.remove(piece);
	}
	
	public boolean isSameTeam(Piece p1,Piece p2) {
		if (p1 == null || p2 == null) {
			return false;
		} 
		return p1.isWhite == p2.isWhite;
	}
	
	Piece findKing(boolean isWhite) {
		for (Piece p : pieceList) {
			if (isWhite == p.isWhite && p.type == Type.KING) {
				return p;
			}
		}
		return null;
	}
	
	public boolean isValidMove(Movements move) {
		
		if (isGameOver) {
			return false;
		}
		
		if (move.piece.isWhite != isWhiteToMove) {
			return false;
		}
		
		if (isSameTeam(move.piece,move.capture)) {
			return false;
		}
		
		if (!move.piece.isValidMovement(move.newCol,move.newRow)) {
			return false;
		}
		
		if (move.piece.isCollide(move.newCol, move.newRow)) {
			return false;
		}
		
		if (checkFinder.isKingInCheck(move)) {
			return false;
		}
		
		if (move.newCol < 0 || move.newCol >= cols || move.newRow < 0 || move.newRow >= rows) {
	        return false;
	    }
		
		return true;
	}
	
	public int getSquareNum(int col,int row) {
		return row*rows + col;
	}
	
	public void addPieces() {
		//white
//		pieceList.add(new Pawn(this,true,0,6));  
//		pieceList.add(new Pawn(this,true,1,6));
//		pieceList.add(new Pawn(this,true,2,6));
//		pieceList.add(new Pawn(this,true,3,6));
//		pieceList.add(new Pawn(this,true,4,6));
//		pieceList.add(new Pawn(this,true,5,6));
//		pieceList.add(new Pawn(this,true,6,6));
//		pieceList.add(new Pawn(this,true,7,6));
//		pieceList.add(new Rook(this,true,0,7));
//		pieceList.add(new Rook(this,true,7,7));
//		pieceList.add(new Knight(this,true,1,7));
//		pieceList.add(new Knight(this,true,6,7));
//		pieceList.add(new Bishop(this,true,5,7));
//		pieceList.add(new Bishop(this,true,2,7));
		pieceList.add(new Queen(this,true,3,7));
		pieceList.add(new King(this,true,4,7));
				
		//black
//		pieceList.add(new Pawn(this,false,0,1));
//		pieceList.add(new Pawn(this,false,1,1));
//		pieceList.add(new Pawn(this,false,2,1));
//		pieceList.add(new Pawn(this,false,3,1));
//		pieceList.add(new Pawn(this,false,4,1));
//		pieceList.add(new Pawn(this,false,5,1));
//		pieceList.add(new Pawn(this,false,6,1));
//		pieceList.add(new Pawn(this,false,7,1));
//		pieceList.add(new Rook(this,false,0,0));
//		pieceList.add(new Rook(this,false,7,0));
//		pieceList.add(new Knight(this,false,1,0));
//		pieceList.add(new Knight(this,false	,6,0));
//		pieceList.add(new Bishop(this,false,5,0));
//		pieceList.add(new Bishop(this,false,2,0));
//		pieceList.add(new Queen(this,false,3,0));
		pieceList.add(new King(this,false,4,0));
	}
	
	public void updateGame() {
        Piece king = findKing(isWhiteToMove);
        
        if (checkFinder.isStalemate(isWhiteToMove)) {
            isGameOver = true;
            input.disableMouseInput();
            gameOverPanel.showResult("Stalemate!");
        }
        
        if (checkFinder.isGameOver(king)) {
            isGameOver = true;
            input.disableMouseInput();
            String result = isWhiteToMove ? "Black" : "White";
            gameOverPanel.showResult(result + " Wins!");
            return;
        }
        
        
    }
    
    public void resetBoard() {
        pieceList.clear();
        addPieces();
        selectedPiece = null;
        validMoves.clear();
        enPassantSquare = -1;
        isWhiteToMove = true;
        isGameOver = false;
        
        
        whiteTimer.reset(1); // Reset White's timer
        blackTimer.reset(1); // Reset Black's timer
        whiteTimer.stop();  // Start White's timer again
        blackTimer.stop();
        
        input.enableMouseInput();
        gameOverPanel.reset();
        repaint();
    }
    
    
    public void returnToHome() {
        Main.showMenu();
    }

	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		// Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(new Color(50, 50, 50)); 
        g2d.fillRoundRect(0, 0, cols * SQUARE_SIZE, rows * SQUARE_SIZE, BOARD_RADIUS, BOARD_RADIUS);

        // Clip the drawing area to the rounded rectangle to prevent overflow
        g2d.setClip(new RoundRectangle2D.Float(0, 0, cols * SQUARE_SIZE, rows * SQUARE_SIZE, BOARD_RADIUS, BOARD_RADIUS));
		
        
        //draw the chess board
		for (int i = 0;i<rows;i++) {
			for (int j = 0;j<cols;j++) {
				g2d.setColor((i+j)%2==0 ? new Color(235,236,208) : new Color(119,149,86));
				g2d.fillRect(j*SQUARE_SIZE, i*SQUARE_SIZE,SQUARE_SIZE,SQUARE_SIZE);
			}
		}
		//when piece is selected
		if (selectedPiece != null) {
			for (int i = 0;i<rows;i++) {
				for (int j = 0;j<cols;j++) {
					//highlight selected piece
					g2d.setColor(new Color(255, 255, 51,10));
					g2d.fillRect(selectedPiece.col * SQUARE_SIZE, selectedPiece.row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
					
					//show the valid move of selected piece
					if (isValidMove(new Movements(this,selectedPiece,j,i))) {
						g2d.setColor(new Color(0, 0, 0, 50));
						g2d.fillOval(j*SQUARE_SIZE+SQUARE_SIZE/3, i*SQUARE_SIZE+SQUARE_SIZE/3,SQUARE_SIZE/3,SQUARE_SIZE/3);						
					}
				}
			}
		}
		
		for (Piece p : pieceList) {
			p.draw(g2d);
		}
		
		g2d.setClip(null);
	}
}
