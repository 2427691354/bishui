package com.lixing.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

/**
 * @author cc
 * @date 2020/07/14
 **/
@Data
@Document("order_list")
public class Order {

    @Id
    String id;

    Map<String,Object> operator;

    @Field("card_num")
    String cardNum	;
    Double amount;
    Integer type;

    @Field("recharge_time")
    Date rechargeTime;

}
