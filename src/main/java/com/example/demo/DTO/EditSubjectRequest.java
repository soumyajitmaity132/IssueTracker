package com.example.demo.DTO;

public class EditSubjectRequest {
    private String department;
    private String oldSubject;
    private String newSubject;
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getOldSubject() {
        return oldSubject;
    }
    public void setOldSubject(String oldSubject) {
        this.oldSubject = oldSubject;
    }
    public String getNewSubject() {
        return newSubject;
    }
    public void setNewSubject(String newSubject) {
        this.newSubject = newSubject;
    }
    public EditSubjectRequest(String department, String oldSubject, String newSubject) {
        this.department = department;
        this.oldSubject = oldSubject;
        this.newSubject = newSubject;
    }
    
    public EditSubjectRequest() {

    }


}
