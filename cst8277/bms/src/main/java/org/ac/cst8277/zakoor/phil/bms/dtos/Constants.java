package org.ac.cst8277.zakoor.phil.bms.dtos;

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
    public static final String DB = "`bms`";
    public static final String TABLE_POSTS = "`posts`";
    public static final String GET_ALL_POSTS = "SELECT * FROM " + TABLE_POSTS;
    public static final String GET_ALL_POST_BY_USER = "SELECT * FROM " + TABLE_POSTS + " WHERE " + TABLE_POSTS
            + ".`uid` = UUID_TO_BIN(?);";
    public static final String GET_POST_BY_ID = "SELECT * FROM " + TABLE_POSTS + " WHERE " + TABLE_POSTS
            + ".`id`= UUID_TO_BIN(?);";
    public static final String CREATE_POST = "INSERT INTO " + TABLE_POSTS
            + " (`id`, `uid`, `content`, `created`) VALUES "
            + "(UUID_TO_BIN(?), UUID_TO_BIN(?), ?, ?);";
    public static final String DELETE_POST = "DELETE FROM " + TABLE_POSTS + " WHERE `id` = (UUID_TO_BIN(?));";
    public static final String DELETE_POSTS_BY_USER = "DELETE FROM " + TABLE_POSTS + " WHERE `uid` = (UUID_TO_BIN(?));";
    public static final String EDIT_POST = "UPDATE " + TABLE_POSTS
            + " SET `content` = ? WHERE `id` = (UUID_TO_BIN(?));";
}
