package com.beyondbit.ias.admin.controller;

import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.common.Layui;
import com.beyondbit.ias.core.common.PageUtils;
import com.beyondbit.ias.core.common.ZtreeUtil;
import com.beyondbit.ias.core.dao.RoleInfoMapper;
import com.beyondbit.ias.core.entity.DeptInfo;
import com.beyondbit.ias.core.entity.RoleInfo;
import com.beyondbit.ias.core.entity.UserInfo;
import com.beyondbit.ias.core.security.SecurityProvider;
import com.beyondbit.sso.client.util.ServletUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/userinfo")
public class UserInfoController extends BaseController {
    @Autowired
    com.beyondbit.ias.core.service.UserInfoService userinfoService;

    @Autowired
    private SecurityProvider securityProvider;

    @Autowired
    RoleInfoMapper roleInfoMapper;

    /**
     * 列表
     * @return
     */
    @RequestMapping("list")
    public String index(){
        return "userinfo/list.html";
    }

    @RequestMapping("query")
    public String query(){
        return "userinfo/querylist.html";
    }



    @PostMapping("insert")
    @ResponseBody
    public int insertUserInfo(@RequestBody UserInfo userinfo) {
        int result = userinfoService.insertUserInfo(userinfo);

        return result;
    }

    @GetMapping("/queryList")
    @ResponseBody
    public Layui queryList(@RequestParam(value = "page", defaultValue = "1") int pageIndex,
                           @RequestParam(value = "limit", defaultValue = "10") int pageSize,
                           @RequestParam(value="userName",defaultValue="") String userName,
                           @RequestParam(value="orderbyClause",defaultValue=" id asc") String orderbyClause)
    {
        PageUtils list = userinfoService.queryUserInfosByUserName(pageIndex,pageSize,userName,orderbyClause);
        return Layui.data(list.getTotalCount(),list.getList(),"OK");
    }

    @GetMapping("/queryChildren")
    @ResponseBody
    public Layui queryChildren(@RequestParam(value = "page", defaultValue = "1") int pageIndex,
                               @RequestParam(value = "limit", defaultValue = "10") int pageSize,
                               @RequestParam(value="parentGuid",defaultValue="-1") String parentGuid,
                               @RequestParam(value="orderbyClause",defaultValue=" id asc") String orderbyClause)
    {
        PageUtils list = userinfoService.queryUserInfosByParentGuid(pageIndex,pageSize,parentGuid,orderbyClause);
        return Layui.data(list.getTotalCount(),list.getList(),"OK");
    }

    @GetMapping("/queryrole")
    @ResponseBody
    public Layui queryRole(@RequestParam(value = "page", defaultValue = "1") int pageIndex,
                           @RequestParam(value = "limit", defaultValue = "10") int pageSize,
                           @RequestParam(value="roleguid",defaultValue="-1") String roleguid,
                           @RequestParam(value="orderbyClause",defaultValue=" sequence asc,id asc") String orderbyClause)
    {
        PageUtils list = userinfoService.queryUserInfosByRoleGuid(pageIndex,pageSize,roleguid,orderbyClause);
        return Layui.data(list.getTotalCount(),list.getList(),"OK");

    }

    /**
     * 保存
     **/
    @RequestMapping("save")
    @ResponseBody
    public Layui save(UserInfo userinfo) {
        int result;

        //新增需要先判断code是否存在
        if(userinfo.getId()==null || userinfo.getId()==0) {
            UserInfo entity = userinfoService.getByCode(userinfo.getAccount());

            if (entity != null && entity.getAccount() != null && entity.getAccount() != "") {
                return Layui.data(-1, null, "账号已存在");
            }
        }
        userinfo.setGuid(UUID.randomUUID().toString());
        if(userinfo.getPassword() != null && userinfo.getPassword().length() != 0)
        {
            userinfo.setPassword(DigestUtils.md5DigestAsHex(userinfo.getPassword().getBytes()));
        }
        userinfo.setIsDelete(0);
        result = userinfoService.save(userinfo);
        return Layui.data(result, null, "保存成功");
    }

    /**
     * 删除
     * @param userinfo
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public Layui deleteUserInfo(UserInfo userinfo) {

        int i = userinfoService.deleteUserInfo(userinfo);

        return Layui.data(i, null, null);
    }


    /**
     * 获取字典树
     */

    @GetMapping("getZtree")
    @ResponseBody
    public List getZtree() {

        List<ZtreeUtil> zTree = userinfoService.getUserInfoTree();

        return zTree;
    }

    /**
     * 根据角色id获取用户 显示已选
     * @param roleguid
     * @return
     */
    @GetMapping("getCheckedZtree")
    @ResponseBody
    public List getCheckedZtree(String roleguid) {
        List<ZtreeUtil> zTree = userinfoService.getCheckedZtree(roleguid);
        return zTree;
    }


    @GetMapping("updateSequence")
    @ResponseBody
    public int updateSequence(int sequence,int id)
    {
        return  userinfoService.updateSequence(sequence,id);
    }

    /**
     * 获得弹出新增页面
     *
     * @return
     */
    @GetMapping("add")
    public String add() {

        return "userinfo/add.html";
    }

    @GetMapping("getById")
    @ResponseBody
    public UserInfo getById(int id)
    {
        return  userinfoService.getById(id);
    }

    @GetMapping("getByGuid")
    @ResponseBody
    public UserInfo getByGuid(String guid)
    {
        return  userinfoService.getByGuid(guid);
    }

    @GetMapping("getCommonselector")
    public String getCommonselector() {

        return "userinfo/commonselector.html";
    }

    @GetMapping("getUserRoles")
    @ResponseBody
    public List<RoleInfo> getUserRoles(String guid)
    {
        return  roleInfoMapper.findAllByGuid(guid);
    }

    /**
     * 模拟登录
     * 只有管理员角色可以使用
     * @param username
     * @return
     */
    @GetMapping("simlogin")
    public String simlogin(String username) {
        HttpSession session = ServletUtils.getSession();
        session.removeAttribute("beyondbit-web-current-user");
        Authentication token = new UsernamePasswordAuthenticationToken(username,"");
        token = securityProvider.authenticateAdmin(token); // 登陆
        SecurityContextHolder.getContext().setAuthentication(token);
        return "redirect:/portal/index";
    }
}
