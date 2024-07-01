package ru.practicum.shareit.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SqlHelper {
    public static final String SCHEMA = "public";

    public static final String TABLE_ITEMS = "items";
    public static final String ITEM_ID = "id";
    public static final String ITEM_NAME = "name";
    public static final String ITEM_DESCRIPTION = "description";
    public static final String ITEM_AVAILABLE = "available";
    public static final String ITEM_OWNER_ID = "owner_id";
    public static final String ITEM_REQUEST_ID = "request_id";

    public static final String TABLE_USERS = "users";
    public static final String USER_ID = "id";
    public static final String USER_NAME = "name";
    public static final String USER_EMAIL = "email";

    public static final String TABLE_BOOKINGS = "bookings";
    public static final String BOOKING_ID = "id";
    public static final String BOOKING_ITEM_ID = "item_id";
    public static final String BOOKING_STATUS = "status";
    public static final String BOOKING_BOOKER_ID = "booker_id";
    public static final String BOOKING_DATE_START = "date_start";
    public static final String BOOKING_DATE_END = "date_end";

    public static final String TABLE_COMMENTS = "comments";
    public static final String COMMENT_ID = "id";
    public static final String COMMENT_ITEM_ID = "item_id";
    public static final String COMMENT_AUTHOR_ID = "author_id";
    public static final String COMMENT_TEXT = "text";
    public static final String COMMENT_CREATED = "created";

    public static final String TABLE_REQUESTS = "requests";
    public static final String REQUEST_ID = "id";
    public static final String REQUEST_DESCRIPTION = "description";
    public static final String REQUEST_CREATED = "created";
    public static final String REQUEST_USER_ID = "user_id";
}
