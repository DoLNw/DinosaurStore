package com.jcwang.store.member.exception;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: jcwang
 **/
public class UsernameException extends RuntimeException {


    public UsernameException() {
        super("存在相同的用户名");
    }
}
