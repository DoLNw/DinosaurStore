package com.jcwang.store.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.ware.entity.WareInfoEntity;
import com.jcwang.store.ware.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 11:12:26
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据用户的收费地址计算运费
     *
     * @param addrId
     * @return
     */
    FareVo getFare(Long addrId);
}

