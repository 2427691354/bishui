package com.lixing.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Map;

/**
 * @author cc
 * @date 2020/07/14
 **/
@Data
public class Eq {

    @Id
    String id;
    String eq_name;
    String eq_id;
    String agent_id;
    String manager_id;
    Double second_price;
    Integer card_count;
    String eq_account;
    Double eq_lat;
    Double eq_lon;
    Boolean is_active;
    Integer manager_proportion;
    Integer agent_proportion;
    Integer platformp_proportion;


}
