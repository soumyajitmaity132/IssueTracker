package com.example.demo.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
//import org.springframework.data.annotation.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unused")
@Entity
@Getter
@Setter

@AllArgsConstructor
@Table(name = "TICKET")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ticketNumber;

    
    /* @Column(name = "created_at")
@Temporal(TemporalType.TIMESTAMP)
private Date createdAt;

@PrePersist
protected void onCreate() {
    this.createdAt = new Date();
}
   public Date getCreatedAt() {
    return createdAt;
}*/
      


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String department;
    private String description;
    private String assignee;
    @Column(name = "status")
    private String status; // Open, Assigned, Fixed
    private String priority; // High, Medium, Low
    private String severity;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;  // default to false

     public void setIsDeleted(boolean isDeleted){
        this.isDeleted=isDeleted;
     }

     public Boolean isDeleted() {
    return isDeleted;
}



    private String raisedBy;  
    private String employeeEmail;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Message> chatMessages = new ArrayList<>();

public List<Message> getChatMessages() {
    return chatMessages;
}

public void setChatMessages(List<Message> chatMessages) {
    this.chatMessages = chatMessages;
}


    public String getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(String raisedBy) {
        this.raisedBy = raisedBy;
    }

    public Ticket() {
    }

    public Ticket(Long id, String ticketNumber, User user, String department, String description, String assignee, String status, String priority, String severity) {
        this.id = id;
        this.ticketNumber = ticketNumber;
        this.user = user;
        this.department = department;
        this.description = description;
        this.assignee = assignee;
        this.status = status;
        this.priority = priority;
        this.severity = severity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private LocalDateTime createdAt;

    // Getter and Setter
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", user=" + user +
                ", department='" + department + '\'' +
                ", description='" + description + '\'' +
                ", assignee='" + assignee + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", severity='" + severity + '\'' +
                '}';
    }

    public void setRaisedBy(User employee) {
        throw new UnsupportedOperationException("Unimplemented method 'setRaisedBy'");
    }

    public void setEmployeeEmail(String employee_email) {
        this.employeeEmail=employee_email;
    }

    public String getEmployeeEmail() {
       return employeeEmail;
    }
}
