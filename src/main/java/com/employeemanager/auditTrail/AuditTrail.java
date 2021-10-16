package com.employeemanager.auditTrail;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.employeemanager.domain.Employee;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class AuditTrail extends PanacheEntity {

	@Column(nullable = false)
	public String action;

	@Column(nullable = false)
	public Long identifier;

	@Column(nullable = false)
	public LocalDateTime entryDate = LocalDateTime.now();

	@Column(columnDefinition = "LONGTEXT")
	public String oldData;

	@Column(columnDefinition = "LONGTEXT")
	public String newData;

	// relationships
	@ManyToOne
	@JoinColumn(nullable = false)
	public Employee employee;

	public AuditTrail() {
	}

	public AuditTrail(String action, Long identifier, String oldData, String newData, Employee employee) {
		this.action = action;
		this.identifier = identifier;
		this.oldData = oldData;
		this.newData = newData;
		this.employee = employee;
	}

	public static List<AuditTrail> getByIdentifier(Long Identifier) {
		return list("identifier=?1 order by entryDate asc", Identifier);
	}

}
