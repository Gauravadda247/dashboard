package com.adda.users.ui.model;

import java.io.Serializable;
import org.springframework.data.redis.core.RedisHash;

public class UserResponseModel implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -229612321422611667L;
	private String userId;
    private String firstName;
    private String lastName;
    private String  email;
    private int total;
    private int maths;
    private int physics;
    
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getMaths() {
		return maths;
	}
	public void setMaths(Integer maths) {
		this.maths = maths;
	}
	public Integer getPhysics() {
		return physics;
	}
	public void setPhysics(Integer physics) {
		this.physics = physics;
	}

	
	
}
