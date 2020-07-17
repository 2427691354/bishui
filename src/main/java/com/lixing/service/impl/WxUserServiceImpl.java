package com.lixing.service.impl;

import com.lixing.entity.WxUser;
import com.lixing.service.WxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author cc
 * @date 2020/07/16
 **/
@Service
public class WxUserServiceImpl implements WxUserService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public WxUser findById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query,WxUser.class);
    }

    @Override
    public WxUser findByName(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("nick_name").is(name));
        return mongoTemplate.findOne(query,WxUser.class);
    }
}
