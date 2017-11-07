package com.tmall.action;

import com.opensymphony.xwork2.ActionContext;
import com.tmall.comparator.*;
import com.tmall.pojo.OrderItem;
import com.tmall.pojo.Product;
import com.tmall.pojo.User;
import com.tmall.service.ProductImageService;
import javafx.application.Application;
import javassist.compiler.ast.Keyword;
import org.apache.struts2.convention.annotation.Action;
import org.springframework.context.ApplicationContext;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    @Action("foreloginAjax")
    public String liginAjax() {
        user.setName(HtmlUtils.htmlEscape(user.getName()));
        User user_session = userService.get(user.getName(), user.getPassword());
        if(null==user_session)
            return "fail.jsp";
        ActionContext.getContext().getSession().put("user", user_session);
        return "success.jsp";
    }
    @Action("forecategory")
    public String category() {
        t2p(category);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());

        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(category.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
            }
        }
        return "category.jsp";
    }
    @Action("forecheckLogin")
    public String checkLogin() {
        User u =(User) ActionContext.getContext().getSession().get("user");
        if(null==u)
            return "fail.jsp";
        else
            return "success.jsp";
    }
    @Action( "foresearch")
    public String search() {
        products= productService.search(keyword,0,20);
        productService.setSaleAndReviewNumber(products);
        for (Product product : products)
            productImageService.setFirstProdutImage(product);

        return "searchResult.jsp";
    }
    @Action("forebuyone")
    public String buyone() {
        User user =(User) ActionContext.getContext().getSession().get("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.list("user",user,"order", null);
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==product.getId()){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }

        if(!found){
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setNumber(num);
            oi.setProduct(product);
            orderItemService.save(oi);
            oiid = oi.getId();
        }
        return "buyPage";
    }
    @Action("forebuy")
    public String buy() {
        orderItems = new ArrayList<>();
        for (int oiid : oiids) {
            OrderItem oi= (OrderItem) orderItemService.get(oiid);
            total +=oi.getProduct().getPromotePrice()*oi.getNumber();
            orderItems.add(oi);
            productImageService.setFirstProdutImage(oi.getProduct());
        }

        ActionContext.getContext().getSession().put("orderItems", orderItems);
        return "buy.jsp";
    }
    @Action("foreaddCart")
    public String addCart() {

        User user =(User) ActionContext.getContext().getSession().get("user");
        boolean found = false;

        List<OrderItem> ois = orderItemService.list("user",user,"order", null);
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==product.getId()){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                found = true;
                break;
            }
        }

        if(!found){
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setNumber(num);
            oi.setProduct(product);
            orderItemService.save(oi);
        }
        return "success.jsp";
    }
    @Action("forecart")
    public String cart() {
        User user =(User) ActionContext.getContext().getSession().get("user");
        orderItems = orderItemService.list("user",user,"order", null);
        for (OrderItem orderItem : orderItems)
            productImageService.setFirstProdutImage(orderItem.getProduct());
        return "cart.jsp";
    }
}
