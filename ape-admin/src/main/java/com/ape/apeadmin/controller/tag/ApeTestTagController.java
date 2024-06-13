package com.ape.apeadmin.controller.tag;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeTestTag;
import com.ape.apesystem.service.ApeTestTagService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 题目标签controller
 * @date 2024/05/13 04:27
 */
@Controller
@ResponseBody
@RequestMapping("tag")
public class ApeTestTagController {

    @Autowired
    private ApeTestTagService apeTestTagService;

    /** 分页获取题目标签 */
    @Log(name = "分页获取题目标签", type = BusinessType.OTHER)
    @PostMapping("getApeTestTagPage")
    public Result getApeTestTagPage(@RequestBody ApeTestTag apeTestTag) {
        Page<ApeTestTag> page = new Page<>(apeTestTag.getPageNumber(),apeTestTag.getPageSize());
        QueryWrapper<ApeTestTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeTestTag.getName()),ApeTestTag::getName,apeTestTag.getName());
        Page<ApeTestTag> apeTestTagPage = apeTestTagService.page(page, queryWrapper);
        return Result.success(apeTestTagPage);
    }

    @GetMapping("getTagList")
    public Result getTagList() {
        List<ApeTestTag> tagList = apeTestTagService.list();
        return Result.success(tagList);
    }

    /** 根据id获取题目标签 */
    @Log(name = "根据id获取题目标签", type = BusinessType.OTHER)
    @GetMapping("getApeTestTagById")
    public Result getApeTestTagById(@RequestParam("id")String id) {
        ApeTestTag apeTestTag = apeTestTagService.getById(id);
        return Result.success(apeTestTag);
    }

    /** 保存题目标签 */
    @Log(name = "保存题目标签", type = BusinessType.INSERT)
    @PostMapping("saveApeTestTag")
    public Result saveApeTestTag(@RequestBody ApeTestTag apeTestTag) {
        boolean save = apeTestTagService.save(apeTestTag);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑题目标签 */
    @Log(name = "编辑题目标签", type = BusinessType.UPDATE)
    @PostMapping("editApeTestTag")
    public Result editApeTestTag(@RequestBody ApeTestTag apeTestTag) {
        boolean save = apeTestTagService.updateById(apeTestTag);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除题目标签 */
    @GetMapping("removeApeTestTag")
    @Log(name = "删除题目标签", type = BusinessType.DELETE)
    public Result removeApeTestTag(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeTestTagService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("题目标签id不能为空！");
        }
    }

}