package cn.com.ultrapower.user.mapper;

import cn.com.ultrapower.user.entity.BaseUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BaseUserMapper {
    public int saveUserInfo(@Param("param") Map param);
    public int updateUserInfo(@Param("param") Map param);
    public List<BaseUserEntity> queryUserData(Map whereMap);
    public int queryUserCount(Map whereMap);
    public int deleteUserInfo(Map whereMap);
    public int batchDeleteUser(@Param("list") List list);
    /**
     * 批量修改用户信息（部门id，部门名称）
     * @param list
     * @return
     */
    public int updateDepToUserList(@Param("list") List<BaseUserEntity> list);

    /**
     * 批量修改用户信息（角色id，角色名称）
     * @param list
     * @return
     */
    public int updateRoleToUserList(@Param("list") List<BaseUserEntity> list);

}
