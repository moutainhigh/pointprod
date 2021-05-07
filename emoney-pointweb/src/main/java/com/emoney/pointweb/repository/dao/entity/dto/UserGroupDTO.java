package com.emoney.pointweb.repository.dao.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserGroupDTO {
    private String Uid;
    private List<UserGroupData> UserGroupList;
}
