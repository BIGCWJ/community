package cn.ys.community.dto;

import lombok.Data;

/**
 * gihub要求的包含五个值的链接封装
 */
@Data
public class AccessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;
}
