package io.jzheaux.springsecurity.resolutions;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ResolutionController {
	private final ResolutionRepository resolutions;

	public ResolutionController(ResolutionRepository resolutions) {
		this.resolutions = resolutions;
	}

	@GetMapping("/resolutions")
	public Iterable<Resolution> read() {
		return this.resolutions.findAll();
	}

	@GetMapping("/resolution/{id}")
	public Optional<Resolution> read(@PathVariable("id") UUID id) {
		return this.resolutions.findById(id);
	}

/* 
	Following that, go into ResolutionController and update the make method to include the current username as a method parameter:
	In this task, you've used the most advanced version of @CurrentSecurityContext; however, there are simpler incarnations. For example, you can inline @CurrentSecurityContext in the method itself, and without an expression:
	
	public Resolution make(@CurrentSecurityContext SecurityContext ctx, @RequestBody String text) {
    	User user = (User) ctx.getAuthentication().getPrincipal(); 
    
		Or, you can provide an expression in the inlined annotation:
	
	public Resolution make(@CurrentSecurityContext(expression="authentication.name") String owner, @RequestBody String text) {
    	Resolution resolution = new Resolution(text, owner);
    	return this.resolutions.save(resolution);
}	
*/
	@PostMapping("/resolution")
	public Resolution make(@CurrentUsername String owner, @RequestBody String text) {
		// String owner = "user";
		Resolution resolution = new Resolution(text, owner);
		return this.resolutions.save(resolution);
	}

	@PutMapping(path="/resolution/{id}/revise")
	@Transactional
	public Optional<Resolution> revise(@PathVariable("id") UUID id, @RequestBody String text) {
		this.resolutions.revise(id, text);
		return read(id);
	}

	@PutMapping("/resolution/{id}/complete")
	@Transactional
	public Optional<Resolution> complete(@PathVariable("id") UUID id) {
		this.resolutions.complete(id);
		return read(id);
	}
}
