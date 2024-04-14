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
@TableName("room")
public class RoomEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 会议室id
	 */
	@TableId(type = IdType.AUTO)
	@JsonProperty("rId")
	 private int rId;
	 /**
	 * 会议室名
	 */
	private String name;
	/**
	 * 状态（0：不可用 1： 可用）
	 */
	private Integer status;

}
