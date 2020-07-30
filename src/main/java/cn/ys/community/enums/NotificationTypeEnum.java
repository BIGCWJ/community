package cn.ys.community.enums;

public enum NotificationTypeEnum {
    REPLY_QUESTION(1,"回复了问题"),
    REPLY_COMMMENT(2,"回复了评论")
    ;

    NotificationTypeEnum(int status, String name) {
        this.type = status;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    private int type;
    private String name;

    public static String nameOfType(int type){
        for (NotificationTypeEnum notificationTypeEnum : NotificationTypeEnum.values()) {
            if(notificationTypeEnum.getType()==type){
                return notificationTypeEnum.getName();
            }
        }
        return "";
    }


}
