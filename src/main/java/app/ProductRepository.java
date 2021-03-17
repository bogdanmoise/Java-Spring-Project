package app;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> 
{
	@Query("select p from Product p where p.name=:name")
	public List<Product> findByName(@Param("name") String name);
	
	@Query("delete from Product p where p.name=:name")
	@Modifying
    @Transactional
	public void deleteByName(@Param("name") String name);
	
	@Query(value = "SELECT new Product(up.id, p.name, up.quantity, p.price, p.description) "
		   +"FROM UserProduct up JOIN up.product p JOIN up.user u "
		   + "ON p = up.product AND up.user = u AND u=:user")
	public List<Product> findAllBoughtProduct(@Param("user") User user);
	
	@Query(value = "UPDATE Product SET quantity=:quantity WHERE name=:name")
	@Modifying
	@Transactional
		public void updateQuantity(@Param("quantity") Integer quantity, @Param("name") String name);
}
