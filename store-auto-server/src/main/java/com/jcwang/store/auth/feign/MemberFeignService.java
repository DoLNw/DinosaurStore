package com.jcwang.store.auth.feign;

import com.jcwang.store.auth.vo.SocialUser;
import com.jcwang.store.auth.vo.UserLoginVo;
import com.jcwang.store.auth.vo.UserRegisterVo;
import com.jcwang.store.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: jcwang
 **/

@FeignClient("DinosaurStore-member")
public interface MemberFeignService {

    @PostMapping(value = "/member/memberloginlog/register")
    R register(@RequestBody UserRegisterVo vo);


    @PostMapping(value = "/member/memberloginlog/login")
    R login(@RequestBody UserLoginVo vo);

    @PostMapping(value = "/member/memberloginlog/oauth2/login")
    R oauthLogin(@RequestBody SocialUser socialUser) throws Exception;

    @PostMapping(value = "/member/memberloginlog/weixin/login")
    R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo);
}
