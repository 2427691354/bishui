package com.lixing.service;

import com.lixing.entity.WxUser;

/**
 * @author cc
 * @date 2020/07/16
 **/
public interface WxUserService {

    WxUser findById(String id);

    WxUser findByName(String name);
}
