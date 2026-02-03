package cn.iocoder.yudao.framework.excel.core.util;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.ss.usermodel.*;

/**
 * 自定义单元格写入处理器（合并版）
 * 处理所有单元格的长文本换行和行高调整，包括循环数据的特殊处理
 * 合并了原有的CustomCellWriteHandler和LoopDataCellWriteHandler功能
 * @author 波波
 */
public class CustomCellWriteHandler implements CellWriteHandler {

    /**
     * 默认行高
     */
    private int DEFAULT_HEIGHT = 400;

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        Cell cell = context.getCell();
        if (cell == null) {
            return;
        }
        
        try {
            // 处理模板中已存在的所有单元格
            processAllTemplateCells(cell);
            
        } catch (Exception e) {
            // 异常处理：确保设置默认行高
            try {
                if (cell.getRow() != null) {
                    cell.getRow().setHeight((short) DEFAULT_HEIGHT);
                }
            } catch (Exception ex) {
                // 忽略错误继续处理
            }
        }
    }

    /**
     * 计算文本需要的行数
     * @param content 文本内容
     * @return 行数
     * @author 波波
     */
    private int calculateLineCount(String content) {
        if (content == null || content.isEmpty()) {
            return 1;
        }
        
        // 如果包含换行符，按换行符计算
        if (content.contains("\n")) {
            return Math.max(content.split("\n").length, 1);
        }
        
        // 根据文本长度估算行数，更精确的计算
        if (content.length() <= 30) {
            return 1;
        } else if (content.length() <= 60) {
            return 2;
        } else if (content.length() <= 100) {
            return 3;
        } else if (content.length() <= 150) {
            return 4;
        } else {
            // 长文本按每30个字符一行计算
            int estimatedLines = (int) Math.ceil(content.length() / 30.0);
            return Math.min(estimatedLines, 8); // 限制最大8行
        }
    }

    /**
     * 设置单元格自动换行样式
     * @param cell 单元格
     * @author 波波
     */
    private void setCellWrapText(Cell cell) {
        try {
            Workbook workbook = cell.getRow().getSheet().getWorkbook();
            CellStyle cellStyle = workbook.createCellStyle();
            
            // 如果单元格已有样式，则克隆；否则创建新样式
            if (cell.getCellStyle() != null) {
                cellStyle.cloneStyleFrom(cell.getCellStyle());
            }
            
            // 设置自动换行
            cellStyle.setWrapText(true);
            
            // 设置水平居中对齐
            cellStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            
            // 设置垂直居中对齐
            cellStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            
            // 应用样式到单元格
            cell.setCellStyle(cellStyle);
            
        } catch (Exception e) {
            // 如果设置样式失败，忽略错误继续处理
        }
    }

    /**
     * 处理模板中已存在的所有单元格
     * @param cell 当前单元格
     * @author 波波
     */
    private void processAllTemplateCells(Cell cell) {
        try {
            Sheet sheet = cell.getSheet();
            
            // 遍历所有行和列，处理模板中已存在的单元格
            for (Row row : sheet) {
                if (row == null) continue;
                
                // 确保行有基础行高
                if (row.getHeight() <= 0) {
                    row.setHeight((short) DEFAULT_HEIGHT);
                }
                
                // 遍历行中的所有单元格
                for (Cell templateCell : row) {
                    if (templateCell == null) continue;
                    
                    // 处理字符串类型的单元格
                    if (templateCell.getCellType() == CellType.STRING) {
                        String cellValue = templateCell.getStringCellValue();
                        if (cellValue != null && !cellValue.isEmpty()) {
                            // 设置自动换行样式
                            setCellWrapText(templateCell);
                            
                            // 根据内容长度自动调整行高
                            int lineCount = calculateLineCount(cellValue);
                            int calculatedHeight = Math.max(lineCount * DEFAULT_HEIGHT, DEFAULT_HEIGHT);
                            row.setHeight((short) calculatedHeight);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 忽略错误，继续处理
        }
    }

    /**
     * 设置默认行高
     * @param height 行高值
     * @author 波波
     */
    public void setDefaultHeight(Integer height) {
        if (height != null && height > 0) {
            DEFAULT_HEIGHT = height.intValue();
        } else {
            DEFAULT_HEIGHT = 400; // 设置合理的默认值
        }
    }
}
