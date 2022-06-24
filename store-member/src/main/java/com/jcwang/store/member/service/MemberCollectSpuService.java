package com.jcwang.store.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.member.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 11:00:13
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

