package com.bwie.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户上传文件表
 * @TableName tb_upload_file
 */
@TableName(value ="tb_upload_file")
@Data
public class TbUploadFile implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 存储名字
     */
    private String storeName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件扩展名
     */
    private String fileExt;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 删除状态0：未删除1：已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}