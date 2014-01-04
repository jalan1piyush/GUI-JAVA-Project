import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class used to create a drawing panel where we display our geometry 
 *
 */
public class Drawing extends JPanel implements MouseListener, MouseMotionListener, KeyListener{
		
	private static final long serialVersionUID = 1L;
	private int l, x, y, x1, y1, x2, y2, mx, my, xc, yc, del, alton, altoff, w, h;
	private boolean alts;
	private ArrayList<ArrayList<Double>> coord;
	public ArrayList<Integer> selection;
	public static ArrayList<Shape> coords;
	//private double[] id, lop, startx, starty, endx, endy;
	private String Mode;
	public String sfile, ofile;
	private Point2D.Double t1, t2;
	private Ellipse2D.Double p1;
	private Line2D.Double l1;
	private Ellipse2D.Double e1;
	private Rectangle2D.Double r1;
	private Cursor move, def, tar, hand;
	private Shape shp, shpm;
	private Color red, green, blue , black, orange, yellow, purple;
	
	// Create drawing surface and introduce Mouse Listeners 
	// for motion and click events
	/**
	 * Constructor for the class Drawing 
	 */
	public Drawing() {
		setDoubleBuffered(true);
		selection = new ArrayList<Integer>();
		coords = new ArrayList<Shape>();
		coord = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> co = new ArrayList<Double>();
		for (int i = 0; i < 6; i++){
			co = new ArrayList<Double>();
			coord.add(co);
		}
		//setSfile(new String (""));
		setFocusable(true);
		requestFocusInWindow();
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		def = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		move = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
		tar = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		green = new Color(0,150,0);
		blue = new Color(0,0,255);
		purple = new Color(128,0,128);
		red = new Color (255,0,0);
		yellow = new Color (255,255,0);
		orange = new Color (255,69,0);
		black = new Color (0,0,0);
		sfile=null;
	}
	
	//Get Mode set GUIFrame MenuItem
	/**
	 * Method used to set value to the variable Mode. This variable determines the status of the Drawing canvas as to whether we are drawing or selecting etc. 
	 * @param m
	 */
	public void set(String m){
		Mode = m;
		System.out.println("Mode:"+Mode);
	}
	
	// Draw Items from coords ArrayList

	
	
	/**
	 * This method is used to call method for respective drawing classes depending upon the mode and the drawing type
	 * @param g, an instance of the class Graphics
	 */
	
	public void DrawShapes(Graphics g) {
		paint(g);
		Graphics2D shape = (Graphics2D) g;
		for (int i = 0; i < coords.size(); i++){
			// Interruption to not draw items being moved
			if (Mode == "move" && selection.size()>0 && selection.contains(i)){ }
			else{
				if (coords.get(i).getClass().equals(Ellipse2D.Double.class)&& (int)coords.get(i).getBounds().getWidth()==6) {
					shape.fill((Shape) coords.get(i));
				} else {
					shape.draw((Shape) coords.get(i));
				}
			}
		}
		
}
	
	// Draw Point as a filled Ellipse and 
	// add it to coords ArrayList after mouse click
	/**
	 * Method used to draw a point 
	 * @param x datatype: Double 
	 * @param y datatype: Double 
	 */
	public void DrawPoint(double x, double y){
		//Graphics2D shape = (Graphics2D) g;
		p1 = new Ellipse2D.Double(x-3, y-3, 6, 6);
		coords.add(p1);
		DrawShapes(getGraphics());
	}
	
	// Draw intermediate lines, single line after 2 separate mouse clicks,  
	// And add new line to coords ArrayList
	/**
	 * @param g
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param a
	 */
	public void DrawLine(Graphics g, double x1, double y1, double x2, double y2, boolean a) {
		Graphics2D shape = (Graphics2D) g;
		l1 = new Line2D.Double(x1, y1, x2, y2);
		if (a==true){coords.add(l1);}
		shape.draw(l1);
		DrawShapes(getGraphics());
	}
	
	
	/**
	 * @param g
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param a
	 */
	public void DrawEllipse(Graphics g, double x, double y, double w, double h, boolean a) {
		Graphics2D shape = (Graphics2D) g;
		e1 = new Ellipse2D.Double(x, y, w, h);
		//l1 = new Line2D.Double(x1, y1, x2, y2);
		if (a==true){coords.add(e1);}
		shape.draw(e1);
		DrawShapes(getGraphics());
	}
	
