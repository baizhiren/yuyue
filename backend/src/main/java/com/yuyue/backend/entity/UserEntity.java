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
@TableName("user")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@TableId(type = IdType.AUTO)
	@JsonProperty("uId")
	private int uId;
	/**
	 * 微信id
	 */
	@JsonProperty("vId")
	private String vId;
	/**
	 * 用户姓名
	 */
	@JsonProperty("uName")
	private String uName;
	/**
	 * 老师姓名
	 */

	private String teacherName;

	int bookCount;

	int status;

}
