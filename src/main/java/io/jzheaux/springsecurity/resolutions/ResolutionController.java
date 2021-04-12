package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ResolutionController {
	private final ResolutionRepository resolutions;
	// private final UserRepository users;
	UserService users; //instead of UserRepository

	//public ResolutionController(ResolutionRepository resolutions, UserRepository users) {
	public ResolutionController(ResolutionRepository resolutions, UserService users) {
			this.resolutions = resolutions;
		this.users = users;
	}
	// Since you are now using Bearer Tokens, you no longer need to pass credentials from the browser to the API. This means that you can remove the allowCredentials from the @CrossOrigin annotation, making your application more secure.
	//@CrossOrigin(allowCredentials = "true")
	@CrossOrigin // (maxAge = 0) if locally verifying
	@PreAuthorize("hasAuthority('resolution:read')")
	@PostFilter("@post.filter(#root)")
	@GetMapping("/resolutions")
	public Iterable<Resolution> read() {
		Iterable<Resolution> resolutions = this.resolutions.findAll();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("user:read"))) {
			for (Resolution resolution : resolutions) {
				//String fullName = this.users.findByUsername(resolution.getOwner())
				//		.map(User::getFullName).orElse("Anonymous");
				String fullName = this.users.getFullName(resolution.getOwner())
					.orElse("Anonymous");
				
				resolution.setText(resolution.getText() + ", by " + fullName);
			}
		}
		return resolutions;
	}

	@PreAuthorize("hasAuthority('resolution:read')")
	@PostAuthorize("@post.authorize(#root)")
	@GetMapping("/resolution/{id}")
	public Optional<Resolution> read(@PathVariable("id") UUID id) {
		return this.resolutions.findById(id);
	}

	@PreAuthorize("hasAuthority('resolution:write')")
	@PostMapping("/resolution")
	public Resolution make(@CurrentUsername String owner, @RequestBody String text) {
		Resolution resolution = new Resolution(text, owner);
		return this.resolutions.save(resolution);
	}

	@PreAuthorize("hasAuthority('resolution:write')")
	@PostAuthorize("@post.authorize(#root)")
	@PutMapping(path="/resolution/{id}/revise")
	@Transactional
	public Optional<Resolution> revise(@PathVariable("id") UUID id, @RequestBody String text) {
		this.resolutions.revise(id, text);
		return read(id);
	}

	@PreAuthorize("hasAuthority('resolution:write')")
	@PostAuthorize("@post.authorize(#root)")
	@PutMapping("/resolution/{id}/complete")
	@Transactional
	public Optional<Resolution> complete(@PathVariable("id") UUID id) {
		this.resolutions.complete(id);
		return read(id);
	}
	
	@PreAuthorize("hasAuthority('resolution:share')")
	@PostAuthorize("@post.authorize(#root)")
	@PutMapping("/resolution/{id}/share")
	@Transactional
	public Optional<Resolution> share(@AuthenticationPrincipal User user, @PathVariable("id") UUID id) {
		Optional<Resolution> resolution = read(id);
		resolution.filter(r -> r.getOwner().equals(user.getUsername()))
				.map(Resolution::getText).ifPresent(text -> {
					for (User friend : user.getFriends()) {
						make(friend.getUsername(), text);
					}
				});
		return resolution;
	}
}