package com.employeemanager.controller.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.employeemanager.auditTrail.AuditTrail;
import com.employeemanager.auditTrail.AuthTrail;

@ApplicationScoped
public class AuditService {
    
    public List<AuditTrail> getAllAuditTrails(){
        return AuditTrail.listAll();
    }

    public List<AuthTrail> getAllAuthTrails(){
        return AuthTrail.listAll();
    }

}
