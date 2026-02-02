package com.example.demo.repository;

import com.example.demo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepo extends JpaRepository<Todo, Long> {
    
    /**
     * 查找已完成的待办事项
     */
    @Query("SELECT t FROM Todo t WHERE t.done = true")
    List<Todo> findByDone();
    
    /**
     * 根据文本内容模糊查找待办事项
     */
    @Query("SELECT t FROM Todo t WHERE t.text LIKE %:keyword%")
    List<Todo> findByTextContaining(@Param("keyword") String keyword);
    
    /**
     * 使用原生SQL查询已完成的待办事项
     */
    @Query(value = "SELECT * FROM todo WHERE done = ?1", nativeQuery = true)
    List<Todo> findByDoneNative(boolean done);
    
    /**
     * 使用原生SQL根据文本内容模糊查找待办事项
     */
    @Query(value = "SELECT * FROM todo WHERE text LIKE CONCAT('%',?1,'%')", nativeQuery = true)
    List<Todo> findByTextContainingNative(String keyword);
    
    /**
     * 更新待办事项的完成状态
     */
    @Modifying
    @Query("UPDATE Todo t SET t.done = :done WHERE t.id = :id")
    int updateTodoStatus(@Param("id") Long id, @Param("done") boolean done);
    
    /**
     * 根据ID删除待办事项
     */
    @Modifying
    @Query("DELETE FROM Todo t WHERE t.id = :id")
    int deleteByIdCustom(@Param("id") Long id);
    
    /**
     * 获取待办事项总数
     */
    @Query("SELECT COUNT(t) FROM Todo t")
    Long countTodos();
}