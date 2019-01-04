package cn.com.ultrapower.role.mapper;

import cn.com.ultrapower.role.entity.BaseRoleEntity;
import cn.com.ultrapower.role.entity.BaseRoleRelationMenu;
import cn.com.ultrapower.user.entity.BaseUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BaseRoleMapper {

    public int saveRoleInfo(@Param("param") Map param);
    public int updateRoleInfo(@Param("param") Map param);
    public List<BaseRoleEntity> queryRoleData(Map whereMap);
    public int queryRoleRelationUserCount(Map whereMap);
    public int deleteRoleInfo(Map whereMap);
    public List<BaseRoleRelationMenu> queryRoleRelationMenutree(Map whereMap);

    //批量添加
    public int saveRoleRelationMenuList(@Param("roleMenus") List<BaseRoleRelationMenu> roleMenus);
    //批量删除
    public int delRoleRelationMenuS(@Param("map") Map map);

    /**
     * 每个角色的人员
     * @param whereMap
     * @return
     */
    public List<BaseUserEntity> queryRoleToPeo(Map whereMap);
    public int queryUserPeoCount(Map whereMap);



}
