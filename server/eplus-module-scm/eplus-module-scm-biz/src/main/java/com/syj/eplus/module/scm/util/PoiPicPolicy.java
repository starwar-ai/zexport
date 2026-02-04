package com.syj.eplus.module.scm.util;

import com.deepoove.poi.exception.RenderException;
import com.deepoove.poi.policy.AbstractRenderPolicy;
import com.deepoove.poi.render.RenderContext;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Random;

public class PoiPicPolicy extends AbstractRenderPolicy<CustomPictureRenderData> {

    @Override
    protected boolean validate(CustomPictureRenderData data) {
        return (null != data && (null != data.getData() || null != data.getPath()));
    }

    @Override
    protected void afterRender(RenderContext<CustomPictureRenderData> context) {
        clearPlaceholder(context, false);
    }

    @Override
    protected void reThrowException(RenderContext<CustomPictureRenderData> context, Exception e) {
        logger.info("Render picture {}, error: {}",  context.getEleTemplate(),e.getMessage());
        context.getRun().setText(context.getData().getAltMeta(), 0);

    }

    @Override
    public void doRender(RenderContext<CustomPictureRenderData> context) throws Exception {
        Helper.renderPicture(context.getRun(), context.getData());
    }

    public static class Helper {
        public static final int EMU = 9525;

        /**
         * 生成指定长度的随机字符串
         * @param length 字符串长度
         * @return 随机字符串
         */
        private static String generateRandomCode(int length) {
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder result = new StringBuilder(length);
            Random random = new Random();
            for (int i = 0; i < length; i++) {
                result.append(characters.charAt(random.nextInt(characters.length())));
            }
            return result.toString();
        }

        public static void renderPicture(XWPFRun run, CustomPictureRenderData picture) throws Exception {

            int suggestFileType = suggestFileType(picture.getPath());
            InputStream ins = null;
            try {
                ins = null == picture.getData() ? new FileInputStream(picture.getPath())
                        : new ByteArrayInputStream(picture.getData());
                String numberCode = generateRandomCode(4);
                run.addPicture(ins, suggestFileType, "Generated"+numberCode, picture.getWidth() * EMU,
                        picture.getHeight() * EMU);
                CTDrawing drawing = run.getCTR().getDrawingArray(0);
                CTGraphicalObject graphicalobject = drawing.getInlineArray(0).getGraphic();

                //拿到新插入的图片替换添加CTAnchor 设置浮动属性 删除inline属性
                CTAnchor anchor = getAnchorWithGraphic(graphicalobject,
                        Units.toEMU(picture.getWidth()), Units.toEMU(picture.getHeight()),
                        Units.toEMU(picture.getLeftOffset()), Units.toEMU(picture.getTopOffset()));
                //添加浮动属性
                drawing.setAnchorArray(new CTAnchor[]{anchor});
                //删除行内属性
                drawing.removeInline(0);
            }
            finally {
                IOUtils.closeQuietly(ins);
            }
        }

        /**
         * @param ctGraphicalObject 图片数据
         * @param width             宽
         * @param height            高
         * @param leftOffset        水平偏移 left
         * @param topOffset         垂直偏移 top
         * @return
         * @throws Exception
         */
        private static CTAnchor getAnchorWithGraphic(CTGraphicalObject ctGraphicalObject, int width, int height,
                                                     int leftOffset, int topOffset) {
            String anchorXML =
                    "<wp:anchor xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\" "
                            + "simplePos=\"0\" relativeHeight=\"0\" behindDoc=\"0\" locked=\"0\" layoutInCell=\"1\" allowOverlap=\"1\">"
                            + "<wp:simplePos x=\"0\" y=\"0\"/>"
                            + "<wp:positionH relativeFrom=\"column\">"
                            + "<wp:posOffset>" + leftOffset + "</wp:posOffset>"
                            + "</wp:positionH>"
                            + "<wp:positionV relativeFrom=\"paragraph\">"
                            + "<wp:posOffset>" + topOffset + "</wp:posOffset>" +
                            "</wp:positionV>"
                            + "<wp:extent cx=\"" + width + "\" cy=\"" + height + "\"/>"
                            + "<wp:effectExtent l=\"0\" t=\"0\" r=\"0\" b=\"0\"/>"
                            + "<wp:wrapNone/>"
                            + "<wp:docPr id=\"1\" name=\"Drawing 0\" descr=\"G:/11.png\"/><wp:cNvGraphicFramePr/>"
                            + "</wp:anchor>";

            CTDrawing drawing = null;
            try {
                drawing = CTDrawing.Factory.parse(anchorXML);
            } catch (XmlException e) {
                e.printStackTrace();
            }
            CTAnchor anchor = drawing.getAnchorArray(0);
            anchor.setGraphic(ctGraphicalObject);
            return anchor;
        }

        public static int suggestFileType(String imgFile) {
            int format = 0;

            if (imgFile.endsWith(".emf")) format = XWPFDocument.PICTURE_TYPE_EMF;
            else if (imgFile.endsWith(".wmf")) format = XWPFDocument.PICTURE_TYPE_WMF;
            else if (imgFile.endsWith(".pict")) format = XWPFDocument.PICTURE_TYPE_PICT;
            else if (imgFile.endsWith(".jpeg") || imgFile.endsWith(".jpg"))
                format = XWPFDocument.PICTURE_TYPE_JPEG;
            else if (imgFile.endsWith(".png")) format = XWPFDocument.PICTURE_TYPE_PNG;
            else if (imgFile.endsWith(".dib")) format = XWPFDocument.PICTURE_TYPE_DIB;
            else if (imgFile.endsWith(".gif")) format = XWPFDocument.PICTURE_TYPE_GIF;
            else if (imgFile.endsWith(".tiff")) format = XWPFDocument.PICTURE_TYPE_TIFF;
            else if (imgFile.endsWith(".eps")) format = XWPFDocument.PICTURE_TYPE_EPS;
            else if (imgFile.endsWith(".bmp")) format = XWPFDocument.PICTURE_TYPE_BMP;
            else if (imgFile.endsWith(".wpg")) format = XWPFDocument.PICTURE_TYPE_WPG;
            else {
                throw new RenderException("Unsupported picture: " + imgFile
                        + ". Expected emf|wmf|pict|jpeg|png|dib|gif|tiff|eps|bmp|wpg");
            }
            return format;
        }

    }
}

