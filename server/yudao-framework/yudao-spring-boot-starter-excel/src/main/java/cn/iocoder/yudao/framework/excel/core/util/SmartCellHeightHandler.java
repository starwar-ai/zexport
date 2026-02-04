package cn.iocoder.yudao.framework.excel.core.util;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.*;

/**
 * 智能单元格高度处理器
 * 实现Excel填充数据后的自动高度调整功能
 * 
 * @author 系统生成
 * @version 1.0
 */
public class SmartCellHeightHandler implements CellWriteHandler {
    
    /**
     * 默认行高（POI单位，1个单位 ≈ 1/20磅）
     */
    private int defaultRowHeight = 400;
    
    /**
     * 上下边距占总高度的比例
     */
    private static final double PADDING_RATIO = 0.25;
    
    /**
     * 文本高度占总高度的比例
     */
    private static final double TEXT_RATIO = 0.75;
    
    /**
     * 中文字符宽度系数（相对于英文字符）
     */
    private static final double CHINESE_CHAR_WIDTH_FACTOR = 2.0;
    
    /**
     * 英文字符宽度系数
     */
    private static final double ENGLISH_CHAR_WIDTH_FACTOR = 1.0;
    
    /**
     * Excel列宽单位转换系数
     */
    private static final double COLUMN_WIDTH_UNIT_TO_CHARS = 256.0;
    
    /**
     * 最小列宽阈值（如果列宽太小，不调整行高）
     */
    private static final int MIN_COLUMN_WIDTH_THRESHOLD = 768; // 约3个字符宽度
    
    /**
     * 最大行数限制（防止行高过大）
     */
    private static final int MAX_LINES_LIMIT = 15;
    
