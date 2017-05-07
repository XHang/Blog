package com.Blog.Vo;

import java.util.List;

import com.Blog.Model.Article;

/**
 * 文章列表分页对象
 * @author Mr-hang
 *
 */
public class ArticlePageVo {
	
	private int pageNo;
	private int totalPage;
	private int size;
	private List<Article> articleList;
	/**
	 * vo的构造函数
	 * @param pageNo  	当前页数
	 * @param totalPage	总页数
	 * @param size				分页大小
	 */
	public ArticlePageVo(int pageNo,int totalPage,int size){
		this.pageNo=pageNo;
		this.totalPage=totalPage;
		this.size=size;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * 获取文章文章列表
	 * @return
	 */
	public List<Article> getArticleList() {
		return articleList;
	}
	/**
	 * 设置文章列表。
	 * @param articleList
	 */
	public void setArticleList(List<Article> articleList) {
		this.articleList = articleList;
	}
	

}
