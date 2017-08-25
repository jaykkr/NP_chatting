package client;


/**
 * Definition of interface message between ServerMsglistener of ClientHandler
 * and Guest of server and constant values as indication.
 */
public interface Message {
	int FILE_PORT = 2011;	// port number of file transfer
	int CHAT_PORT = 2002;	// port number of chatting
	String SERVER_URL = "localhost";
	int LOGIN_CHECK = 100;
	int MOD_CHECK = 101;
	int MODE_MODIFY = 102;	// Modify user
	int MODE_JOIN = 103;	// sign up
	
	String LOGIN_REQUEST_CHECKID = "1000";
	String LOGIN_REQUEST_CHECKNICK = "1001";
	String LOGIN_CHECKID_SUCCESS = "1002";
	String LOGIN_CHECKID_FAIL = "1003";
	String LOGIN_CHECKNICK_SUCCESS = "1004";
	String LOGIN_CHECKNICK_FAIL = "1005";
	
	String LOGIN_REQUEST_JOIN = "1006";
	String LOGIN_JOIN_SUCCESS = "1007";
	String LOGIN_JOIN_FAIL = "1008";
	String LOGIN_REQUEST = "1009";
	String LOGIN_SUCCESS = "1010";
	String LOGIN_FAIL_WRONG_ID = "1011";
	String LOGIN_FAIL_WRONG_PWD = "1012";
	String LOGIN_FAIL_LOGINED_ID = "1013";
	
	String WAIT_USER_UPDATE = "2000";
	String WAIT_ROOM_UPDATE = "2001";
	String WAIT_USER_OUT = "2002";
	String WAIT_CHAT_MSG = "2003";
	
	String CHAT_USER_UPDATE = "3000";
	String ROOM_REQUEST_MAKE = "3001";
	String ROOM_MAKE_SUCCESS = "3002";
	String ROOM_MAKE_FAIL = "3003";
	String ROOM_USER_OUT = "3004";
	String ROOM_USER_IN = "3005";
	String ROOM_CHAT_MSG = "3006";
	String ROOM_SET_NAME = "3007";
	String ROOM_INVITE_USER = "3008";
	String ROOM_INVITE_REQUEST = "3009";
	String ROOM_INVITE_DENY = "3010";
	String ROOM_INVITE_DENIED = "3011";
	String ROOM_REQUEST_WAITUSER = "3012";
	String ROOM_RETURN_WAITUSER = "3012";
	String ROOM_CHAT_PRIVATE = "3013";
	String ROOM_PRIVATE_FAIL = "3014";
	
	String LOGOUT = "4000";
	String GET_ADDR = "4001";// file transfer
	String MOD_REQUEST_CHECKNICK = "4002";
	String MOD_UPDATE_USERINFO = "4003";
	String MOD_UPDATE_SUCCESS = "4004";
	String MOD_UPDATE_FAIL = "4005";
	String ERR_DATABASE = "4006";
	
	// Message for popup display
	String SUCESS_MSG = "Sucess!";
	String APPROVED_MSG = "Approved!";
	String FAILURE_MSG = "Fail!";
	String DUPLICATED_MSG = "Duplicated!";
	String LOGGED_IN_STATUS_MSG = "Logged in already";

			
}
