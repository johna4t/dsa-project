package com.sharedsystemshome.dsa.security.service;

import com.sharedsystemshome.dsa.model.Owned;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.security.enums.RoleType;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import com.sharedsystemshome.dsa.util.NullOrEmptyValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.sharedsystemshome.dsa.util.BusinessValidationException.*;

@Service
public class UserContextService {

    private static final Logger logger = LoggerFactory.getLogger(UserContextService.class);
    /**
     * Sets the authenticated user in SecurityContextHolder
     */
    public void setAuthenticatedUser(UserAccount user, Long customerAccountId) {
        // ✅ Use the UserAccount directly (since it implements UserDetails)
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        authToken.setDetails(customerAccountId); // Store CustomerAccount ID in details
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    public Authentication getUserContext() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public UserAccount getCurrentUser() {
        Authentication auth = getUserContext();

        if (auth == null
                || !(auth.getPrincipal() instanceof UserAccount)
                || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new SecurityValidationException(
                    "No authenticated " + USER_ACCOUNT + " found in SecurityContext.");
        }

        return (UserAccount) auth.getPrincipal();

    }

    public String getCurrentUserName() {

        return getCurrentUser().getUsername();

    }

    public Long getCurrentCustomerAccountId() {

        CustomerAccount cust = getCurrentUser().getParentAccount();

        if (null == cust) {
            throw new SecurityValidationException(
                    "No " + CUSTOMER_ACCOUNT + " associated with " + USER_ACCOUNT + ".");
        }

        return cust.getId();
    }


    public List<String> getCurrentUserRoles() {
        List<String> userRoles = getUserContext().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (null == userRoles || userRoles.isEmpty()) {
            throw new SecurityValidationException(
                    "No " + ROLE + "s associated with " + USER_ACCOUNT + ".");
        }

        return userRoles;
    }

    public Boolean isAuthorised(String authority) {
        return getCurrentUserRoles().contains(authority);
    }

    private String getRoleName(String roleType) {
        return "ROLE_" + roleType;
    }

    public Boolean isSuperAdmin() {
        return isAuthorised(getRoleName(RoleType.SUPER_ADMIN.name()));
    }

    public Boolean isAccountAdmin() {
        return isAuthorised(getRoleName(RoleType.ACCOUNT_ADMIN.name()));
    }

    public Boolean isMember() {
        return isAuthorised(getRoleName(RoleType.MEMBER.name()));
    }

    public Boolean isAssociate() {
        return isAuthorised(getRoleName(RoleType.ASSOCIATE.name()));
    }

    public Boolean isUser() {
        return isAuthorised(getRoleName(RoleType.USER.name()));
    }

    public <T extends Owned> T validateAccess(T ownedObject) throws SecurityValidationException {
        if (ownedObject == null) {
            throw new NullOrEmptyValueException("Unknown entity");
        }

        Long accountId = this.getCurrentCustomerAccountId();
        Long ownerId = ownedObject.ownerId();

        if (accountId != null && accountId.equals(ownerId) || this.isSuperAdmin()) {
            return ownedObject;
        }

        String msg = String.format(
                "Record with id %s does not exist for Customer with id %s",
                ownedObject.objectId(),
                accountId
        );

        logger.error(msg);
        throw new SecurityValidationException(msg);
    }
}
