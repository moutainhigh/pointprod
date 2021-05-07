package com.emoney.pointweb.repository.dao.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckUserGroupDTO {
    private String Uid;
    private List<CheckUserGroupData> UserGroupList;
}
