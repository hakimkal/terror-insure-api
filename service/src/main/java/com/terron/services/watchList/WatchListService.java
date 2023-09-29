package com.terron.services.watchList;

import com.terron.dto.KeywordOfInterestDto;
import com.terron.dto.MissingPersonsDto;
import com.terron.dto.PersonOfInterestDto;
import com.terron.models.watchList.KeywordOfInterest;
import com.terron.models.watchList.MissingPersons;
import com.terron.models.watchList.PersonOfInterest;
import com.terron.services.utils.PaginationModel;

public interface WatchListService {
    KeywordOfInterest addKeywordOfInterest(KeywordOfInterestDto keywordOfInterestDto) throws Exception;

    PaginationModel getAllKeywordOfInterests(Integer page, Integer pageSize, String searchField);

    PersonOfInterest addPersonOfInterest(PersonOfInterestDto personOfInterestDto) throws Exception;

    PaginationModel getAllPersonOfInterests(Integer page, Integer pageSize, String searchField, String nationality);

    MissingPersons addMissingPersons(MissingPersonsDto missingPersonsDto) throws Exception;

    PaginationModel getAllMissingPersons(Integer page, Integer pageSize, String searchField, String nationality);
}
