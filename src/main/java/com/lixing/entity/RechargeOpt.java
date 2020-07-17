package com.lixing.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author cc
 * @date 2020/07/16
 **/
@Data
@Document("recharge_opt")
public class RechargeOpt {

    Integer type;

    @Field("recharge_amount")
    Integer rechargeAmount;

    @Field("given_ amount")
    Integer givenAmount;

}
