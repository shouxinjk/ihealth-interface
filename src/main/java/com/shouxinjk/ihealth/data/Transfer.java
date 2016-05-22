package com.shouxinjk.ihealth.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.shouxinjk.ihealth.data.jdbc.HikariConnectionProvider;
import com.shouxinjk.ihealth.data.pojo.Tag;
import com.shouxinjk.ihealth.data.pojo.User;
import com.shouxinjk.ihealth.data.pojo.UserDisease;
import com.shouxinjk.ihealth.data.pojo.UserTag;
import com.shouxinjk.ihealth.data.util.Column;

public class Transfer {
	Properties prop = new Properties();
	HikariConnectionProvider connectionProvider;
	Logger logger  = Logger.getLogger(Transfer.class);
	String mode = "production";
	/**
	 * Setup Transfer with default properties
	 * Notice: this may be run under debug mode!
	 */
	public Transfer(){
		//here we use default properties 
		this(Transfer.class.getClassLoader().getResourceAsStream("ihealth-interface.properties"));
	}
	
	/**
	 * setup Transfer by passing properties InputStream.
	 * e.g. Transfer.class.getClassLoader().getResourceAsStream("ihealth-interface.properties")
	 * @param is
	 */
	public Transfer(InputStream is){
    	//here we load configurations from properties file
        try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

        //prepare JDBC configuration
        Map jdbcConfigMap = new HashMap();
        jdbcConfigMap.put("dataSourceClassName", prop.getProperty("mysql.dataSource.className"));//com.mysql.jdbc.jdbc2.optional.MysqlDataSource
        jdbcConfigMap.put("dataSource.url", prop.getProperty("mysql.url"));//jdbc:mysql://localhost/test
        jdbcConfigMap.put("dataSource.user", prop.getProperty("mysql.user"));//root
        jdbcConfigMap.put("dataSource.password", prop.getProperty("mysql.password"));//password

        this.mode = prop.getProperty("common.mode", "production");
        connectionProvider = new HikariConnectionProvider(jdbcConfigMap, 5);
        connectionProvider.prepare();
	}
	
		
	/**
	 * create dynamic fields for customized tag fields
	 * Notice: we only add new fields here for reducing impacts
	 * @param fieldName
	 * @param fieldType
	 */
	public void transferTag(String fieldName,String fieldType) throws Exception{
		checkMode();
		if(fieldName == null || fieldType == null)
			return;
		if(fieldName.trim().length()==0 || fieldType.trim().length()==0)
			return;
		StringBuffer sb = new StringBuffer();
		sb.append("alter table ta_user add column ");
		sb.append("tag_");
		sb.append(fieldName);
		sb.append(" ");
		sb.append(fieldType);
		String sql = sb.toString();
		logger.debug("Try to add custom field.[SQL]"+sql);
		try{
			connectionProvider.execute(sql);
		}catch(Exception ex){
			throw ex;
		}
	}
	
	public void transferTag(Tag tag) throws Exception{
		checkMode();
		transferTag(tag.getFieldName(),tag.getFieldType());
	}
	
