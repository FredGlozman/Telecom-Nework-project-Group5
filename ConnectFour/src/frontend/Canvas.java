package frontend;

import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;

public abstract class Canvas extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 6885140849681187863L;
	protected static final int FPS = 30;
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}
