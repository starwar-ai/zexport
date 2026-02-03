package com.syj.eplus.module.scm.policy;

import com.deepoove.poi.data.ParagraphRenderData;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.Rows;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.deepoove.poi.policy.TableRenderPolicy;
import com.deepoove.poi.util.TableTools;
import com.syj.eplus.module.scm.entity.Skus;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.ArrayList;
import java.util.List;

//该方法没有使用，因为使用了其他方法，方法没有调整完，但是代码保留
public class DetailTablePolicy extends DynamicTableRenderPolicy {

    // 货物明细填充数据所在行数
    int skusStartRow = 1;

    @Override
    public void render(XWPFTable table, Object data) throws Exception {
        if (null == data) return;
        List<Skus> skus = (List<Skus>) data;
        if (null != skus) {
            long count = table.getRow(skusStartRow).getTableCells().stream().count();
            table.removeRow(skusStartRow);
            // 循环插入行
            skus.forEach( it -> {
                RowRenderData rowRenderData0 = Rows.create(it.cskuCode, it.description, null, null, null);
                List<ParagraphRenderData> paragraphs = new ArrayList<>();
                ParagraphRenderData paragraphRenderData = new ParagraphRenderData();
                PictureRenderData pictureRenderData = new PictureRenderData() {
                    @Override
                    public byte[] readPictureData() {
                        try {
                            return it.picture;
                        } catch (Exception e) {
                            logger.error("生成报表图片时报错：" + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                };
                paragraphRenderData.addPicture(pictureRenderData);
                paragraphs.add(paragraphRenderData);
                rowRenderData0.getCells().get(2).setParagraphs(paragraphs);

                RowRenderData rowRenderData1 = Rows.create(it.cskuCode, it.description, it.quantity, it.withTaxPrice, it.amount);

                XWPFTableRow row0 = table.insertNewTableRow(skusStartRow);
                for (int j = 0; j < count; j++) row0.createCell();
                XWPFTableRow row1 = table.insertNewTableRow(skusStartRow + 1);
                for (int j = 0; j < count; j++) row1.createCell();

                // 合并单元格
                TableTools.mergeCellsHorizonal(table, skusStartRow, 2, 4);
                // 单行渲染
                try {
                    TableRenderPolicy.Helper.renderRow(table.getRow(skusStartRow), rowRenderData0);
                    TableRenderPolicy.Helper.renderRow(table.getRow(skusStartRow + 1), rowRenderData1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}