	public void transferUser(User user){
		checkMode();
		StringBuffer sb = new StringBuffer();
		sb.append("insert into ta_user (user_id,userName,name,ip,phone,email,openid,alias,birthday,sex,birthPlace,livePlace,marriageStatus,career,degree,avatar,height,weight,age,lastEvaluatedOn)");
		sb.append("values (");//to insert new user
		sb.append("'"+user.getUser_id()+"'");
		sb.append(",'"+user.getUserName()+"'");	//用户名
		sb.append(",'"+user.getName()+"'");		//姓名
		sb.append(",'"+user.getIp()+"'");			//用户登录ip地址
		sb.append(",'"+user.getPhone()+"'");			// 电话号码
		sb.append(",'"+user.getEmail()+"'");			// 电子邮件
		sb.append(",'"+user.getOpenid()+"'");			// 对应微信OPENID
		sb.append(",'"+user.getAlias()+"'");			// 昵称
		sb.append(",'"+user.getBirthday()+"'");			// 生日 yyyy-MM-dd
		sb.append(",'"+user.getSex()+"'");				// 性别
		sb.append(",'"+user.getBirthPlace()+"'");		// 出生地
		sb.append(",'"+user.getLivePlace()+"'");		// 居住地
		sb.append(",'"+user.getMarriageStatus()+"'");	// 婚姻状态
		sb.append(",'"+user.getCareer()+"'");			// 职业
		sb.append(",'"+user.getDegree()+"'");			// 学历
		sb.append(",'"+user.getAvatar()+"'");			// 用户图像
		sb.append(",'"+user.getHeight()+"'");			// 身高
		sb.append(",'"+user.getWeight()+"'");			// 体重
		sb.append(",'"+user.getAge()+"'");				// 年龄	
		sb.append(",date_sub(now(),interval 1 day)");//lastEvaluatedOn
		sb.append(") ");
		sb.append("on duplicate key update ");//to update user info
		sb.append("userName='"+user.getUserName()+"'");	//用户名
		sb.append(",name='"+user.getName()+"'");		//姓名
		sb.append(",ip='"+user.getIp()+"'");			//用户登录ip地址
		sb.append(",phone='"+user.getPhone()+"'");			// 电话号码
		sb.append(",email='"+user.getEmail()+"'");			// 电子邮件
		sb.append(",openid='"+user.getOpenid()+"'");			// 对应微信OPENID
		sb.append(",alias='"+user.getAlias()+"'");			// 昵称
		sb.append(",birthday='"+user.getBirthday()+"'");			// 生日 yyyy-MM-dd
		sb.append(",sex='"+user.getSex()+"'");				// 性别
		sb.append(",birthPlace='"+user.getBirthPlace()+"'");		// 出生地
		sb.append(",livePlace='"+user.getLivePlace()+"'");		// 居住地
		sb.append(",marriageStatus='"+user.getMarriageStatus()+"'");	// 婚姻状态
		sb.append(",career='"+user.getCareer()+"'");			// 职业
		sb.append(",degree='"+user.getDegree()+"'");			// 学历
		sb.append(",avatar='"+user.getAvatar()+"'");			// 用户图像
		sb.append(",height='"+user.getHeight()+"'");			// 身高
		sb.append(",weight='"+user.getWeight()+"'");			// 体重
		sb.append(",age='"+user.getAge()+"'");				// 年龄	
		sb.append(",lastModifiedOn=now()");//lastModifiedOn
		
		String sql = sb.toString();
		logger.debug("Try to insert/update user info.[SQL]"+sql);
		connectionProvider.execute(sql);
		
		//try to insert new checkup package
		//insert ignore into tb_checkuppackage (checkuppackage_id,status,user_id,createby,createon) values('','pending','','interface',now())
		sb = new StringBuffer("insert ignore into tb_checkuppackage (checkuppackage_id,status,user_id,createby,createon) values('");
		sb.append(user.getUser_id());
		sb.append("','pending','");
		sb.append(user.getUser_id());
		sb.append("','interface',now())");
		sql = sb.toString();
		logger.debug("Try to insert checkup package.[SQL]"+sql);
		connectionProvider.execute(sql);
	}
	
	//update ta_user set tags=? where user_id=?
	public void transferUserTags(UserTag userTag){
		checkMode();
		StringBuffer sb = new StringBuffer();
		sb.append("update ta_user set ");
		
		StringBuffer userTagsSb = new StringBuffer(":");
		
		//query for all custom fields
		List<String> customFields = new ArrayList<String>();
		List<List<Column>> rows = connectionProvider.query("select * from ta_user limit 1");
		List<Column> row = rows.get(0);//Notice here: you must have at least one row in the table
		for(Column column:row){
			String metaFieldName = column.getColumnName();
			if(!metaFieldName.startsWith("tag_"))//we only change custom fields
				continue;
			String fieldName = metaFieldName.substring("tag_".length());//get original field name
			//check if the field was there
			if(userTag.getTagFieldNames().contains(fieldName)){//if the tag selected we will set value by expression
				int index = userTag.getTagFieldNames().indexOf(fieldName);
				sb.append("tag_");
//				sb.append(fieldName);
//				sb.append("='");
				sb.append(userTag.getTagExpressions().get(index));
//				sb.append("'");
				//update user tags
				userTagsSb.append(userTag.getTagNames().get(index));
				userTagsSb.append(":");
			}else{//we will remove old value
				sb.append("tag_");
				sb.append(fieldName);
				sb.append("=null");
			}
			sb.append(",");
		}
		sb.append("tags='");
		sb.append(userTagsSb.toString());
		sb.append("'");
		sb.append(",lastModifiedOn=now()");
		sb.append(" where user_id='");
		sb.append(userTag.getUser_id());
		sb.append("'");
		String sql = sb.toString();
		logger.debug("Try to update user tag.[SQL]"+sql);
		connectionProvider.execute(sql);
	}
	
