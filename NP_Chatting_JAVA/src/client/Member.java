package client;

/**
 * Class for having login users information
 */
public class Member {
	private String id; // user ID
	private String pwd; // User password
	private String name; // Name
	private String nick; // Nickname

	public Member(String id, String pwd, String name, String nick) {	
		this.id = id;
		this.pwd = pwd;
		this.name = name;
		this.nick = nick;
	}

	public Member(String[] info) {
		id = info[0];
		pwd = info[1];
		name = info[2];
		nick = info[3];
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public void setAll(String[] tmp) {
		id = tmp[0];
		pwd = tmp[1];
		name = tmp[2];
		nick = tmp[3];
	}

}
