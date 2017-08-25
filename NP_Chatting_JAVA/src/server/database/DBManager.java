package server.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public interface DBManager {
	public PreparedStatement create(String sql) throws SQLException;
	public Statement create() throws SQLException;
}
