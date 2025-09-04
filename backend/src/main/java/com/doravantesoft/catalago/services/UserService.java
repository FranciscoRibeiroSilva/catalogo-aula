package com.doravantesoft.catalago.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doravantesoft.catalago.DTO.RoleDTO;
import com.doravantesoft.catalago.DTO.UserDTO;
import com.doravantesoft.catalago.DTO.UserInsertDTO;
import com.doravantesoft.catalago.DTO.UserUpdateDTO;
import com.doravantesoft.catalago.entities.Role;
import com.doravantesoft.catalago.entities.User;
import com.doravantesoft.catalago.projections.UserDetailsProjection;
import com.doravantesoft.catalago.repositories.RoleRepository;
import com.doravantesoft.catalago.repositories.UserRepository;
import com.doravantesoft.catalago.services.exceptions.DatabaseException;
import com.doravantesoft.catalago.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService implements UserDetailsService{
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private AuthService authService;
	
	@Transactional(readOnly = true)//ou faz tudo ou não faz nada (readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable){
		Page <User> list = repository.findAll(pageable);
		
		return list.map(x -> new UserDTO(x));
		
		//transforma a lista de categoria em uma stream, e para cada elemento da stream um novo CategoryDTO é instanciado
		//depois usando o collerct é transforma de novo em list e retornado como uma lista de CategoyDTO
		//return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
		
		/**
		List <CategoryDTO> listDto = new ArrayList<>();
		for (Category cat : list) {
			listDto.add(new CategoryDTO(cat));
		}
		return listDto;
		*/
	}
		
	/**
	 *Optional evita o trabalho com valores nulos
	 */
	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = repository.findById(id);
		User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new UserDTO(entity);
	}
	
	@Transactional(readOnly = true)
	public UserDTO findMy() {
		User entity = authService.authenticated();
		return new UserDTO(entity);
	}
	
	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		copyDtoToEntity(dto, entity);
		
		entity.getRoles().clear();
		Role role = roleRepository.findByAuthority("ROLE_OPERATOR");
		entity.getRoles().add(role);
		
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		entity = repository.save(entity);
		return new UserDTO(entity);
	}
	
	@Transactional 
	public UserDTO update(Long id, UserUpdateDTO dto) {
		try {
			User entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new UserDTO(entity);			
		}
		catch(EntityNotFoundException e){
			throw new ResourceNotFoundException("id not found "+id);
		}
	}

	//@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if(!repository.existsById(id)) {
			throw new ResourceNotFoundException("id resource not found");
		}
		try {
			repository.deleteById(id);
		}
		catch(DataIntegrityViolationException e){
			throw new DatabaseException("integrity violation");
		}
		
	}
	
	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
		
		entity.getRoles().clear();
		for(RoleDTO roleDTO: dto.getRoles()) {
			Role role = roleRepository.getReferenceById(roleDTO.getId());
			entity.getRoles().add(role);
			//entity.getCategories().add(new Category (catDto.getId(), null));
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<UserDetailsProjection> result = repository.searchUserAndRolesbyEmail(username);
		
		if (result.size() == 0) {
			throw new UsernameNotFoundException("User not found");
		}
		User user = new User();
		user.setEmail(username);
		user.setPassword(result.get(0).getPassword());
		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}
		return user;
	}


}













