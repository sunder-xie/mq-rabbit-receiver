/*
 *
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/). All rights reserved.
 *
 */

package com.ymatou.mq.rabbit.receiver.rest.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.container.page.pages.SystemPageHandler;
import com.alibaba.fastjson.JSON;
import com.ymatou.messagebus.facade.ReceiveMessageFacade;
import com.ymatou.messagebus.facade.model.ReceiveMessageReq;
import com.ymatou.messagebus.facade.model.ReceiveMessageResp;
import com.ymatou.messagebus.facade.model.ReceiveMessageRestReq;
import com.ymatou.mq.rabbit.receiver.facade.aspect.FacadeAspect;
import com.ymatou.mq.rabbit.receiver.rest.ReceiveMessageResource;
import com.ymatou.mq.rabbit.receiver.rest.RestResp;
import com.ymatou.mq.rabbit.receiver.service.ConfigReloadService;
import com.ymatou.mq.rabbit.receiver.service.MessageFileQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Component("publishMessageResource")
@Service(protocol = "rest")
@Path("/{message:(?i:message)}")
@Produces({"application/json; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON})
public class ReceiveMessageResourceImpl implements ReceiveMessageResource {

    public static final Logger logger = LoggerFactory.getLogger(ReceiveMessageResourceImpl.class);

    @Resource
    ReceiveMessageFacade receiveMessageFacade;

    @Autowired
    MessageFileQueueService messageFileQueueService;

    @Autowired
    ConfigReloadService configReloadService;

    @Override
    @POST
    @Path("/{publish:(?i:publish)}")
    public RestResp publish(ReceiveMessageRestReq req) {

        ReceiveMessageReq receiveMessageReq = new ReceiveMessageReq();
        receiveMessageReq.setAppId(req.getAppId());
        receiveMessageReq.setIp(req.getIp());
        receiveMessageReq.setCode(req.getCode());
        receiveMessageReq.setMsgUniqueId(req.getMsgUniqueId());
        receiveMessageReq.setBody(JSON.toJSONString(req.getBody()));

        ReceiveMessageResp receiveMessageResp = receiveMessageFacade.publish(receiveMessageReq);
        return RestResp.newInstance(receiveMessageResp);
    }

    @GET
    @Path("/{filestatus:(?i:filestatus)}")
    @Override
    public String fileStatus() {
        return messageFileQueueService.getFileDb().status();
    }

    @GET
    @Path("/{deleteKafkaQueue:(?i:deleteKafkaQueue)}")
    @Override
    @Produces({MediaType.TEXT_PLAIN})
    public String deleteKafkaQueue() {
        configReloadService.deleteExchangeAndQueueOfKafka();
        return "ok";
    }
}
