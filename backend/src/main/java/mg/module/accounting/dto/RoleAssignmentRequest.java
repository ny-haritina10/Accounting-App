package mg.module.accounting.dto;

import java.util.List;

public class RoleAssignmentRequest {
    
    private Long userId;
    private List<Long> roleIds;

    // getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}