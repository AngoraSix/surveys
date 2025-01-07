package com.angorasix.surveys.presentation.dto

import org.springframework.hateoas.RepresentationModel

/**
 *
 *
 * @author rozagerardo
 */
data class SurveyResponseDto(
    val surveyKey: String,
    val response: Map<String, Any> = emptyMap(),
    val id: String? = null,
) : RepresentationModel<SurveyResponseDto>()
