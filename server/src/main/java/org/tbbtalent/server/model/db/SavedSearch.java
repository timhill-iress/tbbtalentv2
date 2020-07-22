/*
 * Copyright (c) 2020 Talent Beyond Boundaries. All rights reserved.
 */

package org.tbbtalent.server.model.db;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "saved_search")
@SequenceGenerator(name = "seq_gen", sequenceName = "saved_search_id_seq", allocationSize = 1)
public class SavedSearch extends AbstractCandidateSource {
    private static final Logger log = LoggerFactory.getLogger(SavedSearch.class);

    private String type;
    
    private Boolean defaultSearch = false; 
    
    private String keyword;
    private String statuses;
    private Gender gender;

    private String occupationIds;
    private String orProfileKeyword;

    private String verifiedOccupationIds;
    @Enumerated(EnumType.STRING)
    private SearchType verifiedOccupationSearchType;

    private String nationalityIds;
    @Enumerated(EnumType.STRING)
    private SearchType nationalitySearchType;

    private String countryIds;

    private Integer englishMinWrittenLevel;
    private Integer englishMinSpokenLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_language_id")
    private Language otherLanguage;
    private Integer otherMinWrittenLevel;
    private Integer otherMinSpokenLevel;

    private Boolean unRegistered;

    private LocalDate lastModifiedFrom;
    private LocalDate lastModifiedTo;

    private LocalDate createdFrom;
    private LocalDate createdTo;

    private Integer minAge;
    private Integer maxAge;


    private Integer minEducationLevel;
    private String educationMajorIds;

