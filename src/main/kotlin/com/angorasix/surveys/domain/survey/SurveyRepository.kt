package com.angorasix.surveys.domain.survey

import com.angorasix.surveys.infrastructure.persistence.repository.SurveyFilterRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

/**
 *
 *
 * @author rozagerardo
 */
interface SurveyRepository :
    CoroutineCrudRepository<Survey, String>,
    CoroutineSortingRepository<Survey, String>,
    SurveyFilterRepository
