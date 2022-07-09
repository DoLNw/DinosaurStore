package com.jcwang.store.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.jcwang.store.valid.AddGroup;
import com.jcwang.store.valid.ListValue;
import com.jcwang.store.valid.UpdateGourp;
import com.jcwang.store.valid.UpdateStatusGourp;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 09:59:08
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "新增不能指定id", groups = {UpdateGourp.class})
	@Null(message = "新增不能指定id", groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空", groups = {AddGroup.class, UpdateGourp.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotEmpty(message = "不能为空", groups = {AddGroup.class})
	@URL(message = "logo必须是一个合法的URL地址", groups = {AddGroup.class, UpdateGourp.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ListValue(values={0,1}, groups = {AddGroup.class, UpdateStatusGourp.class, UpdateGourp.class},
			message = "显示状态不是0就是1")
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母", groups = {AddGroup.class, UpdateGourp.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups = {AddGroup.class})
	@Min(value = 0, message = "排序必须大于等于0", groups = {AddGroup.class, UpdateGourp.class})
	private Integer sort;

}
