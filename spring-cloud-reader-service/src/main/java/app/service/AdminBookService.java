package app.service;

import Message.RestResponse;
import app.pojo.Book;

import java.io.InputStream;
import java.util.List;


public interface AdminBookService {
	/**
	 * Uploads a list of books to the server.
	 *
	 * @param tasks The list of books to be uploaded.
	 * @return A boolean value.
	 */
	Boolean uploadBooks(List<Book> tasks);
	
	Boolean shelvesBooks(Long id);
	
	Boolean takeDownBooks(Long id);
	
	Boolean updateBooks(Book book);
	
	List<Object> report();
	
	RestResponse batchUpload(InputStream inputStream, boolean equals);
}
