package app;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String name;
	
	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private Double price;
	
	@Column(nullable = false)
	private String description;
	
	@OneToMany(mappedBy = "product")
    Set<UserProduct> user;
	
	
	
	public Product(String name, Integer quantity, Double price, String description) {
		this.name = name;
		this.quantity = quantity;
		this.price = price;
		this.description = description;
	}

	
	
	public Product(Long id, String name, Integer quantity, Double price, String description) {
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.price = price;
		this.description = description;
	}



	public Set<UserProduct> getUser() {
		return user;
	}

	public void setUser(Set<UserProduct> user) {
		this.user = user;
	}

	public Product(Integer quantity)
	{
		this.name = "";
		this.quantity = quantity;
		this.price = 0.0;
		this.description = "";
	}
	
	public Product()
	{
		
	}

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Integer getQuantity() {
		return quantity;
	}


	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}


	public Double getPrice() {
		return price;
	}


	public void setPrice(Double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
