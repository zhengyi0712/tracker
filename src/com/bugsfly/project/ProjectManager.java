package com.bugsfly.project;

import java.sql.SQLException;

import com.bugsfly.exception.BusinessException;
import com.bugsfly.team.TeamManager;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class ProjectManager {

	public void saveProject(ProjectController controller) throws BusinessException {
		String teamId = controller.getPara("teamId");
		TeamManager teamManager = new TeamManager();
		Record team = teamManager.getTeam(teamId);
		if (team == null) {
			throw new BusinessException("找不到相关的团队");
		}
		
		
		// 判断，保证同一个团队没有重名项目
		final Boolean isO = false;
		Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {

				return false;
			}
		});
	}

}
