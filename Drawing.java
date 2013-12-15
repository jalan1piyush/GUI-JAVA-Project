
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.*;

public class Drawing extends JPanel implements MouseListener, MouseMotionListener, KeyListener{
		
	private static final long serialVersionUID = 1L;
	public int l, x, y, x1, y1, x2, y2, mx, my, xc, yc, del, alton, altoff;
	public double startx, starty, endx, endy;
	public boolean alts;
	public ArrayList<ArrayList<Double>> coord;
	public ArrayList<Integer> selection;
	public ArrayList<Shape> coords;
	public String Mode;
	public Point2D.Double t1, t2, center;
	public Ellipse2D.Double p1;
	public Line2D.Double l1;
	public Rectangle2D.Double r1;
	public Cursor move, def, tar, hand;
	public Shape shp, shpm;
        static final String USER = "root";
        static final String PASS = "0000";

	
	// Create drawing surface and introduce Mouse Listeners 
	// for motion and click events
	public Drawing() {
		selection = new ArrayList<Integer>();
		coords = new ArrayList<Shape>();
		coord = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> co = new ArrayList<Double>();
		for (int i = 0; i < 6; i++){
			co = new ArrayList<Double>();
			coord.add(co);
		}
		setFocusable(true);
		requestFocusInWindow();
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		def = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		move = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
		tar = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		DrawShapes(getGraphics());
	}
	
	//Get Mode set GUIFrame MenuItem
	public void set(String m){
		Mode = m;
		System.out.println("Mode:"+Mode);
		DrawShapes(getGraphics());
	}
	
	// Draw Items from coords ArrayList
	public void DrawShapes(Graphics g) {
		paint(g);
		Graphics2D shape = (Graphics2D) g;
		for (int i = 0; i < coords.size(); i++){
			// Interuption to not draw items being moved
			if (Mode == "move" && selection.size()>0 && selection.contains(i)){ }
			else{
				if (coords.get(i).getClass().equals(Ellipse2D.Double.class)) {
					shape.fill((Shape) coords.get(i));
				} else {
					shape.draw((Shape) coords.get(i));
				}
			}
		}
	}
	
	// Draw Point as a filled Ellipse and 
	// add it to coords ArrayList after mouse click
	public void DrawPoint(Graphics g, double x, double y){
		Graphics2D shape = (Graphics2D) g;
		p1 = new Ellipse2D.Double(x-3, y-3, 6, 6);
		coords.add(p1);
		shape.fill(p1);
		DrawShapes(getGraphics());
		System.out.println(coords);
	}
	
	// Draw itermediate lines, single line after 2 separate mouse clicks,  
	// And add new line to coords ArrayList
	public void DrawLine(Graphics g, double x1, double y1, double x2, double y2, boolean a) {
		Graphics2D shape = (Graphics2D) g;
		l1 = new Line2D.Double(x1, y1, x2, y2);
		if (a==true){coords.add(l1);}
		shape.draw(l1);
	}
	
