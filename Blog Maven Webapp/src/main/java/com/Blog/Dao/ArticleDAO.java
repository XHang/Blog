package com.Blog.Dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.Blog.Model.Article;
import com.Blog.Vo.ArticlePageVo;

public interface ArticleDAO {

	List<Article> selectNewArticle(int id);

	List<Article> selectHotArticle(int id);

	void saveArticle(Article article);
	
	Article selectArticleById(int id);

	void deleteByCategoryID(int categoryid);

	int selectArticleTatal(int userid);

	void deleteArticleById(int id);
	
	List<Article> selectArticleByCategoryId(@Param("categoryId") int categoryId,@Param("from")int from,@Param("size")int size);
	
	List<Article> search(@Param("keyWord")String keyWord,@Param("from")int from,@Param("size")int size);
	
	int searchTotal(String keyWord);
	
	List<Article> selectAllArticleUsePaging(@Param("userid")int userid,@Param("from")int from,@Param("size")int size);

	void update(Article article);
	
	int selectTotalByWherer(@Param("parameterMap") Map parameterMap);

}
