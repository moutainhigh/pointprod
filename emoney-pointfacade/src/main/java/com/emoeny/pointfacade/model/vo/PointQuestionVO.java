package com.emoeny.pointfacade.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author lipengcheng
 * @date 2021-05-07
 */
@Data
public class PointQuestionVO {
    private Integer id;
    /**
     * 题目类型（1单选；2多选；3其他）
     */
    private Integer questionType;

    /**
     * 题目内容
     */
    private String questionContent;

    /**
     * 题目选项（”|“分隔）
     */
    private String questionOptions;

    /**
     * 正确选项（用数字表示，多个正确选项用“|”分隔）
     */
    private String questionRightoptions;

    /**
     * 希望展示时间，为空则随机展示
     */
    private Date showTime;
}
