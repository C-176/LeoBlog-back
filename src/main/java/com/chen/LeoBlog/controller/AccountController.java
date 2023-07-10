package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Account;
import com.chen.LeoBlog.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/account")
@Api("账户管理")
public class AccountController {
    @Resource
    private AccountService accountService;

    @ApiOperation("根据用户id获取账户信息")
    @GetMapping("/{userId}")
    public ResultInfo getAccountByUserId(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @PathVariable Long userId) {
        return accountService.getAccountByUserId(userId);
    }

    @PostMapping("/add")
    public ResultInfo addAccount(@RequestBody Account account) {
        return accountService.addAccount(account);
    }

    @PutMapping("/update")
    public ResultInfo updateAccount(@RequestBody Account account) {
        return accountService.updateAccount(account);
    }

    @GetMapping("/all")
    public ResultInfo getAllAccounts() {
        return accountService.getAllAccounts();
    }


}
