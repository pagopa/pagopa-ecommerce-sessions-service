package it.pagopa.sessionsservice.domain

import java.util.regex.Pattern

data class RptId(val rptId: String) {

    companion object {
        private val rptIdRegex = Pattern.compile("([a-zA-Z\\d]{1,35})|(RF\\d{2}[a-zA-Z\\d]{1,21})")

        fun validate(rptId: String): Boolean{
            return rptId matches rptIdRegex.toRegex()
        }
    }

    init {
        require(
            rptIdRegex.matcher(rptId).matches()
        ) { "Ill-formed RPT id: " + rptId + ". Doesn't match format: " + rptIdRegex.pattern() }
    }
}