package in.prasilabs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class LogDatabase 
{
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/EagleEye";
	static final String USER = "root";
	static final String PASS = "prasi123";
	public void log() throws ClassNotFoundException 
	{
		System.out.println("LogDatabase Started");
		String mycommand = "sudo fswebcam img";
		try {
			Runtime.getRuntime().exec(mycommand);
		} catch (IOException e1) {
			System.out.println("Exception while executing");
		}
		
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

			String sql = "INSERT INTO Logs (image) values (?)";
			PreparedStatement statement = conn.prepareStatement(sql);

			//STEP 4: Execute a query
			System.out.println("Creating statement...");

			File file = new File("/home/prasi/img");
			InputStream inputStream = new FileInputStream(file);
			
			statement.setBlob(1, inputStream);
			//statement.setBlob(0, inputStream, file.length());

			int row = statement.executeUpdate();
			if (row > 0) {
				System.out.println("A contact was inserted with photo image.");
			}
			conn.close();


		}catch(SQLException se){
			//Handle errors for JDBC
		}catch(Exception e){
			//Handle errors for Class.forName
			
		}finally
		{
		
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try
		System.out.println("Goodbye!");
	}//end main
}//end FirstExample

