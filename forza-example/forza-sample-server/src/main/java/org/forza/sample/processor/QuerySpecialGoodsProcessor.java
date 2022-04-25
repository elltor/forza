package org.forza.sample.processor;

import com.forza.sample.api.Goods;
import com.forza.sample.api.GoodsRequestBody;
import com.forza.sample.api.GoodsResponseBody;
import org.forza.protocol.processor.AbstractUserProcessorAdapter;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class QuerySpecialGoodsProcessor extends AbstractUserProcessorAdapter<GoodsRequestBody> {
    @Override
    public Object handleRequest(GoodsRequestBody request) throws Exception {
        String category = request.getCategory();

        int start = new Random().nextInt(1010) + 101;
        int end = start + new Random().nextInt(10) + 2;

        ArrayList<Goods> goodsList = new ArrayList<>(end - start + 1);
        for (; start <= end; start++) {
            goodsList.add(new Goods(start, "XXX-" + start, category));
        }
//        TimeUnit.SECONDS.sleep(10);
        return new GoodsResponseBody(goodsList);
    }
}
