package com.example.main.compilation.repository;

import com.example.main.compilation.model.Compilation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Integer> {
    boolean existsByTitle(String title);

    Page<Compilation> getAllByPinned(boolean pinned, Pageable pageable);
}