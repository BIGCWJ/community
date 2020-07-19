package cn.ys.community.model;

import lombok.Data;

/**
 * 封装一个问题的model
 */
@Data
public class Question {
    private Integer id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;

}
