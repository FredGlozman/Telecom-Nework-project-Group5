package frontend;

import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;

/**
 * Abstract class extended by all view classes. Provides information about the frame per second count (FPS), helper methods
 * to ensure the game looks good in all sizes, and ensures the view is focused properly by user input. 
 */
public abstract class Canvas extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
	private static final long serialVersionUID = 6885140849681187863L;
	protected static final int FPS = 30;
	
	/**
	 * Constructor: initialize all focus to the view.
	 */
	public Canvas() {
		setFocusable(true);
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
		requestFocusInWindow();
	}

	@Override
	protected void paintComponent(Graphics g) {
		// Every time the frame is to be drawn, ensure the previous frame is cleared.
		super.paintComponent(g);
	}
	
	/**
	 * Given a width measure, adjust it to fit the window width.
	 * @param x Width of object to resize.
	 * @return Resized width of the object.
	 */
	protected static int adjustWidth(int x) {
		return (x * WindowFrame.WIDTH) / WindowFrame.DEFAULT_WIDTH_HEIGHT;
	}
	
	/**
	 * Given a height measure, adjust it to fit the window height.
	 * @param x Height of object to resize.
	 * @return Resized height of the object.
	 */
	protected static int adjustHeight(int x) {
		return (x * WindowFrame.HEIGHT) / WindowFrame.DEFAULT_WIDTH_HEIGHT;
	}
	
	/**
	 * Lose all focus on the window upon view switch.
	 */
	public void cleanUp() {
		setFocusable(false);
		removeMouseMotionListener(this);
		removeMouseListener(this);
		removeKeyListener(this);
	}
}
