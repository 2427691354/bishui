package com.lixing.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

/**
 * @author cc
 * @date 2020/07/13
 **/
@Data
public class User {

    @Id
    String id;

    String pwd;

    Integer type;

    Map<String,Object> info;

    @Field("total_revenue")
    Double totalRevenue;

    Double withdrawal;

    Boolean isAdmin;

    @Field("wx_user_id")
    String wxId;
}
