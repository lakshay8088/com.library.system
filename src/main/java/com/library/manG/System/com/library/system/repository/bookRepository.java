package com.library.manG.System.com.library.system.repository;

import com.library.manG.System.com.library.system.models.Book;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface bookRepository extends JpaRepository<Book,Integer> {

    @Query("SELECT b FROM Book b WHERE b.title = :title")
    List<Book> findByTitle(@RequestParam String title);

    @Query(value = "select price from Book where ID = :id  ",nativeQuery = true)
    Integer findPriceById(@Param("id") int id);

    @Query(value = "select * from Book where title = :title",nativeQuery = true)
    List<Book> findBooksByTitle(@Param("title") String title);

}
