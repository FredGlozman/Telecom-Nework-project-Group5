package frontend;

import java.awt.*;
import java.awt.geom.*;

public class FallingToken {
	private Ellipse2D representation;
	private Rectangle bounds;
	private int column, row;
	private int type;
	private int offset;
	private int stepsRemaining;
	
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
	
	public boolean update() {
		this.bounds.y += GameCanvas.ADJUSTED_ANIMATION_SPEED;
		this.representation.setFrame(this.bounds);
		return --this.stepsRemaining == 0;
		
	}
	
	public Ellipse2D getRepresentation() {
		return representation;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public int getType() {
		return type;
	}

	public int getStepsRemaining() {
		return stepsRemaining;
	}
}
