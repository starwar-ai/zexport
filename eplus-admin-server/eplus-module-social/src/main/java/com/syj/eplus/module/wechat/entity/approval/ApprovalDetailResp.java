package com.syj.eplus.module.wechat.entity.approval;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.syj.eplus.module.wechat.entity.common.WechatResp;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Description：获取审批单号详情响应实体
 * @Author：du
 * @Date：2024/3/13 21:42
 */
@Data
@ToString
public class ApprovalDetailResp extends WechatResp {
    @JsonProperty("info")
    private Info info;

    @Data
    public static class Info {
        @JsonProperty("sp_no")
        private String spNo;

        @JsonProperty(value = "sp_name")
        private String spName;

        @JsonProperty("sp_status")
        private int spStatus;

        @JsonProperty("template_id")
        private String templateId;

        @JsonProperty("apply_time")
        private long applyTime;

        @JsonProperty("applyer")
        private Applyer applyer;

        @JsonProperty("sp_record")
        private List<SpRecord> spRecord;

        @JsonProperty("notifyer")
        private List<Object> notifyer; // 根据实际情况转换为合适的类型

        @JsonProperty("apply_data")
        private ApplyData applyData;

        @JsonProperty("comments")
        private List<Object> comments; // 根据实际情况转换为合适的类型

        @Data
        public static class Applyer {
            @JsonProperty("userid")
            private String userid;

            @JsonProperty("partyid")
            private String partyid;
        }

        @Data
        public static class SpRecord {
            @JsonProperty("sp_status")
            private int spStatus;

            @JsonProperty("approverattr")
            private int approverattr;

            @JsonProperty("details")
            private List<Detail> details;

            @Data
            public static class Detail {
                @JsonProperty("approver")
                private Approver approver;

                @JsonProperty("speech")
                private String speech;

                @JsonProperty("sp_status")
                private int spStatus;

                @JsonProperty("sptime")
                private long sptime;

                @JsonProperty("media_id")
                private List<String> mediaId; // 假设是字符串列表，根据实际需求调整

                @Data
                public static class Approver {
                    @JsonProperty("userid")
                    private String userid;
                }
            }
        }

        @Data
        public static class ApplyData {
            @JsonProperty("contents")
            private List<Content> contents;

            @Data
            public static class Content {
                @JsonProperty("control")
                private String control;

                @JsonProperty("id")
                private String id;

                @JsonProperty("title")
                private List<TitleItem> title;

                @JsonProperty("value")
                private Value value;

                @JsonProperty("display")
                private int display;

                @JsonProperty("require")
                private int require;

                @Data
                public static class TitleItem {
                    @JsonProperty("text")
                    private String text;

                    @JsonProperty("lang")
                    private String lang;
                }

                @Data
                public static class Value {
                    @JsonProperty("text")
                    private String text;

                    @JsonProperty("tips")
                    private List<Object> tips;

                    @JsonProperty("members")
                    private List<member> members;
                    // 其他各种列表属性

                    @JsonProperty("attendance")
                    private Attendance attendance;

                    @JsonProperty("date")
                    private Date date;

                    @JsonProperty("new_number")
                    private Integer number;

                    @JsonProperty("selector")
                    private Selector selector;

                    @JsonProperty("children")
                    private List<Children> children;

                    @JsonProperty("new_money")
                    private String money;

                    @JsonProperty("formula")
                    private Formula formula;

                    @JsonProperty("related_approval")
                    private List<RelatedApproval> relatedApproval;

                    @Data
                    public static class RelatedApproval {
                        @JsonProperty("sp_no")
                        private String spNo;
                    }
                    @Data
                    public static class Formula{
                        @JsonProperty("value")
                        private String value;
                    }
                    @Data
                    public static class Children{
                        @JsonProperty("list")
                        private List<Content> childList;
                    }
                    @Data
                    public static class Selector{
                        @JsonProperty("type")
                        private String type;

                        @JsonProperty("options")
                        private List<Options> options;

                        @Data
                        public static class Options{
                            @JsonProperty("key")
                            private String key;

                            @JsonProperty("value")
                            private List<OptValue> value;

                            @Data
                            public static class OptValue{
                                @JsonProperty("text")
                                private String text;

                                @JsonProperty("lang")
                                private String lang;
                            }
                        }
                    }

                    @Data
                    public static class Date{
                        @JsonProperty("type")
                        private String type;

                        @JsonProperty("s_timestamp")
                        private Long timeStamp;
                    }
                    @Data
                    public static class Attendance {
                        @JsonProperty("date_range")
                        private DateRange dateRange;

                        @JsonProperty("type")
                        private int type;

                        @JsonProperty("slice_info")
                        private SliceInfo sliceInfo;

                        @Data
                        public static class DateRange {
                            @JsonProperty("type")
                            private String type;

                            @JsonProperty("new_begin")
                            private long newBegin;

                            @JsonProperty("new_end")
                            private long newEnd;

                            @JsonProperty("new_duration")
                            private long newDuration;
                        }

                        @Data
                        public static class SliceInfo {
                            @JsonProperty("day_items")
                            private List<DayItem> dayItems;

                            @JsonProperty("state")
                            private int state;

                            @JsonProperty("duration")
                            private long duration;

                            @Data
                            public static class DayItem {
                                @JsonProperty("daytime")
                                private long daytime;

                                @JsonProperty("time_sections")
                                private List<Object> timeSections;

                                @JsonProperty("duration")
                                private long duration;
                            }
                        }
                    }

                    @JsonProperty("files")
                    private List<FileItem> files;

                    @Data
                    public static class FileItem {
                        @JsonProperty("file_id")
                        private String fileId;
                    }

                    @Data
                    public static class member {
                        @JsonProperty("name")
                        private String name;

                        @JsonProperty("userid")
                        private String userid;
                    }
                }
            }
        }
    }
}
