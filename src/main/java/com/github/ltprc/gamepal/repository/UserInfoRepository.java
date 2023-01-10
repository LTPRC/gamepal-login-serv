package com.github.ltprc.gamepal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.ltprc.gamepal.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    @Query(value = "select new UserInfo(id, userId, username, password, status, timeCreated, timeUpdated) from UserInfo where id=:id")
    public List<UserInfo> queryUserInfoById(@Param("id") Long id);

    @Query(value = "select new UserInfo(id, userId, username, password, status, timeCreated, timeUpdated) from UserInfo where userId=:userId")
    public List<UserInfo> queryUserInfoByUserid(@Param("userId") String userId);

    @Query(value = "select new UserInfo(id, userId, username, password, status, timeCreated, timeUpdated) from UserInfo where username=:username")
    public List<UserInfo> queryUserInfoByUsername(@Param("username") String username);

    @Query(value = "select new UserInfo(id, userId, username, password, status, timeCreated, timeUpdated) from UserInfo where username=:username and password=:password")
    public List<UserInfo> queryUserInfoByUsernameAndPassword(@Param("username") String username,
            @Param("password") String password);
}
