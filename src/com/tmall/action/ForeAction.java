package com.tmall.action;

import com.opensymphony.xwork2.ActionContext;
import com.tmall.pojo.User;
import com.tmall.service.ProductImageService;
import javafx.application.Application;
import org.apache.struts2.convention.annotation.Action;
import org.springframework.context.ApplicationContext;
import org.springframework.web.util.HtmlUtils;

/**
 * Created by OovEver on 2017/11/6.
 */
public class ForeAction extends Action4Result{
    @Action("forehome")
    public String home() {
        categorys = categoryService.list();
        productService.fill(categorys);
        productService.fillByRow(categorys);
        return "home.jsp";
    }
    @Action("foreregister")
    public String register() {
//        对特殊字符进行转义
        user.setName(HtmlUtils.htmlEscape(user.getName()));
        boolean exist = userService.isExist(user.getName());

        if(exist){
            msg = "用户名已经被使用,不能使用";
            return "register.jsp";
        }
        userService.save(user);
        return "registerSuccessPage";
    }
    @Action("forelogin")
    public String login() {
        user.setName(HtmlUtils.htmlEscape(user.getName()));
        User user_session = userService.get(user.getName(),user.getPassword());
        if(null==user_session){
            msg= "账号密码错误";
            return "login.jsp";
        }
        ActionContext.getContext().getSession().put("user", user_session);
        return "homePage";
    }
    @Action("forelogout")
    public String logout() {
        ActionContext.getContext().getSession().remove("user");
        return "homePage";
    }
    @Action("foreproduct")
    public String product() {
        t2p(product);

        productImageService.setFirstProdutImage(product);
        productSingleImages = productImageService.list("product",product,"type", ProductImageService.type_single);
        productDetailImages = productImageService.list("product",product,"type", ProductImageService.type_detail);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);
        reviews = reviewService.listByParent(product);
        productService.setSaleAndReviewNumber(product);
        return "product.jsp";
    }
}