	//update ta_user set disease=?,inheritDisease=?concernDisease=? where user_id=?
	public void transferUserDisease(UserDisease userDisease){
		checkMode();
		StringBuffer sbSufferedDisease = new StringBuffer(":");
		StringBuffer sbInheritDisease = new StringBuffer(":");
		StringBuffer sbConcernDisease = new StringBuffer(":");
		StringBuffer sb = new StringBuffer();
		sb.append("update ta_user set ");
		//suffered diseases
		for(String disease:userDisease.getSufferedDiseases()){
			sbSufferedDisease.append(disease);
			sbSufferedDisease.append(":");
		}
		sb.append("sufferedDiseases='");
		sb.append(sbSufferedDisease.toString());
		sb.append("'");
		//inheritable diseases
		for(String disease:userDisease.getInheritDiseases()){
			sbInheritDisease.append(disease);
			sbInheritDisease.append(":");
		}
		sb.append(",inheritDiseases='");
		sb.append(sbInheritDisease.toString());
		sb.append("'");
		//concerned diseases
		for(String disease:userDisease.getConcernDiseases()){
			sbConcernDisease.append(disease);
			sbConcernDisease.append(":");
		}
		sb.append(",concernDiseases='");
		sb.append(sbConcernDisease.toString());
		sb.append("'");
		//lastModifiedOn
		sb.append(",lastModifiedOn=now()");
		//where clause
		sb.append(" where user_id='");
		sb.append(userDisease.getUser_id());
		sb.append("'");
		String sql = sb.toString();
		logger.debug("Try to update user diseases.[SQL]"+sql);
		connectionProvider.execute(sql);
	}
	
	//release a new guide line
	//update lastModifiedOn for all users
	public void releaseGuideLine(String guideLineId){
		checkMode();
		StringBuffer sb = new StringBuffer();
		sb.append("update ta_user set lastModifiedOn=now()");
		
		String sql = sb.toString();
		logger.debug("Try to update user info due to add new guideline.[SQL]"+sql);
		connectionProvider.execute(sql);
	}
	
	public void releaseGuideLine(){
		checkMode();
		releaseGuideLine("-1");//we don't have to use guideline ID
	}
	
	//cancel a guideline
	//remove prepared userRule data
	//update lastModifiedOn for impacted users
	public void cancelGuideLine(String guidelineId){
		checkMode();
		//find all impact users
		StringBuffer sb = new StringBuffer();
		sb.append("select distinct(user_id) from ta_userRule where guideline_id='");
		sb.append(guidelineId);
		sb.append("'");
		String sql = sb.toString();
		logger.debug("Try to query impacted users.[SQL]"+sql);
		List<List<Column>> rows = connectionProvider.query(sql);
		
		//remove all impacted userRule data
		sb = new StringBuffer();
		sb.append("delete from ta_userRule where guideline_id='");
		sb.append(guidelineId);
		sb.append("'");	
		sql = sb.toString();
		logger.debug("Try to clean userRule info due to guideline canceled.[SQL]"+sql);
		connectionProvider.execute(sql);
		
		//update lastModifiedOn for impacted users
		//here only update those users impcated
		for(List<Column> row:rows){
			sb = new StringBuffer();
			sb.append("update ta_user set lastModifiedOn=now() where user_id='");
			sb.append(row.get(0).getVal().toString());
			sb.append("'");
			sql = sb.toString();
			logger.debug("Try to update lastModifiedOn due to cancel guideline.[SQL]"+sql);
			connectionProvider.execute(sql);
		}
	}
	
	private void checkMode(){
		if("debug".equalsIgnoreCase(this.mode)){
			logger.warn("The data interface is running under debug mode.");
			return;
		}
	}
}
