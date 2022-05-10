package org.forza.protocol.handler;

import org.apache.commons.lang3.StringUtils;
import org.forza.common.DecodeableInvocation;
import org.forza.common.Invocation;
import org.forza.common.command.CommandCode;
import org.forza.common.command.RemotingCommand;
import org.forza.common.command.RequestCommand;
import org.forza.common.command.ResponseCommand;
import org.forza.common.enums.CommandCodeEnum;
import org.forza.common.exception.RemotingException;
import org.forza.common.extension.ExtensionLoader;
import org.forza.protocol.processor.UserProcessor;
import org.forza.reomoting.RemotingContext;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class GeneralCmdHandler extends AbstractCommandHandler {
    private ConcurrentHashMap<String, UserProcessor<?>> processors = new ConcurrentHashMap<String, UserProcessor<?>>(4);

    public GeneralCmdHandler() {
        Set<String> supportedExtensions = ExtensionLoader.getExtensionLoader(UserProcessor.class).getSupportedExtensions();
        for (String name : supportedExtensions) {
            UserProcessor processor = ExtensionLoader.getExtensionLoader(UserProcessor.class).getExtension(name);
            if (!StringUtils.isNotBlank(processor.interest())) {
                throw new IllegalArgumentException("Processor interest should not be blank!");
            }
            UserProcessor<?> preProcessor = processors.putIfAbsent(processor.interest(),
                    processor);

            if (preProcessor != null) {
                String errMsg = "Processor with interest key ["
                        + processor.interest()
                        + "] has already been registered, can not register again!";
                throw new IllegalArgumentException(errMsg);
            }
        }
    }


//    private void registerProcessor(UserProcessor processor) {
//        ObjectUtils.isNotNull(processor, "Processer should benot null");
//        if (!StringUtils.isNotBlank(processor.interest())) {
//            throw new IllegalArgumentException("Processor interest should not be blank!");
//        }
//        UserProcessor<?> preProcessor = processors.putIfAbsent(processor.interest(),
//                processor);
//        if (preProcessor != null) {
//            String errMsg = "Processor with interest key ["
//                    + processor.interest()
//                    + "] has already been registered, can not register again!";
//            throw new IllegalArgumentException(errMsg);
//        }
//    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCodeEnum.GENERAL_CMD;
    }

    @Override
    public void handle(RemotingContext ctx, RemotingCommand cmd) throws RemotingException {
        UserProcessor processor = null;
        ExecutorService executor = null;

        Optional<Invocation> invocation = Optional.ofNullable(cmd.getInvocation());

        invocation.ifPresent(inv->{
            DecodeableInvocation dinv =(DecodeableInvocation)inv;
            dinv.decodeClassName();
        });

        if (cmd instanceof RequestCommand) {
            processor = processors.get(invocation.get().getClassName());
            if (processor == null) {
                String errorMsg = "No UserProcesser found by interest: " + invocation.get().getClassName()+" from GeneralCmdHandler";
                throw new RemotingException(ctx.getConnection(), errorMsg);
            }
            executor = processor.processInIOThread()
                    ? ctx.getEventLoop() : Optional.ofNullable(processor.getExecutor())
                    .orElseGet(() -> ctx.protocolExecutor());

        } else if (cmd instanceof ResponseCommand) {
            executor = handelInIOThread()
                    ? ctx.getEventLoop() : ctx.protocolExecutor();
        }
        try {
            executor.execute(new HandlerRunnable(ctx, cmd));
        } catch (Throwable e) {
            throw new RemotingException(ctx.getConnection(), e);
        }
    }

    @Override
    public Object handleRequest(RemotingContext ctx, RequestCommand request) throws Exception {
        Invocation inv = request.getInvocation();
        UserProcessor processor = processors.get(inv.getClassName());
        return processor.handleRequest(inv.getData());
    }

    @Override
    public boolean handelInIOThread() {
        return false;
    }
}
