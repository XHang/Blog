package com.Blog.Controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.Blog.Ecxeprion.UserInvalidAccess;
import com.Blog.Ecxeprion.UserNotFound;
import com.Blog.Ecxeprion.UserPasswordError;
import com.Blog.Model.Article;
import com.Blog.Model.User;
import com.Blog.Service.ArticleService;
import com.Blog.Service.CategoryService;
import com.Blog.Service.UserService;
import com.Blog.Util.VerifyCodeUtils;
@Controller
@RequestMapping("/user")
public class UserController {
	private UserService userservice;
	private ArticleService articleservice;
	private CategoryService categoryservice;
	//注册用户
	//TODO 注册用户，密码全部设置为密文
	@RequestMapping("/register")
	public ModelAndView register(User user){
		ModelAndView mv=new ModelAndView();
		try {     
			userservice.add(user);
			mv.setViewName("registerSuccess");		  				 //设置注册成功页面
			mv.addObject("username", user.getUsername());	 //添加视图数据
		} catch (Exception e) {
			e.printStackTrace();
			mv.setViewName("login");					//存储失败跳到注册页面,待添加注册失败信息
		}
		return mv;
	}
	
	/**
	 * 
	 * @param md5随机生成的MD5值
	 * @param id	用户ID
	 * @param threadID 线程ID
	 * @param request		请求
	 * @return
	 */
	@RequestMapping("/{md5}/{id}/{threadID}/userActivation")
	public ModelAndView verify(@PathVariable String md5,@PathVariable int id,@PathVariable int threadID,HttpServletRequest request){
		ModelAndView mv=new ModelAndView();
		try {
			User user=userservice.userActivation(id, md5,threadID);		//进行激活码校验，返回已被激活的user对象
			mv.addObject("infohead", "恭喜"+user.getUsername()+",成功验证邮箱");
			mv.addObject("infobody", "3秒后进入您的博客");
			mv.addObject("flag", "true");
			mv.setViewName("verify");				//设置激活成功的视图
			request.getSession().setAttribute("user", user);//给session设置进user对象
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject("infohead", "很抱歉，您的水平姿势不够，服务器拒绝你的加入");
			mv.addObject("infobody", "可能原因是：激活链接超时！<a href='http://localhost:8080/Blog/user/resendEmail.do?id="+id+"'>点此重发邮件！</a>");
			mv.setViewName("verify");
		}
		return mv;
	}
	//进入空间请求,配置了一个拦截器，检测session有没有user对象
	//TODO 全部改为用ajax读取数据
	@RequestMapping("/intoSpace")
	public ModelAndView intoSpace(HttpServletRequest request){
		User user=(User) request.getSession().getAttribute("user");
		List<Article> newarticle=articleservice.getNewArticle(user.getId());
		List<Article> hotarticle=articleservice.getHotArticle(user.getId());
		List<User> friends=user.getFriends();
		int  categoryTotal=categoryservice.getCategoryTotal(user.getId());
		int ArticleTotal=articleservice.getArticleTotal(user.getId());
		ModelAndView mv=new ModelAndView();
		mv.addObject("user",user);
		mv.addObject("newarticle",newarticle);
		mv.addObject("hotarticle",hotarticle);
		mv.addObject("categoryTotal",categoryTotal);
		mv.addObject("ArticleTotal",ArticleTotal);
		mv.addObject("friends",friends);	//注意了，要改回来
		mv.setViewName("index");
		return mv;
	}
	//登录
	//检查验证码，放在拦截器那里
	@RequestMapping("/login")
	public String login(User user,HttpServletRequest request){
		try {
			user=userservice.getUser(user);
		} catch (UserInvalidAccess e) {	
			e.printStackTrace();
			request.setAttribute("infohead", "你的邮箱居然没激活");
			request.setAttribute("infobody", "赶紧去激活啊混蛋");
			return "verify";
		} catch (UserNotFound e) {
			e.printStackTrace();
			return "login";
		} catch (UserPasswordError e) {
			e.printStackTrace();
			return "login";
		}
 		request.getSession().setAttribute("user", user);
		return "redirect:intoSpace.do";
	}
	//生成验证码
	@RequestMapping("/generateCode")
	public void generateCode(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
        response.setContentType("image/jpeg");  
        String code = VerifyCodeUtils.generateVerifyCode(4);   
		request.getSession().setAttribute("code", code);
		VerifyCodeUtils.outputImage(200, 80, response.getOutputStream(), code);  
		
	}
	
	//修改心情，TODO 修改完毕重新刷新该页面
	@RequestMapping("/changeMood")
	public void changeMood(String mood,HttpSession session){
		User user=(User)session.getAttribute("user");
		user.setMood(mood);
		userservice.changeMood(user);
	}
	//重新发请求到邮箱里验证
	@RequestMapping("resendEmail")
	public ModelAndView resendEmail(int id) throws Exception{
		userservice.resendEmail(id);
		ModelAndView mv=new ModelAndView();
		mv.addObject("infohead", "新的验证码已发到邮箱");
		mv.addObject("infobody", "请登录邮箱再次验证！");
		mv.setViewName("verify");
		return mv;
	}
	//上传用户图片
	//TODO 建议在用户表再添加一个字段用于保存用户图片名
	@RequestMapping("uploadImage")
	public String imageupload(@RequestParam MultipartFile Image,HttpSession session) {
		User user=(User)session.getAttribute("user");
		//Pattern p=Pattern.compile("(\\.\\w{3,})$");		//匹配后缀名
		//Matcher m=p.matcher(Image.getOriginalFilename());
		//m.find();
		//String suffixName=m.group();
		String filename=user.getId()+"";		//获取后缀名
		String path=session.getServletContext().getRealPath("")+"/resources/UserImages/"+filename;
		BufferedOutputStream bos=null;
		BufferedInputStream bis=null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(path));
			bis = new BufferedInputStream(Image.getInputStream());
			int leng;
			while((leng=bis.read())!=-1){
				bos.write(leng);
			}
			bos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try{
				if(bis!=null){
					bis.close();
				}
				if(bos!=null){
					bos.close();	//关闭自动刷新流

				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return "redirect:intoSpace.do";
	}
	//退出登录
	@RequestMapping("exit")
	public String exitLogin(HttpSession session){
		session.removeAttribute("user");
		return "login";
	}
	public UserService getUserservice() {
		return userservice;
	}
	@Autowired
	public void setUserservice(UserService userservice) {
		this.userservice = userservice;
	}
	public ArticleService getArticleservice() {
		return articleservice;
	}
	@Autowired
	public void setArticleservice(ArticleService articleservice) {
		this.articleservice = articleservice;
	}
	public CategoryService getCategoryservice() {
		return categoryservice;
	}
	@Autowired
	public void setCategoryservice(CategoryService categoryservice) {
		this.categoryservice = categoryservice;
	}
	

}
