package com.emoney.pointweb;


import com.emoeny.pointcommon.enums.PointRecordStatusEnum;
import com.emoeny.pointfacade.facade.pointquestion.PointQuestionFacade;
import com.emoney.pointweb.repository.*;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.dto.CreateActivityGrantApplyAccountDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendCouponDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendPrivilegeDTO;
import com.emoney.pointweb.repository.dao.entity.vo.QueryCouponActivityVO;
import com.emoney.pointweb.repository.dao.mapper.PointLimitMapper;
import com.emoney.pointweb.repository.dao.mapper.PointMessageMapper;
import com.emoney.pointweb.repository.dao.mapper.PointRecordMapper;
import com.emoney.pointweb.service.biz.*;
import com.emoney.pointweb.service.biz.kafka.KafkaProducerService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static cn.hutool.core.date.DateUtil.date;

@SpringBootTest
@MapperScan(basePackages = {"com.emoney.pointweb.repository.dao.mapper"})
@ComponentScan(basePackages = {"com.emoney.pointweb"})
@Slf4j
class PointWebApplicationTests {

    @Autowired
    private PointLimitMapper pointLimitDOMapper;

    @Autowired
    private PointRecordMapper pointRecordMapper;

    @Autowired
    private RedisService redisCache1;

    @Autowired
    private PointTaskConfigInfoService pointTaskConfigInfoService;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private PointRecordESRepository pointRecordESRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @Autowired
    private PointRecordRepository pointRecordRepository;

    @Autowired
    private PointRecordService pointRecordService;

    @Autowired
    private PointMessageRepository pointMessageRepository;

    @Autowired
    private PointMessageMapper pointMessageMapper;

    @Autowired
    private PointOrderRepository pointOrderRepository;


    @Autowired
    private SignInRecordRepository signInRecordRepository;


    @Autowired
    private SingInRecordESRepository singInRecordESRepository;

    @Autowired
    private MailerService mailerService;

    @Value("${mail.toMail.addr}")
    private String toMailAddress;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private PointQuestionFacade pointQuestionFacade;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private MessageService messageService;

