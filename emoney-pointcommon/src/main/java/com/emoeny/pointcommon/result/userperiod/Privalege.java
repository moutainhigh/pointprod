package com.emoeny.pointcommon.result.userperiod;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Privalege implements Serializable {
    private String UId;
    private List<PrivalegeData> Privileges;
}
