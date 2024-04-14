package com.yuyue.backend.entity;

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
@TableName("appointment")
public class AppointmentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 预约id
	 */
	@TableId
	@JsonProperty("aId")
	private int aId;
	 /**
	 * 时段id
	 */
	@JsonProperty("tId")
	private Integer tId;
	/**
	 * 会议室id
	 */
	@JsonProperty("rId")
	private Integer rId;
	/**
	 * 用户id
	 */
	@JsonProperty("uId")
	private Integer uId;
	/**
	 * 会议室名
	 */
	@JsonProperty("rName")
	private String rName;
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

}
