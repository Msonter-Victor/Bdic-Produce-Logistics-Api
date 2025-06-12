package dev.gagnon.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "IssueReports")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason; // WRONG_ITEM, DAMAGED, etc.

    private String description;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;
// OPEN, RESOLVED, ESCALATED
    private LocalDateTime reportedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
