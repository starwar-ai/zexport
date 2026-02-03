package cn.iocoder.yudao.framework.excel.core.util;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.AbstractVerticalCellStyleStrategy;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class CustomVerticalCellStyleStrategy extends AbstractVerticalCellStyleStrategy {

    // 重写定义表头样式的方法
    @Override
    protected WriteCellStyle headCellStyle(Head head) {
        WriteCellStyle writeCellStyle = new WriteCellStyle();
//        writeCellStyle.setFillBackgroundColor(IndexedColors.RED.getIndex());
//        WriteFont writeFont = new WriteFont();
//        writeFont.setColor(IndexedColors.RED.getIndex());
//        writeFont.setBold(false);
//        writeFont.setFontHeightInPoints(Short.valueOf((short)15));
//        writeCellStyle.setWriteFont(writeFont);
        return writeCellStyle;
    }

    // 重写定义内容部分样式的方法
    @Override
    protected WriteCellStyle contentCellStyle(Head head) {
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        writeCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return writeCellStyle;
    }

}

