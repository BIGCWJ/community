package cn.ys.community.enums;

public enum CommentTpyeEnum {
    QUESTION(1), COMMENT(2);

    private Integer type;

    public static boolean isExist(Integer type) {
        for (CommentTpyeEnum commentTpyeEnum : CommentTpyeEnum.values()) {

            if (commentTpyeEnum.type == type) {
                return true;
            }

        }
        return false;
    }

    public Integer getType() {
        return type;
    }

    CommentTpyeEnum(Integer type) {
        this.type = type;
    }
}
