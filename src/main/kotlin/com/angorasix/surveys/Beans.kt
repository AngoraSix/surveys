package com.angorasix.surveys

import com.angorasix.surveys.application.SurveyService
import com.angorasix.surveys.infrastructure.security.SurveySecurityConfiguration
import com.angorasix.surveys.presentation.handler.SurveyHandler
import com.angorasix.surveys.presentation.router.SurveyRouter
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans

val beans = beans {
    bean {
        SurveySecurityConfiguration().springSecurityFilterChain(ref())
    }
    bean<SurveyService>()
    bean<SurveyHandler>()
    bean {
        SurveyRouter(ref(), ref()).surveysRouterFunction()
    }
}

class BeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) = beans.initialize(context)
}
