package cn.silince.silincemall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 成长值变化历史记录
 * 
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 19:03:11
 */
@Data
@TableName("ums_growth_change_history")
public class GrowthChangeHistoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * member_id
	 */
	private Long memberId;
	/**
	 * create_time
	 */
	private Date createTime;
	/**
	 * 改变的值（正负计数）
	 */
	private Integer changeCount;
	/**
	 * 备注
	 */
	private String note;
	/**
	 * 积分来源[0-购物，1-管理员修改]
	 */
	private Integer sourceType;

}