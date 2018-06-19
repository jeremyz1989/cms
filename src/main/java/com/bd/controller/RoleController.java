package com.bd.controller;

import com.bd.model.AdminUser;
import com.bd.model.Menu;
import com.bd.model.Role;
import com.bd.model.WebResult;
import com.bd.service.RoleMenuService;
import com.bd.service.RoleService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 角色相关控制器
 * Created by Raye on 2017/3/18.
 */
@Controller
@RequestMapping("admin/role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleMenuService roleMenuService;

    private Logger logger= LoggerFactory.getLogger(RoleController.class);

    /**
     * 进入角色列表管理页面
     * @param page
     * @param pageSize
     * @param query
     * @param map
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10")int pageSize,
                        @RequestParam(defaultValue = "")String query, ModelMap map){
        List<Role> roles = roleService.select(page,pageSize,query);
        map.put("roles",roles);
        int count = roleService.selectCount(query);
        map.put("count",count);
        map.put("maxPage",count/10);
        map.put("page",page);
        map.put("pageSize",pageSize);
        map.put("query",query);
        map.put("nowBegin",pageSize * (page - 1 )+1);
        map.put("nowEnd",pageSize * (page - 1 )+roles.size());
        return "/role/index";
    }

    /**
     * 进入角色编辑页面
     * @param id
     * @param map
     * @return
     */
    @RequestMapping(value = "edit",method = RequestMethod.GET)
    public String edit(@RequestParam(defaultValue = "0") int id, ModelMap map){
        if(id != 0){

            logger.info("=============="+id);
            map.put("role",roleService.selectById(id));
        }
        return "/role/edit";
    }

    /**
     * 更新角色信息
     * @param role
     * @return
     */
    @RequestMapping(value = "edit",method = RequestMethod.POST)
    @ResponseBody
    public WebResult edit(Role role, HttpSession session){
        boolean success = false;
        AdminUser loginUser = (AdminUser) session.getAttribute("loginUser");

        if(role.getId() != null){
            role.setCreator(loginUser.getId());
            success = roleService.update(role);
        }else {
            role.setUpdateuser(loginUser.getId());
            role.setUpdatetime(new Date());
            success = roleService.insert(role);
        }
        if(success){
            return WebResult.success();
        }else{
            return WebResult.unKnown();
        }
    }

    /**
     * 删除角色信息
     * @param id
     * @return
     */
    @RequestMapping(value = "delete",method = RequestMethod.POST)
    @ResponseBody
    public WebResult delete(String id){
        boolean success = roleService.delete(id);
        if(success){
            return WebResult.success();
        }else{
            return WebResult.unKnown();
        }
    }

    /**
     * 进入角色资源权限页面
     * @param id
     * @param map
     * @return
     */
    @RequestMapping(value = "menu",method = RequestMethod.GET)
    public String toMenu(int id,ModelMap map){
        List<Menu> menus = roleMenuService.selectByRoleId(id);
        map.put("treeMenu",menus);
        map.put("menus",new Gson().toJson(menus));
        map.put("roleid",id);
        return "/role/menu";
    }

    /**
     * 更新角色资源权限
     * @param ids
     * @param roleid
     * @return
     */
    @RequestMapping(value = "menu",method = RequestMethod.POST)
    @ResponseBody
    public WebResult update(String ids, int roleid, HttpSession session){
        AdminUser loginUser = (AdminUser) session.getAttribute("loginUser");
        if(roleMenuService.updateRoleMenu(ids,roleid,loginUser.getId())){
            return WebResult.success();
        }
        return WebResult.unKnown();
    }
}
