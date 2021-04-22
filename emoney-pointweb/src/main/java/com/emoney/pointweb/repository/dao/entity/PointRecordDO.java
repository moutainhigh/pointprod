package com.emoney.pointweb.repository.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@ToString
@Document(indexName = "pointrecord", createIndex = true, shards = 5, replicas = 1)
public class PointRecordDO {
    @Id
    @Field(type = FieldType.Long)
    private Long id;
    @Field(type = FieldType.Long)
    private Long uid;
    @Field(type = FieldType.Long)
    private Long taskId;
    @Field(type = FieldType.Integer)
    private Integer platform;
    @Field(type = FieldType.Float)
    private Float taskPoint;
    @Field(type = FieldType.Keyword)
    private String taskName;
    @Field(type = FieldType.Integer)
    private Integer pointStatus;
    @Field(type = FieldType.Keyword)
    private String pid;
    @Field(type = FieldType.Keyword)
    private String emNo;
    @Field(type = FieldType.Boolean)
    private Boolean isDailytask;
    @Field(type = FieldType.Keyword)
    private String subId;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expirationTime;
    @Field(type = FieldType.Float)
    private Float leftPoint;
    @Field(type = FieldType.Boolean)
    private Boolean isValid;
    @Field(type = FieldType.Boolean)
    private Boolean isDirectional;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @Field(type = FieldType.Keyword)
    private String createBy;
    @Field(type = FieldType.Keyword)
    private String updateBy;
    @Field(type = FieldType.Keyword)
    private String remark;
}

