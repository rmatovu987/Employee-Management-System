package com.employeemanager.auditTrail;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class AuthTrail extends PanacheEntity {

	@NotNull
	@Column(nullable = false)
	private String ipAddress;

	@NotNull
	@Column(nullable = false)
	public String email;

	@NotNull
	@Column(nullable = false)
	public String deviceDetail;

	@NotNull
	@Column(nullable = false)
	public LocalDateTime entryDate = LocalDateTime.now();

	@NotNull
	@Column(nullable = false)
	private String status;

	public AuthTrail() {
	}

	public AuthTrail(String ipAddress, String email, String deviceDetail, String status) {
		this.ipAddress = ipAddress;
		this.email = email;
		this.deviceDetail = deviceDetail;
		this.status = status;
	}

}
