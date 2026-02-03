package db.migration.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class StatementUtil {
    protected static final Logger logger = LoggerFactory.getLogger(StatementUtil.class);

    private static final String querySql = "SELECT column_name FROM information_schema.columns WHERE table_schema = 'foreign_trade' and table_name = '%s' and column_name = '%s'";

    private static final String addColumnSql = "ALTER TABLE `foreign_trade`.`%s` ADD COLUMN `%s` %s AFTER `%s`,DROP PRIMARY KEY, ADD PRIMARY KEY (`id`) USING BTREE";

    private static final String updateColumnSql = "ALTER TABLE `foreign_trade`.`%s` CHANGE %s %s %s,DROP PRIMARY KEY, ADD PRIMARY KEY (`id`) USING BTREE";
    private static final String dropColumnSql = "ALTER TABLE `foreign_trade`.`%s` drop %s,DROP PRIMARY KEY, ADD PRIMARY KEY (`id`) USING BTREE";

    public static void addColumn(Connection connection, String tableName, String columnName, String typeAndComment, String afterColumnName) {
        if (Objects.isNull(connection) || !StringUtils.hasText(tableName) || !StringUtils.hasText(columnName)||!StringUtils.hasText(typeAndComment)) {
            return;
        }
        String sql = String.format(addColumnSql, tableName, columnName, typeAndComment, afterColumnName);
        Statement queryStat = null;
        Statement executeStat = null;
        try{
             queryStat = connection.createStatement();
            var frontShippingMark = queryStat.executeQuery(String.format(querySql, tableName, columnName));
            if (!frontShippingMark.next()) {
                executeStat = connection.createStatement();
                executeStat.execute(sql);
            }
        } catch (SQLException e) {
            logger.warn("[addColumn]通过statement对象执行sql语句出错sql-{}", sql);
            logger.warn("异常原因：", e);
        }finally {
            if (Objects.nonNull(queryStat)){
                try {
                    queryStat.close();
                } catch (SQLException e) {
                    logger.warn("[addColumn]queryStat资源释放异常");
                }
            }
            if (Objects.nonNull(executeStat)){
                try {
                    executeStat.close();
                } catch (SQLException e) {
                    logger.warn("[addColumn]executeStat资源释放异常");
                }
            }
        }
    }

    public static void addColumn(Connection connection, String tableName, String columnName, String typeAndComment) {
        addColumn(connection, tableName, columnName, typeAndComment, "id");
    }

    public static void changeColumn(Connection connection, String tableName, String oldColumnName, String newColumnName, String typeAndComment) {
        if (Objects.isNull(connection) || !StringUtils.hasText(tableName) || !StringUtils.hasText(oldColumnName) || !StringUtils.hasText(newColumnName)||!StringUtils.hasText(typeAndComment)) {
            return;
        }
        String sql = String.format(updateColumnSql, tableName, oldColumnName, newColumnName, typeAndComment);
        Statement oldQueryStat = null;
        Statement newQueryStat = null;
        Statement updateStat = null;
        try {
            oldQueryStat = connection.createStatement();
            newQueryStat = connection.createStatement();
            updateStat = connection.createStatement();
            var oldFrontShippingMark = oldQueryStat.executeQuery(String.format(querySql, tableName, oldColumnName));
            var newFrontShippingMark = newQueryStat.executeQuery(String.format(querySql, tableName, newColumnName));
            boolean condition;
            if (oldColumnName.equals(newColumnName)) {
                condition = oldFrontShippingMark.next();
            } else {
                condition = oldFrontShippingMark.next() && !newFrontShippingMark.next();
            }
            if (condition) {
                updateStat.execute(sql);
            }
        } catch (SQLException e) {
            logger.warn("[changeColumn]通过statement对象执行sql语句出错 sql-{}", sql);
        }finally {
            if (Objects.nonNull(oldQueryStat)){
                try {
                    oldQueryStat.close();
                } catch (SQLException e) {
                    logger.warn("[changeColumn]oldQueryStat资源释放异常");
                }
            }
            if (Objects.nonNull(newQueryStat)){
                try {
                    newQueryStat.close();
                } catch (SQLException e) {
                    logger.warn("[changeColumn]newQueryStat资源释放异常");
                }
            }
            if (Objects.nonNull(updateStat)){
                try {
                    updateStat.close();
                } catch (SQLException e) {
                    logger.warn("[changeColumn]updateStat资源释放异常");
                }
            }
        }
    }

    public static void dropColumn(Connection connection, String tableName, String columnName) {
        if (Objects.isNull(connection) || !StringUtils.hasText(tableName) || !StringUtils.hasText(columnName)) {
            return;
        }
        String sql = String.format(dropColumnSql, tableName, columnName);
        Statement queryStat = null;
        Statement updateStat = null;
        try {
            queryStat = connection.createStatement();
            updateStat = connection.createStatement();
            var frontShippingMark = queryStat.executeQuery(String.format(querySql, tableName, columnName));
            if (frontShippingMark.next()) {
                updateStat.execute(sql);
            }
        } catch (SQLException e) {
            logger.warn("[dropColumn]通过statement对象执行sql语句出错sql-{}", sql);
        }finally {
            if (Objects.nonNull(queryStat)){
                try {
                    queryStat.close();
                } catch (SQLException e) {
                    logger.warn("[dropColumn]queryStat资源释放异常");
                }
            }
            if (Objects.nonNull(updateStat)){
                try {
                    updateStat.close();
                } catch (SQLException e) {
                    logger.warn("[dropColumn]updateStat资源释放异常");
                }
            }
        }

    }
}
