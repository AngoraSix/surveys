package com.angorasix.surveys.domain.survey

import com.angorasix.surveys.infrastructure.persistence.repository.SurveyFilterRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

/**
 *
 *
 * @author rozagerardo
 */
interface SurveyResponseRepository :
    CoroutineCrudRepository<SurveyResponse, String>,
    CoroutineSortingRepository<SurveyResponse, String>,
    SurveyFilterRepository
