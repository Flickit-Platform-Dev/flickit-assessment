package org.flickit.assessment.data.jpa.kit.attribute;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@IdClass(AttributeJpaEntity.EntityId.class)
@Table(name = "fak_attribute")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttributeJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_attribute_id_seq")
    @SequenceGenerator(name = "fak_attribute_id_seq", sequenceName = "fak_attribute_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Id
    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Column(name = "ref_num", nullable = false)
    private UUID refNum;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "index", nullable = false)
    private Integer index;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;


    public AttributeJpaEntity(Long id,
                              long kitVersionId,
                              String code,
                              String title,
                              Integer index,
                              String description,
                              Integer weight,
                              LocalDateTime creationTime,
                              LocalDateTime lastModificationTime,
                              long subjectId) {
        this.id = id;
        this.kitVersionId = kitVersionId;
        this.code = code;
        this.title = title;
        this.index = index;
        this.description = description;
        this.weight = weight;
        this.creationTime = creationTime;
        this.lastModificationTime = lastModificationTime;
        this.subjectId = subjectId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId {
        private Long id;
        private Long kitVersionId;
    }
}
