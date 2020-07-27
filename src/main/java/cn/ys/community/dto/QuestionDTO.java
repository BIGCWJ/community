package cn.ys.community.dto;

import cn.ys.community.model.User;
import lombok.Data;

/**
 * 关于封装一个问题所需的元素DTO，question和对应的user封装
 */
@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private Long viewCount;
    private Long commentCount;
    private Long likeCount;
    private User user;
}
