package io.jzheaux.springsecurity.resolutions;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

// import java.util.UUID;

@Component
public class ResolutionInitializer implements SmartInitializingSingleton {
	private final ResolutionRepository resolutions;
	private final UserRepository users;


	public ResolutionInitializer(ResolutionRepository resolutions, UserRepository users) {
		this.resolutions = resolutions;
		this.users = users;
	}

	@Override
	public void afterSingletonsInstantiated() {
		this.resolutions.save(new Resolution("Read War and Peace", "user"));
		this.resolutions.save(new Resolution("Free Solo the Eiffel Tower", "user"));
		this.resolutions.save(new Resolution("Hang Christmas Lights", "user"));

		User user = new User("user",
				"{bcrypt}$2a$10$fkYoT0rToCLS7e8TJP2JCOTBtcjWCwhuEs6cDfqhiyGrOTU9Dq77m");
		user.setFullName("User Userson");
		user.grantAuthority("resolution:read");
		user.grantAuthority("user:read");
		this.users.save(user);

		//User user = new User("user", "{bcrypt}$2a$10$3njzOWhsz20aimcpMamJhOnX9Pb4Nk3toq8OO0swIy5EPZnb1YyGe");
		//user.grantAuthority("resolution:read");
		//this.users.save(user);

		User hasRead = new User("hasread", 
			"{bcrypt}$2a$10$fkYoT0rToCLS7e8TJP2JCOTBtcjWCwhuEs6cDfqhiyGrOTU9Dq77m");
		hasRead.setFullName("Has Read");
		hasRead.grantAuthority("resolution:read");
		hasRead.grantAuthority("user:read");
		this.users.save(hasRead);

		User hasWrite = new User("haswrite",  
			"{bcrypt}$2a$10$fkYoT0rToCLS7e8TJP2JCOTBtcjWCwhuEs6cDfqhiyGrOTU9Dq77m");
		hasWrite.setFullName("Has Write");
		hasWrite.addFriend(hasRead);
		hasWrite.setSubscription("premium");
		hasWrite.grantAuthority("resolution:write");
		hasWrite.grantAuthority("user:read");
		this.users.save(hasWrite);	

		
		User admin = new User("admin", "{bcrypt}$2a$10$3njzOWhsz20aimcpMamJhOnX9Pb4Nk3toq8OO0swIy5EPZnb1YyGe");
		admin.setFullName("Admin Adminson");
		admin.grantAuthority("ROLE_ADMIN");
		this.users.save(admin);

		Iterable<User> users_list = this.users.findAll();
	    for (User x : users_list){
			System.out.println(x.toString());
			String fullName = this.users.findByUsername(x.getUsername())
			  	.map(User::getFullName).orElse("Anonymous");
		}

	}
}
