package com.jcwang.store.member.dao;

import com.jcwang.store.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 11:00:13
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
