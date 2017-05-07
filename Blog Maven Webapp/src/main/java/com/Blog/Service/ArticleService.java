package com.Blog.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Blog.Dao.ArticleDAO;
import com.Blog.Model.Article;
import com.Blog.Vo.ArticlePageVo;
@Service
public class ArticleService {
	private ArticleDAO dao;
	public List<Article> getNewArticle(int id) {
		return dao.selectNewArticle(id);
	}

	public List<Article> getHotArticle(int id) {
		return dao.selectHotArticle(id);
	}
	/**
	 * 保存用户的文章到数据库
	 * @param article
	 */
	public void save(Article article) {
		dao.saveArticle(article);
	}
	/**
	 * 得到用户文章总数
	 * @param userid
	 * @return
	 */
	public int getArticleTotal(int userid){
		return dao.selectArticleTatal(userid);
	}
	/**
	 * 根据文章ID得到文章对象
	 * @param id 
	 * @return
	 */
	public Article getArticle(int id){
		return dao.selectArticleById(id);
	}
	/**
	 * 根据文章ID删掉文章
	 * @param articleid
	 */
	public void deleteArticleById(int  articleid){
		dao.deleteArticleById(articleid);
		
	}
	/**
	 * 根据类别列表取出文章列表,使用分页技术
	 * @param categoryId
	 * @param pageNo
	 * @param size
	 * @return
	 */
	public ArticlePageVo getArticleListByCategory(int categoryId,int pageNo,int size) {
		int from=pageNo*size;	//从第几个数据开始取。
		List<Article> articles=dao.selectArticleByCategoryId(categoryId,from,size);
		Map parameterMap=new HashMap<>();
		parameterMap.put("categoryid", categoryId);
		int total=dao.selectTotalByWherer(parameterMap);
		ArticlePageVo vo=new ArticlePageVo(pageNo, total, articles.size());
		vo.setArticleList(articles);
		return vo;
	}
	/**
	 * 根据关键字搜索文章，得到文章列表，使用分页技术
	 * @param keyWord
	 * @param pageNo
	 * @param size
	 * @return
	 */
	public ArticlePageVo search(String keyWord,int pageNo,int size) {
		keyWord="%"+keyWord+"%";
		int from=pageNo*size;	//从第几个数据开始取。
		List<Article> articles=dao.search(keyWord,from,size);
		int total=dao.searchTotal(keyWord);
		ArticlePageVo vo=new ArticlePageVo(pageNo, total, articles.size());
		vo.setArticleList(articles);
		return vo;
			
	}
	/**
	 * 使用分页获取文章列表
	 * @param id  用户ID
	 * @param pageNo  当前页数
	 * @param size  分页大小
	 * @return
	 */
	public ArticlePageVo getAllArticleUsePaging(int id,int pageNo,int size) {
		int from=pageNo*size;		//从第几个数据开始取。
		List<Article> articles=dao.selectAllArticleUsePaging(id,from,size);
		Map parameterMap=new HashMap<>();
		parameterMap.put("userid", id);
		int total=dao.selectTotalByWherer(parameterMap);
		ArticlePageVo vo=new ArticlePageVo(pageNo, total, articles.size());
		vo.setArticleList(articles);
		return vo;
		
	}
	public void update(Article article) {
		dao.update(article);
	}
	public ArticleDAO getDao() {
		return dao;
	}
	@Autowired
	public void setDao(ArticleDAO dao) {
		this.dao = dao;
		
	}
}
