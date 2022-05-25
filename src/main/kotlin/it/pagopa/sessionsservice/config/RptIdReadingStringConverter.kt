package it.pagopa.sessionsservice.config

import it.pagopa.sessionsservice.domain.RptId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.stereotype.Component


@Component
@ReadingConverter
class RptIdReadingStringConverter :
    Converter<String?, RptId> {
    override fun convert(source: String): RptId? {
        return RptId(source)
    }
}