    private Boolean includeDraftAndDeleted;
    private Boolean reviewable = false;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "savedSearch", cascade = CascadeType.MERGE)
    private Set<SearchJoin> searchJoins = new HashSet<>();
    
    //Note use of Set rather than List as strongly recommended for Many to Many
    //relationships here: 
    // https://thoughts-on-java.org/best-practices-for-many-to-many-associations-with-hibernate-and-jpa/
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sharedSearches", cascade = CascadeType.MERGE)
    private Set<User> users = new HashSet<>();     

    @Transient private List<String> countryNames;
    @Transient private List<String> nationalityNames;
    @Transient private List<String> vettedOccupationNames;
    @Transient private List<String> occupationNames;
    @Transient private List<String> educationMajors;
    @Transient private String englishWrittenLevel;
    @Transient private String englishSpokenLevel;
    @Transient private String otherWrittenLevel;
    @Transient private String otherSpokenLevel;
    @Transient private String minEducationLevelName;
    @Transient private SavedSearchType savedSearchType;
    @Transient private SavedSearchSubtype savedSearchSubtype;

    public SavedSearch() {
    }

    public Boolean getDefaultSearch() {
        return defaultSearch;
    }

    public void setDefaultSearch(Boolean defaultSearch) {
        if (defaultSearch != null) {
            this.defaultSearch = defaultSearch;
        }
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStatuses() {
        return statuses;
    }

    public void setStatuses(String statuses) {
        this.statuses = statuses;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getOccupationIds() {
        return occupationIds;
    }

    public void setOccupationIds(String occupationIds) {
        this.occupationIds = occupationIds;
    }

    public String getOrProfileKeyword() {
        return orProfileKeyword;
    }

    public void setOrProfileKeyword(String orProfileKeyword) {
        this.orProfileKeyword = orProfileKeyword;
    }

    public String getVerifiedOccupationIds() {
        return verifiedOccupationIds;
    }

    public void setVerifiedOccupationIds(String verifiedOccupationIds) {
        this.verifiedOccupationIds = verifiedOccupationIds;
    }

    public String getNationalityIds() {
        return nationalityIds;
    }

    public void setNationalityIds(String nationalityIds) {
        this.nationalityIds = nationalityIds;
    }

    public SearchType getVerifiedOccupationSearchType() {
        return verifiedOccupationSearchType;
    }

    public void setVerifiedOccupationSearchType(SearchType verifiedOccupationSearchType) {
        this.verifiedOccupationSearchType = verifiedOccupationSearchType;
    }

    public SearchType getNationalitySearchType() {
        return nationalitySearchType;
    }

    public void setNationalitySearchType(SearchType nationalitySearchType) {
        this.nationalitySearchType = nationalitySearchType;
    }

    public String getCountryIds() {
        return countryIds;
    }

    public void setCountryIds(String countryIds) {
        this.countryIds = countryIds;
    }

    public Integer getEnglishMinWrittenLevel() {
        return englishMinWrittenLevel;
    }

    public void setEnglishMinWrittenLevel(Integer englishMinWrittenLevel) {
        this.englishMinWrittenLevel = englishMinWrittenLevel;
    }

    public Integer getEnglishMinSpokenLevel() {
        return englishMinSpokenLevel;
    }

    public void setEnglishMinSpokenLevel(Integer englishMinSpokenLevel) {
        this.englishMinSpokenLevel = englishMinSpokenLevel;
    }

    public Language getOtherLanguage() {
        return otherLanguage;
    }

    public void setOtherLanguage(Language otherLanguage) {
        this.otherLanguage = otherLanguage;
    }

    public Integer getOtherMinWrittenLevel() {
        return otherMinWrittenLevel;
    }

    public void setOtherMinWrittenLevel(Integer otherMinWrittenLevel) {
        this.otherMinWrittenLevel = otherMinWrittenLevel;
    }

    public Integer getOtherMinSpokenLevel() {
        return otherMinSpokenLevel;
    }

    public void setOtherMinSpokenLevel(Integer otherMinSpokenLevel) {
        this.otherMinSpokenLevel = otherMinSpokenLevel;
    }

    public Boolean getUnRegistered() {
    return unRegistered;
  }

    public void setUnRegistered(Boolean unRegistered) {
        this.unRegistered = unRegistered;
    }

    public LocalDate getLastModifiedFrom() {
        return lastModifiedFrom;
    }

    public void setLastModifiedFrom(LocalDate lastModifiedFrom) {
        this.lastModifiedFrom = lastModifiedFrom;
    }

    public LocalDate getLastModifiedTo() {
        return lastModifiedTo;
    }

    public void setLastModifiedTo(LocalDate lastModifiedTo) {
        this.lastModifiedTo = lastModifiedTo;
    }

    public LocalDate getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(LocalDate createdFrom) {
        this.createdFrom = createdFrom;
    }

    public LocalDate getCreatedTo() {
        return createdTo;
    }

    public void setCreatedTo(LocalDate createdTo) {
        this.createdTo = createdTo;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

     public Integer getMinEducationLevel() {
        return minEducationLevel;
    }

    public void setMinEducationLevel(Integer minEducationLevel) {
        this.minEducationLevel = minEducationLevel;
    }

    public String getEducationMajorIds() {
        return educationMajorIds;
    }

    public void setEducationMajorIds(String educationMajorIds) {
        this.educationMajorIds = educationMajorIds;
    }

    public Set<SearchJoin> getSearchJoins() {
        return searchJoins;
    }

    public void setSearchJoins(Set<SearchJoin> searchJoins) {
        this.searchJoins = searchJoins;
    }

    public List<String> getCountryNames() {
        return countryNames;
    }

    public void setCountryNames(List<String> countryNames) {
        this.countryNames = countryNames;
    }

    public List<String> getNationalityNames() {
        return nationalityNames;
    }

    public void setNationalityNames(List<String> nationalityNames) {
        this.nationalityNames = nationalityNames;
    }

    public List<String> getVettedOccupationNames() {
        return vettedOccupationNames;
    }

    public void setVettedOccupationNames(List<String> vettedOccupationNames) {
        this.vettedOccupationNames = vettedOccupationNames;
    }

    public List<String> getOccupationNames() {
        return occupationNames;
    }

    public void setOccupationNames(List<String> occupationNames) {
        this.occupationNames = occupationNames;
    }

    public List<String> getEducationMajors() {
        return educationMajors;
    }

    public void setEducationMajors(List<String> educationMajors) {
        this.educationMajors = educationMajors;
    }

    public String getEnglishWrittenLevel() {
        return englishWrittenLevel;
    }

    public void setEnglishWrittenLevel(String englishWrittenLevel) {
        this.englishWrittenLevel = englishWrittenLevel;
    }

    public String getEnglishSpokenLevel() {
        return englishSpokenLevel;
    }

    public void setEnglishSpokenLevel(String englishSpokenLevel) {
        this.englishSpokenLevel = englishSpokenLevel;
    }

    public String getOtherWrittenLevel() {
        return otherWrittenLevel;
    }

    public void setOtherWrittenLevel(String otherWrittenLevel) {
        this.otherWrittenLevel = otherWrittenLevel;
    }

    public String getOtherSpokenLevel() {
        return otherSpokenLevel;
    }

    public void setOtherSpokenLevel(String otherSpokenLevel) {
        this.otherSpokenLevel = otherSpokenLevel;
    }

    public String getMinEducationLevelName() {
        return minEducationLevelName;
    }

    public void setMinEducationLevelName(String minEducationLevelName) {
        this.minEducationLevelName = minEducationLevelName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setType(SavedSearchType savedSearchType, SavedSearchSubtype subtype) {
        setSavedSearchType(savedSearchType);
        setSavedSearchSubtype(subtype);

        String type = makeStringSavedSearchType(savedSearchType, subtype);
        setType(type);
    }

    public static String makeStringSavedSearchType(
            SavedSearchType savedSearchType, SavedSearchSubtype subtype) {
        String type = null;
        if (savedSearchType != null) {
            type = savedSearchType.toString();
            if (subtype != null) {
                type += "/" + subtype.toString();
            }
        }
        return type;
    }

    public SavedSearchType getSavedSearchType() {
        return savedSearchType;
    }

    public void setSavedSearchType(SavedSearchType type) {
        this.savedSearchType = type;
    }

    @Nullable public SavedSearchSubtype getSavedSearchSubtype() {
        return savedSearchSubtype;
    }

    public void setSavedSearchSubtype(@Nullable SavedSearchSubtype savedSearchSubtype) {
        this.savedSearchSubtype = savedSearchSubtype;
    }

    public void parseType() {
        if (!StringUtils.isEmpty(type)) {
            String[] parts = type.split("/");
            try {
                SavedSearchType savedSearchType = SavedSearchType.valueOf(parts[0]);
                setSavedSearchType(savedSearchType);

                //Check for subtype
                if (parts.length > 1) {
                    SavedSearchSubtype savedSearchSubtype = SavedSearchSubtype.valueOf(parts[1]);
                    setSavedSearchSubtype(savedSearchSubtype);
                }
            } catch (IllegalArgumentException ex) {
                log.error("Bad type '" + type + "' of saved search " + getId(), ex);
            }
        }
    }

    public Boolean getIncludeDraftAndDeleted() {
        return includeDraftAndDeleted;
    }

    public void setIncludeDraftAndDeleted(Boolean includeDraftAndDeleted) {
        this.includeDraftAndDeleted = includeDraftAndDeleted;
    }
    
    public Boolean getReviewable() {
        return reviewable;
      }
    
    public void setReviewable(Boolean reviewable) {
        if (reviewable != null) {
            this.reviewable = reviewable;
        }
    }

    public Set<User> getUsers() {
        return users;
    }

    @Override
    public Set<SavedSearch> getUsersCollection(User user) {
        return user.getSharedSearches();
    }
}