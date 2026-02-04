package cn.iocoder.yudao.framework.excel.core.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.ImageData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.Cleanup;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Excel 工具类
 *
 * @author 芋道源码
 */
public class ExcelUtils {

    /**
     * 公式计算处理器
     */
    private static class FormulaEvaluateHandler implements CellWriteHandler {
        @Override
        public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                   List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
            // 获取工作簿
            Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
            // 设置工作簿为自动计算模式
            workbook.setForceFormulaRecalculation(true);
            
            // 如果是公式单元格，强制计算
            if (cell.getCellType() == CellType.FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                evaluator.evaluateFormulaCell(cell);
            }
        }
    }

    /**
     * 将列表以 Excel 响应给前端
     *
     * @param response 响应
     * @param filename 文件名
     * @param sheetName Excel sheet 名
     * @param head Excel head 头
     * @param data 数据列表哦
     * @param <T> 泛型，保证 head 和 data 类型的一致性
     * @throws IOException 写入失败的情况
     */
    public static <T> void write(HttpServletResponse response, String filename, String sheetName,
                                 Class<T> head, List<T> data) throws IOException {
        // 输出 Excel
        EasyExcel.write(response.getOutputStream(), head)
                .autoCloseStream(false) // 不要自动关闭，交给 Servlet 自己处理
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 基于 column 长度，自动适配。最大 255 宽度
                 .registerConverter(new LongStringConverter()) // 避免 Long 类型丢失精度
                 //.registerConverter(new BigDecimalStringConverter()) // 避免 Long 类型丢失精度
                 //.registerWriteHandler(new CellWrapHandler()) // 添加自动换行处理
                .sheet(sheetName)
                .doWrite(data);
        // 设置 header 和 contentType。写在最后的原因是，避免报错时，响应 contentType 已经被修改了
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
    }

    /**
     * 将列表以 Excel 响应给前端（支持自定义Handler）
     *
     * @param response 响应
     * @param templateFileInputStream 模板文件
     * @param params 要替换的数据信息
     * @param fileName 文件名
     * @param tableData 输出列表信息
     * @param tableParams 需要替换列表信息
     * @param rowHeight 行高
     * @param customHandlers 自定义CellWriteHandler列表
     * @throws IOException 写入失败的情况
     */
    public static <T> void writeByTemplate(HttpServletResponse response, ByteArrayInputStream templateFileInputStream,
            HashMap<String, Object> params, String fileName, List<T> tableData, HashMap<String, Object> tableParams, 
            Integer rowHeight, List<CellWriteHandler> customHandlers) throws IOException {
        // 设置行高
//        CustomCellWriteHeightConfig customCellWriteHeightConfig = new CustomCellWriteHeightConfig();
//        if (rowHeight != null && rowHeight > 0) {
//            customCellWriteHeightConfig.setDefaultHight(rowHeight);
//        }
        
        // 创建ExcelWriter，使用模板
        ExcelWriterBuilder builder = EasyExcel.write(response.getOutputStream())
                .withTemplate(templateFileInputStream)
                .autoCloseStream(false) // 不要自动关闭，交给 Servlet 自己处理
                .registerConverter(new LongStringConverter()) // 避免 Long 类型丢失精度
                //.registerConverter(new BigDecimalStringConverter()) // 避免 BigDecimal 类型丢失精度
                //.registerWriteHandler(customCellWriteHeightConfig) // 只设置行高
                .registerWriteHandler(new FormulaEvaluateHandler()) // 注册公式计算处理器
                //.registerWriteHandler(new CellWrapHandler()) // 添加自动换行处理
                .registerConverter(new ListConverter());
    
        SmartCellHeightHandler smartCellHeightHandler = new SmartCellHeightHandler();
        // 设置行高，如果为null或0则使用默认值600
        if (rowHeight != null && rowHeight > 0) {
            smartCellHeightHandler.setDefaultRowHeight(rowHeight);
        }
        else {
            smartCellHeightHandler.setDefaultRowHeight(600); // 使用默认值
        }
        // 注册新的智能高度处理器
        builder.registerWriteHandler(smartCellHeightHandler);

        // 注册自定义Handler
        if (customHandlers != null) {
            for (CellWriteHandler handler : customHandlers) {
                builder.registerWriteHandler(handler);
            }
        }
        
        ExcelWriter excelWriter = builder.build();
                
        // 写入数据
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        
        // 填充列表数据
        if (tableData != null && !tableData.isEmpty()) {
            excelWriter.fill(tableData, fillConfig, writeSheet);
        }
        
        // 填充动态表格数据
        if (tableParams != null && !tableParams.isEmpty()) {
            tableParams.keySet().forEach(key -> {
                List<T> data = (List<T>) tableParams.get(key);
                excelWriter.fill(new FillWrapper(key, data), writeSheet);
            });
        }
        
        // 填充其他参数
        if (params != null && !params.isEmpty()) {
            excelWriter.fill(params, fillConfig, writeSheet);
        }
        
        // 设置响应头
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        
        // 完成写入
        excelWriter.finish();
    }

    /**
     * 将列表以 Excel 响应给前端（保持原有兼容性）
     *
     * @param response 响应
     * @param templateFileInputStream 模板文件
     * @param params 要替换的数据信息
     * @param fileName 文件名
     * @param tableData 输出列表信息
     * @param tableParams 需要替换列表信息
     * @param rowHeight 行高
     * @throws IOException 写入失败的情况
     */
    public static <T> void writeByTemplate(HttpServletResponse response, ByteArrayInputStream templateFileInputStream,
            HashMap<String, Object> params, String fileName, List<T> tableData, HashMap<String, Object> tableParams, Integer rowHeight) throws IOException {
        writeByTemplate(response, templateFileInputStream, params, fileName, tableData, tableParams, rowHeight, null);
    }

    public static <T> List<T> read(MultipartFile file, Class<T> head, int headRowNumber) throws IOException {
        @Cleanup
        InputStream inputStream = file.getInputStream();
        return EasyExcel.read(inputStream, head, null)
               .headRowNumber(headRowNumber)
                .autoCloseStream(false)  // 不要自动关闭，交给 Servlet 自己处理
                .doReadAllSync();
    }

    public static <T> List<T> read(MultipartFile file, Class<T> head, int headRowNumber,Integer sheetNum) throws IOException {
        @Cleanup
        InputStream inputStream = file.getInputStream();
        return EasyExcel.read(inputStream, head, null)
                .headRowNumber(headRowNumber)
                .autoCloseStream(false)
                .sheet(sheetNum)
                .doReadSync();
    }

    /**
     * 单元格自动换行处理器
     */
    private static class CellWrapHandler implements CellWriteHandler {
        @Override
        public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                         WriteCellData<?> cellData, Cell cell, Head head, Integer relativeRowIndex,
                                         Boolean isHead) {
            // 获取单元格样式
            CellStyle cellStyle = cell.getCellStyle();
            // 设置自动换行
            cellStyle.setWrapText(true);
            // 应用样式
            cell.setCellStyle(cellStyle);
        }
    }


    //参数依次为图片字节，图片宽度(像素)，图片高度，行高(厘米，多个单元格合并后的值)，列宽(厘米，多个单元格合并后的值),行偏移量，列偏移量
    public static WriteCellData<Void> imageCells(byte[] bytes,Double imageWidth,Double imageHight,Double rowLength,Double columLength,Integer lastRowIndex,Integer lastColumnIndex) throws IOException {
        //等比例缩小图片，直到图片能放在单元格下，每次缩小20%
        Integer top = 0;
        Integer left = 0;
        //厘米转换成像素，按实际需求转换
        rowLength = rowLength*28;
        columLength = columLength*28;
        while (true){
            if(imageHight <= rowLength && imageWidth <= columLength){
                //计算边框值
                top = Math.toIntExact(Math.round((rowLength - imageHight)/2));
                left = Math.toIntExact(Math.round((columLength - imageWidth)/2));
                break;
            }else {
                imageHight = imageHight*0.8;
                imageWidth = imageWidth*0.8;
            }
        }
        WriteCellData<Void> writeCellData = new WriteCellData<>();
        // 这里可以设置为 EMPTY 则代表不需要其他数据了
        // 可以放入多个图片，目前只放一张
        List<ImageData> imageDataList = new ArrayList<>();
        writeCellData.setImageDataList(imageDataList);
        ImageData imageData = new ImageData();
        imageDataList.add(imageData);
        // 设置图片
        imageData.setImage(bytes);
        // 上右下左需要留空，通过这种方式调整图片大小，单位为像素
        imageData.setTop(top);
        imageData.setRight(left);
        imageData.setBottom(top);
        imageData.setLeft(left);

        //以下四个属性分别为设置单元格偏移量，因为图片可能占据多个单元格（合并单元格）
        // 这里我以左上角单元格为起始，所以FirstRowIndex和FirstColumnIndex默认为0
        // 向右增加一格则设置LastColumnIndex为1，
        // 向下增加一格设置LastRowIndex属性为1，
        imageData.setRelativeFirstRowIndex(0);
        imageData.setRelativeFirstColumnIndex(0);
        imageData.setRelativeLastRowIndex(lastRowIndex);
        imageData.setRelativeLastColumnIndex(lastColumnIndex);
        return writeCellData;
    }
}
