package com.shouxinjk.ihealth.data.pojo;

import java.util.ArrayList;
import java.util.List;

public class UserTag {
	private static final long serialVersionUID = 1L;
	private String user_id;		//用户id
	private List<String> tagNames=new ArrayList<String>();
	private List<String> tagFieldNames = new ArrayList<String>();
	private List<String> tagExpressions = new ArrayList<String>();
	/**
	 * @return the user_id
	 */
	public String getUser_id() {
		return user_id;
	}
	/**
	 * @param user_id the user_id to set
	 */
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
	public void addTag(String tagName,String tagFieldName,String tagExpression){
		this.tagNames.add(tagName);
		this.tagFieldNames.add(tagFieldName);
		this.tagExpressions.add(tagExpression);
	}
	
	public List<String> getTagNames(){
		return this.tagNames;
	}
	
	public List<String> getTagExpressions(){
		return this.tagExpressions;
	}
	
	public List<String> getTagFieldNames(){
		return this.tagFieldNames;
	}
}
