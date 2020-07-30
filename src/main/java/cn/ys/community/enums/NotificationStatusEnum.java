package cn.ys.community.enums;

public enum NotificationStatusEnum {
    UNREAD(0),READ(1)
    ;

    public int getStatus() {
        return status;
    }

    private int status;

    NotificationStatusEnum(int status) {
        this.status = status;
    }
}
