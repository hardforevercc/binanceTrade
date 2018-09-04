package com.binance.mybatis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.binance.mybatis.model.OkexTradeInfo;
import com.binance.mybatis.model.OkexTradeInfoExample;

public interface OkexTradeInfoMapper {
    /**
     * 根据条件计数
     *
     * @param example
     */
    int countByExample(OkexTradeInfoExample example);

    /**
     * 根据主键删除数据库的记录
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入数据库记录
     *
     * @param record
     */
    int insert(OkexTradeInfo record);

    /**
     * 插入数据库记录
     *
     * @param record
     */
    int insertSelective(OkexTradeInfo record);

    /**
     * 根据条件查询列表
     *
     * @param example
     */
    List<OkexTradeInfo> selectByExample(OkexTradeInfoExample example);

    /**
     * 根据主键获取一条数据库记录
     *
     * @param id
     */
    OkexTradeInfo selectByPrimaryKey(Integer id);

    /**
     * 选择性更新数据库记录
     *
     * @param record
     * @param example
     */
    int updateByExampleSelective(@Param("record") OkexTradeInfo record, @Param("example") OkexTradeInfoExample example);

    /**
     * 选择性更新数据库记录
     *
     * @param record
     * @param example
     */
    int updateByExample(@Param("record") OkexTradeInfo record, @Param("example") OkexTradeInfoExample example);

    /**
     * 根据主键来更新部分数据库记录
     *
     * @param record
     */
    int updateByPrimaryKeySelective(OkexTradeInfo record);

    /**
     * 根据主键来更新数据库记录
     *
     * @param record
     */
    int updateByPrimaryKey(OkexTradeInfo record);
}