package cn.iocoder.yudao.framework.datapermission.core.rule.dept;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.datapermission.core.rule.DataPermissionRule;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.util.MyBatisUtils;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import cn.iocoder.yudao.module.system.api.permission.dto.FieldPermissionDTO;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.PermissionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.InExpression;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 基于部门的 {@link DataPermissionRule} 数据权限规则实现
 * <p>
 * 注意，使用 DeptDataPermissionRule 时，需要保证表中有 dept_id 部门编号的字段，可自定义。
 * <p>
 * 实际业务场景下，会存在一个经典的问题？当用户修改部门时，冗余的 dept_id 是否需要修改？
 * 1. 一般情况下，dept_id 不进行修改，则会导致用户看不到之前的数据。【yudao-server 采用该方案】
 * 2. 部分情况下，希望该用户还是能看到之前的数据，则有两种方式解决：【需要你改造该 DeptDataPermissionRule 的实现代码】
 * 1）编写洗数据的脚本，将 dept_id 修改成新部门的编号；【建议】
 * 最终过滤条件是 WHERE dept_id = ?
 * 2）洗数据的话，可能涉及的数据量较大，也可以采用 user_id 进行过滤的方式，此时需要获取到 dept_id 对应的所有 user_id 用户编号；
 * 最终过滤条件是 WHERE user_id IN (?, ?, ? ...)
 * 3）想要保证原 dept_id 和 user_id 都可以看的到，此时使用 dept_id 和 user_id 一起过滤；
 * 最终过滤条件是 WHERE dept_id = ? OR user_id IN (?, ?, ? ...)
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Slf4j
public class DeptDataPermissionRule implements DataPermissionRule {

    /**
     * LoginUser 的数据权限缓存 Key
     */
    protected static final String DATA_CONTEXT_KEY = "dataContext";
    /**
     * LoginUser 的字段权限缓存 key
     */
    protected static final String COLUMN_CONTEXT_KEY = "columnContext";

    private static final String DEPT_COLUMN_NAME = "dept_id";
    private static final String USER_COLUMN_NAME = "user_id";

    private static final String FIND_IN_SET = "FIND_IN_SET";
    private static final String JSON_EXTRACT = "JSON_EXTRACT";
    private static final String JSON_CONTAINS = "JSON_CONTAINS";
    static final Expression EXPRESSION_NULL = new NullValue();

    private final PermissionApi permissionApi;

    /**
     * 基于部门的表字段配置
     * 一般情况下，每个表的部门编号字段是 dept_id，通过该配置自定义。
     * <p>
     * key：表名
     * value：字段名
     */
    private final Map<String, String> deptColumns = new HashMap<>();
    /**
     * 基于用户的表字段配置
     * 一般情况下，每个表的部门编号字段是 dept_id，通过该配置自定义。
     * <p>
     * key：表名
     * value：字段名
     */
    private final Map<String, List<Tuple>> userColumns = new HashMap<>();
    /**
     * 所有表名，是 {@link #deptColumns} 和 {@link #userColumns} 的合集
     */
    private final Set<String> TABLE_NAMES = new HashSet<>();

    @Override
    public Set<String> getTableNames() {
        return TABLE_NAMES;
    }

    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        // 只有有登陆用户的情况下，才进行数据权限的处理
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        // 只有管理员类型的用户，才进行数据权限的处理
        if (ObjectUtil.notEqual(loginUser.getUserType(), UserTypeEnum.ADMIN.getValue())) {
            return null;
        }

        // 获得数据权限
        DeptDataPermissionRespDTO deptDataPermission = loginUser.getContext(DATA_CONTEXT_KEY, DeptDataPermissionRespDTO.class);
        // 从上下文中拿不到，则调用逻辑进行获取
        if (deptDataPermission == null) {
            deptDataPermission = permissionApi.getDeptDataPermission(loginUser.getId());
            if (deptDataPermission == null) {
                log.error("[getExpression][LoginUser({}) 获取数据权限为 null]", JsonUtils.toJsonString(loginUser));
                throw new NullPointerException(String.format("LoginUser(%d) Table(%s/%s) 未返回数据权限",
                        loginUser.getId(), tableName, tableAlias.getName()));
            }
            // 添加到上下文中，避免重复计算
            loginUser.setContext(DATA_CONTEXT_KEY, deptDataPermission);
        }

        // 情况一，如果是 ALL 可查看全部，则无需拼接条件
        if (deptDataPermission.getAll()) {
            return null;
        }

