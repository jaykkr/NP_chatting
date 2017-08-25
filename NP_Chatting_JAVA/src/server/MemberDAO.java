package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import client.Member;
import server.database.DBManager;

/**
 * DB Query 
 * DAO ( Data Access Object) : Class for DB processing( Connect DB, Select, update, insert, delete )
 * 
 * REFERENCE
 * http://m.blog.naver.com/javaking75/140190272629
 * http://javaengine.tistory.com/26
 * 
 * create table npchat (
 *	id varchar2(16) primary key,
 *	pwd varchar2(16) not null,
 *	name varchar2(30) not null,
 *	nick varchar2(30) not null unique,
 * );
 * 
 * http://javaengine.tistory.com/26
 * How to use JDBC
 * 1. JDBC driver load : Class.forName(oracle.jdbc.driver.OracleDriver);
 * 2. Connect DB Server : DriverManager.getConnection(jdbc:oracle:thin:@localhost:XE, scott, tiger)
 * 3. SQL Query command : Statement or PreparedStatement
 * 4. Result processing : executeQuery(SELECT), executeUpdate(UPDATE, INSERT, DELETE)
 * 5. Connection/disconnection process :
 *    Connection : Connection > Statement or PreparedStatement > ResultSet [ResultSet is only for SELECT ]
 *    Disconnection : ResultSet > Statement or PreparedStatement > Connection [ ResultSet is only for SELECT ]
 */
public class MemberDAO {

	private static MemberDAO instance;
	private PreparedStatement checkIDStmt;
	private PreparedStatement checkNickStmt;
	private PreparedStatement getStmt;
	private PreparedStatement joinStmt;
	private PreparedStatement updateStmt;

	// table name : npchat
	private MemberDAO(DBManager dbmgr) throws SQLException {
		String checkId = "select id from npchat where id = ?";
		String checkNick = "select id from npchat where nick = ?";
		String getMember = "select * from npchat where id = ?";
		String join = "insert into npchat values(?, ?, ?, ?)";
		String update = "update npchat set id = ?, pwd = ?, name = ?, nick = ? where id = ?";

		checkIDStmt = dbmgr.create(checkId);
		checkNickStmt = dbmgr.create(checkNick);
		getStmt = dbmgr.create(getMember);
		joinStmt = dbmgr.create(join);
		updateStmt = dbmgr.create(update);
	}

	synchronized public static MemberDAO getInstance(DBManager dbmgr) throws SQLException {
		if (instance == null)
			instance = new MemberDAO(dbmgr);
		return instance;
	}

	public Member getMember(String id) throws SQLException {
		Member member = null;
		getStmt.setString(1, id);
		ResultSet rs = getStmt.executeQuery();
		if (rs.next())
			member = new Member(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
		return member;
	}

	public boolean checkId(String id) throws SQLException {
		checkIDStmt.setString(1, id);
		return !checkIDStmt.executeQuery().next();

	}

	public boolean checkNick(String nick) throws SQLException {
		checkNickStmt.setString(1, nick);
		return !checkNickStmt.executeQuery().next();
	}

	public boolean requestJoin(String[] msg) throws SQLException {
		joinStmt.setString(1, msg[0]);
		joinStmt.setString(2, msg[1]);
		joinStmt.setString(3, msg[2]);
		joinStmt.setString(4, msg[3]);
		return joinStmt.executeUpdate() != 0;
	}

	public boolean updateMember(String[] info) throws SQLException {
		updateStmt.setString(1, info[0]);
		updateStmt.setString(2, info[1]);
		updateStmt.setString(3, info[2]);
		updateStmt.setString(4, info[3]);
		updateStmt.setString(5, info[0]);
		return updateStmt.executeUpdate() != 0;
	}
}
