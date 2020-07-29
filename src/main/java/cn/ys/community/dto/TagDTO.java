package cn.ys.community.dto;

import lombok.Data;

import java.util.List;
@Data
public class TagDTO {

    public String categoryName;
    private List<String> tags;

}
