package it.pagopa.sessionsservice.domain

import java.util.*
import java.util.regex.Pattern

class RptId(rptId: String) {
    val rptId: String

    companion object {
        private val rptIdRegex = Pattern.compile("([a-zA-Z\\d]{1,35})|(RF\\d{2}[a-zA-Z\\d]{1,21})")
    }

    init {
        require(
            rptIdRegex.matcher(rptId).matches()
        ) { "Ill-formed RPT id: " + rptId + ". Doesn't match format: " + rptIdRegex.pattern() }
        this.rptId = rptId
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val that = obj as RptId
        return rptId == that.rptId
    }

    override fun hashCode(): Int {
        return Objects.hash(rptId)
    }

    override fun toString(): String {
        return "RptId[" +
                "rptId=" + rptId + ']'
    }
}