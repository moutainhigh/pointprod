package com.emoeny.pointcommon.constants;

public class RedisConstants {
    /**
     * Redis Key
     */
    public static String REDISKEY_PointTaskConfigInfo_GETBYTASKID = "pointprod:poninttaskconfginfo_getbytaskid_{0}_{1}";//taskId,subId
    public static String REDISKEY_PointTaskConfigInfo_GETALLEFFECTIVETASKS = "pointprod:poninttaskconfginfo_getalleffectivetasks";
    public static String REDISKEY_PointTaskConfigInfo_GETTASKSBYTASKTYPE = "pointprod:poninttaskconfginfo_gettasksbytasktype_{0}";//taskType

    public static String REDISKEY_PointRecord_GETBYUID = "pointprod:pointrecord_getbyuid_{0}";//uid
    public static String REDISKEY_PointRecord_GETSUMMARYBYUID = "pointprod:pointrecord_getsummarybyuid_{0}";//uid
    public static String REDISKEY_PointRecord_GETSUMMARYBYUIDANDCREATETIME = "pointprod:pointrecord_getsummarybyuidandcreatetime_{0}_{1}_{2}";//uid,dtStart,dtEnd
    public static String REDISKEY_PointRecord_SETPOINTRECORDID = "pointprod:pointrecord_setpointrecordid_{0}_{1}";//uid,id
    public static String REDISKEY_PointRecord_GETUNCLAIMRECORDSBYUID = "pointprod:pointrecord_getunclaimrecordsbyuid_{0}";//uid
    public static String REDISKEY_PointRecord_CREATE_LOCKKEY="pointprod:pointrecord_create_lockkey_{0}_{1}";//uid,taskId


    public static String REDISKEY_PointLimitType_GETBYTYPE = "pointprod:pointlimisttype_getbytype_{0}_{1}";//pointLimittype,pointListto

    public static String REDISKEY_SignInRecord_GETBYUID = "pointprod:signinrecord_getbyuid_{0}";//uid
    public static String REDISKEY_SignInRecord_CREATE_LOCKKEY="pointprod:signinrecord_create_lockkey_{0}";//uid

    public static String REDISKEY_PointProduct_GETALLEFFECTIVEPRODUCTS = "pointprod:pointproduct_getalleffectiveproducts";
    public static String REDISKEY_PointProduct_GETBYID = "pointprod:pointproduct_getbyid_{0}";//productid

    public static String REDISKEY_PointOrder_SETORDERKEY = "pointprod:pointorder_setorderkey_{0}";//orderno
    public static String REDISKEY_PointOrder_CREATE_LOCKKEY="pointprod:pointorder_create_lockkey_{0}_{1}";//uid,productid
    public static String REDISKEY_PointOrder_GETBYUID = "pointprod:pointorder_getbyuid_{0}";//uid

    public static String REDISKEY_PointQuotation_GETALL = "pointprod:pointquotation_getall";

    public static String REDISKEY_PointMessage_GETBYUID = "pointprod:pointmessage_getbyuid_{0}";//uid

    public static String REDISKEY_PointQuestion_QUERYAll = "pointprod:pointquestion_queryAll";
    public static String REDISKEY_PointQuestion_GETBYID = "pointprod:pointquestion_queryallbyid_{0}";//id
}
