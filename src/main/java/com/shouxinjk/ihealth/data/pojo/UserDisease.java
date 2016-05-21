package com.shouxinjk.ihealth.data.pojo;

import java.util.ArrayList;
import java.util.List;

public class UserDisease {
	private static final long serialVersionUID = 1L;
	private String user_id;		//用户id
	private List<String> sufferedDiseases=new ArrayList<String>();
	private List<String> inheritDiseases=new ArrayList<String>();
	private List<String> concernDiseases=new ArrayList<String>();
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
	/**
	 * @return the sufferedDiseases
	 */
	public List<String> getSufferedDiseases() {
		return sufferedDiseases;
	}
	/**
	 * @param sufferedDiseases the sufferedDiseases to set
	 */
	public void setSufferedDiseases(List<String> sufferedDiseases) {
		this.sufferedDiseases = sufferedDiseases;
	}
	/**
	 * @return the inheritDiseases
	 */
	public List<String> getInheritDiseases() {
		return inheritDiseases;
	}
	/**
	 * @param inheritDiseases the inheritDiseases to set
	 */
	public void setInheritDiseases(List<String> inheritDiseases) {
		this.inheritDiseases = inheritDiseases;
	}
	/**
	 * @return the concernDiseases
	 */
	public List<String> getConcernDiseases() {
		return concernDiseases;
	}
	/**
	 * @param concernDiseases the concernDiseases to set
	 */
	public void setConcernDiseases(List<String> concernDiseases) {
		this.concernDiseases = concernDiseases;
	}

	public void addSufferedDisease(String disease){
		this.sufferedDiseases.add(disease);
	}
	
	public void addInheritDisease(String disease){
		this.inheritDiseases.add(disease);
	}
	
	public void addConcernDisease(String disease){
		this.concernDiseases.add(disease);
	}
}
