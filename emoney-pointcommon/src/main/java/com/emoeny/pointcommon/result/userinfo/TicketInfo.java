package com.emoeny.pointcommon.result.userinfo;

import lombok.Data;

import java.util.Date;

@Data
public class TicketInfo {
    public String Ticket;
    public String AppID;
    public String UserID;
    public String MaskUserName;
    public String UserName;
    public Date LoginTime;
}
