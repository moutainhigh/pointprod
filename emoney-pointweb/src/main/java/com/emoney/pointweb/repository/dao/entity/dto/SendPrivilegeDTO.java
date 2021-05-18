package com.emoney.pointweb.repository.dao.entity.dto;

import lombok.Data;

@Data
public class SendPrivilegeDTO {
    //赠送来源ID（a001:物流，a002：官网，a003：智投，a004：移动，a005：好股，a006：机构）
    private String AppId;
    //赋权政策（P1001：常规政策，P1002：促销政策，P1003：系统配置）
    private String GrantPolicy;
    //赋权类别
    private String GrantCategory;
    //OA提案ID
    private String OAProposalID;
    //OA提案名称
    private String OAProposalName;
    //申请原因
    private String Reason;
    //是否发送短信（1：是，0：否）
    private Integer IsSendMobile;
    //成本承担部门编码（赋权政策为常规政策、促销政策，该参数必填）
    private String CostBearDeptCode;
    //申请人OA账号
    private String ApplyUserID;
}