	// Show selected items
	public void DrawSel(Graphics g, ArrayList<Integer> sel){
		Graphics2D shape = (Graphics2D) g;
		shape.setColor(Color.red);
		// Differentiation between points to draw
		for (int i = 0; i<sel.size(); i++)
			if (coords.get(sel.get(i)).getClass().equals(Ellipse2D.Double.class)) {
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
	public void DrawSelBox(Graphics g, int x1, int y1, int x2, int y2){
		Graphics2D shape = (Graphics2D) g;
		shape.setColor(Color.blue);
		shape.setStroke(new BasicStroke(4));
		x = Math.min(x1, x2);  
		y = Math.min(y1, y2);
		int abx = Math.abs(x1 - x2);  int aby = Math.abs(y1 - y2);
		r1 = new Rectangle2D.Double(x, y, abx, aby);
		//shape.setColor(Color.TRANSLUCENT);
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
	public void DrawMove(Graphics g, double x, double y, int a, ArrayList<Integer> sel){
		Graphics2D shape = (Graphics2D) g;
		for (int i = 0; i<sel.size(); i++) {
			// Differentiation between points to draw
			if (coords.get(sel.get(i)).getClass().equals(Ellipse2D.Double.class)) {
				if(a==2){
					shape.setColor(Color.red);
					p1 = new Ellipse2D.Double(x-4, y-4, 8, 8);
				} else if(a==3){
					shape.setColor(Color.black);
					coords.remove((int)sel.get(i));
					p1 = new Ellipse2D.Double(x-3, y-3, 6, 6);
					coords.add(p1);
					selection.clear();					
				}
				DrawShapes(getGraphics());
				shape.fill(p1);
			// And of lines to draw
			} else {
				if(a==2){
					shape.setColor(Color.red);
					xc = (int)(((((Line2D) coords.get((int)sel.get(i))).getX1()+((Line2D) coords.get((int)sel.get(i))).getX2())/2)-x);
					yc = (int)(((((Line2D) coords.get((int)sel.get(i))).getY1()+((Line2D) coords.get((int)sel.get(i))).getY2())/2)-y);
					t1 = new Point2D.Double(((Line2D) coords.get((int)sel.get(i))).getX1()-xc,((Line2D) coords.get((int)sel.get(i))).getY1()-yc);
					t2 = new Point2D.Double(((Line2D) coords.get((int)sel.get(i))).getX2()-xc,((Line2D) coords.get((int)sel.get(i))).getY2()-yc);
					l1 = new Line2D.Double(t1, t2);				
				} else if(a==3){
					shape.setColor(Color.black);
					xc = (int)(((((Line2D) coords.get((int)sel.get(i))).getX1()+((Line2D) coords.get((int)sel.get(i))).getX2())/2)-x);
					yc = (int)(((((Line2D) coords.get((int)sel.get(i))).getY1()+((Line2D) coords.get((int)sel.get(i))).getY2())/2)-y);
					t1 = new Point2D.Double(((Line2D) coords.get((int)sel.get(i))).getX1()-xc,((Line2D) coords.get((int)sel.get(i))).getY1()-yc);
					t2 = new Point2D.Double(((Line2D) coords.get((int)sel.get(i))).getX2()-xc,((Line2D) coords.get((int)sel.get(i))).getY2()-yc);
					l1 = new Line2D.Double(t1, t2);						
					coords.add(l1);
					coords.remove((int)sel.get(i));
					selection.clear();
				}
				shape.setStroke(new BasicStroke(5));
				DrawShapes(getGraphics());
				shape.draw(l1);
			}
		}
	}
		
	
	public void Save(){
		//coord.clear();
		for (int i = 0; i < coords.size(); i ++) {
			if (coords.get(i).getClass().equals(Ellipse2D.Double.class)) {
				startx = ((Ellipse2D) coords.get(i)).getX();
				starty = ((Ellipse2D) coords.get(i)).getY();
				coord.get(0).add((double)i);
				coord.get(1).add((double)0);
				coord.get(2).add(startx);
				coord.get(3).add(starty);
				coord.get(4).add((double) 0);
				coord.get(5).add((double) 0);
			} else if ((coords.get(i).getClass().equals(Line2D.Double.class))){
				startx = ((Line2D) coords.get(i)).getX1();
				starty = ((Line2D) coords.get(i)).getY1();
				endx = ((Line2D) coords.get(i)).getX2();
				endy = ((Line2D) coords.get(i)).getY2();
				coord.get(0).add((double)i);
				coord.get(1).add((double)1);
				coord.get(2).add(startx);
				coord.get(3).add(starty);
				coord.get(4).add(endx);
				coord.get(5).add(endy);
			}
		}
		System.out.println(coord);
               // GUIFrame.coords(coord);                
                
                
                
                
                
                
                 Connection conn = null;
                 Statement stmt = null;
                 try{
                     
                 // JDBC driver name and database URL
                     
                String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
                String DB_URL = "jdbc:mysql://localhost/LANDP";
                
                 //STEP 2: Register JDBC driver
                 Class.forName("com.mysql.jdbc.Driver");

                 //STEP 3: Open a connection
                 System.out.println("Connecting to database...");
                 conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 stmt = conn.createStatement();
                                String sqlinsert = ""; 
                                String a1 = String.valueOf(coord.get(0).get(0));
                                System.out.println(a1);
                                System.out.println(coord.get(0).get(0));
          
                                
                         
                                for (int i=0; i<coord.get(0).size();i++)
        {
            sqlinsert = "INSERT INTO COORDINATES (ID,LOP,STARTX,STARTY,ENDX,ENDY)"+
                            "VALUES ("+  
                            coord.get(0).get(i)+", "+
                            coord.get(1).get(i)+", "+
                            coord.get(2).get(i)+", "+
                            coord.get(3).get(i)+", "+
                            coord.get(4).get(i)+", "+
                            coord.get(5).get(i)+")";
                            stmt.executeUpdate(sqlinsert);
                
        };
        
        
                              /*
            sqlinsert = "INSERT INTO COORDINATES (ID,LOP,STARTX,STARTY,ENDX,ENDY)"+
                            "VALUES (0,0,1.0,1.0,1.0,1.0);";
                            System.out.println("vhj: "+sqlinsert);
                            stmt.executeUpdate(sqlinsert);
                */
        
        
                      
                 
                 
                 
                 
                 

               
                 }
                 catch(SQLException se){
                  //Handle errors for JDBC
                   se.printStackTrace();
                 }
                 catch(Exception e){
                    //Handle errors for Class.forName
                    e.printStackTrace();
                 }
                 finally{
                    //finally block used to close resources
                    try{
                       if(stmt!=null)conn.close();
                    }
                    catch(SQLException se){
                    }// do nothing
                    try{
                       if(conn!=null)conn.close();
                    }
                    catch(SQLException se){
                       se.printStackTrace();
                    }//end finally try
                  }//end try
            System.out.println("Goodbye!");
                       
	}
	
	// MousePressed listener for mouse click events
	public void mousePressed (MouseEvent event) {
		//Left-Click events
		if (SwingUtilities.isLeftMouseButton(event)){
			// Get X, Y for point - Initiate DrawPoint()
			if (Mode == "point"){
				x = event.getX();
				y = event.getY();
				DrawPoint(getGraphics(), x, y);
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
	    			}
				}
			}
		}
	}

	// Mouse Drag events for Object Move and Bounding Box
	// Additional variables to set borders of the Bounding Box
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
		
	public void mouseMoved (MouseEvent ev){
		if (Mode == "point" || Mode == "line"){
			setCursor(tar);
		} else {
			setCursor(def);
		}
		if (Mode == "line" && l==1) {
			DrawShapes(getGraphics());
			DrawLine(getGraphics(), x1, y1, ev.getX(), ev.getY(), false);
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

}
