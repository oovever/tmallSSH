package com.tmall.service;

/**
 * Created by OovEver on 2017/11/6.
 */
public interface OrderService extends BaseService{
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";
}