    @Test
    void contextLoads() throws ExecutionException, InterruptedException, ParseException {

//        List<QueryCouponActivityVO> queryCouponActivityVOS = logisticsService.getCouponRulesByAcCode("cp-1210126151518711");
//
//        SendCouponDTO sendCouponDTO = new SendCouponDTO();
//        sendCouponDTO.setPRESENT_ACCOUNT_TYPE(2);
//        sendCouponDTO.setPRESENT_ACCOUNT("0x44FF54D091ADCBCA1E7B88FC75276469");
//        sendCouponDTO.setCOUPON_ACTIVITY_ID("cp-1210126151518711");
//        sendCouponDTO.setCOUPON_RULE_PRICE(BigDecimal.valueOf(100));
//        sendCouponDTO.setPRESENT_PERSON("积分商城");
//        Boolean result = logisticsService.SendCoupon(sendCouponDTO);
//

        SendPrivilegeDTO sendPrivilegeDTO=new SendPrivilegeDTO();
        sendPrivilegeDTO.setAppId("A009");
        sendPrivilegeDTO.setActivityID("PAC1210519101327340");
        sendPrivilegeDTO.setApplyUserID("xueqiuyun");
        sendPrivilegeDTO.setReason("积分商城");
        List<CreateActivityGrantApplyAccountDTO> createActivityGrantApplyAccountDTOS=new ArrayList<>();
        CreateActivityGrantApplyAccountDTO createActivityGrantApplyAccountDTO=new CreateActivityGrantApplyAccountDTO();
        createActivityGrantApplyAccountDTO.setAccountType(2);
        createActivityGrantApplyAccountDTO.setMID("0x44FF54D091ADCBCA1E7B88FC75276469");
        createActivityGrantApplyAccountDTOS.add(createActivityGrantApplyAccountDTO);
        sendPrivilegeDTO.setAccounts(createActivityGrantApplyAccountDTOS);
        Boolean result = logisticsService.SenddPrivilege(sendPrivilegeDTO);

//        List<PointRecordDO> pointRecordDOS = pointRecordRepository.getByUid(1001471383L);
//
//        long L=    pointRecordDOS.stream().filter(h -> h.getTaskId().equals(1392719202749648896L)&&h.getSubId().equals(null) && h.getPointStatus().equals(Integer.valueOf(PointRecordStatusEnum.UNCLAIMED.getCode()))).count();
//


        //String pid=userInfoService.getPidByUserId(1001471383L);
        // String uid = userInfoService.getUidByEmNo("syjsb1710003");

        // String ret =messageService.sendMessage(1001471383L,"","http://test.point.emoney.cn/message/index");

        //List<PointRecordSummaryDO> pointRecordSummaryDOS= pointRecordService.getPointRecordSummaryByUid(1001471383L);

//        QueryStockUpLogisticsOrderDTO queryStockUpLogisticsOrderDTO=new QueryStockUpLogisticsOrderDTO();
//        queryStockUpLogisticsOrderDTO.setProductID("888010000,888020000,888080000,888040000,888090000");
//        queryStockUpLogisticsOrderDTO.setRefund_Sign(1);
//        queryStockUpLogisticsOrderDTO.setStockUpDate_Start("2021-01-01");
//        queryStockUpLogisticsOrderDTO.setStockUpDate_End("2021-05-01");
//        List<QueryLogisticsOrderVO> queryLogisticsOrderVOS=logisticsOrderService.getStockUpLogisticsOrder(queryStockUpLogisticsOrderDTO);


        //List<UserInfoVO> userInfoVOS=userInfoService.getUserInfoByUid(2020117908L);


        // Result<PointQuestionVO> pointQuestionVO=pointQuestionFacade.queryPointQuestion();

//        CheckUserGroupDTO checkUserGroupDTO = new CheckUserGroupDTO();
//        checkUserGroupDTO.setUid("2020614624");
//        List<CheckUserGroupData> checkUserGroupDataList = new ArrayList<>();
//
//        CheckUserGroupData checkUserGroupData = new CheckUserGroupData();
//        checkUserGroupData.setCheckResult(false);
//        checkUserGroupData.setGroupId(184);
//        checkUserGroupDataList.add(checkUserGroupData);
//
//        checkUserGroupData = new CheckUserGroupData();
//        checkUserGroupData.setCheckResult(false);
//        checkUserGroupData.setGroupId(171);
//        checkUserGroupDataList.add(checkUserGroupData);
//
//        checkUserGroupDTO.setUserGroupList(checkUserGroupDataList);
//
//        CheckUserGroupVO checkUserGroupVO = pointTaskConfigInfoService.getUserGroupCheckUser(checkUserGroupDTO);

        //List<PointRecordDO> pointRecordDOS=pointRecordRepository.getByPager(1001539325L,-1,null,null,0,5);

        //Date dt=DateUtil.parseDateTime((DateUtil.year(DateUtil.date()) +1) + "-03-31 23:59:59");
//        Date dt =DateUtil.parseDate("2099-12-31");
//       log.info("日志测试info........................");
//       log.error("日志测试error........................");

//        pointRecordESRepository.deleteAll();
//        singInRecordESRepository.deleteAll();

        // List<PointRecordDO> pageInfo = pointRecordRepository.getByPager(1001539325L, 0, 3);
//
        //List<PointOrderDO> pointOrderDOS = pointOrderRepository.getByUid(2020117908L,2,0,3);
        //PointOrderDO pointOrderDO=pointOrderRepository.getByOrderNo(1382204887978348544L);
//
//        List<Integer> pointStatus=new ArrayList<>();
//        pointStatus.add(1);
//        pointStatus.add(2);
//        Pageable pageable = PageRequest.of(0,2);
//        Page<PointRecordDO> pointRecordDOPage=pointRecordESRepository.findByUidAndPointStatusInOrderByCreateTimeDesc(1001539325L,pointStatus,pageable);
//

        // List<PointRecordDO> pointRecordDOS=pointRecordMapper.getHisByUidHis(1001539325L);
        //List<PointMessageDO>  pointMessageDOS= pointMessageRepository.getByUid(2020117908L);

        //Integer count= pointMessageRepository.getByUidAndSrc(1001539327L,"1381776190670508032");
        //redisCache1.removePattern("pointprod:signinrecord_getbyuid_*");
        //Long recordByTaskId=pointRecordService.calPointRecordByTaskId(1377426874657017856L,"",0,10);

        //Long recordByTaskId1=pointRecordService.calPointRecordByTaskId(1369566660398288896L,"111",0,10);

//        String url="http://webapi.emoney.cn/user/api/User.GetAccountList";
//        Map<String,String> map=new HashMap<String,String>();
//        map.put("appid","10060");
//        map.put("username","18512182115");
//        String res= OkHttpUtil.get(url,map);
//        System.out.println(res);

        //单表测试
//        PointLimitDO pointLimitDO =new PointLimitDO();
//        pointLimitDO.setPointLimittype(1);
//        pointLimitDO.setPointLimitvalue(1.0f);
//        pointLimitDO.setRemark("1.0f");
//        Integer id= pointLimitDOMapper.insert(pointLimitDO);
//
//           List<PointLimitDO> pointLimitDOs=pointLimitDOMapper.getById(1);
//        pointLimitDOs.stream()

        //分表测试
//        PointRecordDO pointRecordDO = new PointRecordDO();
//        pointRecordDO.setId(IdUtil.getSnowflake(1, 1).nextId());
//        pointRecordDO.setUid(10000772L);
//        pointRecordDO.setCreateTime(new Date());
//        pointRecordDO.setLockDays(0);
//        pointRecordDO.setPointStatus(2);
//        pointRecordDO.setIsValid(true);
//       // pointRecordMapper.insert(pointRecordDO);
//
////        SignInRecordDO signInRecordDO=new SignInRecordDO();
////        signInRecordDO.setId(IdUtil.getSnowflake(1, 1).nextId());
////        signInRecordDO.setUid(10000772L);
////        signInRecordRepository.insert(signInRecordDO);
//
//
//        //pointRecordESRepository.deleteById(1371816217081417728l);
//
////        List<PointRecordDO> pointRecordDOS = pointRecordMapper.getByUid(10000772);
////        //spring-data-elasticsearch 测试
//        pointRecordESRepository.save(pointRecordDO);
//
//        List<PointRecordDO> pointRecordDOS =pointRecordESRepository.findByLockDaysIsGreaterThanAndIsValid(0,true);


        //List<PointRecordDO> pointRecordDOS=pointRecordRepository.getByUid((long)10000772);
        //List<PointRecordDO> pointRecordDOS =pointRecordESRepository.findByUid(10014713888l);


//        Date d=new Date();
//        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
//        Date date1=df.parse(df.format(new Date(d.getTime() + (long)2 * 24 * 60 * 60 * 1000)));
//        Date date2=df.parse(df.format(new Date(d.getTime() + (long)3 * 24 * 60 * 60 * 1000)));
//        List<PointRecordDO> pointRecordDOS11=pointRecordESRepository.findByUidAndCreateTimeBetween(10000772,date1,date2);
//                List<PointRecordDO> pointRecordDOS1 = pointRecordESRepository.findByUid((long)1001471383);
//                pointRecordDOS1.forEach((PointRecordDO pointRecordDO)->{
//                    pointRecordESRepository.delete(pointRecordDO);
//                });


        //redis测试
//        redisCache1.set("testkey", "123", 2 * 60l, TimeUnit.SECONDS);
//        String testvalue = redisCache1.get("testkey", String.class);

        //kafka测试
//        ListTopicsResult result1= kafkaAdminClient.listTopics();
//        // 打印Topic的名称Set<String>
//        System.out.println(result1.names().get());
//        // 打印Topic列表的信息Map<String, TopicListing>>
//        System.out.println(result1.namesToListings().get());
//        // 打印Topic列表的信息Collection<TopicListing>>
//        System.out.println(result1.listings().get());
//        for (int i = 0; i < 10; i++) {
//            kafkaProducerService.sendMessageAsync("test1", "发送消息:" + i);
//        }

//
//        PointSummaryDO pointSummaryDO = new PointSummaryDO();
//        pointSummaryDO.setPointTotal(1.0f);
//        pointSummaryDO.setUid(10000772l);
//        pointSummaryRepository.insertOrUpdate(pointSummaryDO);

        //List<PointRecordSummaryDO> pointRecordSummaryDOS=pointRecordRepository.getPointRecordSummaryByUid((long)1001471383);

        //Pageable pageable = PageRequest.of(0, 10);
        //Page<PointRecordDO> pointRecordDOS = pointRecordESRepository.findByTaskId(1372817436314832896l, pageable);

        //List<PointRecordDO> pointRecordDOS =pointRecordESRepository.findByTaskId(1372817436314832896l);

        //mailerService.sendSimpleTextMailActual("发送主题","发送内容",new String[]{"meixiaohu@emoney.cn"},null,null,null);

        //TicketInfo userInfo = redisCache1.get("emoney.pointweb.userinfo.redis.userid:1001471383", TicketInfo.class);
    }

}
