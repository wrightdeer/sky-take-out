package com.sky.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sky.properties.BaiduMapProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Slf4j
public class BaiduMapUtil {

    private final static String geocodingUrl = "https://api.map.baidu.com/geocoding/v3/";
    private final static String distanceUrl = "https://api.map.baidu.com/direction/v2/riding";

    private BaiduMapProperties baiduMapProperties;

    public String getShopAddress() {
        return baiduMapProperties.getAddress();
    }

    /**
     * 将指定的地址转换为经纬度
     *
     * @param address 地址
     * @return 经纬度坐标，格式为 "lng,lat"
     * @throws IOException 如果请求失败
     */
    public String addressToLatLng(String address) throws IOException {
        Map map = new HashMap();
        map.put("address", address);
        map.put("output", baiduMapProperties.getOutput());
        map.put("ak", baiduMapProperties.getAk());
        String json = HttpClientUtil.doGet(geocodingUrl, map);
        log.info("json:{}", json);
        if (json.contains("result")) {
            JSONObject jsonObject = JSON.parseObject(json);
            JSONObject result = jsonObject.getJSONObject("result");
            JSONObject location = result.getJSONObject("location");
            String lat = location.getString("lat");
            String lng = location.getString("lng");
            return lat + "," + lng;
        }
        return null;
    }

    /**
     * 计算给定的两个地址之间的路程距离
     *
     * @param origin      起点地址
     * @param destination 终点地址
     * @return 距离（米）
     * @throws IOException 如果请求失败
     */
    public Integer calculateDistance(String origin, String destination) throws IOException {
        Map map = new HashMap();
        map.put("origin", origin);
        map.put("destination", destination);
        map.put("ak", baiduMapProperties.getAk());
        String json = HttpClientUtil.doGet(distanceUrl, map);
        log.info("json:{}", json);
        if (json.contains("result")) {
            JSONObject jsonObject = JSON.parseObject(json);
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray routes = result.getJSONArray("routes");
            Integer distance = routes.getJSONObject(0).getInteger("distance");
           return distance;
        }
        return null;
    }
}