package com.emoney.pointweb.controller;

import com.emoeny.pointcommon.utils.ExcelUtils;
import com.emoney.pointweb.repository.dao.entity.PointFeedBackDO;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.service.biz.PointOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/pointorder")
public class PointOrderController {

    @Resource
    private PointOrderService pointOrderService;

    @RequestMapping
    public String index() {
        return "pointorder/pointorder.index";
    }

//    @RequestMapping("/pageList")
//    @ResponseBody
//    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") Integer productType) {
//        List<PointOrderDO> list = pointOrderService.getAllByOrderStatus(1);
//        if (productType != 0) {
//            list = list.stream().filter(h -> h.getProductType() != null && h.getProductType().equals(productType)).collect(Collectors.toList());
//        }
//        Map<String, Object> result = new HashMap<>();
//        result.put("data", list);
//        return result;
//    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> queryPointOrder(@RequestParam(required = false, defaultValue = "0") Integer start,
                                               @RequestParam(required = false, defaultValue = "10") Integer length,
                                               @RequestParam(required = false, defaultValue = "0") Integer productType) {
        PageHelper.startPage(start, length);
        List<PointOrderDO> list = pointOrderService.queryAllByProductType(productType);
        PageInfo<PointOrderDO> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        result.put("recordsTotal", pageInfo.getTotal());
        result.put("recordsFiltered", pageInfo.getTotal());
        return result;
    }

    @RequestMapping("/exportData")
    public String exportData(HttpServletResponse response, @RequestParam(required = false, defaultValue = "0") int productType) {
        List<PointOrderDO> list = pointOrderService.getAllByOrderStatus(1);
        if (productType != 0) {
            list = list.stream().filter(h -> h.getProductType()!=null&&h.getProductType() == productType).collect(Collectors.toList());
        }
        List<LinkedHashMap<String, Object>> maps = new ArrayList<>();

        if (list != null && list.size() > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            for (PointOrderDO item : list) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("????????????", item.getProductTitle());
                map.put("????????????", getProductTypeName(item.getProductType()));
                map.put("????????????", item.getEmNo());
                map.put("????????????", formatter.format(item.getCreateTime()));
                map.put("??????????????????", item.getMobile());
                map.put("?????????", item.getOrderNo());
                map.put("????????????", item.getPoint());
                map.put("????????????", item.getCash());
                map.put("???????????????", item.getExpressMobileMask());
                map.put("???????????????", item.getExpressMobile());
                map.put("????????????", item.getExpressAddress());
                maps.add(map);
            }
        } else {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("????????????", "");
            map.put("????????????", "");
            map.put("????????????", "");
            map.put("????????????", "");
            map.put("??????????????????", "");
            map.put("?????????", "");
            map.put("????????????", "");
            map.put("????????????", "");
            map.put("???????????????", "");
            map.put("???????????????", "");
            map.put("????????????", "");
            maps.add(map);
        }
        ExcelUtils.exportToExcel(response, "????????????", maps);
        return null;
    }

    public String getProductTypeName(Integer productType) {
        String result = "";
        if (productType==null){
            return result;
        }
        switch (productType) {
            case 1:
                result = "???????????????";
                break;
            case 2:
                result = "?????????";
                break;
            case 3:
                result = "???????????????";
                break;
            case 4:
                result = "????????????";
                break;
            case 5:
                result = "????????????";
                break;
        }
        return result;
    }
}
