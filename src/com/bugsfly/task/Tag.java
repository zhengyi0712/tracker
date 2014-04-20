package com.bugsfly.task;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class Tag extends Model<Tag>{

	private static final long serialVersionUID = -1603131146337415550L;
	
	public final static Tag dao = new Tag();
	
	public List<Tag> findAll(){
		return dao.find("select * from tag");
	}
	
	
}
