package com.emoeny.pointcommon.result.userperiod;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrivalegeData implements Serializable {
    private String ActivityName;
    private String ActivityID;
    private String AddDate;
    private String SeriesNumber;
    private String StartDate;
    private String EndDate;
    private String DayCount;
    private String UId;
    private String RowVersion;
    private String PeriodTab;
    private String Fresher;
}