    /**
     * 已处理的工作表集合
     */
    private final Set<String> processedSheets = new HashSet<>();
    
    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                List<WriteCellData<?>> cellDataList, Cell cell, Head head, 
                                Integer relativeRowIndex, Boolean isHead) {
        if (cell == null || cell.getSheet() == null) {
            return;
        }
        
        Sheet sheet = cell.getSheet();
        String sheetKey = sheet.getSheetName() + "_" + sheet.getWorkbook().hashCode();
        
        // 每个sheet只处理一次
        if (processedSheets.contains(sheetKey)) {
            return;
        }
        processedSheets.add(sheetKey);
        
        try {
            // 处理整个工作表的行高调整
            processSheetRowHeight(sheet);
        } catch (Exception e) {
            // 异常处理，确保不影响Excel生成
            System.err.println("处理行高时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 处理整个工作表的行高调整
     * @param sheet 工作表
     */
    private void processSheetRowHeight(Sheet sheet) {
        // 第一步：获取所有合并单元格信息
        Map<String, CellRangeAddress> mergedRegionsMap = getMergedRegionsMap(sheet);
        Set<String> mergedCellKeys = mergedRegionsMap.keySet();
        
        // 第二步：收集每行的原始高度和需要的高度（不包括合并单元格）
        Map<Integer, RowHeightInfo> rowHeightInfoMap = new HashMap<>();
        
        // 遍历所有行，处理非合并单元格
        for (Row row : sheet) {
            if (row == null) continue;
            
            int rowIndex = row.getRowNum();
            int originalHeight = row.getHeight() > 0 ? row.getHeight() : defaultRowHeight;
            
            RowHeightInfo heightInfo = new RowHeightInfo();
            heightInfo.originalHeight = originalHeight;
            heightInfo.requiredHeight = originalHeight; // 初始值为原始高度
            
            // 处理行内非合并单元格
            for (Cell cell : row) {
                if (cell == null) continue;
                
                String cellKey = getCellKey(cell.getRowIndex(), cell.getColumnIndex());
                
                // 跳过合并单元格的从属单元格
                if (mergedCellKeys.contains(cellKey)) {
                    continue;
                }
                
                // 计算单个单元格所需高度
                int cellRequiredHeight = calculateCellRequiredHeight(cell, originalHeight);
                if (cellRequiredHeight > heightInfo.requiredHeight) {
                    heightInfo.requiredHeight = cellRequiredHeight;
                }
            }
            
            rowHeightInfoMap.put(rowIndex, heightInfo);
        }
        
        // 第三步：处理合并单元格
        processMergedCellsHeight(sheet, mergedRegionsMap, rowHeightInfoMap);
        
        // 第四步：应用最终计算的行高
        applyCalculatedRowHeights(sheet, rowHeightInfoMap);
    }
    
    /**
     * 获取所有合并单元格区域映射
     * @param sheet 工作表
     * @return 合并单元格映射
     */
    private Map<String, CellRangeAddress> getMergedRegionsMap(Sheet sheet) {
        Map<String, CellRangeAddress> map = new HashMap<>();
        
        int mergedRegionCount = sheet.getNumMergedRegions();
        for (int i = 0; i < mergedRegionCount; i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            
            // 为合并区域的每个单元格创建映射
            for (int row = region.getFirstRow(); row <= region.getLastRow(); row++) {
                for (int col = region.getFirstColumn(); col <= region.getLastColumn(); col++) {
                    map.put(getCellKey(row, col), region);
                }
            }
        }
        
        return map;
    }
    
    /**
     * 处理合并单元格的高度
     * @param sheet 工作表
     * @param mergedRegionsMap 合并单元格映射
     * @param rowHeightInfoMap 行高信息映射
     */
    private void processMergedCellsHeight(Sheet sheet, Map<String, CellRangeAddress> mergedRegionsMap,
                                        Map<Integer, RowHeightInfo> rowHeightInfoMap) {
        Set<String> processedRegions = new HashSet<>();
        
        for (CellRangeAddress region : mergedRegionsMap.values()) {
            String regionKey = getRegionKey(region);
            
            if (processedRegions.contains(regionKey)) {
                continue;
            }
            processedRegions.add(regionKey);
            
            // 获取合并单元格的主单元格（第一个单元格）
            Row firstRow = sheet.getRow(region.getFirstRow());
            if (firstRow == null) continue;
            
            Cell mergedCell = firstRow.getCell(region.getFirstColumn());
            if (mergedCell == null) continue;
            
            // 计算合并区域跨越的行数
            int rowSpan = region.getLastRow() - region.getFirstRow() + 1;
            
            // 获取原始行高（单行高度，不是合并后的总高度）
            int originalSingleRowHeight = firstRow.getHeight() > 0 ? firstRow.getHeight() : defaultRowHeight;
            
            // 计算这些行当前的高度总和（作为最小高度）
            int currentTotalHeight = 0;
            for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
                RowHeightInfo info = rowHeightInfoMap.get(i);
                if (info != null) {
                    currentTotalHeight += info.requiredHeight;
                }
            }
            
            // 计算合并单元格实际需要的高度（使用单行原始高度）
            int mergedCellRequiredHeight = calculateCellRequiredHeight(mergedCell, originalSingleRowHeight);
            
            // 如果合并单元格需要更大的高度，则分配差值
            if (mergedCellRequiredHeight > currentTotalHeight) {
                int heightDiff = mergedCellRequiredHeight - currentTotalHeight;
                distributeHeightDifference(region, heightDiff, rowHeightInfoMap);
            }
        }
    }
    
    /**
     * 分配高度差值到合并区域的各行
     * @param region 合并区域
     * @param heightDiff 需要分配的高度差值
     * @param rowHeightInfoMap 行高信息映射
     */
    private void distributeHeightDifference(CellRangeAddress region, int heightDiff,
                                          Map<Integer, RowHeightInfo> rowHeightInfoMap) {
        int rowSpan = region.getLastRow() - region.getFirstRow() + 1;
        int avgIncrease = heightDiff / rowSpan;
        int remainder = heightDiff % rowSpan;
        
        for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
            RowHeightInfo info = rowHeightInfoMap.get(i);
            if (info != null) {
                int increase = avgIncrease;
                
                // 余数分配给前几行
                if (i - region.getFirstRow() < remainder) {
                    increase += 1;
                }
                
                info.requiredHeight += increase;
            }
        }
    }
    
    /**
     * 计算单个单元格所需的高度
     * @param cell 单元格
     * @param originalHeight 原始行高
     * @return 所需高度
     */
    private int calculateCellRequiredHeight(Cell cell, int originalHeight) {
        if (cell == null || cell.getCellType() != CellType.STRING) {
            return originalHeight;
        }
        
        String content = cell.getStringCellValue();
        if (content == null || content.trim().isEmpty()) {
            return originalHeight;
        }
        
        try {
            // 获取单元格列宽
            int columnWidth = getActualColumnWidth(cell);
            
            // 如果列宽太窄，保持原高度
            if (columnWidth < MIN_COLUMN_WIDTH_THRESHOLD) {
                return originalHeight;
            }
            
            // 计算需要的行数
            int requiredLines = calculateRequiredLines(content, columnWidth);
            
            // 如果只需要1行，保持原高度
            if (requiredLines <= 1) {
                return originalHeight;
            }
            
            // 计算新的高度
            return calculateHeightByLines(requiredLines, originalHeight);
            
        } catch (Exception e) {
            return originalHeight;
        }
    }
    
    /**
     * 获取单元格实际列宽（考虑合并单元格）
     * @param cell 单元格
     * @return 列宽
     */
    private int getActualColumnWidth(Cell cell) {
        Sheet sheet = cell.getSheet();
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        
        // 检查是否为合并单元格
        int numMergedRegions = sheet.getNumMergedRegions();
        for (int i = 0; i < numMergedRegions; i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
            
            if (mergedRegion.isInRange(rowIndex, columnIndex)) {
                // 计算合并单元格的总宽度
                int totalWidth = 0;
                for (int col = mergedRegion.getFirstColumn(); col <= mergedRegion.getLastColumn(); col++) {
                    totalWidth += sheet.getColumnWidth(col);
                }
                return totalWidth;
            }
        }
        
        // 普通单元格，返回列宽
        return sheet.getColumnWidth(columnIndex);
    }
    
    /**
     * 计算内容需要的行数
     * @param content 内容
     * @param columnWidth 列宽
     * @return 行数
     */
    private int calculateRequiredLines(String content, int columnWidth) {
        if (content == null || content.isEmpty()) {
            return 1;
        }
        
        // 处理显式换行符
        if (content.contains("\n")) {
            String[] lines = content.split("\n");
            int totalLines = 0;
            for (String line : lines) {
                totalLines += calculateRequiredLinesForSingleLine(line, columnWidth);
            }
            return Math.min(totalLines, MAX_LINES_LIMIT);
        }
        
        return Math.min(calculateRequiredLinesForSingleLine(content, columnWidth), MAX_LINES_LIMIT);
    }
    
    /**
     * 计算单行内容需要的行数
     * @param content 单行内容
     * @param columnWidth 列宽
     * @return 行数
     */
    private int calculateRequiredLinesForSingleLine(String content, int columnWidth) {
        if (content == null || content.trim().isEmpty()) {
            return 1;
        }
        
        // 转换为可容纳的字符数（预留10%边距）
        double maxCharsInColumn = (columnWidth / COLUMN_WIDTH_UNIT_TO_CHARS) * 0.9;
        
        // 计算内容等效宽度
        double contentWidth = calculateContentWidth(content);
        
        // 计算需要的行数
        return (int) Math.ceil(contentWidth / maxCharsInColumn);
    }
    
    /**
     * 计算内容等效宽度（区分中英文）
     * @param content 内容
     * @return 等效宽度
     */
    private double calculateContentWidth(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        
        double width = 0;
        for (char c : content.toCharArray()) {
            if (isChinese(c)) {
                width += CHINESE_CHAR_WIDTH_FACTOR;
            } else {
                width += ENGLISH_CHAR_WIDTH_FACTOR;
            }
        }
        return width;
    }
    
    /**
     * 判断字符是否为中文
     * @param c 字符
     * @return 是否为中文
     */
    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }
    
    /**
     * 根据行数计算高度（考虑原有边距）
     * @param lines 行数
     * @param originalHeight 原始高度
     * @return 计算后的高度
     */
    private int calculateHeightByLines(int lines, int originalHeight) {
        // 从原始高度中提取边距和单行高度
        int padding = (int) (originalHeight * PADDING_RATIO);
        int singleLineHeight = (int) (originalHeight * TEXT_RATIO);
        
        // 新高度 = 边距 + (行数 × 单行高度)
        return padding + (lines * singleLineHeight);
    }
    
    /**
     * 应用计算出的行高
     * @param sheet 工作表
     * @param rowHeightInfoMap 行高信息映射
     */
    private void applyCalculatedRowHeights(Sheet sheet, Map<Integer, RowHeightInfo> rowHeightInfoMap) {
        for (Map.Entry<Integer, RowHeightInfo> entry : rowHeightInfoMap.entrySet()) {
            int rowIndex = entry.getKey();
            RowHeightInfo heightInfo = entry.getValue();
            
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                // 确保行高在合理范围内（最大不超过 5000）
                int finalHeight = Math.min(heightInfo.requiredHeight, 5000);
                row.setHeight((short) finalHeight);
            }
        }
    }
    
    /**
     * 获取单元格键
     * @param row 行号
     * @param col 列号
     * @return 单元格键
     */
    private String getCellKey(int row, int col) {
        return row + "," + col;
    }
    
    /**
     * 获取合并区域键
     * @param region 合并区域
     * @return 区域键
     */
    private String getRegionKey(CellRangeAddress region) {
        return region.getFirstRow() + "," + region.getFirstColumn() + 
               "-" + region.getLastRow() + "," + region.getLastColumn();
    }
    
    /**
     * 设置默认行高
     * @param height 默认行高
     */
    public void setDefaultRowHeight(int height) {
        if (height > 0) {
            this.defaultRowHeight = height;
        }
    }
    
    /**
     * 行高信息类
     */
    private static class RowHeightInfo {
        int originalHeight;   // 原始高度
        int requiredHeight;   // 所需高度
    }
}