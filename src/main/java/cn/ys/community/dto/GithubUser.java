package cn.ys.community.dto;

import lombok.Data;

/**
 * 从github用户返回的表单中要获得的数据
 */
@Data
public class GithubUser {
    private String name;
    private Long id;
    private String bio;
    private String avatar_url;
}
