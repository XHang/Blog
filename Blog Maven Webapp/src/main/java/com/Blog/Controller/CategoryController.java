package com.Blog.Controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.Blog.Model.Category;
import com.Blog.Model.User;
import com.Blog.Service.CategoryService;
@Controller
@RequestMapping("/category")
public class CategoryController {
	CategoryService categoryservice;
	//ajax请求类别列表json
	@RequestMapping("/getCategoryList")
	@ResponseBody
	public List<Category> getCategory(HttpSession session){
		User user=(User) session.getAttribute("user");
		return categoryservice.getCategorys(user.getId());
	}
	//TODO  可精简掉该方法，统一用ajax请求类别列表
	@RequestMapping("/getCategoryList")
	public ModelAndView getCategoryList(HttpSession session){
		User user=(User) session.getAttribute("user");
		List<Category> categorys=categoryservice.getCategorys(user.getId());
		ModelAndView mv=new ModelAndView();
		mv.addObject("categorys",categorys);
		mv.setViewName("CategoryList");
		return mv;
	}
	@RequestMapping("/update")
	public void update(Category category,ModelAndView mv){
		categoryservice.modifiCategory(category);
	}
	@RequestMapping("/add")
	public void add(Category category,HttpSession session){
		User user=(User) session.getAttribute("user");
		category.setUser(user);
		categoryservice.addCategory(category);
	}
	@RequestMapping("/delete")
	public void delete(Category category){
		categoryservice.deleteCategory(category);
	}
	public CategoryService getCategoryservice() {
		return categoryservice;
	}
	@Autowired
	public void setCategoryservice(CategoryService categoryservice) {
		this.categoryservice = categoryservice;
	}
	
}
