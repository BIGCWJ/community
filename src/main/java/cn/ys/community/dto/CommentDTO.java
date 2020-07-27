package cn.ys.community.dto;

import cn.ys.community.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private Long gmtCreate;
    private Long modified;
    private Integer likeCount;
    private String content;
    private User user;

}
