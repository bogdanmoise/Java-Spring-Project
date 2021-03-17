package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppController
{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired 
	private ProductRepository prodRep;

	@Autowired
	private UserProductRepository upRep;
	
	@GetMapping("/")
	public String viewHomePage()
	{
		return "start_page";
	}
	
	@GetMapping("/register")
	public String register(Model model)
	{
		model.addAttribute("user", new User());
		
		return "register_page";
	}
	
	@PostMapping("/process_registration")
	public String processRegistration(User user)
	{
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		
		userRepository.save(user);
		return "register_confirmation";
	}
	
	@GetMapping("/home")
	public String homeMode(Model model, Authentication authentication)
	{
		List<Product> productList = prodRep.findAll();
		model.addAttribute("productList", productList);
		
		User user = userRepository.findByUsername(authentication.getName());
		
		model.addAttribute("welcomeMessage", "Welcome to the Home Page, " + user.getUsername() +"!!!");
		
		return "home_page";
	}
	
	@GetMapping("/login")
	public String loginIn()
	{
		return "login_page";
	}
	
	@GetMapping("/additem")
	public String addItem(Model model)
	{
		model.addAttribute("product", new Product());
		
		return "additem_page";
	}
	
	@PostMapping("/save")
	public String saveProduct(@ModelAttribute("product") Product product)
	{
		prodRep.save(product);
		return "redirect:/home";
	}
	
	@GetMapping("/delete/{name}")
	public String deleteItem(@PathVariable(name = "name") String name)
	{
		upRep.deleteByProductId(prodRep.findByName(name).get(0));
		prodRep.deleteByName(name);
		return "redirect:/home";
	}
	
	@GetMapping("/edit/{name}")
	public String editItem(@PathVariable(name = "name") String name, Model model)
	{
		Product editProduct = prodRep.findByName(name).get(0);
		model.addAttribute("product", editProduct);
		return "product_edit";
	}

	@GetMapping("/details/{name}")
	public String accessItemDetails(@PathVariable(name = "name") String name, Model model)
	{
		Product detailedproduct = prodRep.findByName(name).get(0);
		model.addAttribute("product", detailedproduct);
		
		model.addAttribute("boughtQuantity", new Product());
				
		return "/item_details";
	}
	
	@PostMapping("/buy/{name}")
	public String buyProduct(@PathVariable(name = "name") String name, Product boughtQuantity, RedirectAttributes redirectAttributes, Authentication authentication)
	{
		Product detailedproduct = prodRep.findByName(name).get(0);
		
		int quant = boughtQuantity.getQuantity();
		
		if(quant > detailedproduct.getQuantity())
		{
			redirectAttributes.addFlashAttribute("errorMessage", "There is not enough " + name +" in stock!!!");
		}
		else
		{
			User loggedUser = userRepository.findByUsername(authentication.getName());
			detailedproduct.setQuantity(detailedproduct.getQuantity() - quant);
			UserProduct up = new UserProduct(loggedUser, detailedproduct, quant);
			upRep.save(up);
			prodRep.save(detailedproduct);
			
		}
		
		return "redirect:/details/{name}";
	}
	
	@GetMapping("/cart")
	public String cartMode(Model model, Authentication authentication)
	{
		User user = userRepository.findByUsername(authentication.getName());
		List<Product> productList = prodRep.findAllBoughtProduct(user);
		Double totalSum = productList.stream().map(a -> a.getQuantity() * a.getPrice()).reduce(0.0d, (a, b) -> a + b);
		model.addAttribute("productList", productList);
		model.addAttribute("sum", totalSum);
		model.addAttribute("welcomeMessage", "Welcome to your cart, " + user.getUsername() +"!!!");
		
		return "cart_page";
	}
	
	@PostMapping("/deleteItem/{name}")
	public String deleteCartItem(@ModelAttribute("deletedProduct") Product deletedProduct, Authentication authentication)
	{
		User user = userRepository.findByUsername(authentication.getName());
		int returnedQuantity = deletedProduct.getQuantity();
		int remainQuantity = prodRep.findByName(deletedProduct.getName()).get(0).getQuantity();
		prodRep.updateQuantity(returnedQuantity + remainQuantity, deletedProduct.getName());
		upRep.deleteById(deletedProduct.getId());
		return "redirect:/cart";
	}
	
	@GetMapping("/logout")
	public void loginOut(HttpServletRequest request, HttpServletResponse resp, Authentication authentication) throws ServletException, IOException
	{
		request.logout();
		Principal principal = request.getUserPrincipal();
	    if (principal != null) 
	    {
	    	throw new RuntimeException("ERROR: Logout operation failed for user " + authentication.getName() + "!!!");
	    }
	    resp.sendRedirect("/");
	}
}
