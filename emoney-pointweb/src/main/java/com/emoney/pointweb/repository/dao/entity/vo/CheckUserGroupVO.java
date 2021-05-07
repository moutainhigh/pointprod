package com.emoney.pointweb.repository.dao.entity.vo;

import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupData;
import lombok.Data;

import java.util.List;

@Data
public class CheckUserGroupVO {
    private String Uid;
    private List<CheckUserGroupData> UserGroupList;
}
