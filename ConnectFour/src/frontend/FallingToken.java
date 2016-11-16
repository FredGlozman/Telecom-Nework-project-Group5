package frontend;

import java.awt.*;
import java.awt.geom.*;

/**
 * Class responsible for the falling token animation in the game UI. 
 */
public class FallingToken {
	private Ellipse2D representation;
	private Rectangle bounds;
	private int column, row;
	private int type; // Color (1 or 2)
	private int offset; // How much higher the token must be when it starts falling.
	private int stepsRemaining; // Frames remaining before the token reaches its landing position.
	
	/**
	 * Constructor: creates the ellipse representing the token and places it at its initial position
	 * so it will fall in place.
	 * @param column Column where the token is dropped.
	 * @param row Row where the token will ultimately land.
	 * @param type Integer associated to the color of the token.
	 */
	public FallingToken(int column, int row, int type) {
		this.column = column;
		this.row = row;
		this.type = type;
		
		this.representation = GameCanvas.getEllipseAtCoords(column, row); 
		this.bounds = this.representation.getBounds();
		this.offset = (int) (WindowFrame.HEIGHT * (GameCanvas.TOP_BOTTOM_MARGIN + row / 6.0));
		this.bounds.y -= offset;
		this.representation.setFrame(this.bounds);
		this.stepsRemaining = offset / GameCanvas.ADJUSTED_ANIMATION_SPEED;
	}
	
	/**
	 * Single frame movement for the token. Falls by a fixed amount, no acceleration.
	 * @return Whether the token landed in its place.
	 */
	public boolean update() {
		this.bounds.y += GameCanvas.ADJUSTED_ANIMATION_SPEED;
		this.representation.setFrame(this.bounds);
		return --this.stepsRemaining == 0;
	}
	
	/**
	 * Getter function for the token represented as an ellipse, necessary to draw it.
	 * @return Ellipse representation of the token.
	 */
	public Ellipse2D getRepresentation() {
		return representation;
	}

	/**
	 * Getter function for the column in which the token is dropped.
	 * @return Column number (range: 0-6 incl.)
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Getter function for the row where the token will land.
	 * @return Row number (range: 0-5 incl.)
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Getter function for the token color.
	 * @return Color represented as integer (1 or 2)
	 */
	public int getType() {
		return type;
	}

	/**
	 * Getter function to see how many more frames remain until the token lands.
	 * @return Frames remaining.
	 */
	public int getStepsRemaining() {
		return stepsRemaining;
	}
}