	// Show selected items
	/**
	 * Method used to show selected items in red color
	 * @param g, an instance of the class Graphics
	 * @param sel, An ArrayList of Integers
	 */
	public void DrawSel(Graphics g, ArrayList<Integer> sel){
		Graphics2D shape = (Graphics2D) g;
		shape.setColor(red);
		// Differentiation between points to draw
		for (int i = 0; i<sel.size(); i++)
		    if (coords.get(sel.get(i)).getClass().equals(Ellipse2D.Double.class) && (int)coords.get(sel.get(i)).getBounds().getWidth()==6) {
				x = (int)coords.get(sel.get(i)).getBounds().getCenterX();
				y = (int)coords.get(sel.get(i)).getBounds().getCenterY();
	            shape.fillOval(x-4, y-4, 8, 8);
	            // As well as Lines to draw
	        } else {	    	
	        shape.setStroke(new BasicStroke(5));
	        shape.draw((Shape) coords.get(sel.get(i)));
		}
	}
	
	// Creation of visual Bounding Box
	/**
	 * Method used to draw a selection box
	 * @param g, an instance of the class Graphics 
	 * @param x1, Integer
	 * @param y1, Integer
	 * @param x2, Integer
	 * @param y2, Integer
	 */
	public void DrawSelBox(Graphics g, int x1, int y1, int x2, int y2){
		Graphics2D shape = (Graphics2D) g;
		shape.setStroke(new BasicStroke(3));
		x = Math.min(x1, x2);  
		y = Math.min(y1, y2);
		int abx = Math.abs(x1 - x2);  
		int aby = Math.abs(y1 - y2);
		r1 = new Rectangle2D.Double(x, y, abx, aby);
		if(x1<x2){
			shape.setColor(blue);
		} else {
			shape.setColor(green);
			shape.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{9}, 2.0f));
		}
		DrawShapes(getGraphics());
		shape.drawRect(x, y, abx, aby);	
		for (int i = 0; i < coords.size(); i++){
			if (x1 > x2 && coords.get(i).intersects(r1) && selection.contains(i)==false){
				selection.add(i);
			} 
			else if (x1 < x2 && r1.contains(coords.get(i).getBounds()) && selection.contains(i)==false) {
				selection.add(i);		
			}
			
			DrawSel(getGraphics(), selection);
		}
	}
	
	// Show Objects to be deleted
	/**
	 * Method used to delete elements 
	 * @param del : ArrayList of Integers
	 */
	public void DrawDel(ArrayList<Integer> del) {
		Collections.sort(del);
		if (del.size() < 1){
			System.out.println("Select something dummy!");
		} else {
			for (int i = del.size()-1; i >= 0; i--){
				coords.remove((int)del.get(i));
			}
			del.clear();
			DrawShapes(getGraphics());
		}
	}
	
	// Change location of Object
	/**
	 * Method used to move the graphic elements from one position to another 
	 * @param g Graphics
	 * @param x Double
	 * @param y Double
	 * @param a Double
	 * @param sel ArrayList of selected integers
	 */
	public void DrawMove(Graphics g, double x, double y, int a, ArrayList<Integer> sel){
		Graphics2D shape = (Graphics2D) g;
		for (int i = 0; i<sel.size(); i++) {
			// Differentiation between points to draw
			if (coords.get(sel.get(i)).getClass().equals(Ellipse2D.Double.class) && (int)coords.get(sel.get(i)).getBounds().getWidth()==6) {
				if(a==2){
					shape.setColor(red);
					p1 = new Ellipse2D.Double(x-4, y-4, 8, 8);
				} else if(a==3){
					shape.setColor(black);
					coords.remove((int)sel.get(i));
					p1 = new Ellipse2D.Double(x-3, y-3, 6, 6);
					coords.add(p1);
					selection.clear();					
				}
				DrawShapes(getGraphics());
				shape.fill(p1);
			// And of lines to draw
			} else if (coords.get(sel.get(i)).getClass().equals(Line2D.Double.class))
			{
				if(a==1){
					xc = (int)x;
					yc = (int)y;
					l1 = ((Line2D.Double) coords.get((int)sel.get(i)));
					shape.setColor(red);
				}
				if(a==2){
					shape.setColor(red);
					t1 = new Point2D.Double(((Line2D) coords.get((int)sel.get(i))).getX1()+(x-xc),((Line2D) coords.get((int)sel.get(i))).getY1()+(y-yc));
					t2 = new Point2D.Double(((Line2D) coords.get((int)sel.get(i))).getX2()+(x-xc),((Line2D) coords.get((int)sel.get(i))).getY2()+(y-yc));
					l1 = new Line2D.Double(t1, t2);
				} else if(a==3){
					shape.setColor(black);
					t1 = new Point2D.Double(((Line2D) coords.get((int)sel.get(i))).getX1()+(x-xc),((Line2D) coords.get((int)sel.get(i))).getY1()+(y-yc));
					t2 = new Point2D.Double(((Line2D) coords.get((int)sel.get(i))).getX2()+(x-xc),((Line2D) coords.get((int)sel.get(i))).getY2()+(y-yc));
					((Line2D) coords.get((int)sel.get(i))).setLine(t1, t2);
					//shape.draw(l1);
					selection.clear();
				}
				shape.setStroke(new BasicStroke(5));
				DrawShapes(getGraphics());
				shape.draw(l1);
			} else  
			{
				if(a==1){
					xc = (int)x;
					yc = (int)y;
					e1 = ((Ellipse2D.Double) coords.get((int)sel.get(i)));
					shape.setColor(red);
				}
				if(a==2){
					shape.setColor(red);
					//t1 = new Ellipse2D.Double(((Ellipse2D) coords.get((int)sel.get(i))).getX()+(x-xc),((Ellipse2D) coords.get((int)sel.get(i))).getY()+(y-yc));
					//t2 = new Point2D.Double(((Line2D) coords.get((int)sel.get(i))).getX2()+(x-xc),((Line2D) coords.get((int)sel.get(i))).getY2()+(y-yc));
					//l1 = new Line2D.Double(t1, t2);
					e1 = new Ellipse2D.Double(((Ellipse2D) coords.get((int)sel.get(i))).getX()+(x-xc),((Ellipse2D) coords.get((int)sel.get(i))).getY()+(y-yc),((Ellipse2D) coords.get((int)sel.get(i))).getWidth(),((Ellipse2D) coords.get((int)sel.get(i))).getHeight());
				} else if(a==3){
					shape.setColor(black);
					((Ellipse2D) coords.get((int)sel.get(i))).setFrame(((Ellipse2D) coords.get((int)sel.get(i))).getX()+(x-xc),((Ellipse2D) coords.get((int)sel.get(i))).getY()+(y-yc),((Ellipse2D) coords.get((int)sel.get(i))).getWidth(),((Ellipse2D) coords.get((int)sel.get(i))).getHeight());					
					selection.clear();
				}
				shape.setStroke(new BasicStroke(5));
				DrawShapes(getGraphics());
				shape.draw(e1);
			}
			
		}
	}
		
	// Export of coords into Double ArrayLists
	/**
	 * Method to export the elements from a shape ArrayList 'coords' to a another ArrayList called coord 
	 * @param s Boolean
	 */
	public void Save(boolean s){
		
		for (int i = 0; i < coords.size(); i ++) {
			if (coords.get(i).getClass().equals(Ellipse2D.Double.class) && ((Ellipse2D) coords.get(i)).getHeight()==6) {
				coord.get(0).add((double)i);
				coord.get(1).add((double)0);
				coord.get(2).add(((Ellipse2D) coords.get(i)).getX());
				coord.get(3).add(((Ellipse2D) coords.get(i)).getY());
				coord.get(4).add((double) 0);
				coord.get(5).add((double) 0);
			} else if ((coords.get(i).getClass().equals(Line2D.Double.class))){
				coord.get(0).add((double)i);
				coord.get(1).add((double)1);
				coord.get(2).add(((Line2D) coords.get(i)).getX1());
				coord.get(3).add(((Line2D) coords.get(i)).getY1());
				coord.get(4).add(((Line2D) coords.get(i)).getX2());
				coord.get(5).add(((Line2D) coords.get(i)).getX2());
			} else {
				coord.get(0).add((double)i);
				coord.get(1).add((double)2);
				coord.get(2).add(((Ellipse2D) coords.get(i)).getX());
				coord.get(3).add(((Ellipse2D) coords.get(i)).getY());
				coord.get(4).add(((Ellipse2D) coords.get(i)).getWidth());
				coord.get(5).add(((Ellipse2D) coords.get(i)).getHeight());
				
			}
		} 
		if (s==false){
			JFileChooser saveFile = new JFileChooser();
			saveFile.setMultiSelectionEnabled(false);
			saveFile.setFileFilter(new FileNameExtensionFilter("csv", "CSV Format"));
            saveFile.setDialogTitle("Save");
            saveFile.setApproveButtonText("Save");
        	saveFile.showOpenDialog(null);
        	String file = saveFile.getSelectedFile().getAbsolutePath();
        	sfile = file.replace("\\", "/");
        	System.out.println(sfile);
        	Updatenow.Save(coord, sfile);
		}
		else if(s==true && sfile==null){
			Updatenow.Save(coord, ofile);			
		}
		else{
			Updatenow.Save(coord, sfile);
		}
		
		//System.out.println(coord);
		for (int c = 0; c<coord.size();c++){
			coord.get(c).clear();
		}
		DrawShapes(getGraphics());
	}
		
	
	// Open CSV file and Import from Database
	/**
	 * Method to import elements from a CSV file
	 */
	public void Open(){
		
			JFileChooser openFile = new JFileChooser();
        	openFile.showOpenDialog(null);
        	ofile = openFile.getSelectedFile().getAbsolutePath();
            ofile = ofile.replace("\\", "/");
        	System.out.println(ofile);
        	Updatenow.Open(ofile);
            DrawShapes(getGraphics());
		
	}
	
	// MousePressed listener for mouse click events
	/* (non-Javadoc)
	 * Method called when mouse is pressed and calls respective functions depending upon the mouse click 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed (MouseEvent event) {
		x = event.getX();
		y = event.getY();
		//Left-Click events
		if (SwingUtilities.isLeftMouseButton(event)){
			// Get X, Y for point - Initiate DrawPoint()
			if (Mode == "point"){
				DrawPoint(x, y);
	    	}
			// Get X1, Y1, X2, Y2 for line - Initiate DrawLine()
	    	if (Mode == "line"){
	    		if (l==0){
	    			x1 = event.getX();
	    			y1 = event.getY();
	    		} else {
	    			x2 = event.getX();
	    			y2 = event.getY();
	    			DrawLine(getGraphics(), x1, y1, x2, y2, true);
	    			l=-1;
	    			DrawShapes(getGraphics());
	    		}
	    		l++;
	    	}
	    	if (Mode == "ellipse"){
	    		if (l==0){
	    			x1 = event.getX();
	    			y1 = event.getY();
	    		} else {
	    			x2 = event.getX();
	    			y2 = event.getY();
	    			
	    			w = Math.abs(x1 - x2);
	    			h = Math.abs(y1 - y2);
	    			y = Math.min(y1, y2);
	    			x = Math.min(x1, x2);
	    			DrawEllipse(getGraphics(), x, y, w, h, true);
	    			l=-1;
	    			DrawShapes(getGraphics());
	    		}
	    		l++;
	    	}

	    	// Select Mode - Initiate DrawSelected, 
	    	if (Mode == "sel"){
	    		x1 = event.getX();
    			y1 = event.getY();
	    		if (alts == false){
	    			selection.clear();
	    		}
	    		for (int i = 0; i < coords.size(); i++){
	    			
	    			shp = coords.get(i);
	    			if (shp.intersects(x1, y1, 4, 4) && selection.contains(i)==false){
	    				selection.add(i);
	    				DrawShapes(getGraphics());
	    				DrawSel(getGraphics(), selection);
	    				break;
	    			} else if (shp.intersects(x1, y1, 4, 4) && selection.contains(i)==true) {
	    				selection.remove(selection.indexOf(i));
	    				DrawShapes(getGraphics());
	    				DrawSel(getGraphics(), selection);
	    			}
	    		}
	    	}
	    	if (Mode == "move"){
	    		selection.clear();
	    		for (int i = 0; i < coords.size(); i++){
	    			shp = coords.get(i);
	    			if (shp.intersects(event.getX(), event.getY(), 4, 4)) {
	    				selection.add(i);
	    				DrawShapes(getGraphics());
	    				DrawSel(getGraphics(), selection);
	    				DrawMove(getGraphics(), x, y, 1, selection);
	    			}
				}
			}
		}
	}

	// Mouse Drag events for Object Move and Bounding Box
	// Additional variables to set borders of the Bounding Box
	/* (non-Javadoc)
	 * Mouse Drag events for Object Move and Bounding Box and add variables to set borders of the Bounding Box 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		if (Mode == "sel") {
			if (alts == false){ 
				selection.clear();
			}
			mx = getWidth(); my = getHeight();
			x2 = e.getX();
			y2 = e.getY();
			if (x2<0){x2=0;} if (y2<0){y2=0;}
			if (x2>mx){x2=mx-1;} if (y2>my){y2=my-1;}
			DrawSelBox(getGraphics(), x1, y1, x2, y2);
		}
		if(Mode == "move"){
			x1 = e.getX();
			y1 = e.getY();
			DrawMove(getGraphics(), x1, y1, 2, selection);
		}
	}
		
	/* (non-Javadoc)
	 * Method activated when the mouse is moved and draws the selected type of graphic if any
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved (MouseEvent ev){
		if (Mode == "point" || Mode == "line"|| Mode == "ellipse"){
			setCursor(tar);
		} else {
			setCursor(def);
		}
		if (Mode == "line" && l==1) {
			DrawShapes(getGraphics());
			DrawLine(getGraphics(), x1, y1, ev.getX(), ev.getY(), false);
		}
		if (Mode == "ellipse" && l==1) {
			DrawShapes(getGraphics());
			DrawEllipse(getGraphics(), Math.min(x1, ev.getX()), Math.min(y1, ev.getY()), Math.abs(x1-ev.getX()), Math.abs(y1-ev.getY()), false);
		}
		if (Mode == "sel" || Mode == "delete") {
			for (int i = 0; i < coords.size(); i++){
				shpm = (coords.get(i));
    			if (shpm.intersects(ev.getX(), ev.getY(), 4, 4)){
    				setCursor(hand);
    				break;
    			} else { setCursor(def); }
			}
		}
		if (Mode == "move") {
			for (int i = 0; i < coords.size(); i++){
				shpm = (coords.get(i));
    			if (shpm.intersects(ev.getX(), ev.getY(), 4, 4)){
    				setCursor(move);
    				break;
    			} else { setCursor(def);}
			}
		}
	}
	
	/* (non-Javadoc)
	 * Method initiated when mouse clicked dragged and released 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		if (Mode == "sel"){
			DrawSelBox(getGraphics(), 0, 0, 0, 0);
			DrawSel(getGraphics(), selection);
		}
		if (Mode == "move"){
			x = e.getX();
			y = e.getY();
			DrawMove(getGraphics(), x, y, 3, selection);
			DrawShapes(getGraphics());
		}
	}
	
	public void keyPressed(KeyEvent k){
		del = k.getKeyCode();
		if (del == KeyEvent.VK_DELETE){
			DrawDel(selection);
		}
		alton = k.getKeyCode();
		if (alton == KeyEvent.VK_CONTROL){
			alts = true;
		}
	}
	
	public void keyTyped (KeyEvent k) {}
	public void keyReleased (KeyEvent k) {
		altoff = k.getKeyCode();
		if (altoff == KeyEvent.VK_CONTROL){
			alts = false;
		}
	}
	
	public void mouseEntered(MouseEvent event) {}
	public void mouseExited(MouseEvent event) {}
	public void mouseClicked(MouseEvent event) {}

	/**
	 * Get the filename of the file where arraylist 'coord' has to be saved 
	 * @return String Filename 
	 */
	public String getSfile() {
		return sfile;
	}

	/**
	 * Set the the variable sfile as input parameter String 
	 * @param sfile
	 */
	public void setSfile(String sfile) {
		this.sfile = sfile;
	}

}
	
