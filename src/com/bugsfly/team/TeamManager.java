package com.bugsfly.team;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class TeamManager {
	
	public final static String ROLE_ADMIN = "admin";
	public final static String ROLE_ORDINARY = "ordinary";
	
	public Record getTeam(String id) {
		return Db.findById("team", id);
	}

	public String getRoleOfUser(String teamId, String userId) {
		return Db.queryStr(
				"select role from team_user where team_id=? and user_id=?",
				teamId, userId);
	}
}
