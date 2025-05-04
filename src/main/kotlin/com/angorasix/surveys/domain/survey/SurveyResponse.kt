package com.angorasix.surveys.domain.survey

import com.angorasix.commons.domain.A6Contributor
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.mongodb.core.mapping.Document

/**
 * <p>
 *     Root entity defining the SurveysApplicationTest data.
 * </p>
 *
 * @author rozagerardo
 */
@Document
data class SurveyResponse
    @PersistenceCreator
    private constructor(
        @field:Id val id: String?,
        val surveyKey: String,
        val contributorId: String?,
        val admins: Set<A6Contributor> = emptySet(),
        val response: Map<String, Any> = emptyMap(),
    ) {
        constructor(
            surveyKey: String,
            contributorId: String?,
            admins: Set<A6Contributor> = emptySet(),
            response: Map<String, Any> = emptyMap(),
        ) : this(
            null,
            surveyKey,
            contributorId,
            admins,
            response,
        )

        fun isAdministeredBy(simpleContributor: A6Contributor): Boolean = admins.any { it.contributorId == simpleContributor.contributorId }
    }
