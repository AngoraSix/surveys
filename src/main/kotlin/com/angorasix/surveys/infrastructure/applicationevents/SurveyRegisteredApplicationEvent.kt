package com.angorasix.surveys.infrastructure.applicationevents

import com.angorasix.commons.domain.A6Contributor

class SurveyRegisteredApplicationEvent(
    val surveyResponseId: String,
    val surveyKey: String,
    val response: Map<String, Any>,
    val requestingContributor: A6Contributor? = null,
)
