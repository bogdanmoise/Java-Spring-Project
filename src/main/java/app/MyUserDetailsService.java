package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class MyUserDetailsService implements UserDetailsService
{
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		User userData = userRepository.findByUsername(username);
		if(userData == null)
		{
			throw new UsernameNotFoundException("ERROR : The user " + username + " does not exist!");
		}
		else
		{
			return new MyUserDetails(userData);
		}
	}
}
