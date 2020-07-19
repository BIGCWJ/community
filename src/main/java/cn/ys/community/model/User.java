package cn.ys.community.model;

import lombok.Data;

/**
 * 关于持久层的实体文件
 */
@Data
public class User {
    private Integer id;
    private String name;
    private String accountId;
    private String token;
    private Long gmtCreate;
    private Long gmtModified;
    private String avatarUrl;

}




