package cn.iocoder.yudao.framework.common.dict;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/11 14:20
 */
public class CommonDict {
    /**
     * 英文标点字符类型
     **/
    public static final String ARRAY_START = "[";
    public static final String ARRAY_END = "]";
    public static final String COMMA = ",";
    public static final String SPACE = " ";
    public static final String EMPTY_STR = "";
    public static final String SLASH_CHAR = "/";
    public static final String COLON = ":";
    public static final String VERTICAL_BAR = "|";
    public static final String HYPHEN = "-";
    public static final String SNAKE = "_";
    public static final String BASE_SNAKE = "_";
    public static final String PERCENT_SIGN = "%";

    /**
     * 业务字段
     */
    public static final String BRAND_FIELD_NAME = "品牌";
    public static final String HSCODE_FIELD_NAME = "海关编码";
    public static final String COMPANY_NAME = "采购主体";
    public static final String MANAGER_FIELD_NAME = "manager";
    public static final String PURCHASE_USER_FIELD_NAME = "purchaseUser";
    public static final String SALES_FIELD_NAME = "sales";

    /**
     * 数字
     */

    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String FOUR = "4";
    public static final String FIVE = "5";
    public static final String SIX = "6";
    public static final String SEVEN = "7";
    public static final String EIGHT = "8";
    public static final String NINE = "9";
    public static final String TEN = "10";
    public static final String ONE_HUNDRED = "100";

    /**
     * 查询逻辑删除数据queryWapper后缀(仅查询可用)
     */
    public static final String LAST_SQL = "or deleted = 1";

    /**
     * 仓库
     */
    public static final String DEFAULT_POSITION = "默认位置";
    public static final String SYSTEM_NAME = "系统";
    public static final String TRANSFER = "中转";

    public static final String PURCHASE_QUANTITY = "purchaseQuantity";

    public static final String STOCK_QUANTITY = "stockQuantity";

}
