package com.crm.system.modules.lead.controller;

import com.crm.system.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 线索测试 Controller
 */
@RestController
@RequestMapping("/lead/test")
public class LeadTestController {

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Lead Controller 正常工作！");
    }
}
