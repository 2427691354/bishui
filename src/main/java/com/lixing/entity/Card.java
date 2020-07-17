package com.lixing.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author cc
 * @date 2020/07/14
 **/
@Data
public class Card {
    @Id
    String id;

    @Field("card_num")
    String cardNum;

    String belongs;

    @Field("effluent_time")
    Integer effluentTime;

    Double balance;

    Integer type;

    Set Eqs;

}
