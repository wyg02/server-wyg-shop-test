package com.bwie.vo;

import lombok.Data;
import java.io.Serializable;
/**
 * @Author Sun
 * @Version 1.0
 * @description: TODO
 * @date 2022/12/11 20:28:58
 */


/**
 * @author 军哥
 * @version 1.0
 * @description: FileUploadEndVo
 * @date 2022/12/9 10:02
 */

@Data
public class FileUploadEndVo implements Serializable {
    private String id;
    private String name;
    private Integer size;
    private String type;
    private String ext;
    private String md5;
    private String lastModifiedDate;
}
