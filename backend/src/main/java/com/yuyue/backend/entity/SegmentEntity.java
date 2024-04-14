package com.yuyue.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 
 * 
 * @author chao
 * @email chao@gmail.com
 * @date 2024-03-23 20:49:20
 */
@Data
@TableName("segment")
public class SegmentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 时段id
	 */
	@TableId(type = IdType.AUTO)
	@JsonProperty("tId")
	private int tId;
	/**
	 * 会议室id
	 */
	@JsonProperty("rId")
	private Integer rId;

	/**
	 * 会议室名
	 */
	@JsonProperty("rName")
	private String rName;


	/**
		用户id
	 */
	@JsonProperty("uId")
	private Integer uId;

	/**
	 * 预约人姓名
	 */
	@JsonProperty("uName")
	private String uName;


	/**
	 * 开始时间
	 */
	private Date startTime;
	/**
	 * 结束时间
	 */
	private Date endTime;
	/**
	 * 时段状态（0：不可用  1： 未预约  2： 已预约）
	 */
	private Integer status;


	/**
	* 星期
	 */
	private Integer week;

}
