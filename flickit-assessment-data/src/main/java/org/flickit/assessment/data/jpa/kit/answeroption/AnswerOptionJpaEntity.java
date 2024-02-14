package org.flickit.assessment.data.jpa.kit.answeroption;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.annotation.ReferenceNumberValue;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fak_answer_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnswerOptionJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_answer_option_id_seq")
    @SequenceGenerator(name = "fak_answer_option_id_seq", sequenceName = "fak_answer_option_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "index", nullable = false)
    private Integer index;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    @Column(name = "reference_number", nullable = false)
    @ReferenceNumberValue(query = "(SELECT nextval('fak_answer_option_reference_number_seq'))")
    private Long referenceNumber;

    @Column(name = "kit_id", nullable = false)
    private Long kitId;

}
