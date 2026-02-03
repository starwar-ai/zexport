package cn.iocoder.yudao.framework.excel.core.util;

import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.util.ObjectUtils;

import java.util.Iterator;

public class CustomCellWriteHeightConfig extends AbstractRowHeightStyleStrategy {

    /**
     * 默认高度
     */
    private Integer DEFAULT_HEIGHT = 0;

    @Override
    protected void setHeadColumnHeight(Row row, int relativeRowIndex) {

    }

    @Override
    protected void setContentColumnHeight(Row row, int relativeRowIndex) {
        Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator();
        if (!cellIterator.hasNext()) {
            return;
        }
        // 默认为 1 行高度
        int maxHeight = 1;
//        while (cellIterator.hasNext()) {
//            Cell cell = cellIterator.next();
//            if (cell.getCellType() == CellType.STRING) {
//                var value = insertLineBreaks(cell.getStringCellValue());
//                cell.setCellValue(value);
//                if (value.contains("\n")) {
//                    int length = value.split("\n").length;
//                    if(maxHeight<=3){
//                        maxHeight = Math.max(maxHeight, length);
//                    }
//                }
//            }
//        }
        row.setHeight((short) (maxHeight * DEFAULT_HEIGHT));
    }

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (context.getHead() != null) {
            if(ObjectUtils.isEmpty(context.getRelativeRowIndex())){
                return;
            }
            if (Boolean.TRUE.equals(context.getHead())) {
                this.setHeadColumnHeight(context.getRow(), context.getRelativeRowIndex());
            } else {
                this.setContentColumnHeight(context.getRow(), context.getRelativeRowIndex());
            }
        }
    }

    public  String insertLineBreaks(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        int length = input.length();
        for (int i = 0; i < length; i += 100) {
            int endIndex = Math.min(i + 100, length);
            result.append(input, i, endIndex);
            if (endIndex < length) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    public void setDefaultHight(Integer height){
        if(height!=null){
            DEFAULT_HEIGHT = height;
        }else{
            DEFAULT_HEIGHT = 500;
        }
    }
}


