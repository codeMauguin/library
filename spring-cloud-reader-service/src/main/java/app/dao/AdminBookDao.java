package app.dao;

import app.pojo.Book;
import app.pojo.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminBookDao {
	Boolean insert(@Param("book") Book book);
	
	Boolean initStock(@Param("inventory") Inventory inventory);
	
	Boolean updateStock(@Param("id") Long id, @Param("stock") Integer stock);
	
	Boolean updateBook(@Param("book") Book book);
	
	@Update("UPDATE lb_book SET state = #{state} WHERE id = #{id}")
	Boolean updateBookState(@Param("id") Long id, @Param("state") int state);
}
