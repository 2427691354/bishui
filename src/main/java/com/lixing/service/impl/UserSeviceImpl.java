package com.lixing.service.impl;

import com.lixing.entity.User;
import com.lixing.service.UserService;
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
public class UserSeviceImpl implements UserService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public User findByAgentNick(String agentNick) {
        Query query = new Query();
        query.addCriteria(Criteria.where("type").is(1).and("info.agent_nick").is(agentNick));
        return mongoTemplate.findOne(query,User.class);
    }
}
