package app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, Long> {

	@Query("delete from UserProduct p where p.product=:product")
	@Modifying
    @Transactional
	public void deleteByProductId(@Param("product") Product product);
}
