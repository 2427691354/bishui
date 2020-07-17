package com.lixing.service;

import com.lixing.entity.User;

/**
 * @author cc
 * @date 2020/07/14
 **/

public interface UserService {

    User findByAgentNick(String agentNickS);
}
