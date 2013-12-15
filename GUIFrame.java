import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

public class GUIFrame extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	public Drawing draw;
	public JFrame pane;
	public String Mode;
	public JMenu file, create, edit;
	public JPanel Drawing;
	public JMenuItem point, line, sel, move, delete, open, save, saveas;
	public MouseListener offclick;
        
        //  Database credentials
        static final String USER = "root";
        static final String PASS = "0000";

	public GUIFrame(){
		super("Interface Frame");

		draw = new Drawing();
		draw.set(Mode);
		//addMouseListener((java.awt.event.MouseListener) this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar menu = Menu();
		add(menu, BorderLayout.NORTH);
		add(draw);
	}

	public JMenuBar Menu() {
	  JMenuBar Menu = new JMenuBar();
	  
	  file = new JMenu("File");
	  create = new JMenu("Create");
	  edit = new JMenu("Edit");
	  
      point = new JMenuItem ("Point");
      	KeyStroke ctp = KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK);
      	point.setAccelerator(ctp);
      line = new JMenuItem ("Line");
      	KeyStroke ctl = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK);
    	line.setAccelerator(ctl);
      sel = new JMenuItem ("Select");
      	KeyStroke cts = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK);
      	sel.setAccelerator(cts);
      move = new JMenuItem ("Move Object");
      	KeyStroke ctm = KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK);
    	move.setAccelerator(ctm);
      delete = new JMenuItem ("Delete Object");
      	//KeyStroke del = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
      	//delete.setAccelerator(del);
      open = new JMenuItem("Open");
      	KeyStroke opn = KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK);
      	open.setAccelerator(opn);
      save = new JMenuItem("Save");
      	KeyStroke sve = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK);
  		save.setAccelerator(sve);
      saveas = new JMenuItem("Save As");
      	
      
      file.add(open);
      file.add(save);
      file.add(saveas);
      open.addActionListener(this);
      open.setActionCommand("open");
      save.addActionListener(this);
      save.setActionCommand("save");
      saveas.addActionListener(this);
      saveas.setActionCommand("saveas");
      
      create.add(point);
      create.add(line);
      point.addActionListener(this);
      point.setActionCommand("point");
      line.addActionListener(this);
      line.setActionCommand("line");
       
      edit.add(sel);
      edit.add(move);
      edit.add(delete);
      move.addActionListener(this);
      move.setActionCommand("move");
      sel.addActionListener(this);
      sel.setActionCommand("sel");
      delete.addActionListener(this);
      delete.setActionCommand("delete");
      
      Menu.add(file);
      Menu.add(create);
      Menu.add(edit);
      return Menu;
	}
	//public class as {
		public void mousePressed(MouseEvent e){
			draw.DrawShapes(getGraphics());
		}
	//}
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("open")){
			draw.set("open");
		}
		if (cmd.equals("save")){
			draw.Save();
		}
		if (cmd.equals("saveas")){
			draw.set("saveas");
		}
	    if (cmd.equals("point")){
	    	draw.set("point");
	    }
	    if (cmd.equals("line")){
	    	draw.set("line");
	    }
	    if (cmd.equals("sel")){
	    	draw.set("sel");
	    }
	    if (cmd.equals("move")){
	    	draw.set("move");
	    }
	    if (cmd.equals("delete")){
	    	draw.DrawDel(draw.selection);
	    }
    }	
        

	public static void main (String[] args) {
		 GUIFrame mainwindow = new GUIFrame();
		 mainwindow.setSize(600,550); 
		 mainwindow.setLocation(400,100); 
		 mainwindow.setVisible(true);
                 
                 
                                  
                 Connection conn = null;
                 Statement stmt = null;
                 try{
                     
                 // JDBC driver name and database URL
                     
                String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
                String DB_URL = "jdbc:mysql://localhost/";
                
                 //STEP 2: Register JDBC driver
                 Class.forName("com.mysql.jdbc.Driver");

                 //STEP 3: Open a connection
                 System.out.println("Connecting to database...");
                 conn = DriverManager.getConnection(DB_URL, USER, PASS);

                 //STEP 4: Execute a query
                 System.out.println("Creating database...");
                 stmt = conn.createStatement();
                 
                 //STEP 5: Creating a database
                 String sql1 = "CREATE DATABASE IF NOT EXISTS LANDP";
                 stmt.executeUpdate(sql1);
                 System.out.println("Database created successfully...");
                 
                 DB_URL = "jdbc:mysql://localhost/LANDP";
                //STEP 3: Open a connection
                System.out.println("Connecting to a selected database...");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Connected database successfully...");
      
                //STEP 4: Execute a query
                System.out.println("Dropping table in given database...");
                stmt = conn.createStatement();
                stmt.executeUpdate("DROP TABLE IF EXISTS COORDINATES");
                     
                System.out.println("Creating table in given database...");
                stmt = conn.createStatement();
                 String sql4 = "CREATE TABLE COORDINATES " +
                      "(ID INTEGER not NULL, " +
                        " LOP INTEGER, " + 
                        " STARTX DOUBLE, " + 
                        " STARTY DOUBLE, " + 
                        " ENDX DOUBLE, " + 
                        " ENDY DOUBLE, " +
                        " PRIMARY KEY (ID))"; 

                    stmt.executeUpdate(sql4);
                    System.out.println("Created table in given database...");
               
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
	}//end main
}
