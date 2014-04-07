package com.bugsfly.issue;

import java.util.HashSet;
import java.util.Set;

public class IssueManager {
	public static final Set<String> tags = new HashSet<String>();
	static {
		tags.add("错误");
		tags.add("优化");
		tags.add("改善");
		tags.add("新功能");
	}
}
