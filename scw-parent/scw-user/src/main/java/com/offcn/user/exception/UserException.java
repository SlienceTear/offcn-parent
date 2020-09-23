package com.offcn.user.exception;

import com.offcn.user.enums.UserExceptionEnum;
import org.junit.runner.RunWith;

public class UserException extends RuntimeException {

    public UserException(UserExceptionEnum userExceptionEnum){
        super(userExceptionEnum.getMsg());
    }
}
