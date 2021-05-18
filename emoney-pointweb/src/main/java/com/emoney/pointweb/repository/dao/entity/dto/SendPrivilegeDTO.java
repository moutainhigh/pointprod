package com.emoney.pointweb.repository.dao.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class SendPrivilegeDTO {
    //赠送来源ID（a001:物流，a002：官网，a003：智投，a004：移动，a005：好股，a006：机构）
    private String AppId;
    //赋权活动编号
    private String ActivityID;
    //申请原因
    private String Reason;
    //成本承担部门编码（赋权政策为常规政策、促销政策）
    private String CostBearDeptCode;
    //申请人OA账号
    private String ApplyUserID;
    //赋权账号列表
    private List<CreateActivityGrantApplyAccountDTO> Accounts;
}
