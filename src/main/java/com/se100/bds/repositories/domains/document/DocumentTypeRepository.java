package com.se100.bds.repositories.domains.document;

import com.se100.bds.models.entities.document.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, UUID>, JpaSpecificationExecutor<DocumentType> {
    Page<DocumentType> findAllByIsCompulsory(Boolean isCompulsory, Pageable pageable);
}
