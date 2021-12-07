package com.kisswe.scotland.database;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Table("users")
public class User {
    @Id
    private Long id;
    private String nickname;
    private String name;
    private String email;
    private List<Role> roles;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Version
    private Long version;

    public enum Role {
        COMMON, OWNER, ADMIN
    }
}
