package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Account;
import com.chen.LeoBlog.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/account")
public class AccountController {
    @Resource
    private AccountService accountService;


    @GetMapping("/{userId}")
    public ResultInfo getAccountByUserId(@PathVariable Long userId) {
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
