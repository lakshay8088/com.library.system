package com.library.manG.System.com.library.system.controllers;

import com.library.manG.System.com.library.system.DTO.RESPONSE;
import com.library.manG.System.com.library.system.Exceptions.BookNotFoundException;
import com.library.manG.System.com.library.system.Exceptions.BookPriceNegativeException;
import com.library.manG.System.com.library.system.Service.serviceCalling;
import com.library.manG.System.com.library.system.models.Book;
import com.library.manG.System.com.library.system.repository.bookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class controller {

    @Autowired
    serviceCalling service;

    @Value(("${name.value}"))
    private String name;

    private static final Logger log = LoggerFactory.getLogger(controller.class);

    //Requirements:
//Manage books with fields: id, title, author, price, publishedYear
//APIs:
//Add book
//Search book by title or author (query params)
//Update price
//Delete book
//Add validation for price > 0
//Add a global exception handler
//Store data using H2 or MySQL

        @Autowired
        bookRepository repository;

        @GetMapping("/name")
        public String getName(){
            log.info("Entered getName() method");
            return "Lakshay";
        }

        @GetMapping("/Book/Repository")
        public ResponseEntity<Object> getPriceById(@RequestParam int id){
            int priceById = repository.findPriceById(id);
            RESPONSE response=new RESPONSE("Success"," price: " + priceById);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

        @GetMapping("/getBooks")
        public ResponseEntity<Object> findBooksByTitle(@RequestParam String title){
            List<Book> booksByTitle = repository.findBooksByTitle(title);
            log.info("booksByTitle: " + booksByTitle);
            RESPONSE response = new RESPONSE("SUCCESS",booksByTitle);
            return new ResponseEntity<>(response,HttpStatus.OK);

        }

        @GetMapping("/book/{id}")
        public ResponseEntity getBookWithId(@PathVariable int id) throws BookNotFoundException {

          if (!repository.existsById(id)){
              throw new BookNotFoundException("-------not found-----");
          }
            Book book = repository.findById(id).get();
            int bookId = book.getId();

            RESPONSE response = new RESPONSE();
            response.setStatus("SUCCESS");
            response.setResponseObject("ID of book is : " + bookId);
            return new ResponseEntity(response, HttpStatus.OK);
        }

        @PostMapping("/addBook")
    public ResponseEntity<Object> addBook(@RequestBody Book book) throws BookPriceNegativeException {
           if (book.getPrice()<0){
               throw new BookPriceNegativeException("BOOk price is negative");
           }

            Book savedBook = repository.save(book);
            RESPONSE response = new RESPONSE();
            response.setStatus("SUCCESS");
            response.setResponseObject(savedBook);
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        }

        @PostMapping("/book")
            public ResponseEntity<Map<String, Object>> searchByTitle(@RequestParam String title){
            List<Book> byTitle = repository.findByTitle(title);
            Map<String,Object> response = new HashMap<>();
            response.put("STATUS","SUCCESS");
            response.put("BOOKS: " , byTitle);
            return new ResponseEntity<>(response,HttpStatus.OK);

        }

        @DeleteMapping("/delete/{id}")
            public ResponseEntity<Object> deleteBook(@PathVariable int id){
            Optional<Book> byId = repository.findById(id);
            Book book = byId.get();
            repository.deleteById(id);
            RESPONSE response = new RESPONSE();
            response.setStatus("SUCCESS");
            response.setResponseObject("Deleting :"+ book);
            return new ResponseEntity<>(response,HttpStatus.OK);

        }

        @GetMapping("/profile")
        public String getProfile(){
        return name;
        }

        @GetMapping("/pagable/{id}")
    public Page<Book> fetchBookByPagable(@PathVariable int id){
            Sort sort = Sort.by("id").ascending();
            Pageable pageable = PageRequest.of(1, 9, sort);
            return repository.findAll(pageable);

        }

        @PostMapping("/sampleHttp")
    public ResponseEntity<String> calling() throws Exception {
            return ResponseEntity.ok(service.call3PartyApi());
        }


    }
