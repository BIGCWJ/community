package cn.ys.community.dto;

import cn.ys.community.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private String notifierName;
    private String outerTitle;
    private Long outid;
    private String typeName;
    private Integer type;

}
