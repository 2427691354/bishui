package com.lixing.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author cc
 * @date 2020/07/16
 **/

@Data
@Document("wx_user")
public class WxUser {

    @Id
    String id;

    @Field("open_id")
    String openid;

    @Field("nick_name")
    String nickName;

    @Field("head_img_url")
    String headImgUrl;
    Integer sex;
    String province;
    String city;
    String country;

    @Field("subscribe_time")
    Integer subscribeTime;

}
