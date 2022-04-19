package org.forza.sample.processor;


import com.forza.sample.api.SimpleRequestBody;
import com.forza.sample.api.SimpleResponseBody;
import org.forza.protocol.processor.AbstractUserProcessorAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author:  
 * @DateTime: 2020/5/8
 * @Description: TODO
 */
public class SimpleUerProcessor extends AbstractUserProcessorAdapter<SimpleRequestBody> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleUerProcessor.class);

    @Override
    public Object handleRequest(SimpleRequestBody request) throws Exception {
        logger.info("Server Recv: " + request.toString());
        return new SimpleResponseBody(request.toString());
    }

}
