package com.yq.jforgame.dao;

import com.yq.jforgame.pojo.PlayInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlayerInfoMapper {
    int deleteByPrimaryKey(String openId);

    int insert(PlayInfo record);

    int insertSelective(PlayInfo record);

    PlayInfo selectByPrimaryKey(String openId);

    int updateByPrimaryKeySelective(PlayInfo record);

    int updateByPrimaryKey(PlayInfo record);


    /**
     * @param list 用户openId的集合
     * @return  返回数据库中存在的用户信息
     */
    List<PlayInfo>  playerInfoList(@Param("list") List<String> list);
}
