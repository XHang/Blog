package com.Blog.Dao;


import java.security.Provider;
import java.security.Security;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.Blog.Model.Category;
import com.Blog.Service.ArticleService;
import com.Blog.Service.CategoryService;
import com.Blog.Vo.ArticlePageVo;
/**
 * 单元测试，采用SpringTest测试框架！
 * 1
 * @author Mr-hang
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:Spring.xml")
public class TestArticleService {
	@Autowired
	public ArticleService articleService;
	@Test
	public void searchTest(){
		ArticlePageVo vo=articleService.search("安顿", 0, 3);
		System.out.println("分页查询出来的总数为:"+vo.getTotalPage()+"    当前页数为："+vo.getPageNo()+"    当前查询出来有"+vo.getSize()+"条");
	}
	@Test
	public void getArticleListByCategory(){
		ArticlePageVo vo=articleService.getArticleListByCategory(19, 0,3);
		System.out.println("分页查询出来的总数为:"+vo.getTotalPage()+"    当前页数为："+vo.getPageNo()+"    当前查询出来有"+vo.getSize()+"条");
	}
	@Test
	public void getAllArticleUsePagingTest(){
		ArticlePageVo vo=articleService.getAllArticleUsePaging(12, 0, 3);
		System.out.println("分页查询出来的总数为:"+vo.getTotalPage()+"    当前页数为："+vo.getPageNo()+"    当前查询出来有"+vo.getSize()+"条");
	}
	@Test
	public void TestJiaMi(){
		for(Provider p : Security.getProviders()){  
		    System.out.println(p.getName()+"："+p.getInfo());    
		} 
	}
	
	
}
