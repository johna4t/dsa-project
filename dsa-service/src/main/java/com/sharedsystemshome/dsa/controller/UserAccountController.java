package com.sharedsystemshome.dsa.controller;

import com.sharedsystemshome.dsa.security.enums.RoleType;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.service.UserAccountService;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/user-accounts")
public class UserAccountController {

    private final UserAccountService userService;
    private final UserContextService userContext;
    @Autowired
    public UserAccountController(
            UserAccountService userService,
            UserContextService userContext){

        this.userService = userService;
        this.userContext = userContext;

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public ResponseEntity<Long> postUserAccount(RequestEntity<UserAccount> request){

        try{
            return ResponseEntity.status(201).body(this.userService.createUserAccount(request.getBody()));
        } catch(Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<List<UserAccount>> getUserAccounts(RequestEntity<Map<String, String>> requestEntity) {
        Map<String, String> queryParams = requestEntity.getBody();

        if (null == queryParams || 0 == queryParams.size()) {
            // No query params passed, so run default query

            if(this.userContext.isSuperAdmin()){

                return getUserAccountsResponse(this.userService.getUserAccounts());

            } else {

                return getUserAccountsResponse(
                        this.userService.getUserAccountsByCustomerId(
                                this.userContext.getCurrentUser().getParentAccount().getId()));
            }
        } else {
            throw new BadRequestException("Unrecognized query parameter.");
        }
    }


    private ResponseEntity<List<UserAccount>> getUserAccountsResponse(List<UserAccount> users){

        if(null != users && !users.isEmpty()){

            return ResponseEntity.status(200).body(users);

        } else {

            return ResponseEntity.status(204).build();

        }

    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(path = "{id}")
    public ResponseEntity<UserAccount> getUserAccountById(@PathVariable("id") Long id) {

        if(this.userContext.isSuperAdmin()){
            return getUserAccountByIdAsSuperAdmin(id);
        }

        if(this.userContext.isAccountAdmin()){
            return getUserAccountByIdAsAccountAdmin(id);
        }

        return getUserAccountByIdAsMember();

    }

    private ResponseEntity<UserAccount> getUserAccountByIdAsSuperAdmin(Long id){

        try {

            return ResponseEntity.status(200).body(
                    this.userService.getUserAccountById(id));

        } catch (EntityNotFoundException e) {

            return ResponseEntity.status(204).build();

        }

    }

    private ResponseEntity<UserAccount> getUserAccountByIdAsAccountAdmin(Long id){

        try {

            return ResponseEntity.status(200).body(
                    this.userService.getUserAccountByIds(
                            id, this.userContext.getCurrentCustomerAccountId()));

        } catch (EntityNotFoundException e) {

            return ResponseEntity.status(204).build();

        }

    }

    private ResponseEntity<UserAccount> getUserAccountByIdAsMember(){

        try {

            return ResponseEntity.status(200).body(
                    this.userService.getUserAccountById(
                            this.userContext.getCurrentUser().getId()));

        } catch (EntityNotFoundException e) {

            return ResponseEntity.status(204).build();

        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping(path = "{id}")
    public ResponseEntity<UserAccount> putUserAccount(
            @PathVariable("id") Long id,
            RequestEntity<UserAccount> request){

        if(this.userContext.isSuperAdmin()){
            this.userService.updateUserAccount(id, request.getBody());
        }

        if(this.userContext.isAccountAdmin()){
            Long custId = this.userContext.getCurrentCustomerAccountId();
            this.userService.updateUserAccount(id, custId, request.getBody());
        }

        return ResponseEntity.status(204).build();

    }


    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> deleteUserAccount(@PathVariable("id") Long id){

        if(this.userContext.getCurrentUser().getId().equals(id)) return ResponseEntity
                .ok(Map.of("message", "Operation ignored. You cannot delete your own user account."));

        if(this.userContext.isSuperAdmin()){
            this.userService.deleteUserAccount(id);
        }

        if(this.userContext.isAccountAdmin()){
            Long custId = this.userContext.getCurrentCustomerAccountId();
            this.userService.deleteUserAccount(id, custId);
        }

        return ResponseEntity.status(204).build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getUserAccountsCountByRoleAndCustomerAccount(
            @RequestParam String roleName,
            @RequestParam Long parentAccountId
    ) {

        RoleType roleType;

        try {
            roleType = RoleType.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role name: " + roleName);
        }

        Long count = this.userService.countByRoleAndCustomerAccount(roleType, parentAccountId);
        return ResponseEntity.ok(count);
    }

}
