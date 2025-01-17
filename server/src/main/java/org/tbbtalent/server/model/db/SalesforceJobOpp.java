/*
 * Copyright (c) 2022 Talent Beyond Boundaries.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package org.tbbtalent.server.model.db;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * This is a copy of an Employer Job Opportunity on Salesforce
 * <p/>
 * Job Opps are intended to only be used for the monitoring open job opps.
 * They are not intended to completely duplicate what is on SF - eg history
 * <p/>
 * Approach to keeping in sync with Salesforce
 * ===========================================
 * - Only job opps that have been registered in TC are updated from Salesforce. There will be
 * opportunities on Salesforce that are not reflected on the TC.
 * - Once a day local open opportunities on the TC are updated from Salesforce
 * (see JobService.updateOpenJobs)
 *
 * @author John Cameron
 */
@Getter
@Setter
@Entity
@Table(name = "salesforce_job_opp")
@SequenceGenerator(name = "seq_gen", sequenceName = "salesforce_job_opp_tc_job_id_seq", allocationSize = 1)
public class SalesforceJobOpp extends AbstractAuditableDomainObject<Long> {

    /**
     * ID of copied Salesforce job opportunity is also used as id of this copy.
     */
    @Column(name = "sf_job_opp_id")
    private String sfId;

    /**
     * True if the job is currently accepting processing by other than its creator.
     * Setting it false will make the job hidden on the TC front end to all but the creator.
     * The first time accepting is set true for a job effectively "publishes" the job so that
     * others can see it and process it.
     */
    private boolean accepting;

    /**
     * Salesforce id of account (ie employer) associated with opportunity
     */
    private String accountId;

    /**
     * True if opportunity is closed
     */
    private boolean closed;

    /**
     * Email to use for enquiries about this job.
     * <p/>
     * Should default to email of {@link #contactUser} - but can be different
     */
    private String contactEmail;

    /**
     * TC user responsible for this job - will normally be "destination" staff located in the same
     * region as the {@link #employer}
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_user_id")
    private User contactUser;

    /**
     * todo Remove this field when we moved across to using countryObject everywhere
     * FROM SALESFORCE: Name of country where job is located
     */
    private String country;

    /**
     * todo: Once above country field removed, rename countryObject to country
     * References country object on database (set using the country name above that comes from SF)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_object_id")
    private Country countryObject;

    /**
     * Description given to job in job intake.
     */
    private String description;
    
    /**
     * Name of employer - maps to Account name on Salesforce
     */
    private String employer;

    /**
     * Optional exclusion list associated with job.
     * <p/>
     * Used to exclude people who have already been seen and rejected for this job from future
     * searches (see {@link SavedSearch#getExclusionList()}).
     */
    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exclusion_list_id")
    private SavedList exclusionList;

    /**
     * Summary describing job
     */
    private String jobSummary;

    /**
     * Last time that this was updated from Salesforce (which holds the master copy)
     */
    private OffsetDateTime lastUpdate;

    /**
     * Name of opportunity - maps to Opportunity name on Salesforce
     */
    private String name;

    /**
     * Salesforce id of owner of opportunity
     */
    private String ownerId;

    /**
     * User that published job on the TC
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_by")
    private User publishedBy;

    /**
     * Time that this job was published on the TC
     */
    private OffsetDateTime publishedDate;

    /**
     * Recruiter partner responsible for this job.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_partner_id")
    private RecruiterPartnerImpl recruiterPartner;

    /**
     * Stage of job opportunity
     */
    @Enumerated(EnumType.STRING)
    private JobOpportunityStage stage;

    /**
     * Stage of job opportunity expressed as number - 0 being first stage.
     * <p/>
     * Used for sorting by stage.
     * <p/>
     * This is effectively a computed field, computed by calling the ordinal() method of the
     * {@link #stage} enum.
     */
    private int stageOrder;

    //Note use of Set rather than List as strongly recommended for Many to Many
    //relationships here:
    // https://thoughts-on-java.org/best-practices-for-many-to-many-associations-with-hibernate-and-jpa/
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
        name = "user_job",
        joinColumns = @JoinColumn(name = "tc_job_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> starringUsers = new HashSet<>();

    /**
     * Date that submission of candidates to employer is due.
     */
    @Nullable
    private LocalDate submissionDueDate;

    /**
     * This is the official list of candidates which will be submitted to the employer for
     * their consideration.
     * <p/>
     * This list should have the {@link SavedList#getRegisteredJob()} attribute set true.
     * That marks it as a special list associated with a single job.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_list_id")
    private SavedList submissionList;

    /**
     * Optional list containing candidates that the employer/recruiter thought looked right for the
     * job
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suggested_list_id")
    private SavedList suggestedList;

    /**
     * Optional search(es) that the employer/recruiter thought would find candidates matching the
     * job requirements.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "job_suggested_saved_search",
        joinColumns = @JoinColumn(name = "tc_job_id"),
        inverseJoinColumns = @JoinColumn(name = "saved_search_id"))
    private Set<SavedSearch> suggestedSearches = new HashSet<>();
    
    @Nullable
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_opp_intake_id")
    private JobOppIntake jobOppIntake;

    /**
     * Salesforce field: hiring commitment of job opportunity
     * As of 22/5/23 this may change to a text field, stored in database as text but currently a number from SF.
     */
    private Long hiringCommitment;
    
    /**
     * Salesforce field: the website of the employer
     * (On SF exists on Account, but copied to Opportunity and fetched with Opportunity object)
     */
    private String employerWebsite;
    
    /**                        
     * Salesforce field: if the employer has hired internationally before 
     * (On SF exists on Account, but copied to Opportunity and fetched on Opportunity object) 
     */
    private String employerHiredInternationally;

    public void addStarringUser(User user) {
        starringUsers.add(user);
    }

    public void removeStarringUser(User user) {
        starringUsers.remove(user);
    }

    /**
     * Override standard setStage to automatically also update stageOrder
     * @param stage New job opportunity stage
     */
    public void setStage(JobOpportunityStage stage) {
        this.stage = stage;
        setStageOrder(stage.ordinal());
    }
}
