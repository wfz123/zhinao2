package cn.com.ultrapower.login.mapper;


import cn.com.ultrapower.user.entity.BaseUserEntity;

/**
 * Created by Administrator on 2017/3/20.
 */
public interface LoginMapper {
    //查询user
    public BaseUserEntity queryUser(BaseUserEntity entity);

}
