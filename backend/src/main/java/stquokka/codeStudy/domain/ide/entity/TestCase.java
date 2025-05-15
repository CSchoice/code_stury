package stquokka.codeStudy.domain.ide.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "test_cases")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private CodingProblem problem;
    
    @Column(name = "test_number")
    private Integer testNumber;
    
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String input;
    
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String expectedOutput;
    
    @Column(name = "is_sample", nullable = false)
    private Boolean isSample;
    
    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden;
    
    @Column(name = "explanation", columnDefinition = "LONGTEXT")
    private String explanation;
}
