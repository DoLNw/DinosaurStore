package com.jcwang.store.member.exception;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: jcwang
 **/
public class PhoneException extends RuntimeException {

    public PhoneException() {
        super("存在相同的手机号");
    }
}
