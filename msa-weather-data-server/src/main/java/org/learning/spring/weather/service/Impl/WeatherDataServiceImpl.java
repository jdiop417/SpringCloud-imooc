package org.learning.spring.weather.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.learning.spring.weather.service.WeatherDataService;
import org.learning.spring.weather.vo.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class WeatherDataServiceImpl implements WeatherDataService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final String WEATHER_URI = "http://wthrcdn.etouch.cn/weather_mini";

    @Override
    public WeatherResponse getDataByCityId(String cityId) {
        String uri = WEATHER_URI + "?citykey=" + cityId;
        return doGetWeatherData(uri);
    }


    @Override
    public WeatherResponse getDataByCityName(String cityName) {
        String uri = WEATHER_URI + "?city=" + cityName;
        return doGetWeatherData(uri);
    }


    private WeatherResponse doGetWeatherData(String uri) {
        WeatherResponse weather = new WeatherResponse();
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        //无缓冲，请求一波，写缓存
        String value = null;
        if (!stringRedisTemplate.hasKey(uri)) {
            log.info("未找到key:" + uri);
            throw new RuntimeException("未找到key:" + uri);
        } else {//有缓存，取缓存
            log.info("找到key：" + uri);
            value = ops.get(uri);
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            weather = mapper.readValue(value, WeatherResponse.class);
        } catch (IOException e) {
            log.error(e.getMessage());
        }


        return weather;
    }

}
