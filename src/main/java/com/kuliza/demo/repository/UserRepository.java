package com.kuliza.demo.repository;

import com.kuliza.demo.model.userDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<userDetails, String> {
    @Query("Select u from userDetails u where u.user_name=:name")
    userDetails findByUser_name(@Param("name") String user_name);

    @Query("SELECT CASE WHEN COUNT(ud) > 0 THEN true ELSE false END FROM userDetails ud WHERE ud.user_name = :un")
    boolean fetchByUserName(@Param("un") String name);

}
