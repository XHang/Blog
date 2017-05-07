package com.Blog.Controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.Blog.Model.Article;
import com.Blog.Model.User;
import com.Blog.Service.ArticleService;
import com.Blog.Util.DownloadFileUtil;
import com.Blog.Vo.ArticlePageVo;
@Controller
@RequestMapping("/article")
public class ArticleController {
	//书写博客
	ArticleService articleservice;
	@RequestMapping("/writeArticle")
	public ModelAndView writeArticle(HttpServletRequest request){
		ModelAndView mv=new ModelAndView();
		mv.setViewName("writeBlog");
		return mv;
	}
	//保存博客
	@RequestMapping("/save")
	public String saveArticle(Article article){
		articleservice.save(article); 
		return "redirect:/user/intoSpace.do";
	}
	
	//根据博客ID显示博客内容(AJAX)
	@ResponseBody
	@RequestMapping("/read")
	public  Article getArticle(int id){
		return articleservice.getArticle(id);
	}
	//根据类别ID加载该类别下的文章列表(AJAX+分页)
	@ResponseBody
	@RequestMapping("/articleList")
	public ArticlePageVo articleListByCategory(int categoryId,int pageNo,int size){
		return articleservice.getArticleListByCategory(categoryId,pageNo,size);
	}
	//根据关键字搜索文章列表(AJAX+分页)
	@RequestMapping("/search")
	@ResponseBody
	public ArticlePageVo search(String keyWord,int pageNo,int size){
		return articleservice.search(keyWord,pageNo,size);
	}
	//TODO 迭代开发：改成分页
	/**
	 * 分页取出文章列表
	 * @param httpSession
	 * @param pageNo		请求的页数
	 * @param size  每页的显示数目
	 * @return 分页对象
	 */
	@ResponseBody
	@RequestMapping("/getAllArticle")
	public ArticlePageVo getAllArticle(HttpSession httpSession,int pageNo,int size){
		User user=(User)httpSession.getAttribute("user");
		return  articleservice.getAllArticleUsePaging(user.getId(),pageNo,size);
		
	}
	/*//修改文章 
	//TODO 目测可以删除该方法，因为上面已经有了一个功能相同的方法
	@RequestMapping("motdify")
	public ModelAndView modity(int articleId){
		Article article=articleservice.getArticle(articleId);
		ModelAndView mv=new ModelAndView();
		mv.addObject("article",article);
		mv.setViewName("modifyBlog");
		return mv;
		
	}*/
	//接受一篇文章，并更新
	@RequestMapping("execModify")
	public String execModify(Article article){
		articleservice.update(article);
		return "redirect:/user/intoSpace.do";
	}
	//接受一个文章ID，删除该文章
	@RequestMapping("delete")
	public String delete(int articleid){
		articleservice.deleteArticleById(articleid);
		return "redirect:/user/intoSpace.do";
	}
	//文章图片上传！
	//保存路径为当前网站根目录的resource/ArticleImages文件夹里
	//TODO 尝试把返回的JSon数据优化
	@ResponseBody
	@RequestMapping(value="/imageupload",produces="application/json;charset=UTF-8")
	public String imageupload(@RequestParam("editormd-image-file")  MultipartFile Imgfile,HttpServletRequest request,HttpServletResponse response) {
		String filename=Imgfile.getOriginalFilename();
		String path=request.getSession().getServletContext().getRealPath("")+"/resources/ArticleImages/"+filename;
		BufferedOutputStream bos=null;
		BufferedInputStream bis=null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(path));
			bis = new BufferedInputStream(Imgfile.getInputStream());
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
	
		return  "{\"success\": 1, \"message\":\"上传成功\",\"url\":\"http://localhost:8080/Blog/resources/ArticleImages/"+filename+"\"}";
	}
	//下载文件，需要传参数：文件名
	@RequestMapping("downloadFile")
	public void downloadFile(HttpServletRequest request,HttpServletResponse response,String filename) throws UnsupportedEncodingException{
		String path=request.getSession().getServletContext().getRealPath("/resources/download")+"\\";
		DownloadFileUtil.downloadFile(filename,path,response);					//传入文件名，文件类型（不需要），文件目录，response来下载文件。
	}
	public ArticleService getArticleservice() {
		return articleservice;
	}
	@Autowired
	public void setArticleservice(ArticleService articleservice) {
		this.articleservice = articleservice;
	}
}
