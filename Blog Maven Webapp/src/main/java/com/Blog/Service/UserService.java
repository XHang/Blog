package com.Blog.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Blog.Dao.CategoryDAO;
import com.Blog.Dao.UserDAO;
import com.Blog.Ecxeprion.UserInvalidAccess;
import com.Blog.Ecxeprion.UserNotFound;
import com.Blog.Ecxeprion.UserPasswordError;
import com.Blog.Model.User;
import com.Blog.Util.CiphertextUitl;
import com.Blog.Util.MD5Util;
import com.Blog.Util.Mail;
import com.Blog.Util.VerificationCodeTask;
@Service("uu")
public class UserService  {
	private UserDAO dao;
	private CategoryDAO categorydao;
	List<Thread> threads=new ArrayList<>();		//定义一个存放线程的集合
	int threadID=0;							//定义每次存入线程集合的线程索引
	
	//添加用户
	//TODO 密码要用加密存储！
	public void  add(User user) throws Exception{
		if(isExist(user)){
			throw new Exception("User Already Exist!");
		}
		
		String md5 =MD5Util.MD5(user.getUsername()+System.currentTimeMillis());//随机生成验证码
		user.setActivationCode(md5);										 										    //给注册用户生成验证码
		user.setPassword(CiphertextUitl.encryption(user.getPassword()));						//给用户密码加密
		dao.saveUser(user);
		categorydao.createDefaultCategory(user.getId());//为注册用户建一个默认的日志类别
		
		//发送注册邮件
		new Mail().sendMsg("用户名", "密码", 
							"smtp.sina.com", 
							"q1083594261@sina.com", 
							user.getEmail(), 
							"欢迎注册轻博客", 
							"请点击以下链接完成注册<br/>"
							+ "http://localhost:8080/Blog/user/"+md5+"/"+user.getId()+"/"+threadID+"/userActivation.do");
		Thread thread=new Thread(new VerificationCodeTask(user.getId()));		
		thread.start();		//开启等待线程！
		threads.add(thread);
		threadID++;
		return;
		}
	/**
	 * 判断用户是否存在,根据用户名或者邮箱判断
	 * @param user
	 * @return
	 */
	public boolean isExist(User user){
		int count=dao.isExist(user);
		if(count==0){
			return false;
		}
		return true;
	}
	//用户激活并返回激活后的实体对象
	public User userActivation(int id,String ActivationCode,int threadID) throws Exception{
		User user=dao.selectUserById(id);		//根据id获取完整版的用户对象
		if(!ActivationCode.equals(user.getActivationCode())){
			threads.remove(threadID);
			this.threadID--;
			throw new Exception("验证失败");
		}
		user.setValid(true);		   		 //设置激活属性
		dao.updateUserValid(user);	       	//要到数据库查看用户的有效字段是否更改了
		threads.get(threadID).interrupt(); //中断当前用户验证码的等待线程
		threads.remove(threadID);			//线程完成任务不需要占位置了，删除之
		this.threadID--;							//索引角标--
		return user;
	}
	//用于登录，若用户不存在抛出异常
	public User getUser(User user) throws UserNotFound,UserInvalidAccess, UserPasswordError{
		User trueuser=dao.selectUser(user.getUsername());
		if(trueuser==null){
			throw new UserNotFound("用户找不到");
		}
		
		if(!(trueuser.getPassword().equals(CiphertextUitl.encryption(user.getPassword())))){
			throw new  UserPasswordError("用户密码错误");
		}
		
		if(!trueuser.isValid()){
			throw new UserInvalidAccess("用户无效访问");
		}
		return trueuser;
	}
	
	//更改心情
	public void changeMood(User user) {
		dao.updateMood(user);
	}
	
	public UserDAO getDao() {
		return dao;
	}
	@Autowired
	public void setDao(UserDAO dao) {
		this.dao = dao;
	}
	public CategoryDAO getCategorydao() {
		return categorydao;
	}
	@Autowired
	public void setCategorydao(CategoryDAO categorydao) {
		this.categorydao = categorydao;
	}
	public void resendEmail(int id) throws Exception {
		User user=dao.selectUserById(id);
		new Mail().sendMsg("用户名", "密码", 
				"smtp.sina.com", 
				"q1083594261@sina.com", 
				user.getEmail(), 
				"欢迎注册轻博客", 
				"请点击以下链接完成注册<br/>"
				+ "http://localhost:8080/Blog/user/"+user.getActivationCode()+"/"+user.getId()+"/"+threadID+"/userActivation.do");
		Thread thread=new Thread(new VerificationCodeTask(user.getId()));		
		thread.start();		//开启等待线程！
		threads.add(thread);
		threadID++;
	}
}
