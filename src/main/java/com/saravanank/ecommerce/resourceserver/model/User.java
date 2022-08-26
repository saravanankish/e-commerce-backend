package com.saravanank.ecommerce.resourceserver.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userId;
	
	@NotEmpty(message = "User name should not empty")
	private String name;
	
	@Email(message = "Invalid email address")
	@NotNull(message = "User email should not be null")
	private String email;
	
	@NotEmpty(message = "Username of user should not be empty")
	@Size(min = 6, max = 20, message = "Username should be between 6 and 20 character")
	private String username;
	
	@NotEmpty(message = "Password should not be empty")
	@Size(min = 6, message = "Password should contain atleast 6 characters")
	private String password;
	
	@NotNull(message = "User role should not be null")
	private Role role;
	
	private boolean accountActive = true;
	private Date creationTime = new Date();
	private Date modifiedTime = new Date();
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private List<MobileNumber> mobileNumbers;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "modified_by")
	private User modifiedBy;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private List<Address> addresses;

}
