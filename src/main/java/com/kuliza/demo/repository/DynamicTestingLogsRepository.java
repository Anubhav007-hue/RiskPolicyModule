package com.kuliza.demo.repository;

import com.kuliza.demo.model.DynamicTestingLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface DynamicTestingLogsRepository extends JpaRepository<DynamicTestingLogs,Long> {

    @Query("Select d from DynamicTestingLogs d where d.user_name=:name")
    List<DynamicTestingLogs> fetcgDetails(@Param("name") String name);
}
