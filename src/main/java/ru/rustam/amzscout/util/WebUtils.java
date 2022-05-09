package ru.rustam.amzscout.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class WebUtils {
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
    private static final String EMPTY_STRING = "";

    @Value("${app.period}")
    private long PERIOD;
    @Value("${app.reqNumber}")
    private int REQ_NUMBER;

    private final Map<String, LinkedList<Long>> ipToListReqTime = new HashMap<>();
    private HttpServletRequest request;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public boolean checkRequestsNumberFromOneIp() {
        final String clientIp = getClientIp();
        final long newReqTime = new Date().getTime();
        synchronized (ipToListReqTime) {
            if (!ipToListReqTime.containsKey(clientIp)) {
                ipToListReqTime.put(clientIp, new LinkedList<>(List.of(newReqTime)));
                return true;
            }
        }
        synchronized (ipToListReqTime.get(clientIp)) {
            if (ipToListReqTime.get(clientIp).size() < REQ_NUMBER) {
                ipToListReqTime.get(clientIp).addLast(newReqTime);
                return true;
            }
            final long msBetweenLastAndNewReq = newReqTime - ipToListReqTime.get(clientIp).getLast();
            if (msBetweenLastAndNewReq > PERIOD) {
                ipToListReqTime.get(clientIp).clear();
                ipToListReqTime.get(clientIp).add(newReqTime);
                return true;
            }
            final long msBetweenFirstAndNewReq = newReqTime - ipToListReqTime.get(clientIp).pollFirst();
            ipToListReqTime.get(clientIp).addLast(newReqTime);
            return msBetweenFirstAndNewReq > PERIOD;
        }
    }

    private String getClientIp() {
        if (request == null) {
            return EMPTY_STRING;
        }
        String clientIp = request.getHeader(X_FORWARDED_FOR);
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp == null ? EMPTY_STRING : clientIp;
    }
}