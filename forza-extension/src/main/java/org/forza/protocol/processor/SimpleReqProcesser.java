package org.forza.protocol.processor;

import org.forza.protocol.ReqBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @Author:  
 * @DateTime: 2020/4/20
 * @Description: TODO
 */
public class SimpleReqProcesser extends AbstractUserProcessorAdapter<ReqBody> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String interest() {
        return ReqBody.class.getName();
    }


    @Override
    public String handleRequest(ReqBody body) throws Exception {
        logger.error("handleRequest: " + body.toString());

        return "server success";
    }

    @Override
    public boolean processInIOThread() {
        return false;
    }
}
