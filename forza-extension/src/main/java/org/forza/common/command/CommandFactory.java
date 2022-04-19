package org.forza.common.command;

import org.forza.common.Invocation;
import org.forza.common.enums.CommandCodeEnum;
import org.forza.common.enums.ResponseStatus;


/**
 * @Author:  
 * @DateTime: 2020/4/11
 * @Description: TODO
 */
public class CommandFactory {

    public RequestCommand createRequest(Object request) {
        CommandCode cmdCode = CommandCodeEnum.GENERAL_CMD;
        if (request instanceof Command) {
            Command command = (Command) request;
            cmdCode = command.cmdCode();
        }
        RequestCommand requestCommand = new RequestCommand(cmdCode);
        Invocation invocation = new Invocation();
        invocation.setClassName(request.getClass().getName());
        invocation.setData(request);
        requestCommand.setInvocation(invocation);
        return requestCommand;
    }

    public ResponseCommand createResponse(RequestCommand request, Object response) {
        if (response instanceof ResponseCommand) {
            return (ResponseCommand) response;
        }
        ResponseCommand responseCommand = new ResponseCommand(request.getId(), request.getCmdCode());
        responseCommand.setStatus(ResponseStatus.SUCCESS);
        Invocation invocation = new Invocation();
        invocation.setClassName(response.getClass().getName());
        invocation.setData(response);
        responseCommand.setInvocation(invocation);
        return responseCommand;
    }

    public ResponseCommand createResponse(RequestCommand request, ResponseStatus status, String errorMsg) {
        ResponseCommand responseCommand = new ResponseCommand(request.getId(), request.getCmdCode());
        responseCommand.setStatus(status);
        responseCommand.setErrorMessage(errorMsg);
        return responseCommand;

    }
}
