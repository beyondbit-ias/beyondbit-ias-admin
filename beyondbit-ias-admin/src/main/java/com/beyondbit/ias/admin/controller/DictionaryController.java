package com.beyondbit.ias.admin.controller;

import com.beyondbit.ias.core.common.ZtreeUtil;
import com.beyondbit.ias.core.entity.Dictionary;
import com.beyondbit.ias.core.common.Layui;
import com.beyondbit.ias.core.common.PageUtils;
import com.beyondbit.ias.core.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DictionaryController class
 * 数据字典Controller类
 *
 * @author song
 * @date 2019/5/13
 */
@CrossOrigin
@Controller
@RequestMapping
public class DictionaryController {
    @Autowired
    DictionaryService dictionaryServiceimpl;

    /**
     * 字典首页面入口
     *
     * @return
     */
    @GetMapping("dictionary/list")
    public String list() {

        return "dictionary/list.html";
    }

    /**
     * 获得弹出新增页面
     *
     * @return
     */
    @GetMapping("dictionary/add")
    public String add() {

        return "dictionary/add.html";
    }

    /**
     * 获得弹出ztree页面
     *
     * @return
     */
    @GetMapping("dictionary/getCommonselector")
    public String getCommonselector() {

        return "dictionary/commonselector.html";
    }

    /**
     * 获取所有字典列表
     *
     * @return
     */
    @GetMapping("findAll")
    @ResponseBody
    public Layui findAllDictionary(Dictionary dictionary) {
        List<Dictionary> dictionaries = dictionaryServiceimpl.findAllDictionary(dictionary);
        return Layui.data(1, dictionaries, null);
    }

    @GetMapping("dictionary/queryChildren")
    @ResponseBody
    public Layui queryChildren( @RequestParam(value = "page", defaultValue = "1") int pageIndex,
                                @RequestParam(value = "limit", defaultValue = "10") int pageSize,
                                @RequestParam(value="parentId",defaultValue="1") int parentId,
                                @RequestParam(value="orderbyClause",defaultValue=" id asc") String orderbyClause)
    {
        PageUtils list = dictionaryServiceimpl.queryDictsByParentId(pageIndex,pageSize,parentId,orderbyClause);
        return Layui.data(list.getTotalCount(),list.getList(),"OK");
    }

    /**
     * 修改的回显功能
     *
     * @param dictionary
     * @return
     */
    @GetMapping("findChilds")
    @ResponseBody
    public List<Dictionary> findDictsByParentId(Dictionary dictionary) {
        return dictionaryServiceimpl.findDictsByParentId(dictionary);
    }
    //根据字典项父节点code获取所有
    @GetMapping("findChildsByCode")
    @ResponseBody
    public List<Dictionary> findDictsByParentCode(String code) {
        return dictionaryServiceimpl.findDictsByParentCode(code);
    }

    /**
     * 保存
     **/
    @RequestMapping("dictionary/save")
    @ResponseBody
    public Layui save(Dictionary dictionary) {
        int result;

        //新增需要先判断code是否存在
        if(dictionary.getId()==null) {
            Dictionary entity = dictionaryServiceimpl.getByCode(dictionary.getCode());

            if (entity != null && entity.getCode() != null && entity.getCode() != "") {
                return Layui.data(-1, null, "编码已存在");
            }
        }
            result = dictionaryServiceimpl.save(dictionary);
            return Layui.data(result, null, "保存成功");




    }

    /**
     * 修改字典
     *
     * @param dictionary
     * @return
     */
    @RequestMapping("dictionary/updateDictionary")
    @ResponseBody
    public Layui updateDictionary(Dictionary dictionary) {

        int i = dictionaryServiceimpl.updateDictionary(dictionary);

        return Layui.data(i, null, null);
    }

    /**
     * 新增字典项
     *
     * @param dictionary
     * @return
     */

    @RequestMapping("dictionary/insertDictionary")
    @ResponseBody
    public Layui insertDictionary(Dictionary dictionary) {

        int i = dictionaryServiceimpl.insertDictionary(dictionary);

        return Layui.data(i, null, null);
    }

    /**
     * 删除字典
     *
     * @param dictionary
     * @return
     */
    @RequestMapping("dictionary/delete")
    @ResponseBody
    public Layui deleteDictionary(Dictionary dictionary) {

        int i = dictionaryServiceimpl.deleteDictionary(dictionary);

        return Layui.data(i, null, null);
    }

    /**
     * 获取字典树
     */

    @GetMapping("dictionary/getZtree")
    @ResponseBody
    public List getDictionaryTree() {

        List<ZtreeUtil> dictsTree = dictionaryServiceimpl.getDictsTree();

        return dictsTree;
    }

    /**
     * 获取字典树
     */

    @GetMapping("dictionary/getZtreeByCode")
    @ResponseBody
    public List getZtreeByCode(String code) {

        List<ZtreeUtil> dictsTree = dictionaryServiceimpl.getDictsTreeByCode(code);

        return dictsTree;
    }




    /**对外接口
     * 需要传入父节点parentid或者编码code
     * 获取字典树子节点
     */

    @GetMapping("dictionary/getChildNodes")
    @ResponseBody
    public List getChildNodes(Dictionary dictionary) {

        List<Dictionary> allNodes = dictionaryServiceimpl.findAllDictionary(dictionary);

        return allNodes;
    }

    /**对外接口
     * 需要传入父节点编码code
     * 获取字典树子节点
     */

    @GetMapping("dictionary/getChildNodesByCode")
    @ResponseBody
    public List getChildNodesByCode(String code) {

        List<Dictionary> allNodes = dictionaryServiceimpl.findDictsByParentCode(code);

        return allNodes;
    }

    @GetMapping("dictionary/getById")
    @ResponseBody
    public Dictionary getById(int id)
    {
        return  dictionaryServiceimpl.getById(id);
    }

    @GetMapping("dictionary/updateSequence")
    @ResponseBody
    public int updateSequence(int sequence,int id)
    {
        return  dictionaryServiceimpl.updateSequence(sequence,id);
    }



}
