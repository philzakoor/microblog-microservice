package org.ac.cst8277.zakoor.phil.sms.dtos;

public class Constants {
    private Constants() {
        throw new IllegalStateException("Constants only class");
    }

    // HTTP Section
    public static final String CODE = "code";
    public static final String MESSAGE = "message";
    public static final String DATA = "data";

    // HEADERS Section
    public static final String APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";
    public static final String TOKEN = "Token";

    // Database Section
    public static final String DB = "`sms`";
    public static final String TABLE_SUB = "`subscriptions`";
    public static final String GET_ALL_SUBS = "SELECT * FROM " + TABLE_SUB;
    public static final String GET_ALL_SUBS_BY_USER = "SELECT * FROM " + TABLE_SUB + " WHERE " + TABLE_SUB
            + ".`subid` = UUID_TO_BIN(?);";
    public static final String SUBSCRIBE = "INSERT INTO " + TABLE_SUB + " VALUES (UUID_TO_BIN(?), UUID_TO_BIN(?));";
    public static final String UNSUBSCRIBE = "DELETE FROM " + TABLE_SUB + " WHERE `subId` = UUID_TO_BIN(?) AND `proId` = UUID_TO_BIN(?);";
}