        // 情况二，即不能查看部门，又不能查看自己，则说明 100% 无权限
        if (CollUtil.isEmpty(deptDataPermission.getDeptIds())
                && Boolean.FALSE.equals(deptDataPermission.getSelf())) {
            return new EqualsTo(null, null); // WHERE null = null，可以保证返回的数据为空
        }

        // 情况三，拼接 Dept 和 User 的条件，最后组合

//        Expression deptExpression = buildDeptExpression(tableName, tableAlias, deptDataPermission.getDeptIds());
//        Expression userExpression = buildUserExpression(tableName, tableAlias, deptDataPermission.getSelf(), loginUser.getId());
        List<Expression> expressions = buildExpression(tableName, tableAlias, deptDataPermission.getDeptIds(), deptDataPermission.getSelf(), loginUser.getId());
        if (CollUtil.isEmpty(expressions)) {
            return null;
        }
        AtomicReference<Expression> expression= new AtomicReference<>();
        expressions.forEach(s->{
            if (expression.get() ==null){
                expression.set(s);
            }
            expression.set(new OrExpression(expression.get(), s));
        });
//        if (deptExpression == null && userExpression == null) {
//            // TODO 芋艿：获得不到条件的时候，暂时不抛出异常，而是不返回数据
//            log.warn("[getExpression][LoginUser({}) Table({}/{}) DeptDataPermission({}) 构建的条件为空]",
//                    JsonUtils.toJsonString(loginUser), tableName, tableAlias, JsonUtils.toJsonString(deptDataPermission));
////            throw new NullPointerException(String.format("LoginUser(%d) Table(%s/%s) 构建的条件为空",
////                    loginUser.getId(), tableName, tableAlias.getName()));
//            return EXPRESSION_NULL;
//        }
//        if (deptExpression == null) {
//            return userExpression;
//        }
//        if (userExpression == null) {
//            return deptExpression;
//        }
//        // 目前，如果有指定部门 + 可查看自己，采用 OR 条件。即，WHERE (dept_id IN ? OR user_id = ?)
//        return new Parenthesis(new OrExpression(deptExpression, userExpression));
        return new Parenthesis(expression.get());
    }

    @Override
    public boolean checkFieldPermission(String tableName, String fieldName) {
        // 只有有登陆用户的情况下，才进行数据权限的处理
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return false;
        }
        // 只有管理员类型的用户，才进行数据权限的处理
        if (ObjectUtil.notEqual(loginUser.getUserType(), UserTypeEnum.ADMIN.getValue())) {
            return false;
        }
        // 获得字段权限
        Set<FieldPermissionDTO> fieldPermissionDTOList = loginUser.getContext(COLUMN_CONTEXT_KEY, Set.class);
        // 从上下文中拿不到，则调用逻辑进行获取
        if (fieldPermissionDTOList == null) {
            List<FieldPermissionDTO> fieldPermission = permissionApi.getFieldPermission(loginUser.getId());
            if (CollUtil.isNotEmpty(fieldPermission)) {
                //只要当前用户字段权限中包含sql中返回字段则返回true
                return fieldPermission.stream().distinct().filter(s -> {
                    return tableName.equals(s.getTableName()) && fieldName.equals(s.getFieldName());
                }).count() > 0;
            }
            // 添加到上下文中，避免重复计算
            loginUser.setContext(COLUMN_CONTEXT_KEY, fieldPermissionDTOList);
        }
        return false;
    }

    private Expression buildDeptExpression(String tableName, Alias tableAlias, Set<Long> deptIds) {
        // 如果不存在配置，则无需作为条件
        String columnName = deptColumns.get(tableName);
        if (StrUtil.isEmpty(columnName)) {
            return null;
        }
        // 如果为空，则无条件
        if (CollUtil.isEmpty(deptIds)) {
            return null;
        }
        // 拼接条件
        return new InExpression(MyBatisUtils.buildColumn(tableName, tableAlias, columnName),
                new ExpressionList(CollectionUtils.convertList(deptIds, LongValue::new)));
    }

    private List<Expression> buildExpression(String tableName, Alias tableAlias, Set<Long> deptIds, Boolean self, Long userId) {
        // 如果不存在配置，则无需作为条件
        List<Tuple> tuples = userColumns.get(tableName);
        if (CollUtil.isEmpty(tuples)){
            return null;
        }
        return tuples.stream().map(s->transferDeptExpression(tableName, tableAlias, deptIds, self, userId,s)).flatMap(List::stream).toList();
    }

    private List<Expression> transferDeptExpression(String tableName, Alias tableAlias, Set<Long> deptIds, Boolean self, Long userId,Tuple tuple) {
        String columnName = tuple.get(0);
        Integer permissionType = tuple.get(1);
        Integer containLeaderFlag = tuple.get(2);
        List<Expression> result = new ArrayList<>();
        Set<Long> userIdList = new HashSet<>();
        userIdList.add(userId);
        // 如果为空，则默认为仅自己
        if (CollUtil.isEmpty(deptIds)) {
            // 如果查看自己，则无需通过部门查找用户
            if (Boolean.TRUE.equals(self)) {
                // 是否包含部门负责人
                if (BooleanEnum.YES.getValue().equals(containLeaderFlag)){
                    Set<Long> deptUserIds = permissionApi.getDeptUserIdsByUserId(userId);
                    if (CollUtil.isNotEmpty(deptUserIds)){
                        userIdList.addAll(deptUserIds);
                    }
                }
                userIdList.forEach(s->{
                    if (PermissionTypeEnum.FIND_IN_SET.getType().equals(permissionType)) {
                        result.add(buildFindInSetExpression(tableName, tableAlias, columnName, s));
                    } else if (PermissionTypeEnum.SINGLE_JSON.getType().equals(permissionType)) {
                        result.add(buildSingleJsonExpression(tableName, tableAlias, columnName, s));
                    } else if (PermissionTypeEnum.JSON_CONTAINS.getType().equals(permissionType)){
                        result.add(buildJsonContainsExpression(tableName, tableAlias, columnName, s));
                    }else {
                        result.add(new EqualsTo(null, null));
                    }
                });
            }
        } else {
            List<Long> deptUserIdList = permissionApi.getUserIdByDeptIdList(deptIds);
            if (CollUtil.isNotEmpty(userIdList)) {
                result.add(transferDeptExpression(deptUserIdList, tableName, tableAlias, columnName, permissionType));
            }
        }
        return result;
    }

    /**
     * 构建find_in_set函数的表达式
     *
     * @param tableName  表名称
     * @param tableAlias 表别名
     * @param columnName 字段名称
     * @param userId     用户id
     * @return sql表达式
     */
    private Expression buildFindInSetExpression(String tableName, Alias tableAlias, String columnName, Long userId) {
        // 构造FIND_IN_SET函数表达式
        Function findInSetFunc = new Function();
        findInSetFunc.setName(FIND_IN_SET);
        ExpressionList expressionList = new ExpressionList();
        // 此处表达式列表添加元素时需注意顺序问题，对应find_in_set函数中参数顺序！！！
        expressionList.addExpressions(new StringValue(String.valueOf(userId)));
        expressionList.addExpressions(MyBatisUtils.buildColumn(tableName, tableAlias, columnName));
        findInSetFunc.setParameters(expressionList);
        // sql解析器中 ">" 的构造器
        GreaterThan greaterThan = new GreaterThan();
        greaterThan.setLeftExpression(findInSetFunc);
        // find_in_set函数执行结果大于0则表示有匹配记录
        greaterThan.setRightExpression(new LongValue(0L));
        return greaterThan;
    }

    /**
     * 构建JSON_CONTAINS函数的表达式
     *
     * @param tableName  表名称
     * @param tableAlias 表别名
     * @param columnName 字段名称
     * @param userId     用户id
     * @return sql表达式
     */
    private Expression buildJsonContainsExpression(String tableName, Alias tableAlias, String columnName, Long userId) {
        // 构造 JSON_CONTAINS 函数表达式
        Function jsonContainsFunc = new Function();
        jsonContainsFunc.setName(JSON_CONTAINS); // 设置函数名称为 JSON_CONTAINS

        // 构造参数列表
        ExpressionList expressionList = new ExpressionList();
        // 第一个参数：需要查询的 JSON 列
        expressionList.addExpressions(MyBatisUtils.buildColumn(tableName, tableAlias, columnName));
        // 第二个参数：需要匹配的值，这里使用 userId
        expressionList.addExpressions(new StringValue(String.format("{\"userId\": %s}",userId)));

        // 设置函数参数
        jsonContainsFunc.setParameters(expressionList);

        // 返回 JSON_CONTAINS 函数的表达式
        return new EqualsTo(jsonContainsFunc, new LongValue(1)); // JSON_CONTAINS 返回 1 表示匹配成功
    }

    /**
     * 构建JSON_EXTRACT函数的表达式
     *
     * @param tableName  表名称
     * @param tableAlias 表别名
     * @param columnName 字段名称
     * @param userId     用户id
     * @return sql表达式
     */
    private Expression buildSingleJsonExpression(String tableName, Alias tableAlias, String columnName, Long userId) {
        // 构造FIND_IN_SET函数表达式
        Function findInSetFunc = new Function();
        findInSetFunc.setName(JSON_EXTRACT);
        ExpressionList expressionList = new ExpressionList();
        // 此处表达式列表添加元素时需注意顺序问题，对应find_in_set函数中参数顺序！！！
        expressionList.addExpressions(MyBatisUtils.buildColumn(tableName, tableAlias, columnName));
        expressionList.addExpressions(new StringValue("$.userId"));
        findInSetFunc.setParameters(expressionList);
        return new EqualsTo(findInSetFunc, new LongValue(userId));
    }


    /**
     * 转换表达式列表
     *
     * @param userIdList 用户id列表
     * @param tableName  表名称
     * @param tableAlias 表别名
     * @param columnName 字段名称
     * @return sql表达式
     */
    private Expression transferDeptExpression(List<Long> userIdList, String tableName, Alias tableAlias, String columnName, Integer permissionType) {
        if (CollUtil.isEmpty(userIdList)) {
            return null;
        }
        List<Expression> expressionList = new ArrayList<>();
        userIdList.forEach(userId -> {
            if (PermissionTypeEnum.FIND_IN_SET.getType().equals(permissionType)) {
                expressionList.add(buildFindInSetExpression(tableName, tableAlias, columnName, userId));
            } else if (PermissionTypeEnum.SINGLE_JSON.getType().equals(permissionType)) {
                expressionList.add(buildSingleJsonExpression(tableName, tableAlias, columnName, userId));
            }else if (PermissionTypeEnum.JSON_CONTAINS.getType().equals(permissionType)){
                expressionList.add(buildJsonContainsExpression(tableName, tableAlias, columnName, userId));
            }
        });
        return recursionExpression(expressionList);
    }

    /**
     * 递归构建OR条件表达式(jsqlparser中orexpression不支持多参数构建)
     *
     * @param expressionList sql表达式列表
     * @return sql表达式
     */
    private Expression recursionExpression(List<Expression> expressionList) {
        if (CollUtil.isEmpty(expressionList)) {
            return null;
        }
        if (expressionList.size() == 1) {
            return expressionList.get(0);
        }
        Expression leftExpression = expressionList.get(0);
        Expression rightExpression = expressionList.get(1);
        expressionList = expressionList.subList(2, expressionList.size());
        expressionList.add(new OrExpression(leftExpression, rightExpression));
        return recursionExpression(expressionList);
    }

    // ==================== 添加配置 ====================

    public void addDeptColumn(Class<? extends BaseDO> entityClass) {
        addDeptColumn(entityClass, DEPT_COLUMN_NAME);
    }

    public void addDeptColumn(Class<? extends BaseDO> entityClass, String columnName) {
        String tableName = TableInfoHelper.getTableInfo(entityClass).getTableName();
        addDeptColumn(tableName, columnName);
    }

    public void addDeptColumn(String tableName, String columnName) {
        deptColumns.put(tableName, columnName);
        TABLE_NAMES.add(tableName);
    }

    public void addUserColumn(Class<? extends BaseDO> entityClass) {
        addUserColumn(entityClass, USER_COLUMN_NAME, PermissionTypeEnum.FIND_IN_SET.getType(),BooleanEnum.NO.getValue());
    }

    public void addUserColumn(Class<? extends BaseDO> entityClass, String columnName, Integer permissionType) {
        String tableName = TableInfoHelper.getTableInfo(entityClass).getTableName();
        addUserColumn(tableName, columnName, permissionType,BooleanEnum.NO.getValue());
    }
    public void addUserColumn(Class<? extends BaseDO> entityClass, String columnName, Integer permissionType,Integer containLeaderFlag) {
        String tableName = TableInfoHelper.getTableInfo(entityClass).getTableName();
        addUserColumn(tableName, columnName, permissionType,containLeaderFlag);
    }

    public void addUserColumn(String tableName, String columnName, Integer permissionType,Integer containLeaderFlag) {
        List<Tuple> value =new ArrayList<>();
        value.add(new Tuple(columnName, permissionType,containLeaderFlag));
        userColumns.merge(tableName,value, (oldValue, newValue) -> {
            oldValue.addAll(newValue);
            return oldValue;
        });
        TABLE_NAMES.add(tableName);
    }
}
