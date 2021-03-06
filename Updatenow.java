import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Updatenow {
	private static String url = "jdbc:mysql://localhost/LANDP";
	private static String driver = "com.mysql.jdbc.Driver";
	private static String userName = "root"; 
	private static String password = "0000";

//Save details to MySQL Database and then to CSV
	/**
	 * Method created to save details like coordinates, geometry type and index to first database and then export it to CSV
	 * @param x: ArrayList, coord passed from Drawing Class
	 * @param sfile : String, filename on which Save Method is to work
	 * 
	 */
	public static void Save(ArrayList<ArrayList<Double>> x, String sfile) {
		int n = x.get(0).size();
		System.out.println(sfile);
		try{
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url,userName, password);
			PreparedStatement clear = conn.prepareStatement("TRUNCATE `LANDP`.`COORDINATES`");
			clear.execute();
			Statement sql = conn.createStatement();
			PreparedStatement out = conn.prepareStatement("INSERT INTO LANDP.COORDINATES (ID,LOP,STARTX,STARTY,ENDX,ENDY) VALUES (?,?,?,?,?,?)");
			for (int i = 0; i<n; i++){
				out.setInt(1, i+1);
				out.setDouble(2, x.get(1).get(i));
				out.setDouble(3, x.get(2).get(i));
                out.setDouble(4, x.get(3).get(i));
				out.setDouble(5, x.get(4).get(i));
                out.setDouble(6, x.get(5).get(i));
				out.execute();
			}
					
			System.out.println("Before 'endswith' check condition and sfile=" + sfile);
			if(!sfile.endsWith(".csv"))
			{
				System.out.println(" 'sFile' File name does not end with csv");
				String save = ("SELECT ID,LOP,STARTX,STARTY,ENDX,ENDY FROM LANDP.COORDINATES INTO OUTFILE '"+sfile+".csv' FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\n'");
				sql.executeQuery(save);
			}
			else
			{	
				System.out.println(" 'sFile' File name ends with csv");
				String save = ("SELECT ID,LOP,STARTX,STARTY,ENDX,ENDY FROM LANDP.COORDINATES INTO OUTFILE '"+sfile+"' FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\n'");
				sql.executeQuery(save);
			}
				
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//Open file to MySQL Database and then to CSV
	/**
	 * Method created to enter details like coordinates, geometry type and index from selected CSV to first database and then display it on screen
	 * @param ofile, filename of the file from which the geometry is to be extracted 
	 */
	public static void Open (String ofile) {
		ArrayList<ArrayList<Double>> In = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> Insp = new ArrayList<Double>();
		for (int i = 0; i < 6; i++){
			Insp = new ArrayList<Double>();
			In.add(Insp);
		}
	
		try{
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url,userName, password);
			Statement sql = conn.createStatement(); 
			String open = ("load data local infile '"+ofile+"' into table coordinates fields terminated by ',' enclosed by '\"' lines terminated by '\n' (ID,LOP,STARTX,STARTY,ENDX,ENDY);");
			sql.executeQuery(open);
			String in = ("SELECT ID,LOP,STARTX,STARTY,ENDX,ENDY FROM LANDP.COORDINATES ORDER BY ID ASC;");
			ResultSet result = sql.executeQuery(in);
			while (result.next()){
				In.get(0).add((double) result.getInt("ID")-1);
				In.get(1).add(result.getDouble("LOP"));
				In.get(2).add(result.getDouble("STARTX"));
                In.get(3).add(result.getDouble("STARTY"));
                In.get(4).add(result.getDouble("ENDX"));
                In.get(5).add(result.getDouble("ENDY"));
                
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(In);	
                
                		for (int i = 0; i < In.get(0).size(); i++){
			if(In.get(1).get(i)==0){
				Ellipse2D.Double p1 = new Ellipse2D.Double(In.get(2).get(i)-3, In.get(3).get(i)-3, 6,6);
				Drawing.coords.add(p1);
			}
			else if(In.get(1).get(i)==1){
				Line2D.Double l1 = new Line2D.Double(In.get(2).get(i), In.get(3).get(i), In.get(4).get(i), In.get(5).get(i));
                                Drawing.coords.add(l1);
			}
			else if(In.get(1).get(i)==2){				
				Ellipse2D.Double e1 = new Ellipse2D.Double(In.get(2).get(i), In.get(3).get(i), In.get(4).get(i), In.get(5).get(i));
                                Drawing.coords.add(e1);
			}
		}
                
                
	}
}
