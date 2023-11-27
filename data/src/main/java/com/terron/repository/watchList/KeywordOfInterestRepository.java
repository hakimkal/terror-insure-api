package com.terron.repository.watchList;

import com.terron.models.watchList.KeywordOfInterest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordOfInterestRepository extends JpaRepository<KeywordOfInterest, Long> {

    Page<KeywordOfInterest> findByKeywordContaining(String keyword, Pageable pagination);
}
