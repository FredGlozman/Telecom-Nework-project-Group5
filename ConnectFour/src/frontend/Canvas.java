package frontend;

import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;

public abstract class Canvas extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
	private static final long serialVersionUID = 6885140849681187863L;
	protected static final int FPS = 30;
	
	public Canvas() {
		setFocusable(true);
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
		requestFocusInWindow();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
	
	protected static int adjustWidth(int x) {
		return (x * WindowFrame.WIDTH) / WindowFrame.DEFAULT_WIDTH_HEIGHT;
	}
	
	protected static int adjustHeight(int x) {
		return (x * WindowFrame.HEIGHT) / WindowFrame.DEFAULT_WIDTH_HEIGHT;
	}
}
