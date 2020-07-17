package com.lixing.service.impl;

import com.lixing.entity.Eq;
import com.lixing.service.EqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author cc
 * @date 2020/07/14
 **/
@Service
public class EqServiceImpl implements EqService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Eq findByEqName(String eqName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("eq_name").is(eqName));
        return mongoTemplate.findOne(query,Eq.class);
    }
}
