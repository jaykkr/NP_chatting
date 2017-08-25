package server.database;

import java.io.File;
import java.io.FileReader;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


/** Reference from
 * https://www.mkyong.com/jdbc/connect-to-oracle-db-via-jdbc-driver-java/
 * DB table structure( https://www.youtube.com/watchv=sEK6bwvDRK0 )
 * 
 * Oracle DB table for network programming assignment-chat
 * create table npchat (
 *	id varchar2(16) primary key,
 *	pwd varchar2(16) not null,
 *	name varchar2(30) not null,
 *	nick varchar2(30) not null unique,
 * );
 * 
 * Download Oracle Database Express Edition 11g Release 2 
 * http://www.oracle.com/technetwork/database/database-technologies/express-edition/downloads/index.html
 * 
 */
public class OracleManager implements DBManager {
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private Connection con;

	public OracleManager() throws Exception {

		Class.forName(driver);
		System.out.println("oracle driver is registered correctly....Good Job!!");
		
		// Oracle Express Edition - Free ware
		String url = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
		// use default ID in Oracle
		String id = "scott"; 
		String pass = "tiger";
		con = DriverManager.getConnection(url,id,pass);	
		System.out.println("From OracleManager :"+con);
		
	}

	@Override
	public PreparedStatement create(String sql) throws SQLException {
		System.out.println("From PreparedStatement :"+con.prepareStatement(sql));
		return con.prepareStatement(sql);
	}

	@Override
	public Statement create() throws SQLException {
		System.out.println("From Statement :"+con.createStatement());
		return con.createStatement();
	}

}
