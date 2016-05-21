package com.shouxinjk.ihealth.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.shouxinjk.ihealth.data.pojo.User;
import com.shouxinjk.ihealth.data.pojo.UserDisease;
import com.shouxinjk.ihealth.data.pojo.UserTag;
import com.shouxinjk.ihealth.data.util.Column;

public class TransferTest {
	Transfer transfer;
	 @BeforeClass
	 public void setUp() {
	   transfer = new Transfer();
	 }
	 
	 @DataProvider(name = "tags")
	 public Object[][] createTags() {
	  return new Object[][] {
	    { "cust_field_"+System.currentTimeMillis(), "varchar(20)" },
	    { "cust_col_"+System.currentTimeMillis(), "int"},
	  };
	 }
	 
	 @DataProvider(name = "users")
	 public Object[][] createUsers() {
	  return new Object[][] {
	    { "0453deafa8114ce9a55f9245a87b709c","张某某","baijie","192.168.1.101","13312345678","baijie@163.com","openid","张老师","1984-02-02","女","成都","成都","未婚","白领","大学","http://www",165,48},
	    { "0e589df1ee70439e9af5a11e642dd1c7","马冬梅","meihong","192.168.1.102","13312345679","meihong@163.com","openid22","李老师","1986-01-02","女","重庆","重庆","已婚","蓝领","高中","http://www2",168,51},
	    { "test_user","马冬梅","meihong","192.168.1.102","13312345679","meihong@163.com","openid22","李老师","1986-01-02","女","重庆","重庆","已婚","蓝领","高中","http://www2",168,51},
	    { "test_user2","马冬梅","meihong","192.168.1.102","13312345679","meihong@163.com","openid22","李老师","1986-01-02","女","重庆","重庆","已婚","蓝领","高中","http://www2",168,51},

	  };
	 }
	  
	 @DataProvider(name = "userTags")
	 public Object[][] createUserTags() {
	  return new Object[][] {
	    { "0453deafa8114ce9a55f9245a87b709c","tag1","cust_field1", "cust_field1='张某某'" ,"张某某"},
	    { "0e589df1ee70439e9af5a11e642dd1c7","tag2","cust_field2", "cust_field2='100'","100"},
	  };
	 }

	 @DataProvider(name = "userDiseases")
	 public Object[][] createUserDiseases() {
	  return new Object[][] {
	    { "0453deafa8114ce9a55f9245a87b709c","甲状腺,高血压" ,"高血压","过劳死"},
	    { "0e589df1ee70439e9af5a11e642dd1c7","肾结石,胆囊炎", "","乳腺癌"},
	  };
	 }
	 
	 @DataProvider(name = "guidelines")
	 public Object[][] createGuidelines() {
	  return new Object[][] {
	    { "1"},
//	    { "e00f9b2d6bee4dc7aeb0d821f8ac068d"},
	  };
	 }
	 
	 @Test(groups = { "fast" },dataProvider = "tags")
	 public void transferTag(String fieldName,String fieldType) {
	   try {
		transfer.transferTag(fieldName, fieldType);
		} catch (Exception e) {
			e.printStackTrace();
			assert false;
		}
	   
	   List<List<Column>> rows = transfer.connectionProvider.query("select * from ta_user");
	   assert(rows.size()>0);
	   
	   String metaColumns = ",";
	   List<Column> row = rows.get(0);
	   for(Column column:row){
		   metaColumns += ","+column.getColumnName();
	   }
	   assert metaColumns.indexOf(","+fieldName)>0;
	 }
	 
	 @Test(groups = { "fast" },dataProvider = "userTags")
	 public void transferUserTag(String userId,String tagName,String fieldName,String tagExpr,String value){
		 UserTag userTag = new UserTag();
		 userTag.setUser_id(userId);
		 userTag.addTag(tagName, fieldName, tagExpr);
		 transfer.transferUserTags(userTag);
		 
		 //assert custom field values are set correctly
		 List<List<Column>> rows = transfer.connectionProvider.query("select * from ta_user where user_id='"+userId
				 +"' and tag_"+fieldName+"='"+value+"'");
		 assert(rows.size()>0);
		 
		 //assert tags string are set correctly
		 List<List<Column>> rows2 = transfer.connectionProvider.query("select locate('"+tagName+"',tags) as indicator from ta_user where user_id='"+userId+"'");
		 assert(rows.size()>0);
		 
		 List<Column> row = rows2.get(0);
		 Column column = row.get(0);
		 assert(Integer.parseInt(column.getVal().toString())>0);
	 }
	 
