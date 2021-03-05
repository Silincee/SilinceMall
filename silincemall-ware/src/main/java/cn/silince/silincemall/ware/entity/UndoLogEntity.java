package cn.silince.silincemall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 19:31:06
 */
@Data
@TableName("undo_log")
public class UndoLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 
	 */
	private Long branchId;
	/**
	 * 
	 */
	private String xid;
	/**
	 * 
	 */
	private String context;
	/**
	 * 
	 */
	private Long rollbackInfo;
	/**
	 * 
	 */
	private Integer logStatus;
	/**
	 * 
	 */
	private Date logCreated;
	/**
	 * 
	 */
	private Date logModified;
	/**
	 * 
	 */
	private String ext;

}