	 @Test(groups = { "fast" },dataProvider = "userDiseases")
	 public void transferUserDisease(String userId,String personDisease,String familyDisease,String concernDisease){
		 UserDisease userDisease = new UserDisease();
		 userDisease.setUser_id(userId);
		 List<String> sufferedDiseaseList = new ArrayList<String>();
		 List<String> inheritDiseaseList = new ArrayList<String>();
		 List<String> concernDiseaseList = new ArrayList<String>();
		 
		 String[] personDiseaseArray = personDisease.split(",");
		 String[] familyDiseaseArray = familyDisease.split(",");
		 String[] concernDiseasArray = concernDisease.split(",");
		 
		 for(String disease:personDiseaseArray){
			 userDisease.addSufferedDisease(disease);
			 sufferedDiseaseList.add(disease);
		 }
		 for(String disease:familyDiseaseArray){
			 userDisease.addInheritDisease(disease);
			 inheritDiseaseList.add(disease);
		 }
		 for(String disease:concernDiseasArray){
			 userDisease.addConcernDisease(disease);
			 concernDiseaseList.add(disease);
		 }
		 
		 transfer.transferUserDisease(userDisease);
		 
		 //assert suffered diseases string are set correctly
		 for(String disease:personDiseaseArray){
			 List<List<Column>> rows = transfer.connectionProvider.query("select locate('"+disease+"',sufferedDiseases) as indicator from ta_user where user_id='"+userId+"'");
			 assert(rows.size()>0);
			 
			 List<Column> row = rows.get(0);
			 Column column = row.get(0);
			 assert(Integer.parseInt(column.getVal().toString())>0);
		 }
		 
		 //assert inherit diseases string are set correctly
		 for(String disease:familyDiseaseArray){
			 List<List<Column>> rows = transfer.connectionProvider.query("select locate('"+disease+"',inheritDiseases) as indicator from ta_user where user_id='"+userId+"'");
			 assert(rows.size()>0);
			 
			 List<Column> row = rows.get(0);
			 Column column = row.get(0);
			 assert(Integer.parseInt(column.getVal().toString())>0);
		 }
		 
		 //assert concern diseases string are set correctly
		 for(String disease:concernDiseasArray){
			 List<List<Column>> rows = transfer.connectionProvider.query("select locate('"+disease+"',concernDiseases) as indicator from ta_user where user_id='"+userId+"'");
			 assert(rows.size()>0);
			 
			 List<Column> row = rows.get(0);
			 Column column = row.get(0);
			 assert(Integer.parseInt(column.getVal().toString())>0);
		 }
	 }
	 
	 @Test(groups = { "fast" },dataProvider = "users")
	 public void transferUser(String userId,String userName,String name,String ip,String phone,String email,String openid,String alias,String birthday,String sex,
			 String birthPlace,String livePlace,String marriageStatus,String career,String degree,String avatar,int height,int weight){
		 User user = new User();
		 user.setUser_id(userId);
		 user.setUserName(userName);
		 user.setName(name);
		 user.setIp(ip);
		 user.setPhone(phone);
		 user.setEmail(email);
		 user.setOpenid(openid);
		 user.setAlias(alias);
		 user.setBirthday(birthday);
		 user.setSex(sex);
		 user.setBirthPlace(birthPlace);
		 user.setLivePlace(livePlace);
		 user.setMarriageStatus(marriageStatus);
		 user.setCareer(career);
		 user.setDegree(degree);
		 user.setAvatar(avatar);
		 user.setHeight(height);
		 user.setWeight(weight);
		 
		 SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		 int age = 0;
		 try {
			Date birthDate = format.parse(birthday);
			int year1 = birthDate.getYear();
			Date now = new Date();
			int year2 = now.getYear();
			age = year2-year1;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 
		user.setAge(age);
		transfer.transferUser(user);
		
	   List<List<Column>> rows = transfer.connectionProvider.query("select * from ta_user where user_id='"+userId+"'");
	   assert(rows.size()>0);

	   List<Column> row = rows.get(0);
	   Map<String,Object> map = new HashMap<String,Object>();
	   for(Column column:row){
		   map.put(column.getColumnName().toLowerCase(), column.getVal());
	   }
	   
		assert(userName.equals(map.get("username").toString()));	//用户名
		assert(name.equals(map.get("name").toString()));		//姓名
		assert(ip.equals(map.get("ip").toString()));			//用户登录ip地址
		assert(phone.equals(map.get("phone").toString()));			// 电话号码
		assert(email.equals(map.get("email").toString()));			// 电子邮件
		assert(openid.equals(map.get("openid").toString()));			// 对应微信OPENID
		assert(alias.equals(map.get("alias").toString()));			// 昵称
		assert(birthday.equals(map.get("birthday").toString()));			// 生日 yyyy-MM-dd
		assert(sex.equals(map.get("sex").toString()));				// 性别
		assert(birthPlace.equals(map.get("birthplace").toString()));		// 出生地
		assert(livePlace.equals(map.get("liveplace").toString()));		// 居住地
		assert(marriageStatus.equals(map.get("marriagestatus").toString()));	// 婚姻状态
		assert(career.equals(map.get("career").toString()));			// 职业
		assert(degree.equals(map.get("degree").toString()));			// 学历
		assert(avatar.equals(map.get("avatar").toString()));			// 用户图像
		assert(height==Integer.parseInt(map.get("height").toString()));			// 身高
		assert(weight==Integer.parseInt(map.get("weight").toString()));			// 体重
		assert(age==Integer.parseInt(map.get("age").toString()));				// 年龄	
	 }
	 
	 @Test(groups = { "fast" },dataProvider = "guidelines")
	 public void releaseGuideLine(String guidelineId){
		 transfer.releaseGuideLine();
		 assert true;
	 }
	 
	 @Test(groups = { "fast" },dataProvider = "guidelines")
	 public void cancelGuideLine(String guidelineId){
		 List<List<Column>> rows = transfer.connectionProvider.query("select * from ta_userRule where guideline_id='"+guidelineId+"'");
		 assert(rows.size()>0);
		 
		 transfer.cancelGuideLine(guidelineId);
		 rows = transfer.connectionProvider.query("select * from ta_userRule where guideline_id='"+guidelineId+"'");
		 assert(rows.size()==0);
		 
	 }
	 
}
